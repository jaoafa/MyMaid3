package com.jaoafa.MyMaid3.Task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.TPSChecker;

public class Task_TPSTimings extends BukkitRunnable {
	JavaPlugin plugin;

	public Task_TPSTimings(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	private double OldTps1m = 20;
	private boolean timingsChecking = false;

	@Override
	public void run() {
		String tps1m = TPSChecker.getTPS1m();
		try {
			double tps1m_double = Double.parseDouble(tps1m);

			if (timingsChecking) {
				// 5分後
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timings paste");
				Main.ReportChannel.sendMessage("timings pasted. " + OldTps1m + " -> " + tps1m);
				timingsChecking = false;
				runTaskTimer(plugin, 1200L, 1200L);
				return;
			}

			if (tps1m_double <= 18 && OldTps1m > 18) {
				// やばいぞ
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timings on");
				timingsChecking = true;
				cancel();
				runTaskLater(plugin, 6000L);
			}
			OldTps1m = tps1m_double;
		} catch (NumberFormatException e) {
		}
	}
}
