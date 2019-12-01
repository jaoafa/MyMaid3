package com.jaoafa.MyMaid3.Task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Lib.AFKPlayer;

public class Task_AFK extends BukkitRunnable {
	/**
	 * AFKチェックタスク(1分毎)
	 * @author mine_book000
	 */
	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			AFKPlayer afkplayer = new AFKPlayer(player);
			if (afkplayer.isAFK()) {
				continue; // AFKかどうかを調べるのでAFKは無視
			}
			if (afkplayer.getLastActionTime() == -1L) {
				continue; // AFKタイムが設定されてないと処理しようがないので無視
			}
			if (player.getGameMode() == GameMode.SPECTATOR) {
				continue; // スペクテイターモードは誰かにくっついて動いててもMoveイベント発生しないので無視
			}
			if (player.isInsideVehicle()) {
				continue; // トロッコ関連はMoveイベント発生しないっぽい？
			}
			long nowtime = System.currentTimeMillis() / 1000L;
			long lastmovetime = afkplayer.getLastActionTime();
			long sa = nowtime - lastmovetime; // 前回移動した時間から現在の時間の差を求めて3分差があったらAFK扱い
			if (sa >= 180) {
				afkplayer.start();
			}
		}
	}
}
