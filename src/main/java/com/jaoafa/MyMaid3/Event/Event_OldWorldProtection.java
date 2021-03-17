package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class Event_OldWorldProtection extends MyMaidLibrary implements Listener {
    final String[] worldNames = new String[]{
            "kassi-hp-tk",
            "Jao_Afa_1",
            "Jao_Afa_2",
            "SandBox_1",
            "SandBox_2",
            "SandBox_3",
            "ReJao_Afa",
            "Summer2017",
            "Summer2018",
            "Summer2020"
    };
    final Material[] ignoreClickCancel = new Material[]{};
    final Material[] ignoreTargetClickCancel = new Material[]{
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR,
            Material.TRAP_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.STONE_PLATE,
            Material.WOOD_PLATE,
            Material.COMMAND,
    };

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = event.getBlock().getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック設置は許可されていません。");
        if (isA(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        World world = event.getBlock().getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック破壊は許可されていません。");
        if (isA(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのブロック着火は許可されていません。");
        if (isA(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでの液体撒きは許可されていません。");
        if (isA(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでの液体掬いは許可されていません。");
        if (isA(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : player.getLocation();
        if (!Arrays.asList(worldNames).contains(world.getName())) {
            return;
        }
        if (event.getItem() != null) {
            if (Arrays.asList(ignoreClickCancel).contains(event.getItem().getType())) {
                return;
            }
        }
        if (event.getClickedBlock() != null) {
            if (Arrays.asList(ignoreTargetClickCancel).contains(event.getClickedBlock().getType())) {
                return;
            }
        }
        player.sendMessage("[OldWorldProtection] " + ChatColor.GREEN + "旧ワールドでのインタラクトは許可されていません。インタラクトが必要な場合は開発部に以下のメッセージのスクリーンショットを提示し除外するよう申請してください。");
        player.sendMessage("[OldWorldProtection-DEBUG] " + ChatColor.GREEN + "Location: " + world.getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
        if (event.getItem() != null)
            player.sendMessage("[OldWorldProtection-DEBUG] " + ChatColor.GREEN + "event.getItem(): " + event.getItem().getType().name());
        if (event.getClickedBlock() != null)
            player.sendMessage("[OldWorldProtection-DEBUG] " + ChatColor.GREEN + "event.getClickedBlock(): " + event.getClickedBlock().getType().name());
        player.sendMessage("[OldWorldProtection-DEBUG] " + ChatColor.GREEN + "MyMaid3 Version: " + Main.getMain().getDescription().getVersion());
        if (isA(player)) {
            return;
        }
        event.setCancelled(true);
    }
}
