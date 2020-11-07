package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Cmd_Workbench extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }
        Player player = (Player) sender;
        player.openWorkbench(null, true);
        return true;
    }

    @Override
    public String getDescription() {
        return "作業台を開きます。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/workbench");
            }
        };
    }
}
