package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cmd_Vercheck extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }
        String[] plugins = new String[]{
                "MyMaid3",
                "AntiAlts3",
                "PeriodMatch2",
                "Bakushinchi",
                "jao-Super-Achievement2"
        };

        SendMessage(sender, cmd, "----- Version Check -----");
        for (String plName : plugins) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(plName);
            if (plugin == null) {
                System.out.println("[VerCheck] " + plName + ": null");
                continue;
            }
            String nowVer = plugin.getDescription().getVersion();
            String nowVerSha = getVersionSha(nowVer);
            String latestVerSha = getLastCommitSha(plName);

            String status;
            if (nowVerSha.equalsIgnoreCase(latestVerSha)) {
                status = ChatColor.AQUA + "This plugin is up to date.";
            } else {
                status = ChatColor.RED + "This plugin is out of date.";
            }

            SendMessage(sender, cmd,
                    plName + ": " + nowVer + " - " + latestVerSha + " (" + status + ChatColor.GREEN + ")");
        }
        return true;
    }

    private String getLastCommitSha(String repo) {
        try {
            String url = "https://api.github.com/repos/jaoafa/" + repo + "/commits";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                return null;
            }
            JSONArray array = new JSONArray(response.body().string());
            response.close();

            return array.getJSONObject(0).getString("sha").substring(0, 7);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getVersionSha(String version) {
        String[] day_time = version.split("_");
        if (day_time.length == 3) {
            return day_time[2];
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "開発部が制作しているプラグインのバージョンをチェックします。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/vercheck");
            }
        };
    }
}
