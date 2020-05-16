package com.jaoafa.MyMaid3.Event;

import java.io.File;

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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Task.Task_CoOLD;

public class Event_CoOLD extends MyMaidLibrary implements Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Location loc = block.getLocation();
		Player player = event.getPlayer();

		if (!Main.coOLDEnabler.containsKey(player.getUniqueId())) {
			return;
		}
		event.setCancelled(true);

		sendBlockEditData(player, loc);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Location loc = block.getLocation();
		Player player = event.getPlayer();

		if (!Main.coOLDEnabler.containsKey(player.getUniqueId())) {
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

		if (Main.coOLDEnabler.get(player.getUniqueId()) != null
				&& !Main.coOLDEnabler.get(player.getUniqueId()).isCancelled()) {
			Main.coOLDEnabler.get(player.getUniqueId()).cancel();
		}

		File file = new File(Main.getJavaPlugin().getDataFolder(), "coold.yml");
		if (!file.exists()) {
			player.sendMessage(
					"[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "MySQLへの接続に失敗しました。(MySQL接続するためのファイルが見つかりません)");
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		try {
			if (Main.MySQLDBManager_COOLD == null) {
				Main.MySQLDBManager_COOLD = new MySQLDBManager(
						config.getString("sqlserver"),
						config.getString("sqlport"),
						config.getString("sqldatabase"),
						config.getString("sqluser"),
						config.getString("sqlpassword"));
			}
		} catch (ClassNotFoundException e) {
			player.sendMessage(
					"[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "MySQLへの接続に失敗しました。(MySQL接続するためのクラスが見つかりません)");
			return;
		}

		player.sendMessage("[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "Please wait...");
		BukkitTask task = new Task_CoOLD(player, loc, 1).runTaskAsynchronously(Main.getJavaPlugin());
		Main.coOLDEnabler.put(player.getUniqueId(), task);
		Main.coOLDLoc.put(player.getUniqueId(), loc);
	}
}
