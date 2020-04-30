package com.jaoafa.MyMaid3.Event;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Event_CommandGameMode extends MyMaidLibrary implements Listener {
	Boolean DEBUG = true;

	@EventHandler
	public void onGameModeCommand(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage();
		Player player = event.getPlayer();
		String[] args = command.split(" ", 0);
		if (args.length == 0) {
			return; // 本来発生しないと思うけど
		}
		if (!args[0].equalsIgnoreCase("/gamemode")
				&& !args[0].equalsIgnoreCase("/minecraft:gamemode")) {
			return; // gamemodeコマンド以外
		}
		// /gamemode <mode> [player]
		if (args.length == 2) {
			// /gamemode <mode>
			printDebugMsg("OK 自身のゲームモードを変更");
		} else if (args.length == 3) {
			// /gamemode <mode> [player]
			if (isAMR(player)) {
				return;
			}
			printDebugMsg("NG 他のユーザーのゲームモードを変更");
			SendMessage(player, "GameMode", "あなたの権限では他のユーザーのゲームモードを変更することはできません。自身のゲームモードを変更する場合はプレイヤー名を入れずに入力してください。");
			event.setCancelled(true);
			return;
		}
	}

	void printDebugMsg(String msg) {
		boolean debug = true;
		if (!debug)
			return;
		Logger LOGGER = Bukkit.getLogger();
		LOGGER.info(msg);
	}
}