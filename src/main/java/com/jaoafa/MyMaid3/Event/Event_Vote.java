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

package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.*;
import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Event_Vote extends MyMaidLibrary implements Listener {
    public static void successNotifyMinecraftJP(String name, int oldVote, int newVote, boolean isAutoFill) {
        String autoFillMessage = isAutoFill ? " [自動補填]" : "";
        Bukkit.broadcastMessage(
                "[MyMaid] " + ChatColor.GREEN + "プレイヤー「" + name + "」がminecraft.jpで投票をしました！(現在の投票数:" + newVote + "回)" + autoFillMessage);
        Bukkit.broadcastMessage("[MyMaid] " + ChatColor.GREEN + "投票をよろしくお願いします！ https://jaoafa.com/vote");
        MyMaidConfig.getServerChatChannel()
                .sendMessage("プレイヤー「" + DiscordEscape(name) + "」がminecraft.jpで投票をしました！(現在の投票数:" + newVote + "回)" + autoFillMessage)
                .queue();
        MyMaidConfig.getServerChatChannel().sendMessage("投票をよろしくお願いします！ https://jaoafa.com/vote").queue();

        MyMaidConfig.getJDA(.getTextChannelById(499922840871632896L))
                .sendMessage(":o: `" + name + "`の投票特典付与処理に成功しました(minecraft.jp): " + oldVote + "回 -> " + newVote + "回" + autoFillMessage)
                .queue();
    }

    public static void successNotifyMonocraftNet(String name, int oldVote, int newVote, boolean isAutoFill) {
        String autoFillMessage = isAutoFill ? " [自動補填]" : "";
        Bukkit.broadcastMessage(
                "[MyMaid] " + ChatColor.GREEN + "プレイヤー「" + name + "」がmonocraft.netで投票をしました！(現在の投票数:" + newVote + "回)" + autoFillMessage);
        Bukkit.broadcastMessage("[MyMaid] " + ChatColor.GREEN + "投票をよろしくお願いします！ https://jaoafa.com/monovote");
        MyMaidConfig.getServerChatChannel()
                .sendMessage("プレイヤー「" + DiscordEscape(name) + "」がmonocraft.netで投票をしました！(現在の投票数:" + newVote + "回)" + autoFillMessage)
                .queue();
        MyMaidConfig.getServerChatChannel().sendMessage("投票をよろしくお願いします！ https://jaoafa.com/monovote").queue();

        MyMaidConfig.getJDA(.getTextChannelById(499922840871632896L))
                .sendMessage(":o: `" + name + "`の投票特典付与処理に成功しました(monocraft.net): " + oldVote + "回 -> " + newVote + "回" + autoFillMessage)
                .queue();
    }

    public static void checkjSA(OfflinePlayer offplayer, boolean isTodayFirst, int newVote) {
        if (isTodayFirst) {
            Achievementjao.getAchievement(offplayer, new AchievementType(54)); // 筆頭株主 - 誰よりも早くjao鯖に投票
        }
        Achievementjao.getAchievement(offplayer, new AchievementType(55)); // 期待の新人 - 初めての投票
        if (newVote >= 10) {
            Achievementjao.getAchievement(offplayer, new AchievementType(56)); // 安定株主 - 10回投票
        }
        if (newVote >= 20) {
            Achievementjao.getAchievement(offplayer, new AchievementType(59)); // VIPPERな俺 - 20回投票
        }
        if (newVote >= 100) {
            Achievementjao.getAchievement(offplayer, new AchievementType(57)); // 大株主 - 100回投票
        }
        if (newVote >= 1000) {
            Achievementjao.getAchievement(offplayer, new AchievementType(58)); // 伝説の株主 - 1000回投票
        }
    }

    @EventHandler
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();
        String name = vote.getUsername();
        String service = vote.getServiceName();
        System.out.println("onVotifierEvent[MyMaid3]: " + vote.getUsername() + " " + vote.getAddress() + " "
                + vote.getServiceName() + " " + vote.getTimeStamp());
        if (service.equalsIgnoreCase("minecraft.jp")) {
            VoteReceive(name);
        } else if (service.equalsIgnoreCase("monocraft.net")) {
            VoteReceiveMonocraftNet(name);
        }
    }

    void VoteReceive(String name) {
        MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
        if (MySQLDBManager == null) {
            missedNotifyMinecraftJP(name, "MySQLDBManager == null");
            return;
        }
        // nameからuuidを取得する
        UUID uuid = getUUID(MySQLDBManager, name);
        if (uuid == null) {
            missedNotifyMinecraftJP(name, "UUID取得失敗");
            return;
        }

        OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);

        if (offplayer.getName() == null) {
            missedNotifyMinecraftJP(name, "OfflinePlayer取得失敗");
            return;
        }

        if (!offplayer.getName().equals(name)) {
            name += "(" + offplayer.getName() + ")";
        }

        //boolean first = PlayerVoteData.TodayFirstVote();

        int oldVote;
        int newVote;
        boolean isTodayFirst;
        try {
            PlayerVoteData pvd = new PlayerVoteData(offplayer);
            oldVote = pvd.get();

            isTodayFirst = PlayerVoteData.isTodayFirstVote();

            pvd.add();

            newVote = pvd.get();
        } catch (SQLException | NullPointerException e) {
            missedNotifyMinecraftJP(name, e.getClass().getName() + " -> " + e.getMessage() + " (投票数追加失敗)");
            e.printStackTrace();
            return;
        }

        successNotifyMinecraftJP(name, oldVote, newVote);
        checkjSA(offplayer, isTodayFirst, newVote);
    }

    void VoteReceiveMonocraftNet(String name) {
        MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
        if (MySQLDBManager == null) {
            missedNotifyMonocraftNet(name, "MySQLDBManager == null");
            return;
        }
        // nameからuuidを取得する
        UUID uuid = getUUID(MySQLDBManager, name);
        if (uuid == null) {
            missedNotifyMonocraftNet(name, "UUID取得失敗");
            return;
        }

        OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);

        if (offplayer.getName() == null) {
            missedNotifyMonocraftNet(name, "OfflinePlayer取得失敗");
            return;
        }

        if (!offplayer.getName().equals(name)) {
            name += "(" + offplayer.getName() + ")";
        }

        int oldVote;
        int newVote;
        boolean isTodayFirst;
        try {
            PlayerVoteData_Monocraft pvd = new PlayerVoteData_Monocraft(offplayer);
            oldVote = pvd.get();

            isTodayFirst = PlayerVoteData_Monocraft.isTodayFirstVote();

            pvd.add();

            newVote = pvd.get();
        } catch (SQLException | NullPointerException e) {
            missedNotifyMonocraftNet(name, e.getClass().getName() + " -> " + e.getMessage() + " (投票数追加失敗)");
            e.printStackTrace();
            return;
        }

        successNotifyMonocraftNet(name, oldVote, newVote);
        checkjSA(offplayer, isTodayFirst, newVote);
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
        MyMaidConfig.getJDA(.getTextChannelById(499922840871632896L))
                .sendMessage(":x: <@221991565567066112> `" + name + "`の投票特典付与処理に失敗しました: `" + reason + "`")
                .queue();
    }

    void missedNotifyMinecraftJP(String name, String reason) {
        MyMaidConfig.getJDA(.getTextChannelById(499922840871632896L))
                .sendMessage(":x: <@221991565567066112> `" + name + "`の投票特典付与処理に失敗しました(minecraft.jp): `" + reason + "`")
                .queue();
    }

    void missedNotifyMonocraftNet(String name, String reason) {
        MyMaidConfig.getJDA(.getTextChannelById(499922840871632896L))
                .sendMessage(
                ":x: <@221991565567066112> `" + name + "`の投票特典付与処理に失敗しました(monocraft.net): `" + reason + "`")
                .queue();
    }

    void successNotifyMinecraftJP(String name, int oldVote, int newVote) {
        successNotifyMinecraftJP(name, oldVote, newVote, false);
    }

    void successNotifyMonocraftNet(String name, int oldVote, int newVote) {
        successNotifyMonocraftNet(name, oldVote, newVote, false);
    }
}
