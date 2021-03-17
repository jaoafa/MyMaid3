/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

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
