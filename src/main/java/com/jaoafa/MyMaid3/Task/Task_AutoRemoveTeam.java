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

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Task_AutoRemoveTeam extends BukkitRunnable {
    @Override
    public void run() {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        int i = 0;
        for (Team team : sb.getTeams()) {
            if (team.getSize() != 0) {
                continue;
            }
            team.unregister();
            i++;
        }
        if (i != 0)
            Bukkit.getLogger().info(i + " teams have been deleted from the scoreboard.");
    }
}
