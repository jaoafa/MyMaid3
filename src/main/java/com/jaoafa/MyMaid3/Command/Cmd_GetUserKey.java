package com.jaoafa.MyMaid3.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;

public class Cmd_GetUserKey extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (!(sender instanceof Player)) {
			SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
		if (MySQLDBManager == null) {
			SendMessage(player, cmd, "データベースサーバに接続できません。時間をおいて再度お試しください。");
			return true;
		}
		try {
			Connection conn = MySQLDBManager.getConnection();
			String userkey = getUserKeyExistCheck(conn, uuid);
			if (userkey == null) {
				userkey = getUserKey(conn);

				PreparedStatement statement = conn.prepareStatement(
						"INSERT INTO userkey (player, uuid, userkey) VALUES (?, ?, ?);");
				statement.setString(1, player.getName());
				statement.setString(2, uuid.toString());
				statement.setString(3, userkey);
				statement.executeUpdate();
				statement.close();
			}

			if (userkey == null) {
				SendMessage(player, cmd, "UserKeyを生成できませんでした。時間をおいて再度お試しください。");
				return true;
			}

			SendMessage(player, cmd, "あなたのUserKeyは「" + userkey + "」です。");
			return true;
		} catch (SQLException e) {
			SendMessage(player, cmd, "データベースサーバに接続できません。時間をおいて再度お試しください。");
			e.printStackTrace();
			return true;
		}
	}

	String getUserKey(Connection conn) throws SQLException {
		String userkey = null;
		while (true) {
			userkey = RandomStringUtils.randomAlphabetic(10);
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM userkey WHERE userkey = ?");
			statement.setString(1, userkey);
			ResultSet res = statement.executeQuery();
			if (!res.next()) {
				statement.close();
				return userkey;
			}
		}
	}

	String getUserKeyExistCheck(Connection conn, UUID uuid) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM userkey WHERE uuid = ? AND used = ?");
		statement.setString(1, uuid.toString());
		statement.setBoolean(2, false);
		ResultSet res = statement.executeQuery();
		if (res.next()) {
			String userkey = res.getString("userkey");
			statement.close();
			return userkey;
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "ユーザーを認証するためのキーを取得します。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/getuserkey");
			}
		};
	}
}
