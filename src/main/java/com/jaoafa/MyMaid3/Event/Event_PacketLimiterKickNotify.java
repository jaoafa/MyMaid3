package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.awt.*;
import java.util.Random;

public class Event_PacketLimiterKickNotify extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        if (event.getReason().equalsIgnoreCase("You are sending too many packets!") ||
                event.getReason().equalsIgnoreCase("You are sending too many packets, :(")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("警告！！");
            embed.appendDescription("プレイヤーがパケットを送信しすぎてKickされました。ハッククライアントの可能性があります。");
            embed.setAuthor(event.getPlayer().getName(),
                    "https://jaoafa.com/user/" + event.getPlayer().getUniqueId().toString(),
                    "https://crafatar.com/avatars/" + event.getPlayer().getUniqueId().toString());
            embed.setColor(Color.ORANGE);
            embed.addField("プレイヤー", event.getPlayer().getName(), true);
            embed.addField("理由", event.getReason(), false);
            Location loc = event.getPlayer().getLocation();
            String location = loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " "
                    + loc.getBlockZ();
            embed.addField("座標", location, false);

            Random rand = new Random();
            boolean x_isMinus = rand.nextBoolean();
            int x = rand.nextInt(310) + 152; // 152 - 462
            x = x_isMinus ? -x : x;

            boolean z_isMinus = rand.nextBoolean();
            int z = rand.nextInt(310) + 152; // 152 - 462
            z = x_isMinus ? -z : z;

            Location teleportLoc = new Location(Bukkit.getWorld("Jao_Afa"), x, 70, z);
            event.getPlayer().teleport(teleportLoc);
            System.out.println("[PacketLimiter_AutoTP] teleport to Jao_Afa " + x + " 70 " + z);

            MyMaidConfig.getJDA().getTextChannelById(597423444501463040L).sendMessage(embed.build()).queue();
        }
    }
}
