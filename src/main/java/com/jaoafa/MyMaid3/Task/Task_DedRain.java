package com.jaoafa.MyMaid3.Task;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class Task_DedRain extends BukkitRunnable {
    @Override
    public void run() {
        if (MyMaidConfig.isDedRaining()) {
            return;
        }
        MyMaidConfig.setDedRaining(true);
        Bukkit.broadcastMessage("[DedRain] " + ChatColor.GREEN + "DedRain設定を自動的にオンにします。");
        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);
        }
    }
}
