package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event_SignPicStick extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.STICK)
            return;
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Material material = clickedBlock.getType();
        if (material == Material.SIGN_POST || material == Material.WALL_SIGN) {
            Sign sign = (Sign) clickedBlock.getState();

            if (!sign.getLine(0).startsWith("#")) {
                return;
            }

            String signtext = String.join("", sign.getLines());
            signtext = ChatColor.stripColor(signtext);

            player.sendMessage("[SignPicStick] " + ChatColor.GREEN + signtext);

            Pattern p = Pattern.compile("\\$([\\w.\\-/:#?=&;%~+]+)");
            Matcher m = p.matcher(signtext);
            if (m.find(1)) {
                String url = m.group(1);
                player.sendMessage("[SignPicStick] " + ChatColor.GREEN + url);
            }
        }
    }
}
