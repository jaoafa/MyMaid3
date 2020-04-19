package com.jaoafa.MyMaid3.Task;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Lib.AFKPlayer;
import com.jaoafa.jaoSuperAchievement2.API.AchievementAPI;
import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;

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

		if (afkplayer.getAFKingSec() >= 5 * 60) {
			if (!Achievementjao.getAchievement(player, new AchievementType(32))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
				return;
			}
		}
		if (afkplayer.getAFKingSec() >= 15 * 60) {
			if (!Achievementjao.getAchievement(player, new AchievementType(33))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
				return;
			}
		}
	}
}
