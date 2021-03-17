/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.MyMaid3.Lib;

import org.bukkit.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EBanライブラリ
 *
 * @author tomachi
 */
public class EBan extends MyMaidLibrary {
    static final Map<UUID, EBan> data = new HashMap<>();

    final OfflinePlayer player;
    String name;
    UUID uuid;

    boolean banned = false;
    String banned_by = null;
    String lastreason = null;
    long banned_unixtime = -1L;

    long DBSyncTime = -1L;

    public EBan(OfflinePlayer offplayer) {
        if (data.containsKey(offplayer.getUniqueId())) {
            EBan eban = data.get(offplayer.getUniqueId());
            this.name = eban.getName();
            this.uuid = eban.getUUID();
            this.banned = eban.isBanned();
            this.lastreason = eban.getLastBanReason();
            this.banned_unixtime = eban.getBannedUnixTime();
            this.banned_by = eban.getBannedBy();
            this.DBSyncTime = eban.getDBSyncTime();
        }
        this.player = offplayer;
        DBSync();
    }

    public EBan(UUID uuid) {
        if (data.containsKey(uuid)) {
            EBan eban = data.get(uuid);
            this.name = eban.getName();
            this.uuid = eban.getUUID();
            this.banned = eban.isBanned();
            this.lastreason = eban.getLastBanReason();
            this.banned_unixtime = eban.getBannedUnixTime();
            this.banned_by = eban.getBannedBy();
            this.DBSyncTime = eban.getDBSyncTime();
        }
        this.player = Bukkit.getOfflinePlayer(uuid);
        DBSync();
    }

    /**
     * 現在処罰中のユーザーの一覧を返します。
     *
     * @return 現在処罰中のユーザーの一覧。取得に失敗した場合はnull
     */
    public static Set<EBan> getList() {
        Set<EBan> EBanList = new HashSet<>();
        if (MyMaidConfig.getMySQLDBManager() == null) {
            throw new IllegalStateException("Main.MySQLDBManager == null");
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM eban WHERE status = ?");
            statement.setString(1, "punishing");
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                EBan eban = new EBan(UUID.fromString(res.getString("uuid")));
                EBanList.add(eban);
            }
            res.close();
            statement.close();
            return EBanList;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * このユーザーを処罰します。
     */
    public boolean addBan(String banned_by, String reason) {
        DBSync();
        if (MyMaidConfig.getMySQLDBManager() == null) {
            throw new IllegalStateException("Main.MySQLDBManager == null");
        }

        if (isBanned()) {
            return false;
        }

        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO eban (player, uuid, banned_by, reason, status) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, banned_by);
            statement.setString(4, reason);
            statement.setString(5, "punishing");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        DBSync(true);

        Bukkit.broadcastMessage(
                "[EBan] " + ChatColor.RED + "プレイヤー:「" + player.getName() + "」が「" + reason + "」という理由でEBanされました。");
        MyMaidConfig.getJaotanChannel().sendMessage(
                "__**EBan[追加]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「" + DiscordEscape(banned_by)
                        + "」によって「" + reason + "」という理由でEBanされました。")
                .queue();
        MyMaidConfig.getServerChatChannel().sendMessage(
                "__**EBan[追加]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「" + DiscordEscape(banned_by)
                        + "」によって「" + reason + "」という理由でEBanされました。")
                .queue();

        if (player.isOnline()) {
            if (player.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                player.getPlayer().setGameMode(GameMode.CREATIVE);
            }

            World Jao_Afa = Bukkit.getServer().getWorld("Jao_Afa");
            Location minami = new Location(Jao_Afa, 2856, 69, 2888);
            player.getPlayer().teleport(minami);

            player.getPlayer().sendMessage("[EBan] " + ChatColor.RED + "解除申請の方法や、Banの方針などは以下ページをご覧ください。");
            player.getPlayer().sendMessage("[EBan] " + ChatColor.RED + "https://jaoafa.com/rule/management/punishment");
        }

        return true;
    }

