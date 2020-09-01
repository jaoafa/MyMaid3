package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.Arrays;

public class Event_OldWorldProtection extends MyMaidLibrary implements Listener {
    String[] worldNames = new String[]{
            "kassi-hp-tk",
            "Jao_Afa_1",
            "Jao_Afa_2",
            "SandBox_1",
            "SandBox_2",
            "SandBox_3",
            "ReJao_Afa",
            "Summer2017",
            "Summer2018"
    };

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = event.getBlock().getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        if (isA(player)) {
            player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック設置は許可されていません。");
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック設置は許可されていません。");
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        World world = event.getBlock().getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        if (isA(player)) {
            player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック破壊は許可されていません。");
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック破壊は許可されていません。");
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgniteEvent(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        World world = event.getBlock().getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        if (player == null) {
            event.setCancelled(true);
            return;
        }
        if (isA(player)) {
            player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック着火は許可されていません。");
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック着火は許可されていません。");
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        if (isA(player)) {
            player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでの液体撒きは許可されていません。");
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでの液体撒きは許可されていません。");
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        if (isA(player)) {
            player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでの液体掬いは許可されていません。");
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでの液体掬いは許可されていません。");
        event.setCancelled(true);
    }
}
