package com.jaoafa.MyMaid3.Command;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cmd_Bug extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    private static final Map<UUID, String> bugreports = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        if (args.length == 0) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }
        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("true")) {
            if (!bugreports.containsKey(player.getUniqueId())) {
                // ない
                player.sendMessage("[Bug] " + ChatColor.GREEN + "送信できるメッセージがありません。まず/bug <Message>を実行してください。");
                return true;
            }
            sendMessage(bugreports.get(player.getUniqueId()), player);
            bugreports.remove(player.getUniqueId());
            return true;
        } else if (args[0].equalsIgnoreCase("false")) {
            bugreports.remove(player.getUniqueId());
            player.sendMessage("[Bug] " + ChatColor.GREEN + "送信予定メッセージを削除しました。");
            return true;
        }

        player.sendMessage("[Bug] " + ChatColor.RED + "送信前に再度次の内容をご確認ください！");
        player.sendMessage("[Bug] " + ChatColor.GREEN + "送信予定メッセージ: " + String.join(" ", args));
        player.sendMessage("[Bug] " + ChatColor.GREEN + "・公式Discordサーバの#develop_todoを確認してください。");
        player.sendMessage("[Bug] " + ChatColor.GREEN + "  既に同様の報告がなされていませんか？");
        player.sendMessage("[Bug] " + ChatColor.GREEN + "・5W1Hを用いてわかりやすく説明されていますか？");
        player.sendMessage("[Bug] " + ChatColor.GREEN + "  特に「どこで」「どうすると」バグが起こるかを詳しく記載してください。");

        String nowVer = Main.getJavaPlugin().getDescription().getVersion();
        String nowVerSha = getVersionSha(nowVer);
        String latestVerSha = getLastCommitSha("MyMaid3");
        if (!nowVerSha.equalsIgnoreCase(latestVerSha)) {
            player.sendMessage("[Bug] " + ChatColor.GREEN + "・MyMaid3の現在導入済みバージョン(" + nowVerSha + ")と最新リリースバージョン("
                    + latestVerSha + ")が異なります。");
            player.sendMessage("[Bug] " + ChatColor.GREEN + "  最近更新された事項でないかどうか確認してください。");
        }
        player.sendMessage("[Bug] " + ChatColor.GREEN + "");

        TextComponent component = Component.text().append(
                Component.text("[Bug]"),
                Component.space(),
                Component.text("上記内容を確認し、送信する場合は")
                        .style(Style.style(NamedTextColor.GREEN)),
                Component.text("ここ")
                        .style(Style.style(NamedTextColor.AQUA, TextDecoration.UNDERLINED))
                        .hoverEvent(HoverEvent.showText(
                                Component.text("「/bug true」を実行します。")
                        ))
                        .clickEvent(ClickEvent.runCommand("/bug true")),
                Component.space(),
                Component.text("をクリックしてください。")
                        .style(Style.style(NamedTextColor.GREEN))
        ).build();
        player.sendMessage(component);

        // ----- //

        TextComponent component2 = Component.text().append(
                Component.text("[Bug]"),
                Component.space(),
                Component.text("送信しない場合は")
                        .style(Style.style(NamedTextColor.GREEN)),
                Component.text("ここ")
                        .style(Style.style(NamedTextColor.AQUA, TextDecoration.UNDERLINED))
                        .hoverEvent(HoverEvent.showText(
                                Component.text("「/bug false」を実行します。")
                        ))
                        .clickEvent(ClickEvent.runCommand("/bug false")),
                Component.space(),
                Component.text("をクリックしてください。")
                        .style(Style.style(NamedTextColor.GREEN))
        ).build();
        player.sendMessage(component2);

        bugreports.put(player.getUniqueId(), String.join(" ", args));
        return true;
    }

    @Override
    public String getDescription() {
        return "バグ報告を行います。メッセージは公式Discordサーバの#develop_todoに送られます。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "bug",
                new CmdUsage.Cmd("<Message>", "バグ報告を行います。")
        );
    }

    private static void sendMessage(String mesg, Player player) {
        String message = mesg + "\nby `" + player.getName() + "`";

        WebhookClientBuilder builder = new WebhookClientBuilder(MyMaidConfig.getBugReportWebhookUrl());
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("MyMaid3BugReporter");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        WebhookClient client = builder.build();
        WebhookMessageBuilder msgbuilder = new WebhookMessageBuilder();
        msgbuilder.setUsername("MyMaid3 BugReporter");
        msgbuilder.setContent(message);
        player.sendMessage("[Bug] " + ChatColor.GREEN + "バグ報告を送信しています…");
        client.send(msgbuilder.build()).whenComplete((msg, ex) -> {
            // 成功した場合
            if (ex == null) player.sendMessage("[Bug] " + ChatColor.GREEN + "バグ報告を送信しました。ありがとうございます！");
            else {
                // 失敗した場合
                player.sendMessage("[Bug] " + ChatColor.GREEN + "大変申し訳ございません、バグ報告を送信できませんでした。");
                player.sendMessage("[Bug] " + ChatColor.GREEN + "少し時間をおいてから再度実行するか、別の方法を用いて運営までお問い合わせください。");
            }
        });
    }

    private static String getLastCommitSha(String repo) {
        try {
            String url = "https://api.github.com/repos/jaoafa/" + repo + "/commits";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) return null;
            JSONArray array = new JSONArray(response.body().string());
            response.close();

            return array.getJSONObject(0).getString("sha").substring(0, 7);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getVersionSha(String version) {
        String[] day_time = version.split("_");
        if (day_time.length == 3) return day_time[2];
        return null;
    }
}
