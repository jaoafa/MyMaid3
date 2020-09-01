package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Cmd_Test extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            PermissionsManager.setPermissionsGroup(player.getUniqueId().toString(), "test");
        } else {
            PermissionsManager.setPermissionsGroup(player, "test");
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/test");
            }
        };
    }
}
