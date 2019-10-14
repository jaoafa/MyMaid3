
package com.jaoafa.MyMaid3.Event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Event_PlaceTNT extends MyMaidLibrary implements Listener {
	@EventHandler
	public void onPlaceTNT(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (block.getType() != Material.TNT) {
			return;
		}
		String group = PermissionsManager.getPermissionMainGroup(player);
		if (!group.equalsIgnoreCase("Default") && !group.equalsIgnoreCase("Verified")
				&& !group.equalsIgnoreCase("Regular")) {
			return;
		}
		event.setCancelled(true);
		Location loc = block.getLocation();
		block.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4L, false, false);
	}
}
