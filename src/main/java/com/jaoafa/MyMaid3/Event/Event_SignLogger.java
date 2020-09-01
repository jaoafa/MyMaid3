package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.ErrorReporter;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Event_SignLogger extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        String line1 = event.getLine(0);
        String line2 = event.getLine(1);
        String line3 = event.getLine(2);
        String line4 = event.getLine(3);
        String text = String.join("", event.getLines());

        MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
        if (MySQLDBManager == null) {
            return;
        }
        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO signlogger (player, uuid, world, x, y, z, line1, line2, line3, line4, text) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, loc.getWorld().getName());
            statement.setInt(4, loc.getBlockX());
            statement.setInt(5, loc.getBlockY());
            statement.setInt(6, loc.getBlockZ());
            statement.setString(7, line1);
            statement.setString(8, line2);
            statement.setString(9, line3);
            statement.setString(10, line4);
            statement.setString(11, text);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            ErrorReporter.report(e);
            return;
        }
    }
}
