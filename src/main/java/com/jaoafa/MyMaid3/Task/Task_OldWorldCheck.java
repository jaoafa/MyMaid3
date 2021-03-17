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
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class Task_OldWorldCheck extends BukkitRunnable {
    final List<String> targetWorlds = Arrays.asList("kassi-hp-tk", "Jao_Afa_1", "Jao_Afa_2", "SandBox_1", "SandBox_2", "SandBox_3", "ReJao_Afa", "Summer2017", "Summer2018");

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            boolean isEmpty = targetWorlds.stream().noneMatch(wname -> world.getName().equalsIgnoreCase(wname));
            if (isEmpty) {
                continue;
            }

            if (!world.getPlayers().isEmpty()) {
                continue;
            }

            Bukkit.broadcastMessage("[OldWorldCheck] " + ChatColor.GREEN + "ワールド「" + world.getName() + "」にはプレイヤーが誰もいないため、アンロードします。");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv remove " + world.getName());
        }
    }
}
