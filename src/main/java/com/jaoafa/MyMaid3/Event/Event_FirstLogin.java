package com.jaoafa.MyMaid3.Event;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Event_FirstLogin extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnEvent_FirstLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPlayedBefore()) {
			return; // 初めてではない
		}

		String reputation = "null";
		if (Main.MCBansRepAPI != null) {
			reputation = getReputation(player.getUniqueId());
			if (reputation == null)
				reputation = "null";
		}

		List<String> players = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(p.getName());
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("NEW PLAYER JOIN");
		builder.appendDescription("新規プレイヤー(`" + player.getName() + "`)がサーバにログインしました！");
		builder.setColor(Color.GREEN);
		builder.addField("プレイヤーID", "`" + player.getName() + "`", false);
		builder.addField("評価値", reputation + " / 10", false);
		builder.addField("プレイヤー数", Bukkit.getOnlinePlayers().size() + "人", false);
		builder.addField("プレイヤー", "`" + String.join(", ", players) + "`", false);
		builder.setTimestamp(Instant.now());
		builder.setThumbnail(
				"https://crafatar.com/renders/body/" + player.getUniqueId().toString() + ".png?overlay=true&scale=10");
		builder.setAuthor(Main.getJDA().getSelfUser().getName(), null,
				Main.getJDA().getSelfUser().getAvatarUrl());
		Main.getJDA().getTextChannelById(597423444501463040L).sendMessage(builder.build()).queue();

		sendMCBansData(player);
	}

	private String getReputation(UUID uuid) {
		try {
			String url = Main.MCBansRepAPI + "?u=" + uuid.toString();
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).get().build();
			Response response = client.newCall(request).execute();
			if (response.code() != 200) {
				return null;
			}
			JSONObject json = new JSONObject(response.body().string());
			response.close();
			if (json.getBoolean("status")) {
				return json.getString("reputation");
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void sendMCBansData(Player player) {
		try {
			String url = Main.MCBansRepAPI + "?u=" + player.getUniqueId().toString() + "&data";
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).get().build();
			Response response = client.newCall(request).execute();
			if (response.code() != 200) {
				return;
			}
			JSONObject json = new JSONObject(response.body().string());
			response.close();

			if (!json.has("status")) {
				return;
			}

			if (!json.getBoolean("status")) {
				return;
			}
			if (!json.has("datacount")) {
				return;
			}
			String count = json.getString("datacount");
			String data = json.getString("data");

			Main.getJDA().getTextChannelById(597423444501463040L)
					.sendMessage("**-----: MCBans Ban DATA / `" + player.getName() + "` :-----**\n"
							+ "Ban: " + count + "\n"
							+ "```" + data + "```")
					.queue();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
