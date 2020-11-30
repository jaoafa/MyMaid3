package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Event_LongTimeNoSee extends MyMaidLibrary implements Listener {
    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        new BukkitRunnable() {
            public void run() {
                try {
                    MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
                    if (MySQLDBManager == null) {
                        return;
                    }
                    Connection conn = MySQLDBManager.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "SELECT unix_timestamp(date) as ts FROM login WHERE uuid = ? ORDER BY id DESC");
                    statement.setString(1, uuid);
                    ResultSet res = statement.executeQuery();
                    res.next();
                    if (res.next()) {
                        String last_str = res.getString("ts");
                        long last = new Long(last_str);
                        long now = System.currentTimeMillis() / 1000L;
                        long sa = now - last;
                        Bukkit.getLogger().info("[LongTimeNoSee] " + player.getName() + ": " + sa + "s (LAST: " + last
                                + " / NOW: " + now + ")");
                        Bukkit.getLogger().info("[LongTimeNoSee] " + player.getName() + ": last_str: " + last_str);
                        if (sa >= 2592000L) {
                            StringBuilder builder = new StringBuilder();

                            int year = (int) Math.floor(sa / 31536000L);
                            int year_remain = (int) Math.floor(sa % 31536000L);
                            if (year != 0) {
                                builder.append(year + "年");
                            }
                            int month = (int) Math.floor(year_remain / 2592000L);
                            int month_remain = (int) Math.floor(year_remain % 2592000L);
                            if (month != 0) {
                                builder.append(month + "か月");
                            }
                            int day = (int) Math.floor(month_remain / 86400L);
                            int day_remain = (int) Math.floor(month_remain % 86400L);
                            if (day != 0) {
                                builder.append(day + "日");
                            }
                            int hour = (int) Math.floor(day_remain / 3600L);
                            int hour_remain = (int) Math.floor(day_remain % 3600L);
                            if (hour != 0) {
                                builder.append(hour + "時間");
                            }
                            int minute = (int) Math.floor(hour_remain / 60L);
                            if (minute != 0) {
                                builder.append(minute + "分");
                            }
                            int sec = (int) Math.floor(hour_remain % 60L);
                            if (sec != 0) {
                                builder.append(sec + "秒");
                            }

                            chatFake(ChatColor.GOLD, "jaotan", player.getName() + "さん、お久しぶりです！" + builder.toString() + "ぶりですね！");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getJavaPlugin());
    }
}
