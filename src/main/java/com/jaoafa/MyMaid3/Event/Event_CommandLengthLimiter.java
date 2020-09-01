package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Event_CommandLengthLimiter extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();

        if (isAMRV(player)) {
            // Default以外
            return;
        }

        if (command.length() < 100) {
            // 100文字未満
            return;
        }

        event.setCancelled(true);
        SendMessage(player, "CmdLengthLimiter", "あなたの権限では100バイトを超えるコマンドの実行はできません。");
    }
}
