package com.jaoafa.MyMaid3.Event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.Historyjao;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Event_AMHistoryNotify extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!isAM(player)) {
			return;
		}

		new BukkitRunnable() {
			public void run() {
				Historyjao histjao = new Historyjao(player);

				for (Player player : Bukkit.getOnlinePlayers()) {
					if (!isAM(player)) {
						continue;
					}
					player.sendMessage("[jaoHistory] " + ChatColor.RED + "プレイヤー「" + player.getName() + "」には"
							+ histjao.getHistoryDatas().size() + "件のjaoHistoryがあります。");
					player.sendMessage(
							"[jaoHistory] " + ChatColor.RED + "コマンド「/history status " + player.getName()
									+ "」で詳細を閲覧できます。");
				}
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
	}
}
