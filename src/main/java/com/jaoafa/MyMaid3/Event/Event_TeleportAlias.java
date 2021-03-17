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

package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.*;
import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Task.Task_TeleportAlias;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Event_TeleportAlias extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onEvent_AntiProblemCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        if (!command.contains(" ")) {
            return;
        }
        String[] args = command.split(" ", 0);
        if (!args[0].equalsIgnoreCase("/tp")) {
            return;
        }
        Jail jail = new Jail(player);
        if (jail.isBanned()) {
            return;
        }
        EBan eban = new EBan(player);
        if (eban.isBanned()) {
            return;
        }

        if (args.length == 2) { // /tp <Player>
            String to = args[1];
            String replacement = TeleportAlias.getReplaceAlias(to);
            if (replacement == null) {
                return;
            }
            Player replacementPlayer = Bukkit.getPlayer(replacement);
            if (replacementPlayer != null && replacementPlayer.isOnline()) {
                if (replacementPlayer.getGameMode() == GameMode.SPECTATOR) {
                    if (isAMR(player)) {
                        SendMessage(player, "tp", "プレイヤー「" + replacementPlayer.getName() + "」はスペクテイターモードのためテレポートできません。");
                    }
                    event.setCancelled(true);
                    return;
                }
                TpDeny tpDeny = new TpDeny(replacementPlayer);
                if (tpDeny.isTpDeny(player) || tpDeny.isTpDeny(player)) {
                    // denied
                    SendMessage(player, "tpDeny", "指定されたプレイヤー「" + replacementPlayer.getName() + "」へのテレポートは拒否されました。");
                    if (tpDeny.isNotify(player))
                        SendMessage(replacementPlayer, "tpDeny", "プレイヤー「" + player.getName() + "」からのテレポートを拒否しました。");
                    event.setCancelled(true);
                }
            }
            new Task_TeleportAlias(player, replacement).runTaskLater(Main.getJavaPlugin(), 5L);
            event.setCancelled(true);
        }
    }
}
