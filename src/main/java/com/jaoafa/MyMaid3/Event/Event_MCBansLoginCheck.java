package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Main;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.UUID;

public class Event_MCBansLoginCheck extends MyMaidLibrary implements Listener {
    @EventHandler
    public void OnLoginCheck(AsyncPlayerPreLoginEvent event) {
        // MCBansが落ちている場合を考慮してjaoafaデータベースからチェック

        // Reputationチェック
        String name = event.getName();
        UUID uuid = event.getUniqueId();

        MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
        if (MySQLDBManager == null) {
            return;
        }

        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT * FROM mcbans WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                float reputation = res.getFloat("reputation");
                if (reputation < 3) {
                    // 3未満は規制
                    String message = ChatColor.RED + "----- MCBans Checker -----\n"
                            + ChatColor.RESET + ChatColor.WHITE + "Access denied.\n"
                            + ChatColor.RESET + ChatColor.WHITE + "Your reputation is below this server's threshold.";
                    event.disallow(Result.KICK_BANNED, message);
                    res.close();
                    return;
                }
                if (reputation != 10) {
                    sendAMR(ChatColor.RED + "[MCBansChecker] " + ChatColor.GREEN + name + " reputation: " + reputation);
                }
            }
            res.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // jaoでBan済みかどうか
        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT * FROM mcbans_jaoafa WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                int banid = res.getInt("banid");
                String type = res.getString("type");
                String reason = res.getString("reason");
                String message = ChatColor.RED + "----- MCBans Checker -----\n"
                        + ChatColor.RESET + ChatColor.WHITE + "Access denied.\n"
                        + ChatColor.RESET + ChatColor.WHITE + "Reason: " + reason + "\n"
                        + ChatColor.RESET + ChatColor.WHITE + "Ban type: " + type + "\n"
                        + ChatColor.RESET + ChatColor.WHITE + "http://mcbans.com/ban/" + banid;
                event.disallow(Result.KICK_BANNED, message);
                res.close();
                statement.close();
                return;
            }
            res.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void OnLoginAfterCheck(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            public void run() {
                try {
                    String url = String.format("https://api.jaoafa.com/users/mcbans/%s", uuid.toString());

                    System.out.println(MessageFormat.format("OnLoginAfterCheck: APIサーバへの接続を開始: {0} -> {1}", player.getName(), url));
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).get().build();
                    Response response = client.newCall(request).execute();
                    if (response.code() != 200) {
                        System.out.printf("OnLoginAfterCheck: APIサーバへの接続に失敗: %s -> %d%n", url, response.code());
                        response.close();
                        return;
                    }
                    ResponseBody body = response.body();
                    if (body == null) {
                        System.out.println(MessageFormat.format("OnLoginAfterCheck: APIサーバへの接続に失敗: {0} -> response.body() is null.", url));
                        response.close();
                        return;
                    }

                    JSONObject json = new JSONObject(body.string());
                    response.close();

                    if (!json.has("status")) {
                        System.out.println("OnLoginAfterCheck: レスポンスの解析に失敗: status not found.");
                        return;
                    }
                    if (!json.getBoolean("status")) {
                        System.out.println("OnLoginAfterCheck: レスポンスの解析に失敗: status not boolean.");
                        return;
                    }
                    if (!json.has("data")) {
                        System.out.println("OnLoginAfterCheck: レスポンスの解析に失敗: data not found.");
                        return;
                    }

                    JSONObject data = json.getJSONObject("data");
                    double reputation = data.getDouble("reputation");
                    int globalCount = data.getInt("global");
                    int localCount = data.getInt("local");

                    MyMaidLibrary.sendAMR(String.format("[MCBans] %s%s -> %s (G:%d | L:%d)", ChatColor.GRAY, player.getName(), reputation, globalCount, localCount));
                    if (reputation < 3) {
                        // 3未満はキック
                        String message = ChatColor.RED + "----- MCBans Checker -----\n"
                                + ChatColor.RESET + ChatColor.WHITE + "Access denied.\n"
                                + ChatColor.RESET + ChatColor.WHITE + "Your reputation is below this server's threshold.";
                        new BukkitRunnable() {
                            public void run() {
                                player.kickPlayer(message);
                            }
                        }.runTask(Main.getJavaPlugin());
                    }
                } catch (IOException e) {
                    System.out.println("OnLoginAfterCheck: IOException Error...");
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getJavaPlugin());
    }
}
