package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cmd_Ded extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    public static Map<String, Location> ded = new HashMap<String, Location>();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはプレイヤーから実行してください。");
            return true;
        }
        Player player = (Player) sender;
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            SendMessage(sender, cmd, "このコマンドはサバイバルモード・アドベンチャーモードの時には利用できません。クリエイティブモードに切り替えてから実行してください。");
            SendMessage(sender, cmd, ChatColor.RED + "" + ChatColor.BOLD
                    + "警告!! PvP等での「/ded」コマンドの利用は原則禁止です！多く使用すると迷惑行為として認識される場合もあります！");
            return true;
        }
        if (!ded.containsKey(player.getName())) {
            SendMessage(sender, cmd, "死亡した情報が存在しません。");
            return true;
        }
        Location loc = ded.get(player.getName());
        player.teleport(loc);
        SendMessage(sender, cmd, loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + "にテレポートしました。");
        SendMessage(sender, cmd,
                ChatColor.RED + "" + ChatColor.BOLD + "警告!! PvP等での「/ded」コマンドの利用は原則禁止です！多く使用すると迷惑行為として認識される場合もあります！");
        return true;
    }

    @Override
    public String getDescription() {
        return "最後に死亡した場所に移動します。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/ded");
            }
        };
    }
}
