package com.jaoafa.MyMaid3.Event;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Event_AntiSplashPotion extends MyMaidLibrary implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onPotionDrink(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item == null) {
			return;
		}
		if (item.getType() != Material.POTION &&
				item.getType() != Material.SPLASH_POTION &&
				item.getType() != Material.LINGERING_POTION) {
			return;
		}
		String group = null;
		try {
			group = PermissionsManager.getPermissionMainGroup(player);
		} catch (IllegalArgumentException e) {
			event.setCancelled(true);
			return;
		}
		if (group.equalsIgnoreCase("Default")) {
			// 所持を含む全部の動作を禁止
			player.getInventory().remove(item);
			player.updateInventory();
			event.setCancelled(true);
			return;
		}
		if (group.equalsIgnoreCase("Verified")) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			for (PotionEffect effect : meta.getCustomEffects()) {
				if (effect.getAmplifier() >= 5) {
					event.setCancelled(true);
					player.getInventory().remove(item);
					player.updateInventory();
					return;
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		ItemStack item = event.getPotion().getItem();
		if (item == null) {
			return;
		}
		if (item.getType() != Material.POTION &&
				item.getType() != Material.SPLASH_POTION &&
				item.getType() != Material.LINGERING_POTION) {
			return;
		}
		Player player = (Player) event.getPotion().getShooter();
		String group = null;
		try {
			group = PermissionsManager.getPermissionMainGroup(player);
		} catch (IllegalArgumentException e) {
			event.setCancelled(true);
			return;
		}

		if (group.equalsIgnoreCase("Default")) {
			// 所持を含む全部の動作を禁止
			player.getInventory().remove(item);
			player.updateInventory();
			event.setCancelled(true);
			return;
		}
		if (group.equalsIgnoreCase("Verified")) {
			for (PotionEffect effect : event.getPotion().getEffects()) {
				if (effect.getAmplifier() >= 5) {
					event.setCancelled(true);
					player.getInventory().remove(item);
					player.updateInventory();
					return;
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPotionInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item == null) {
			return;
		}
		if (item.getType() != Material.POTION &&
				item.getType() != Material.SPLASH_POTION &&
				item.getType() != Material.LINGERING_POTION) {
			return;
		}
		String group = null;
		try {
			group = PermissionsManager.getPermissionMainGroup(player);
		} catch (IllegalArgumentException e) {
			event.setCancelled(true);
			return;
		}
		if (group.equalsIgnoreCase("Default")) {
			// 所持を含む全部の動作を禁止
			player.getInventory().remove(item);
			player.updateInventory();
			event.setCancelled(true);
			return;
		}
		if (group.equalsIgnoreCase("Verified")) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			for (PotionEffect effect : meta.getCustomEffects()) {
				if (effect.getAmplifier() >= 5) {
					event.setCancelled(true);
					player.getInventory().remove(item);
					player.updateInventory();
					return;
				}
			}
		}
	}
}
