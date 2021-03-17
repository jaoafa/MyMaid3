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

import com.jaoafa.MyMaid3.Lib.EBan;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Event_AntiLoginCmd extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onOPCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        String[] args = command.split(" ", 0);
        if (args.length == 0) {
            return; // 本来発生しないと思うけど
        }
        if (!args[0].equalsIgnoreCase("/login")) {
            return; // loginコマンド以外
        }

        EBan eban = new EBan(player);
        if (eban.isBanned()) {
            event.setCancelled(true);
            return;
        }

        eban.addBan("jaotan", String.format("コマンド「%s」を実行したことにより、サーバルールへの違反の可能性を検知したため", command));
        player.kickPlayer("Disconnected.");
        MyMaidConfig.getJaotanChannel().sendMessage(String.format("プレイヤー「%s」がコマンド「%s」を実行したため、キックしました。", player.getName(), command)).queue();
        event.setCancelled(true);
    }
}
