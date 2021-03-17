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

import com.jaoafa.MyMaid3.Lib.Historyjao;
import com.jaoafa.MyMaid3.Lib.Historyjao.HistoryData;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;

public class Event_AMHistoryNotify extends MyMaidLibrary implements Listener {
    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!isAM(player)) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                Historyjao histjao = new Historyjao(player);
                if (histjao.getHistoryDatas().size() == 0) {
                    return;
                }

                for (Player _player : Bukkit.getOnlinePlayers()) {
                    if (!isAM(_player)) {
                        continue;
                    }
                    _player.sendMessage("[jaoHistory] " + ChatColor.RED + "プレイヤー「" + player.getName() + "」には"
                            + histjao.getHistoryDatas().size() + "件のjaoHistoryがあります。");
                    _player.sendMessage(
                            "[jaoHistory] " + ChatColor.RED + "コマンド「/history status " + player.getName()
                                    + "」で詳細を閲覧できます。");
                }

                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle(player.getName() + "'s jaoHistory")
                        .setDescription(histjao.getHistoryDatas().size() + "件のjaoHistoryがあります。")
                        .setColor(Color.ORANGE)
                        .setAuthor(player.getName(), "https://jaoafa.com/user/" + player.getUniqueId().toString(),
                                "https://minotar.net/helm/" + player.getUniqueId().toString() + "/128.png");

                for (HistoryData hist : histjao.getHistoryDatas()) {
                    if (hist.disabled) {
                        continue;
                    }
                    eb.addField("[" + hist.id + "] " + sdfFormat(hist.getCreatedAt()), hist.message, false);
                }

                MyMaidConfig.getJDA(.getTextChannelById(597423444501463040L)).sendMessage(eb.build()).queue();
            }
        }.runTaskAsynchronously(Main.getJavaPlugin());
    }
}
