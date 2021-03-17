package com.jaoafa.MyMaid3.Task;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task_CoOLD extends BukkitRunnable {
    final Player player;
    final Location loc;
    final int page;

    public Task_CoOLD(Player player, Location loc, int page) {
        this.player = player;
        this.loc = loc;
        this.page = page;
    }

    @Override
    public void run() {
        if (MyMaidConfig.getMySQLDBManager_COOLD() == null) {
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "MySQLへの接続に失敗しました。(MySQLDBManager_COOLD null)");
            return;
        }

        if (page <= 0) {
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "取得に失敗しました。(ページを0以下にすることはできません)");
            return;
        }

        player.sendMessage("----- " + ChatColor.LIGHT_PURPLE + "CoreProtect OLD" + ChatColor.WHITE + " ----- "
                + ChatColor.GRAY + "(x" + loc.getBlockX() + "/y" + loc.getBlockY() + "/z" + loc.getBlockZ() + ")");

        try {
            Connection conn = MyMaidConfig.getMySQLDBManager_COOLD().getConnection();

            PreparedStatement statement = conn.prepareStatement(
                    "SELECT * FROM co_block WHERE wid = ? AND type != 3 AND x = ? AND y = ? AND z = ? ORDER BY rowid LIMIT ?, ?");
            statement.setInt(1, 1); // wid : Jao_Afa
            statement.setInt(2, loc.getBlockX());
            statement.setInt(3, loc.getBlockY());
            statement.setInt(4, loc.getBlockZ());
            statement.setInt(5, (page - 1) * 10); // limit min
            statement.setInt(6, page * 10); // limit max
            ResultSet res = statement.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            boolean found = false;
            while (res.next()) {
                int rolled_back = res.getInt("rolled_back");
                long time = res.getLong("time");
                String date = sdf.format(new Date(time * 1000));
                String username = getUserName(res.getInt("user"));
                int type = res.getInt("type");
                String typeja = getTypeJa(type);
                Material material = Material.getMaterial(res.getInt("type"));
                String block = material != null ? nameFilter(material, res.getInt("data"))
                        : res.getInt("type") + ":" + res.getInt("data");

                if (rolled_back == 1) {
                    // rollbacked
                    player.sendMessage(date + ChatColor.WHITE + " - "
                            + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + username + " "
                            + ChatColor.WHITE + ChatColor.STRIKETHROUGH + typeja + " "
                            + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + block);
                } else {
                    player.sendMessage(date + ChatColor.WHITE + " - "
                            + ChatColor.DARK_AQUA + username + " "
                            + ChatColor.WHITE + typeja + " "
                            + ChatColor.DARK_AQUA + block);
                }
                found = true;
            }
            res.close();
            statement.close();
            if (!found) {
                player.sendMessage("[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "not found.");
            }
        } catch (SQLException e) {
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "操作に失敗しました。");
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "詳しくはサーバコンソールをご確認ください");
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "再度実行しなおすと動作するかもしれません。");
            e.printStackTrace();
        }
    }

    String getUserName(int userid) throws SQLException {
        Connection conn = MyMaidConfig.getMySQLDBManager_COOLD().getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM co_user WHERE rowid = ?");
        statement.setInt(1, userid);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
            return res.getString("user");
        } else {
            return "null";
        }
    }

    String getTypeJa(int type) {
        String a2 = "placed";
        if (type == 0) {
            a2 = "removed";
        } else if (type == 2) {
            a2 = "clicked";
        }
        return a2;
    }

    String nameFilter(Material material, int data) {
        if (material == Material.STONE) {
            switch (data) {
                case 1:
                    return "granite";
                case 2:
                    return "polished_granite";
                case 3:
                    return "diorite";
                case 4:
                    return "polished_diorite";
                case 5:
                    return "andesite";
                case 6:
                    return "polished_andesite";
            }
        }
        return material.name().toLowerCase() + ":" + data;
    }
}