    /**
     * このユーザーの処罰を解除します。
     */
    public boolean removeBan(String removePlayerName) {
        DBSync();
        if (MyMaidConfig.getMySQLDBManager() == null) {
            throw new IllegalStateException("Main.MySQLDBManager == null");
        }
        if (!isBanned()) {
            return false;
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("UPDATE eban SET status = ? WHERE uuid = ?;");
            statement.setString(1, "end");
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        DBSync(true);

        if (player.isOnline()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            player.getPlayer()
                    .sendMessage(ChatColor.GRAY + "[" + sdf.format(new Date()) + "]" + ChatColor.GOLD + "■jaotan"
                            + ChatColor.WHITE + ": " + "じゃあな…！");
        }
        Bukkit.broadcastMessage("[EBan] " + ChatColor.RED + "プレイヤー:「" + player.getName() + "」のEBanを解除しました。");
        MyMaidConfig.getJaotanChannel().sendMessage(
                "__**EBan[解除]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」のEBanを「"
                        + DiscordEscape(removePlayerName) + "」によって解除されました。")
                .queue();
        MyMaidConfig.getServerChatChannel()
                .sendMessage(
                        "__**EBan[解除]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」のEBanを「"
                                + DiscordEscape(removePlayerName) + "」によって解除されました。")
                .queue();
        return true;
    }

    /**
     * このユーザーが処罰されているかどうかを調べます。
     *
     * @return このユーザーがEBanされているかどうか
     */
    public boolean isBanned() {
        DBSync();
        return banned;
    }

    /**
     * このユーザーの処罰時刻をDateで返します。
     *
     * @return Dateの処罰時刻
     */
    public Date getBannedDate() {
        DBSync();
        return new Date(banned_unixtime * 1000);
    }

    /**
     * このユーザーの処罰時刻をUnixTimeで返します。
     *
     * @return UnixTimeの処罰時刻
     */
    public long getBannedUnixTime() {
        DBSync();
        return banned_unixtime;
    }

    /**
     * 最後の処罰理由を取得します。
     *
     * @return 最後の処罰理由
     */
    public String getLastBanReason() {
        DBSync();
        return lastreason;
    }

    /**
     * 処罰を行ったユーザーを返します。
     *
     * @return 処罰を行ったユーザー
     */
    public String getBannedBy() {
        DBSync();
        return banned_by;
    }

    /**
     * このユーザーのデータをデータベースから取得します。<br>
     * 取得前に前回の取得から30分を経過しているかを調べ、経過していなければなにもしません。
     */
    public void DBSync() {
        DBSync(false);
    }

    /**
     * このユーザーのデータをデータベースから取得します。<br>
     * forceがfalseの場合、取得前に前回の取得から30分を経過しているかを調べ、経過していなければなにもしません。
     *
     * @param force 30分経過をチェックせずに強制的に取得するか
     */
    public void DBSync(boolean force) {
        if (!force && ((DBSyncTime + 30 * 60 * 1000) > System.currentTimeMillis())) {
            return; // 30分未経過
        }
        if (MyMaidConfig.getMySQLDBManager() == null) {
            throw new IllegalStateException("Main.MySQLDBManager == null");
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM eban WHERE uuid = ? ORDER BY id DESC LIMIT 1");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                this.name = res.getString("player");
                this.uuid = UUID.fromString(res.getString("uuid"));
                this.banned = res.getString("status").equalsIgnoreCase("punishing");
                this.lastreason = res.getString("reason");
                this.banned_unixtime = res.getTimestamp("created_at").getTime() / 1000;
                this.banned_by = res.getString("banned_by");
            } else {
                this.name = player.getName();
                this.uuid = player.getUniqueId();
                this.banned = false;
                this.lastreason = null;
                this.banned_unixtime = -1L;
                this.banned_by = null;
            }
            res.close();
            statement.close();

            DBSyncTime = System.currentTimeMillis();
        } catch (SQLException e) {
            e.printStackTrace();

            this.name = player.getName();
            this.uuid = player.getUniqueId();
            this.banned = false;
            this.lastreason = null;
            this.banned_unixtime = -1L;
            this.banned_by = null;
        }
        data.put(player.getUniqueId(), this);
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getDBSyncTime() {
        return DBSyncTime;
    }
}
