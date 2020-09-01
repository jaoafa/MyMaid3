package com.jaoafa.MyMaid3.Event;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Event_CommandSendAM extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (!(player instanceof Player)) {
            return;
        }
        String command = e.getMessage();
        if (isAMRV(player)) {
            // Default以上は実行試行したコマンドを返す
            player.sendMessage(ChatColor.DARK_GRAY + "Cmd: " + command); // 仮
        }
        String group = PermissionsManager.getPermissionMainGroup(player);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (isAM(p) && (!player.getName().equals(p.getName()))) {
                p.sendMessage(
                        ChatColor.GRAY + "(" + group + ") " + player.getName() + ": " + ChatColor.YELLOW + command);
            }
        }

        // Lunachat - jp translate

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("LunaChat")) {
            return;
        }
        LunaChat lunachat = (LunaChat) Bukkit.getServer().getPluginManager().getPlugin("LunaChat");
        LunaChatAPI lunachatapi = lunachat.getLunaChatAPI();

        if (!command.contains(" ")) {
            return;
        }
        String[] commands = command.split(" ", 0);
        List<String> tells = new ArrayList<String>() {
            {
                add("/tell");
                add("/msg");
                add("/message");
                add("/m");
                add("/t");
                add("/w");
            }
        };

        if (tells.contains(commands[0])) {
            if (commands.length <= 2) {
                return;
            }
            String text = String.join(" ", Arrays.copyOfRange(commands, 2, commands.length));
            if (!lunachatapi.isPlayerJapanize(player.getName())) {
                return;
            }
            String jp = lunachatapi.japanize(text, JapanizeType.GOOGLE_IME);
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (isAM(p) && (!player.getName().equals(p.getName()))) {
                    p.sendMessage(ChatColor.GRAY + "(" + ChatColor.YELLOW + jp + ChatColor.GRAY + ")");
                }
            }
        } else if (commands[0].equalsIgnoreCase("/r")) {
            if (commands.length <= 1) {
                return;
            }
            String text = String.join(" ", Arrays.copyOfRange(commands, 1, commands.length));
            if (!lunachatapi.isPlayerJapanize(player.getName())) {
                return;
            }
            String jp = lunachatapi.japanize(text, JapanizeType.GOOGLE_IME);
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (isAM(p) && (!player.getName().equals(p.getName()))) {
                    p.sendMessage(ChatColor.GRAY + "(" + ChatColor.YELLOW + jp + ChatColor.GRAY + ")");
                }
            }
        }
    }
}
