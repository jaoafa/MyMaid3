package com.jaoafa.MyMaid3.Lib;

import org.bukkit.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ChatJailライブラリ
 *
 * @author tomachi
 */
public class ChatBan extends MyMaidLibrary {
    static final Map<UUID, ChatBan> data = new HashMap<>();

    final OfflinePlayer player;
    String name;
    UUID uuid;

    boolean banned = false;
    String banned_by = null;
    String lastreason = null;
    long banned_unixtime = -1L;

    long DBSyncTime = -1L;

    public ChatBan(OfflinePlayer offplayer) {
        if (data.containsKey(offplayer.getUniqueId())) {
            ChatBan chatban = data.get(offplayer.getUniqueId());
            this.name = chatban.getName();
            this.uuid = chatban.getUUID();
            this.banned = chatban.isBanned();
            this.lastreason = chatban.getLastBanReason();
            this.banned_unixtime = chatban.getBannedUnixTime();
            this.banned_by = chatban.getBannedBy();
            this.DBSyncTime = chatban.getDBSyncTime();
        }
        this.player = offplayer;
        DBSync();
    }

    public ChatBan(UUID uuid) {
        if (data.containsKey(uuid)) {
            ChatBan chatban = data.get(uuid);
            this.name = chatban.getName();
            this.uuid = chatban.getUUID();
            this.banned = chatban.isBanned();
            this.lastreason = chatban.getLastBanReason();
            this.banned_unixtime = chatban.getBannedUnixTime();
            this.banned_by = chatban.getBannedBy();
            this.DBSyncTime = chatban.getDBSyncTime();
        }
        this.player = Bukkit.getOfflinePlayer(uuid);
        DBSync();
    }

    /**
     * 現在処罰中のユーザーの一覧を返します。
     *
     * @return 現在処罰中のユーザーの一覧。取得に失敗した場合はnull
     */
    public static Set<ChatBan> getList() {
        Set<ChatBan> chatBanList = new HashSet<>();
        if (MyMaidConfig.getMySQLDBManager() == null) {
            throw new IllegalStateException("Main.MySQLDBManager == null");
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM chatjail WHERE status = ?");
            statement.setBoolean(1, true);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                ChatBan chatban = new ChatBan(UUID.fromString(res.getString("uuid")));
                chatBanList.add(chatban);
            }
            res.close();
            statement.close();
            return chatBanList;
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
                    "INSERT INTO chatjail (player, uuid, banned_by, reason) VALUES (?, ?, ?, ?);");
            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, banned_by);
            statement.setString(4, reason);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        DBSync(true);

        Bukkit.broadcastMessage(
                "[ChatBan] " + ChatColor.RED + "プレイヤー:「" + player.getName() + "」が「" + reason + "」という理由でChatJailされました。");
        MyMaidConfig.getJaotanChannel().sendMessage(
                "__**ChatBan[追加]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「" + DiscordEscape(banned_by)
                        + "」によって「" + reason + "」という理由でChatJailされました。")
                .queue();
        MyMaidConfig.getServerChatChannel().sendMessage(
                "__**ChatBan[追加]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「" + DiscordEscape(banned_by)
                        + "」によって「" + reason + "」という理由でChatJailされました。")
                .queue();

        if (player.isOnline()) {
            if (player.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                player.getPlayer().setGameMode(GameMode.CREATIVE);
            }

            World Jao_Afa = Bukkit.getServer().getWorld("Jao_Afa");
            Location minami = new Location(Jao_Afa, 2856, 69, 2888);
            player.getPlayer().teleport(minami);

            player.getPlayer().sendMessage("[ChatBan] " + ChatColor.RED + "解除申請の方法や、Banの方針などは以下ページをご覧ください。");
            player.getPlayer().sendMessage("[ChatBan] " + ChatColor.RED + "https://jaoafa.com/rule/management/punishment");
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
                    .prepareStatement("UPDATE chatjail SET status = ? WHERE uuid = ?;");
            statement.setBoolean(1, false);
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
        Bukkit.broadcastMessage("[ChatBan] " + ChatColor.RED + "プレイヤー:「" + player.getName() + "」のChatJailを解除しました。");
        MyMaidConfig.getJaotanChannel().sendMessage(
                "__**ChatBan[解除]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」のChatJailを「"
                        + DiscordEscape(removePlayerName) + "」によって解除されました。")
                .queue();
        MyMaidConfig.getServerChatChannel()
                .sendMessage(
                        "__**ChatBan[解除]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」のChatJailを「"
                                + DiscordEscape(removePlayerName) + "」によって解除されました。")
                .queue();
        return true;
    }

    public void addMessageDB(String message) {
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("INSERT INTO chatjailmsg (player, uuid, message) VALUES (?, ?, ?);");
            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, message);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * このユーザーが処罰されているかどうかを調べます。
     *
     * @return このユーザーがChatJailされているかどうか
     */
    public boolean isBanned() {
        DBSync();
        return banned;
    }

    /**
     * このユーザーの処罰時刻をDateで返します。
     *
     * @return 処罰時刻(Date)
     */
    public Date getBannedDate() {
        DBSync();
        return new Date(banned_unixtime * 1000);
    }

    /**
     * このユーザーの処罰時刻をUnixTimeで返します。
     *
     * @return 処罰時刻(UnixTime)
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
                    .prepareStatement("SELECT * FROM chatjail WHERE uuid = ? ORDER BY id DESC LIMIT 1");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                this.name = res.getString("player");
                this.uuid = UUID.fromString(res.getString("uuid"));
                this.banned_by = res.getString("banned_by");
                this.lastreason = res.getString("reason");
                this.banned = res.getBoolean("status");
                this.banned_unixtime = res.getTimestamp("created_at").getTime() / 1000;
            } else {
                this.name = player.getName();
                this.uuid = player.getUniqueId();
                this.banned_by = null;
                this.lastreason = null;
                this.banned = false;
                this.banned_unixtime = -1L;
            }
            res.close();
            statement.close();

            DBSyncTime = System.currentTimeMillis();
        } catch (SQLException e) {
            e.printStackTrace();

            this.name = player.getName();
            this.uuid = player.getUniqueId();
            this.banned_by = null;
            this.banned = false;
            this.lastreason = null;
            this.banned_unixtime = -1L;
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
