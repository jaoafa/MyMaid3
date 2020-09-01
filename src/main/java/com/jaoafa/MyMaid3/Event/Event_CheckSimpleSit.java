package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Event_CheckSimpleSit extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onSimpleSitPluginCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        String[] args = command.split(" ", 0);
        if (args.length == 0) {
            return; // 本来発生しないと思うけど
        }

        if (!args[0].equalsIgnoreCase("/sit")
                && !args[0].equalsIgnoreCase("/simplesit:sit")
                && !args[0].equalsIgnoreCase("/lay")
                && !args[0].equalsIgnoreCase("/simplesit:lay")) {
            return; // sit, layコマンド以外
        }

        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            player.sendMessage("[SimpleSit] " + ChatColor.GREEN
                    + "このコマンドはサバイバルモード・アドベンチャーモードの時には利用できません。クリエイティブモードに切り替えてから実行してください。");
            event.setCancelled(true);
        }
    }
}
