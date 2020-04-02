package com.jaoafa.MyMaid3.Event;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Event_AMVersionCheck extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!isAM(player)) {
			return;
		}

		Plugin plugin = Bukkit.getPluginManager().getPlugin("MyMaid3");
		if (plugin == null) {
			return;
		}
		String nowVer = plugin.getDescription().getVersion();
		String latestVer = getVersion("MyMaid3");
		if (nowVer.equalsIgnoreCase(latestVer)) {
			return;
		}
		player.sendMessage("[MyMaid3] " + ChatColor.RED + "MyMaid3のバージョンが最新ではありません。直近の更新内容が反映されていない可能性があります。");
		player.sendMessage("[MyMaid3] " + ChatColor.RED + "現在のバージョン: " + nowVer + " / 最新のバージョン: " + latestVer);
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
}
