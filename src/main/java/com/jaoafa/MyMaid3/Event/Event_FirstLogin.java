package com.jaoafa.MyMaid3.Event;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MCBans;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import net.dv8tion.jda.api.EmbedBuilder;

public class Event_FirstLogin extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnEvent_FirstLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPlayedBefore()) {
			return; // 初めてではない
		}

		String reputation = "null";
		MCBans mcbans = null;
		try {
			mcbans = new MCBans(player);
			reputation = String.valueOf(mcbans.getReputation());
		} catch (IOException e) {
			reputation = "null";
		}

		List<String> players = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(p.getName());
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("NEW PLAYER JOIN", "https://jaoafa.com/user/" + player.getUniqueId().toString());
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

		if (mcbans.getGlobalCount() > 0 || mcbans.getLocalCount() > 0) {
			int[] global_ids = mcbans.getGlobalBanIds();
			int[] local_ids = mcbans.getLocalBanIds();

			EmbedBuilder embed = new EmbedBuilder();
			builder.setTitle("MCBans DATA : " + player.getName(),
					"https://www.mcbans.com/player/" + player.getUniqueId().toString().replace("-", "") + "/");
			builder.setColor(Color.RED);
			builder.setDescription("Global: " + mcbans.getGlobalCount() + " / Local: " + mcbans.getLocalCount());
			builder.setTimestamp(Instant.now());

			for (int id : global_ids) {
				try {
					MCBans.Ban ban = new MCBans.Ban(id);
					String reason = ban.getReason();
					String date = ban.getDate();
					String banned_by = ban.getBannedBy();
					String server = ban.getServer();

					embed.addField("[Global] `" + server + "`",
							"Reason: " + reason + "\nBanned_by: `" + banned_by + "`\n" + date, false);
				} catch (IOException e) {
					embed.addField("BanID: " + id, "Failed to get the data", false);
				}
			}
			for (int id : local_ids) {
				try {
					MCBans.Ban ban = new MCBans.Ban(id);
					String reason = ban.getReason();
					String date = ban.getDate();
					String banned_by = ban.getBannedBy();
					String server = ban.getServer();

					embed.addField("[Local] `" + server + "`",
							"Reason: " + reason + "\nBanned_by: `" + banned_by + "`\n" + date, false);
				} catch (IOException e) {
					embed.addField("BanID: " + id, "Failed to get the data", false);
				}
			}

			Main.getJDA().getTextChannelById(597423444501463040L).sendMessage(embed.build()).queue();
		}
	}
}
