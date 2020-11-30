package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Event_AntiTargetAllEntityCmd extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        String[] args = command.split(" ");
        if (args.length == 0) {
            return; // 本来発生しないと思うけど
        }
        for (String arg : args) {
            if (arg.equalsIgnoreCase("@e")) {
                player.chat("開けてみたいでしょ～？");
                player.chat("うん、みたーい！");
                player.chat("行きますよー！");
                player.chat("はい！");
                player.chat("せーのっ！");
                player.chat("あぁ～！水素の音ォ〜！！");
                checkSpam(player);
                event.setCancelled(true);
                return;
            }
        }
    }
}
