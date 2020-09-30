package com.jaoafa.MyMaid3.Lib;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TpDeny {
    Player player;

    public TpDeny(Player player) {
        this.player = player;
    }

    public boolean isTpDeny(OfflinePlayer target) {
        if (MyMaidConfig.getMySQLDBManager() == null) {
            return false;
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tpdeny WHERE uuid = ? AND deny_uuid = ? AND disabled = ?");
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, target.getUniqueId().toString());
            stmt.setBoolean(3, false);
            ResultSet res = stmt.executeQuery();
            boolean bool = res.next();
            res.close();
            stmt.close();
            return bool; // 存在するならdeny
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addDeny(OfflinePlayer target) {
        if (MyMaidConfig.getMySQLDBManager() == null) {
            return false;
        }
        if (isTpDeny(target)) {
            return false; // already denied
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO tpdeny (player, uuid, deny_player, deny_uuid, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)");
            stmt.setString(1, player.getName());
            stmt.setString(2, player.getUniqueId().toString());
            stmt.setString(3, target.getName());
            stmt.setString(4, target.getUniqueId().toString());
            int count = stmt.executeUpdate();
            stmt.close();
            return count != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean disableDeny(int id) {
        if (MyMaidConfig.getMySQLDBManager() == null) {
            return false;
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE tpdeny SET disabled = ? WHERE rowid = ?");
            stmt.setBoolean(1, true);
            stmt.setInt(2, id);
            int count = stmt.executeUpdate();
            stmt.close();
            return count != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TpDenyData> getDenys() {
        List<TpDenyData> rets = new ArrayList<>();
        if (MyMaidConfig.getMySQLDBManager() == null) {
            return rets;
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tpdeny WHERE uuid = ? AND disabled = ?");
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setBoolean(2, false);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                rets.add(new TpDenyData(
                        res.getInt("rowid"),
                        Bukkit.getOfflinePlayer(UUID.fromString(res.getString("uuid"))),
                        Bukkit.getOfflinePlayer(UUID.fromString(res.getString("deny_uuid"))),
                        res.getBoolean("disabled"),
                        res.getTimestamp("created_at"),
                        res.getTimestamp("updated_at")
                ));
            }
            return rets;
        } catch (SQLException e) {
            e.printStackTrace();
            return rets;
        }
    }

    public static class TpDenyData {
        public final int id;
        public final OfflinePlayer player;
        public final OfflinePlayer target;
        public final boolean disabled;
        public final Date created_at;
        public final Date updated_at;

        TpDenyData(int id, OfflinePlayer player, OfflinePlayer target, boolean disabled, Date created_at, Date updated_at) {
            this.id = id;
            this.player = player;
            this.target = target;
            this.disabled = disabled;
            this.created_at = created_at;
            this.updated_at = updated_at;
        }
    }
}
