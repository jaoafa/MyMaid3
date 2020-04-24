package com.jaoafa.MyMaid3.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.connorlinfoot.titleapi.TitleAPI;
import com.jaoafa.MyMaid3.Lib.AFKPlayer;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.jaoSuperAchievement2.API.AchievementAPI;
import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;

public class Event_AFK extends MyMaidLibrary implements Listener {
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		AFKPlayer afkplayer = new AFKPlayer(player);
		afkplayer.setNowLastActionTime();
		if (!afkplayer.isAFK()) {
			return;
		}
		afkplayer.end();
	}

	@EventHandler
	public void OnEvent_PlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		AFKPlayer afkplayer = new AFKPlayer(player);
		if (afkplayer.isAFK()) {
			if (!Achievementjao.getAchievement(player, new AchievementType(36))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
				return;
			}
		}
		afkplayer.clear();
	}

	@EventHandler
	public void OnEvent_PlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		AFKPlayer afkplayer = new AFKPlayer(player);
		afkplayer.setNowLastActionTime();
		if (!afkplayer.isAFK()) {
			return;
		}
		afkplayer.end();
		TitleAPI.clearTitle(player);
	}
}
