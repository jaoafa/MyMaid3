package com.jaoafa.MyMaid3.Event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Event_SandBoxRegularOnly extends MyMaidLibrary implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSandBox(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World fromWorld = event.getFrom();
		World toWorld = player.getWorld();
		if (!toWorld.getName().equalsIgnoreCase("SandBox")) {
			return; // SandBoxのみ
		}
		String group = PermissionsManager.getPermissionMainGroup(player);
		if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
				|| group.equalsIgnoreCase("Admin")) {
			return; // RMA除外
		}
		if (fromWorld.getName().equalsIgnoreCase("SandBox")) {
			player.teleport(new Location(Bukkit.getWorld("Jao_Afa"), 0, 68, 0));
			player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxワールドを利用することかできません。");
			return;
		}
		player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxワールドを利用することかできません。");
		player.teleport(new Location(fromWorld, 0, 68, 0));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		Player player = event.getPlayer();

		World fromWorld = from.getWorld();
		World toWorld = to.getWorld();
		if (!toWorld.getName().equalsIgnoreCase("SandBox")) {
			return; // SandBoxのみ
		}
		String group = PermissionsManager.getPermissionMainGroup(player);
		if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
				|| group.equalsIgnoreCase("Admin")) {
			return; // RMA除外
		}
		if (fromWorld.getName().equalsIgnoreCase("SandBox")) {
			player.teleport(new Location(Bukkit.getWorld("Jao_Afa"), 0, 68, 0));
			player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxワールドを利用することかできません。");
			return;
		}
		player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxワールドを利用することかできません。");
		event.setCancelled(true);
	}
}
