package com.jaoafa.MyMaid3.Event;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONException;

import com.jaoafa.MyMaid3.Main;
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

		new BukkitRunnable() {
			public void run() {
				Plugin plugin = Bukkit.getPluginManager().getPlugin("MyMaid3");
				if (plugin == null) {
					return;
				}
				String nowVer = plugin.getDescription().getVersion();
				String nowVerSha = getVersionSha(nowVer);
				String latestVerSha = getLastCommitSha("MyMaid3");
				if (nowVerSha.equalsIgnoreCase(latestVerSha)) {
					return;
				}
				player.sendMessage("[MyMaid3] " + ChatColor.RED + "MyMaid3のバージョンが最新ではありません。直近の更新内容が反映されていない可能性があります。");
				player.sendMessage("[MyMaid3] " + ChatColor.RED + "現在のバージョン: " + nowVerSha + " (" + nowVer
						+ ") / 最新のバージョン: " + nowVerSha);
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
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
}
