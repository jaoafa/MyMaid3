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

package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Cmd_GetUserKey extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
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

            Component component = Component.text().append(
                    Component.text("あなたのUserKeyは"),
                    Component.text(String.format("「%s」", userkey))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text(String.format("文字列「%s」をコピーします", userkey))
                            ))
                            .clickEvent(ClickEvent.copyToClipboard(userkey))
                            .style(Style.style(NamedTextColor.AQUA, TextDecoration.UNDERLINED)),
                    Component.text("です。クリックするとコピーできます。")
            ).build();
            SendMessage(player, cmd, component);
            return true;
        } catch (SQLException e) {
            SendMessage(player, cmd, "データベースサーバに接続できません。時間をおいて再度お試しください。");
            e.printStackTrace();
            return true;
        }
    }

    String getUserKey(Connection conn) throws SQLException {
        String userkey;
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
    public CmdUsage getUsage() {
        return new CmdUsage(
                "getuserkey",
                new CmdUsage.Cmd("", getDescription())
        );
    }
}
