/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Cmd_MyMaid3 extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        PluginDescriptionFile desc = Main.getJavaPlugin().getDescription();
        String nowVer = desc.getVersion();
        Date nowVerDate = getVersionDate(nowVer);
        String nowVerSha = getVersionSha(nowVer);
        if(nowVerSha == null){
            SendMessage(sender, cmd, ChatColor.AQUA + "現バージョン情報の取得に失敗しました。");
            return true;
        }
        String latestVer = getVersion(desc.getName());
        if(latestVer == null){
            SendMessage(sender, cmd, ChatColor.AQUA + "最新バージョン情報の取得に失敗しました。");
            return true;
        }
        Date latestVerDate = getVersionDate(latestVer);
        String latestVerSha = getLastCommitSha(desc.getName());

        SendMessage(sender, cmd, "----- " + desc.getName() + " infomation -----");

        if (nowVer.equals(latestVer)) {
            SendMessage(sender, cmd, ChatColor.AQUA + "現在導入されているバージョンは最新です。");
            SendMessage(sender, cmd, ChatColor.AQUA + "導入バージョン: " + nowVer);
        } else if (nowVerSha.equals(latestVerSha)) {
            // shaがおなじ
            SendMessage(sender, cmd, ChatColor.AQUA + "現在導入されているバージョンは最新です。");
            SendMessage(sender, cmd, ChatColor.AQUA + "導入バージョン: " + nowVer);
            SendMessage(sender, cmd, ChatColor.AQUA + "最新バージョン: " + latestVer + " (" + latestVerSha + ")");
        } else if (nowVerDate.before(latestVerDate)) {
            // 新しいバージョンあり
            SendMessage(sender, cmd, ChatColor.RED + "現在導入されているバージョンよりも新しいバージョンがリリースされています。");
            SendMessage(sender, cmd, ChatColor.AQUA + "導入バージョン: " + nowVer);
            SendMessage(sender, cmd, ChatColor.AQUA + "最新バージョン: " + latestVer + " (" + latestVerSha + ")");
        } else if (nowVerDate.after(latestVerDate)) {
            // リリースバージョンよりも導入されている方が新しい
            SendMessage(sender, cmd, ChatColor.AQUA + "現在導入されているバージョンは最新です。(※)");
            SendMessage(sender, cmd, ChatColor.AQUA + "導入バージョン: " + nowVer);
            SendMessage(sender, cmd, ChatColor.AQUA + "最新バージョン: " + latestVer + " (" + latestVerSha + ")");
        }

        SendMessage(sender, cmd, "最近の更新履歴");
        List<String> commits = getCommits(desc.getName());
        if (commits == null) {
            SendMessage(sender, cmd, "- コミット履歴の取得に失敗しました。");
            return true;
        }
        for (String commit : commits) {
            SendMessage(sender, cmd, "- " + commit);
        }
        return true;
    }

    private List<String> getCommits(String repo) {
        LinkedList<String> ret = new LinkedList<>();
        try {
            String url = "https://api.github.com/repos/jaoafa/" + repo + "/commits";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            JSONArray array;
            try (Response response = client.newCall(request).execute()) {
                if (response.code() != 200) {
                    return null;
                }
                ResponseBody body = response.body();
                if(body == null) throw new NullPointerException("Body is null.");
                array = new JSONArray(body.string());
            }

            for (int i = 0; i < array.length() && i < 5; i++) {
                JSONObject obj = array.getJSONObject(i).getJSONObject("commit");
                Date date = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT
                        .parse(obj.getJSONObject("committer").getString("date"));
                String sha = array.getJSONObject(i).getString("sha").substring(0, 7);
                ret.add("[" + sdfFormat(date) + "|" + sha + "] " + obj.getString("message"));
            }

            return ret;
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLastCommitSha(String repo) {
        try {
            String url = "https://api.github.com/repos/jaoafa/" + repo + "/commits";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            JSONArray array;
            try (Response response = client.newCall(request).execute()) {
                if (response.code() != 200) {
                    return null;
                }
                ResponseBody body = response.body();
                if(body == null) throw new NullPointerException("Body is null.");
                array = new JSONArray(body.string());
            }

            return array.getJSONObject(0).getString("sha").substring(0, 7);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date getVersionDate(String version) {
        String[] day_time = version.split("_");
        String[] days = day_time[0].split("\\.");
        String[] times = day_time[1].split("\\.");
        Calendar build_cal = Calendar.getInstance();
        build_cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        build_cal.set(Integer.parseInt(days[0]),
                Integer.parseInt(days[1]),
                Integer.parseInt(days[2]),
                Integer.parseInt(times[0]),
                Integer.parseInt(times[1]));
        return build_cal.getTime();
    }

    private String getVersionSha(String version) {
        String[] day_time = version.split("_");
        if (day_time.length == 3) {
            return day_time[2];
        }
        return null;
    }

    private String getVersion(String repo) {
        try {
            String url = "https://raw.githubusercontent.com/jaoafa/" + repo + "/master/src/main/resources/plugin.yml";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                return null;
            }
            ResponseBody body = response.body();
            if(body == null) throw new NullPointerException("Body is null.");
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(body.charStream());
            response.close();
            if (yaml.contains("version")) {
                return yaml.getString("version");
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDescription() {
        return "MyMaid3のリリース情報を表示します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "mymaid3",
                new CmdUsage.Cmd("", getDescription())
        );
    }
}
