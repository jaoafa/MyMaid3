package com.jaoafa.MyMaid3.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Cmd_Vercheck extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		String[] plugins = new String[] {
				"MyMaid3",
				"AntiAlts3",
				"PeriodMatch2",
				"Bakushinchi"
		};

		SendMessage(sender, cmd, "----- Version Check -----");
		for (String plName : plugins) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin(plName);
			if (plugin == null) {
				System.out.println("[VerCheck] " + plName + ": null");
				continue;
			}
			String nowVer = plugin.getDescription().getVersion();
			String latestVer = getVersion(plName);

			String status;
			if (nowVer.equalsIgnoreCase(latestVer)) {
				status = "This plugin is up to date.";
			} else {
				status = ChatColor.RED + "This plugin is out of date.";
			}

			SendMessage(sender, cmd,
					plName + ": " + nowVer + " - " + latestVer + " (" + status + ChatColor.GREEN + ")");
		}
		return true;
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
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(response.body().charStream());
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
