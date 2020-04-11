package com.jaoafa.MyMaid3.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;

public class Event_MCBansLoginCheck extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnLoginCheck(AsyncPlayerPreLoginEvent event) {
		// MCBansが落ちている場合を考慮してjaoafaデータベースからチェック

		// Reputationチェック
		String name = event.getName();
		UUID uuid = event.getUniqueId();

		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return;
		}

		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM mcbans WHERE uuid = ?");
			statement.setString(1, uuid.toString());
			ResultSet res = statement.executeQuery();
			if (res.next()) {
				float reputation = res.getFloat("reputation");
				if (reputation < 3) {
					// 3未満は規制
					String message = ChatColor.RED + "----- MCBans Checker -----\n"
							+ ChatColor.RESET + ChatColor.WHITE + "Access denied.\n"
							+ ChatColor.RESET + ChatColor.WHITE + "Your reputation is below this server's threshold.";
					event.disallow(Result.KICK_BANNED, message);
					return;
				}
				if (reputation != 10) {
					sendAMR(ChatColor.RED + "[MCBansChecker] " + ChatColor.GREEN + name + " reputation: " + reputation);
				}
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		// jaoでBan済みかどうか
		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM mcbans_jaoafa WHERE uuid = ?");
			statement.setString(1, uuid.toString());
			ResultSet res = statement.executeQuery();
			if (res.next()) {
				int banid = res.getInt("banid");
				String type = res.getString("type");
				String reason = res.getString("reason");
				String message = ChatColor.RED + "----- MCBans Checker -----\n"
						+ ChatColor.RESET + ChatColor.WHITE + "Access denied.\n"
						+ ChatColor.RESET + ChatColor.WHITE + "Reason: " + reason + "\n"
						+ ChatColor.RESET + ChatColor.WHITE + "Ban type: " + type + "\n"
						+ ChatColor.RESET + ChatColor.WHITE + "http://mcbans.com/ban/" + banid;
				event.disallow(Result.KICK_BANNED, message);
				return;
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
}
