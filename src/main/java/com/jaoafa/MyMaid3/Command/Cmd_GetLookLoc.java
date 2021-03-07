package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Cmd_GetLookLoc extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }

        Player player = (Player) sender;
        TextComponent message = new TextComponent("[GETLOOKLOC] " + ChatColor.GREEN + "ここをクリックすると見ているブロックの座標がサジェストされます。Ctrl+A Ctrl+Cしてコピーしてください。");
        Location loc = player.getTargetBlock(null, 50).getLocation();
        String text = String.format("%d %d %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, text));
        player.spigot().sendMessage(message);
        return true;
    }

    @Override
    public String getDescription() {
        return "見ている先のブロック座標をサジェストします。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/getlookloc: 見ている先のブロック座標をサジェストします。");
            }
        };
    }
}
