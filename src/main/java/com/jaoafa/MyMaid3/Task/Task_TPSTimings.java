package com.jaoafa.MyMaid3.Task;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.TPSChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Task_TPSTimings extends BukkitRunnable {
    JavaPlugin plugin;
    private double OldTps1m = 20;
    private boolean timingsChecking = false;

    public Task_TPSTimings(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Task_TPSTimings(JavaPlugin plugin, double OldTps1m, boolean timingsChecking) {
        this.plugin = plugin;
        this.OldTps1m = OldTps1m;
        this.timingsChecking = timingsChecking;
    }

    @Override
    public void run() {
        String tps1m = TPSChecker.getTPS1m();
        try {
            double tps1m_double = Double.parseDouble(tps1m);

            if (timingsChecking) {
                // 5分後
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timings paste");
                MyMaidConfig.getReportChannel().sendMessage("timings pasted. " + OldTps1m + " -> " + tps1m);
                timingsChecking = false;
                cancel();
                new Task_TPSTimings(plugin, OldTps1m, timingsChecking).runTaskLater(plugin, 1200L);
                return;
            }

            if (tps1m_double <= 18 && OldTps1m > 18) {
                // やばいぞ
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timings on");
                timingsChecking = true;
                cancel();
                new Task_TPSTimings(plugin, OldTps1m, timingsChecking).runTaskLater(plugin, 6000L);
            }
            OldTps1m = tps1m_double;
        } catch (NumberFormatException e) {
        }
    }
}
