package com.jaoafa.MyMaid3.Task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Task_TeleportAlias extends BukkitRunnable {
    final Player player;
    final String replacement;

    public Task_TeleportAlias(Player player, String replacement) {
        this.player = player;
        this.replacement = replacement;
    }

    @Override
    public void run() {
        player.performCommand("tp " + replacement);
    }
}
