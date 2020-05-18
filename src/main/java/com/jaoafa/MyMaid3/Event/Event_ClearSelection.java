package com.jaoafa.MyMaid3.Event;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;
import com.jaoafa.MyMaid3.Lib.SelClickManager;

public class Event_ClearSelection extends MyMaidLibrary implements Listener {
	LinkedList<UUID> sprinting = new LinkedList<>();

	@EventHandler
	public void onAirClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_AIR)
			return; // 空気左クリック以外無視

		Player player = event.getPlayer();
		if (!sprinting.contains(player.getUniqueId())) {
			return;
		}
		String group = PermissionsManager.getPermissionMainGroup(player);
		if (group.equalsIgnoreCase("Default")) {
			return;
		}

		if (player.getGameMode() != GameMode.CREATIVE) {
			return;
		}

		if (!SelClickManager.isEnable(player)) {
			return;
		}

		boolean bool = player.performCommand("/sel");
		if (!bool) {
			player.sendMessage("[SEL] " + ChatColor.GREEN + "//selコマンドの実行に失敗しました。");
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onToggleSprint(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if (event.isSprinting()) {
			sprinting.add(player.getUniqueId());
		} else {
			sprinting.remove(player.getUniqueId());
		}
	}
}
