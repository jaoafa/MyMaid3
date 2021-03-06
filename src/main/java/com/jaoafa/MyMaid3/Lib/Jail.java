package com.jaoafa.MyMaid3.Lib;

import com.jaoafa.jaoSuperAchievement2.API.AchievementAPI;
import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Jailライブラリ
 *
 * @author tomachi
 */
public class Jail extends MyMaidLibrary {
    static Map<UUID, Jail> data = new HashMap<>();

    OfflinePlayer player;
    String name;
    UUID uuid;

    boolean banned = false;
    String banned_by = null;
    String lastreason = null;
    String lasttestment = null;
    long banned_unixtime = -1L;

    long DBSyncTime = -1L;

    public Jail(OfflinePlayer offplayer) {
        if (data.containsKey(offplayer.getUniqueId())) {
            Jail jail = data.get(offplayer.getUniqueId());
            this.name = jail.getName();
            this.uuid = jail.getUUID();
            this.banned = jail.isBanned();
            this.lastreason = jail.getLastBanReason();
            this.lasttestment = jail.getLastBanTestment();
            this.banned_unixtime = jail.getBannedUnixTime();
            this.banned_by = jail.getBannedBy();
            this.DBSyncTime = jail.getDBSyncTime();
        }
        this.player = offplayer;
        DBSync();
    }

    public Jail(UUID uuid) {
        if (data.containsKey(uuid)) {
            Jail jail = data.get(uuid);
            this.name = jail.getName();
            this.uuid = jail.getUUID();
            this.banned = jail.isBanned();
            this.lastreason = jail.getLastBanReason();
            this.lasttestment = jail.getLastBanTestment();
            this.banned_unixtime = jail.getBannedUnixTime();
            this.banned_by = jail.getBannedBy();
            this.DBSyncTime = jail.getDBSyncTime();
        }
        this.player = Bukkit.getOfflinePlayer(uuid);
        DBSync();
    }

    /**
     * 現在処罰中のユーザーの一覧を返します。
     *
     * @return 現在処罰中のユーザーの一覧。取得に失敗した場合はnull
     */
    public static Set<Jail> getList() {
        Set<Jail> JailList = new HashSet<>();
        if (MyMaidConfig.getMySQLDBManager() == null) {
            throw new IllegalStateException("Main.MySQLDBManager == null");
        }
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM jail WHERE status = ?");
            statement.setBoolean(1, true);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                Jail jail = new Jail(UUID.fromString(res.getString("uuid")));
                JailList.add(jail);
            }
            res.close();
            statement.close();
            return JailList;
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
                    "INSERT INTO jail (player, uuid, banned_by, reason) VALUES (?, ?, ?, ?);");
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
                "[Jail] " + ChatColor.GREEN + "プレイヤー:「" + player.getName() + "」が「" + reason + "」という理由でJailされました。");
        TextChannel sendTo = getDiscordSendTo();
        sendTo.sendMessage(
                "__**Jail[追加]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「" + DiscordEscape(banned_by)
                        + "」によって「" + DiscordEscape(reason) + "」という理由でJailされました。")
                .queue();
        MyMaidConfig.getServerChatChannel().sendMessage(
                "__**Jail[追加]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「" + DiscordEscape(banned_by)
                        + "」によって「" + DiscordEscape(reason) + "」という理由でJailされました。")
                .queue();

        if (player.isOnline()) {
            if (player.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                player.getPlayer().setGameMode(GameMode.CREATIVE);
            }

            World Jao_Afa = Bukkit.getServer().getWorld("Jao_Afa");
            Location minami = new Location(Jao_Afa, 2856, 69, 2888);
            player.getPlayer().teleport(minami);

            if (!Achievementjao.getAchievement(player.getPlayer(), new AchievementType(22))) {
                player.getPlayer().sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
            }
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
        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("UPDATE jail SET status = ? WHERE uuid = ?");
            statement.setBoolean(1, false);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        DBSync(true);

        Bukkit.broadcastMessage("[Jail] " + ChatColor.GREEN + "プレイヤー:「" + player.getName() + "」のJailを解除しました。");
        TextChannel sendTo = getDiscordSendTo();
        sendTo.sendMessage(
                "__**Jail[解除]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」のJailを「"
                        + DiscordEscape(removePlayerName) + "」によって解除されました。")
                .queue();
        MyMaidConfig.getServerChatChannel().sendMessage(
                "__**Jail[解除]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」のJailを「"
                        + DiscordEscape(removePlayerName) + "」によって解除されました。")
                .queue();
        return true;
    }

    /**
     * このユーザーが処罰されているかどうかを調べます。
     *
     * @return このユーザーがJailされているかどうか
     */
    public boolean isBanned() {
        DBSync();
        return banned;
    }

    public boolean setTestment(String testment) {
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
                    .prepareStatement("UPDATE jail SET testment = ? WHERE uuid = ? ORDER BY id DESC LIMIT 1;");
            statement.setString(1, testment);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        DBSync(true);

        Bukkit.broadcastMessage(
                "[JAIL] " + ChatColor.GREEN + "プレイヤー「" + player.getName() + "」が遺言を残しました。遺言:「" + testment + "」");
        TextChannel sendTo = getDiscordSendTo();
        sendTo.sendMessage(
                "__**Jail[遺言]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「" + DiscordEscape(testment)
                        + "」という遺言を残しました。")
                .queue();
        MyMaidConfig.getServerChatChannel()
                .sendMessage("__**Jail[遺言]**__: プレイヤー「" + DiscordEscape(player.getName()) + "」が「"
                        + DiscordEscape(testment) + "」という遺言を残しました。")
                .queue();

        if (lastreason.equals("jaoium所持")) {
            removeBan("jaotan");
        }
        return true;
    }

    /**
     * このユーザーの処罰時刻をDateで返します。
     *
     * @return
     */
    public Date getBannedDate() {
        DBSync();
        return new Date(banned_unixtime * 1000);
    }

    /**
     * このユーザーの処罰時刻をUnixTimeで返します。
     *
     * @return
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
     * 最後の処罰遺言を取得します。
     *
     * @return
     */
    public String getLastBanTestment() {
        DBSync();
        return lasttestment;
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
                    .prepareStatement("SELECT * FROM jail WHERE uuid = ? ORDER BY id DESC LIMIT 1");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                this.name = res.getString("player");
                this.uuid = UUID.fromString(res.getString("uuid"));
                this.banned = res.getBoolean("status");
                this.lastreason = res.getString("reason");
                this.lasttestment = res.getString("testment");
                this.banned_unixtime = res.getTimestamp("created_at").getTime() / 1000;
                this.banned_by = res.getString("banned_by");
            } else {
                this.name = player.getName();
                this.uuid = player.getUniqueId();
                this.banned = false;
                this.lastreason = null;
                this.lasttestment = null;
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
            this.lasttestment = null;
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

    public TextChannel getDiscordSendTo() {
        try {
            String group;
            if (player.isOnline()) {
                group = PermissionsManager.getPermissionMainGroup(player.getPlayer());
            } else {
                group = PermissionsManager.getPermissionMainGroup(name);
            }

            if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                    || group.equalsIgnoreCase("Admin")) {

                return MyMaidConfig.getJDA().getTextChannelById(690854369783971881L); // #rma_jail
            } else {
                return MyMaidConfig.getJDA().getTextChannelById(709399145575874690L); // #jail
            }
        } catch (IllegalArgumentException e) {
            return MyMaidConfig.getJDA().getTextChannelById(709399145575874690L); // #jaotan
        }
    }
}
