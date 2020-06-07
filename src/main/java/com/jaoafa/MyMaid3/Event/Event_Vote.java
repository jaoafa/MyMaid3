package com.jaoafa.MyMaid3.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.jaoafa.MinecraftJPVoteMissFiller.CustomEvent.VoteMissFillerEvent;
import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Lib.PlayerVoteData;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class Event_Vote extends MyMaidLibrary implements Listener {
	@EventHandler
	public void onVotifierEvent(VotifierEvent event) {
		Vote vote = event.getVote();
		String name = vote.getUsername();
		VoteReceive(name);
	}

	@EventHandler
	public void onVoteMissFillerEvent(VoteMissFillerEvent event) {
		String player = event.getStringPlayer();
		Main.getJDA().getTextChannelById(499922840871632896L)
				.sendMessage(":mailbox_with_mail: **投票自動補填通知**: " + player + "の投票が受信されていなかったため、自動補填を行います。").queue();
		VoteReceive(player);
	}

	void VoteReceive(String name) {
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			missedNotify(name, "MySQLDBManager == null");
			return;
		}
		// nameからuuidを取得する
		UUID uuid = getUUID(MySQLDBManager, name);
		if (uuid == null) {
			missedNotify(name, "UUID取得失敗");
			return;
		}

		OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);

		if (offplayer == null || offplayer.getName() == null) {
			missedNotify(name, "OfflinePlayer取得失敗");
			return;
		}

		if (!offplayer.getName().equals(name)) {
			name += "(" + offplayer.getName() + ")";
		}

		//boolean first = PlayerVoteData.TodayFirstVote();

		int oldVote = -1;
		int newVote = -1;
		try {
			PlayerVoteData pvd = new PlayerVoteData(offplayer);
			oldVote = pvd.get();

			pvd.add();

			newVote = pvd.get();
		} catch (ClassNotFoundException | SQLException | NullPointerException e) {
			missedNotify(name, e.getClass().getName() + " -> " + e.getMessage() + " (投票数追加失敗)");
			e.printStackTrace();
			return;
		}

		Bukkit.broadcastMessage(
				"[MyMaid] " + ChatColor.GREEN + "プレイヤー「" + name + "」が投票をしました！(現在の投票数:" + newVote + "回)");
		Bukkit.broadcastMessage("[MyMaid] " + ChatColor.GREEN + "投票をよろしくお願いします！ https://jaoafa.com/vote");
		Main.ServerChatChannel.sendMessage("プレイヤー「" + DiscordEscape(name) + "」が投票をしました！(現在の投票数:" + newVote + "回)")
				.queue();
		Main.ServerChatChannel.sendMessage("投票をよろしくお願いします！ https://jaoafa.com/vote").queue();

		successNotify(name, oldVote, newVote);
	}

	UUID getUUID(MySQLDBManager MySQLDBManager, String name) {
		UUID uuid = null;
		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM login WHERE player = ? ORDER BY id DESC");
			statement.setString(1, name);

			ResultSet res = statement.executeQuery();
			if (res.next()) {
				uuid = UUID.fromString(res.getString("uuid"));
			}
			return uuid;
		} catch (SQLException e) {
			missedNotify(name, e.getClass().getName() + " -> " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	void missedNotify(String name, String reason) {
		Main.getJDA().getTextChannelById(499922840871632896L)
				.sendMessage(":x: <@221991565567066112> `" + name + "`の投票特典付与処理に失敗しました: `" + reason + "`").queue();
	}

	void successNotify(String name, int oldVote, int newVote) {
		Main.getJDA().getTextChannelById(499922840871632896L)
				.sendMessage(":o: `" + name + "`の投票特典付与処理に成功しました: " + oldVote + "回 -> " + newVote + "回").queue();
	}
}
