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

import com.jaoafa.MyMaid3.Lib.ErrorReporter;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Event_LoginSuccessful extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        new BukkitRunnable() {
            public void run() {
                MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
                if (MySQLDBManager == null) {
                    return;
                }
                try {
                    Connection conn = MySQLDBManager.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "UPDATE login SET login_success = ? WHERE uuid = ? ORDER BY id DESC LIMIT 1");
                    statement.setBoolean(1, true);
                    statement.setString(2, uuid.toString());
                    statement.executeUpdate();
                    statement.close();
                } catch (SQLException e) {
                    ErrorReporter.report(e);
                }
            }
        }.runTaskAsynchronously(Main.getJavaPlugin());
    }
}
