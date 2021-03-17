package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Cmd_Test extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはプレイヤーから実行してください。");
            return true;
        }
        Player player = (Player) sender;
        if(!player.getUniqueId().toString().equalsIgnoreCase("32ff7cdc-a1b4-450a-aa7e-6af75fe8c37c")){
            SendMessage(sender, cmd, "このコマンドは試験コマンドのため、利用が制限されています。");
            return true;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "test",
                new CmdUsage.Cmd("", getDescription())
        );
    }
}
