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

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Task.Task_CoOLD;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public class Event_CoOLD extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Location loc = block.getLocation();
        Player player = event.getPlayer();

        if (!MyMaidConfig.getCoOLDEnabler().containsKey(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);

        sendBlockEditData(player, loc);
    }

    void sendBlockEditData(Player player, Location loc) {
        World world = loc.getWorld();
        if (!world.getName().equalsIgnoreCase("Jao_Afa")) {
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "Jao_Afaワールド以外でのCoreProtectOLDログ閲覧はできません。");
            return;
        }

        if (MyMaidConfig.getCoOLDEnabler().get(player.getUniqueId()) != null
                && !MyMaidConfig.getCoOLDEnabler().get(player.getUniqueId()).isCancelled()) {
            MyMaidConfig.getCoOLDEnabler().get(player.getUniqueId()).cancel();
        }

        File file = new File(Main.getJavaPlugin().getDataFolder(), "coold.yml");
        if (!file.exists()) {
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "MySQLへの接続に失敗しました。(MySQL接続するためのファイルが見つかりません)");
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            if (MyMaidConfig.getMySQLDBManager_COOLD() == null) {
                MyMaidConfig.setMySQLDBManager_COOLD(new MySQLDBManager(
                        config.getString("sqlserver"),
                        config.getString("sqlport"),
                        config.getString("sqldatabase"),
                        config.getString("sqluser"),
                        config.getString("sqlpassword")));
            }
        } catch (ClassNotFoundException e) {
            player.sendMessage(
                    "[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "MySQLへの接続に失敗しました。(MySQL接続するためのクラスが見つかりません)");
            return;
        }

        player.sendMessage("[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "Please wait...");
        BukkitTask task = new Task_CoOLD(player, loc, 1).runTaskAsynchronously(Main.getJavaPlugin());
        MyMaidConfig.putCoOLDEnabler(player.getUniqueId(), task);
        MyMaidConfig.putCoOLDLoc(player.getUniqueId(), loc);
    }
}
