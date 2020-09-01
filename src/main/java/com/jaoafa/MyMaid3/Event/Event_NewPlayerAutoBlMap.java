package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Event_NewPlayerAutoBlMap extends MyMaidLibrary implements Listener {
    Set<UUID> firstLoginer = new HashSet<>();

    @EventHandler
    public void OnEvent_FirstLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            firstLoginer.remove(player.getUniqueId());
            return;
        }
        System.out.println("NewPlayerAutoBlMap: 初ログインユーザーがログイン");
        firstLoginer.add(player.getUniqueId());
    }

    @EventHandler
    public void OnEvent_Quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!firstLoginer.contains(player.getUniqueId())) {
            return;
        }
        System.out.println("NewPlayerAutoBlMap: 初ログインユーザーがログアウト");
        new BukkitRunnable() {
            public void run() {
                firstLoginer.remove(player.getUniqueId());
                String url = "https://api.jaoafa.com/cities/getblockimg?uuid=" + player.getUniqueId().toString();

                TextChannel channel = MyMaidConfig.getJaotanChannel();
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .build();
                    Request request = new Request.Builder().url(url).build();

                    Response response = client.newCall(request).execute();
                    if (response.code() != 200 && response.code() != 302) {
                        System.out.println("NewPlayerAutoBlMap: APIサーバへの接続に失敗: " + response.code() + " "
                                + response.body().string());
                        response.close();
                        return;
                    }
                    if (response.body() == null) {
                        System.out.println("NewPlayerAutoBlMap: APIサーバへの接続に失敗: response.body() is null.");
                        response.close();
                        return;
                    }
                    System.out.println("NewPlayerAutoBlMap: ブロック編集マップ取得完了");

                    channel.sendFile(response.body().byteStream(), player.getUniqueId().toString() + ".png")
                            .append(String.format("新規プレイヤー「%s」のブロック編集マップ", player.getName())).queue(msg -> {
                        System.out.println("NewPlayerAutoBlMap: メッセージ送信完了 (" + msg.getJumpUrl() + ")");
                        response.close();
                    }, failure -> {
                        System.out.println("NewPlayerAutoBlMap: メッセージ送信失敗 (" + failure.getMessage() + ")");
                        failure.printStackTrace();
                        response.close();
                    });
                } catch (IOException ex) {
                    System.out.println("NewPlayerAutoBlMap: APIサーバへの接続に失敗: " + ex.getMessage());
                }
            }
        }.runTaskAsynchronously(Main.getJavaPlugin());
    }
}
