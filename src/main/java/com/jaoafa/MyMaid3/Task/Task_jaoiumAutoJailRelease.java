package com.jaoafa.MyMaid3.Task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Lib.Jail;

public class Task_jaoiumAutoJailRelease extends BukkitRunnable {
	private Player player;

	public Task_jaoiumAutoJailRelease(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		if (!player.isOnline()) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) {
			return;
		}
		String reason = jail.getLastBanReason();
		if (!reason.equals("jaoium所持")) {
			return;
		}
		jail.removeBan("jaotan");
	}
}
