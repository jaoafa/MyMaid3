package com.jaoafa.MyMaid3.Lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.jaoafa.MyMaid3.Main;

public class Historyjao extends MyMaidLibrary {
	static Map<UUID, Historyjao> data = new HashMap<>();

	OfflinePlayer player;
	String name;
	UUID uuid;
	boolean found = false;
	List<HistoryData> histdatas = new ArrayList<>();

	long DBSyncTime = -1L;

	public Historyjao(OfflinePlayer offplayer) {
		if (data.containsKey(offplayer.getUniqueId())) {
			Historyjao hist = data.get(offplayer.getUniqueId());
			this.name = hist.getName();
			this.uuid = hist.getUUID();
			this.histdatas = hist.getHistoryDatas();
			this.DBSyncTime = hist.getDBSyncTime();
		}
		this.player = offplayer;
		DBSync();
	}

	public boolean add(String message) {
		if (Main.MySQLDBManager == null) {
			throw new IllegalStateException("Main.MySQLDBManager == null");
		}
		try {
			Connection conn = Main.MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO jaoHistory (player, uuid, message, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP);");
			statement.setString(1, player.getName());
			statement.setString(2, player.getUniqueId().toString());
			statement.setString(3, message);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		DBSync(true);
		return true;
	}

	public boolean disable(int id) {
		if (Main.MySQLDBManager == null) {
			throw new IllegalStateException("Main.MySQLDBManager == null");
		}
		try {
			Connection conn = Main.MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement(
					"UPDATE jail SET disabled = ? WHERE uuid = ? AND id = ? ORDER BY id DESC");
			statement.setBoolean(1, true);
			statement.setString(2, player.getUniqueId().toString());
			statement.setInt(3, id);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		DBSync(true);

		return true;
	}

	public void DBSync() {
		DBSync(false);
	}

	private void DBSync(boolean force) {
		if (!force && ((DBSyncTime + 30 * 60 * 1000) > System.currentTimeMillis())) {
			return; // 30分未経過
		}
		if (Main.MySQLDBManager == null) {
			throw new IllegalStateException("Main.MySQLDBManager == null");
		}
		try {
			Connection conn = Main.MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM jaoHistory WHERE uuid = ?");
			statement.setString(1, player.getUniqueId().toString());
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				this.name = res.getString("player");
				this.uuid = UUID.fromString(res.getString("uuid"));

				HistoryData histdata = new HistoryData();
				histdata.id = res.getInt("id");
				histdata.player = res.getString("player");
				histdata.message = res.getString("message");
				histdata.disabled = res.getBoolean("disabled");
				histdata.created_at = res.getTimestamp("created_at").getTime() / 1000;
				histdata.updated_at = res.getTimestamp("updated_at").getTime() / 1000;
				this.histdatas.add(histdata);

				this.found = true;
			}
			res.close();
			statement.close();

			DBSyncTime = System.currentTimeMillis();
		} catch (SQLException e) {
			e.printStackTrace();

			this.name = player.getName();
			this.uuid = player.getUniqueId();
			this.found = false;
		}
		data.put(player.getUniqueId(), this);
	}

	public String getName() {
		return name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public List<HistoryData> getHistoryDatas() {
		return histdatas;
	}

	public long getDBSyncTime() {
		return DBSyncTime;
	}

	public boolean isFound() {
		return found;
	}

	public class HistoryData {
		public int id;
		public String player;
		public String message;
		public boolean disabled;
		public long created_at;
		public long updated_at;

		public Date getCreatedAt() {
			return new Date(created_at * 1000);
		}

		public Date getUpdatedAt() {
			return new Date(updated_at * 1000);
		}
	}
}
