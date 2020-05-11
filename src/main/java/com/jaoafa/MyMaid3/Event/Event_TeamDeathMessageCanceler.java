package com.jaoafa.MyMaid3.Event;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Event_TeamDeathMessageCanceler extends MyMaidLibrary implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			return;
		}
		Player player = (Player) event.getEntity();
		String deathMessage = event.getDeathMessage();

		Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
		if (team == null) {
			return;
		}

		for (String entry : team.getEntries()) {
			Player p = Bukkit.getPlayerExact(entry);
			if (p == null) {
				continue;
			}
			p.sendMessage(deathMessage);
			System.out.println("death message send to " + p.getName());
		}

		event.setDeathMessage(null);
	}
}
