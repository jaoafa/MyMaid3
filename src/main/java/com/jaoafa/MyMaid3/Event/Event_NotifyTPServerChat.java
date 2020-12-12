package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Event_NotifyTPServerChat extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleportCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        String[] args = command.split(" ", 0);
        if (args.length == 0) {
            return; // 本来発生しないと思うけど
        }
        if (!args[0].equalsIgnoreCase("/tp")
                && !args[0].equalsIgnoreCase("/minecraft:tp")) {
            return; // tpコマンド以外
        }
        // /tp [target player] <destination player>
        // /tp [target player] <x> <y> <z> [<yaw> <pitch>]
        if (args.length == 2) { // /tp <Player>
            String to = args[1];
            Player to_player = Bukkit.getPlayerExact(to);
            if (to_player == null) {
                return;
            }
            MyMaidConfig.getServerChatChannel().sendMessage(String.format("*[%s: Teleported %s to %s]*", MyMaidLibrary.DiscordEscape(player.getName()), MyMaidLibrary.DiscordEscape(player.getName()), MyMaidLibrary.DiscordEscape(to_player.getName()))).queue();
        } else if (args.length == 3) { // /tp <Player> <Player>
            String from = args[1];
            String to = args[2];
            Player from_player = Bukkit.getPlayerExact(from);
            Player to_player = Bukkit.getPlayerExact(to);
            if (from_player == null || to_player == null) {
                return;
            }
            MyMaidConfig.getServerChatChannel().sendMessage(String.format("*[%s: Teleported %s to %s]*",
                    MyMaidLibrary.DiscordEscape(player.getName()), MyMaidLibrary.DiscordEscape(from_player.getName()), MyMaidLibrary.DiscordEscape(to_player.getName()))).queue();
        }/* else if (args.length == 4) { // /tp ~ ~ ~
            String x = args[1];
            String y = args[2];
            String z = args[3];
            // unsupported
        } else if (args.length == 5) { // /tp <Player> ~ ~ ~
            String from = args[1];
            String x = args[2];
            String y = args[3];
            String z = args[4];
            Player from_player = Bukkit.getPlayer(from);
            if (from_player == null) {
                return;
            }
            // unsupported
        } else if (args.length == 6) { // /tp ~ ~ ~ 0 0
            String x = args[1];
            String y = args[2];
            String z = args[3];
            String yaw = args[4];
            String pitch = args[5];
            // unsupported
        } else if (args.length == 7) { // /tp <Player> ~ ~ ~ 0 0
            String from = args[1];
            String x = args[2];
            String y = args[3];
            String z = args[4];
            String yaw = args[5];
            String pitch = args[6];
            Player from_player = Bukkit.getPlayer(from);
            if (from_player == null) {
                return;
            }
            // unsupported
        }*/
    }
}
