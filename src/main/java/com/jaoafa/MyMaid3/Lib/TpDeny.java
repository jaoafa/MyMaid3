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

    /**
     * targetがコマンド実行者であった場合、テレポートを拒否するか
     *
     * @param target コマンド実行者
     * @return テレポートを拒否するならばtrue
     */
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

    /**
     * 以降のテレポートを拒否する
     *
     * @param target テレポートを拒否するプレイヤー
     * @return 成功したか
     */
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

    /**
     * テレポート拒否設定を無効化(オフに)する
     *
     * @param id TpDenyId
     * @return 成功したか
     */
    public boolean disableDeny(int id) {
        if (MyMaidConfig.getMySQLDBManager() == null) {
            return false;
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE tpdeny SET disabled = ? WHERE rowid = ? AND uuid = ?");
            stmt.setBoolean(1, true);
            stmt.setInt(2, id);
            stmt.setString(3, player.getUniqueId().toString());
            int count = stmt.executeUpdate();
            stmt.close();
            return count != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 現在設定されているテレポート拒否設定の一覧を表示する。
     *
     * @return テレポートを拒否するList
     */
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

    /**
     * テレポートを拒否した場合、通知するか
     *
     * @param target 対象プレイヤー
     * @return 通知するか否か
     */
    public boolean isNotify(OfflinePlayer target) {
        if (MyMaidConfig.getMySQLDBManager() == null) {
            return true;
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tpdeny WHERE uuid = ? AND deny_uuid = ? AND disabled = ?");
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, target.getUniqueId().toString());
            stmt.setBoolean(3, false);
            ResultSet res = stmt.executeQuery();
            if (!res.next()) {
                return true;
            }
            boolean bool = res.getBoolean("notify");
            res.close();
            stmt.close();
            return bool; // 存在するならdeny
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * テレポートを拒否した場合、通知するかを設定する
     *
     * @param id   TpDenyId
     * @param bool 通知するかどうか
     * @return 成功したか
     */
    public boolean setNotify(int id, boolean bool) {
        if (MyMaidConfig.getMySQLDBManager() == null) {
            return false;
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE tpdeny SET notify = ? WHERE rowid = ? AND uuid = ?");
            stmt.setBoolean(1, bool);
            stmt.setInt(2, id);
            stmt.setString(3, player.getUniqueId().toString());
            int count = stmt.executeUpdate();
            stmt.close();
            return count != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
