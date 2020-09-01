package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Event_SandBoxRegularOnly extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void ontoSandBox(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World toWorld = player.getWorld();
        if (!toWorld.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxで建築することはできません。");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSandBoxPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        World world = loc.getWorld();
        Player player = event.getPlayer();

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxでブロック編集することはできません。");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSandBoxBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        World world = loc.getWorld();
        Player player = event.getPlayer();

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxでブロック編集することはできません。");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSandBoxIgniteEvent(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        World world = loc.getWorld();
        Player player = event.getPlayer();

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxで着火することはできません。");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSandBoxBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();
        Location loc = block.getLocation();
        World world = loc.getWorld();
        Player player = event.getPlayer();

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxで水や溶岩を撒くことはできません。");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSandBoxBucketFillEvent(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();
        Location loc = block.getLocation();
        World world = loc.getWorld();
        Player player = event.getPlayer();

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxで水や溶岩を撒くことはできません。");
        event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        Location loc = player.getLocation();
        World world = loc.getWorld();

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractRight(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        World world = player.getWorld();

        if ((player.getInventory().getItemInMainHand() == null
                || player.getInventory().getItemInMainHand().getType() == Material.AIR)
                && (player.getInventory().getItemInOffHand() == null
                || player.getInventory().getItemInOffHand().getType() == Material.AIR)) {
            return;
        }

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxに干渉することはできません。");
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractLeft(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        World world = player.getWorld();

        if ((player.getInventory().getItemInMainHand() == null
                || player.getInventory().getItemInMainHand().getType() == Material.AIR)
                && (player.getInventory().getItemInOffHand() == null
                || player.getInventory().getItemInOffHand().getType() == Material.AIR)) {
            return;
        }

        if (!world.getName().equalsIgnoreCase("SandBox")) {
            return; // SandBoxのみ
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        if (group.equalsIgnoreCase("Regular") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Admin")) {
            return; // RMA除外
        }
        player.sendMessage("[SandBox] " + ChatColor.RED + "あなたの権限ではSandBoxに干渉することはできません。");
        event.setCancelled(true);
    }
}
