package com.jaoafa.MyMaid3.Task;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Lib.AFKPlayer;

public class Task_AFKING extends BukkitRunnable {
	private Player player;

	public Task_AFKING(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		AFKPlayer afkplayer = new AFKPlayer(player);
		if (!afkplayer.isAFK()) {
			return;
		}
		if (!player.isOnline()) {
			afkplayer.end();
		}
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		String listname = player.getPlayerListName();
		if (!listname.contains(ChatColor.DARK_GRAY + player.getName())) {
			listname = listname.replaceAll(player.getName(), ChatColor.DARK_GRAY + player.getName());
			player.setPlayerListName(listname);
		}
	}
}
