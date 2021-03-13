package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Command.Cmd_Cauldron;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Event_PlaceCauldron extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceCommand(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() != Material.CAULDRON) {
            return;
        }
        if (!Cmd_Cauldron.cauldrons.containsKey(player.getUniqueId())) {
            return;
        }
        int level = Cmd_Cauldron.cauldrons.get(player.getUniqueId());
        BlockState cauldronState = block.getState();
        cauldronState.getData().setData((byte) level);
        cauldronState.update();
    }
}
