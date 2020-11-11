package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Cmd_Hide extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }

        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはプレイヤーから実行してください。");
            return true;
        }
        Player player = (Player) sender;
        if (!isAMR(player)) {
            SendMessage(sender, cmd, "このコマンドは管理部・モデレータ・常連のみ使用できます。");
            return true;
        }
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.hidePlayer(Main.getJavaPlugin(), player);
        }

        if (!MyMaidConfig.isHid(player.getUniqueId())) {
            MyMaidConfig.addHid(player.getUniqueId());
        }

        SendMessage(sender, cmd, "あなたは他のプレイヤーから見えなくなりました。見えるようにするには/showを実行しましょう。");
        SendMessage(sender, cmd, "なお、プレイヤーリストからも見えなくなりますのでお気をつけて。");
        return true;
    }

    @Override
    public String getDescription() {
        return "姿を隠します。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/hide");
            }
        };
    }
}
