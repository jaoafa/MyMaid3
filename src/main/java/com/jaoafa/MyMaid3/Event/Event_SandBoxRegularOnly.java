package com.jaoafa.MyMaid3.Event;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Event_SandBoxRegularOnly extends MyMaidLibrary implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void ontoSandBox(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World toWorld = player.getWorld();
		if (!toWorld.getName().equalsIgnoreCase("SandBox")) {
			return; // SandBoxのみ
		}
		String group = PermissionsManager.getPermissionMainGroup(player);
		if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
				|| group.equalsIgnoreCase("Admin")) {
			return; // RMA除外
		}
		player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxで建築することはできません。");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSandBoxPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Location loc = block.getLocation();
		World world = loc.getWorld();
		Player player = event.getPlayer();

		if (!world.getName().equalsIgnoreCase("SandBox")) {
			return; // SandBoxのみ
		}
		String group = PermissionsManager.getPermissionMainGroup(player);
		if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
				|| group.equalsIgnoreCase("Admin")) {
			return; // RMA除外
		}
		player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxでブロック編集することはできません。");
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSandBoxBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Location loc = block.getLocation();
		World world = loc.getWorld();
		Player player = event.getPlayer();

		if (!world.getName().equalsIgnoreCase("SandBox")) {
			return; // SandBoxのみ
		}
		String group = PermissionsManager.getPermissionMainGroup(player);
		if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
				|| group.equalsIgnoreCase("Admin")) {
			return; // RMA除外
		}
		player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxでブロック編集することはできません。");
		event.setCancelled(true);
	}
}
