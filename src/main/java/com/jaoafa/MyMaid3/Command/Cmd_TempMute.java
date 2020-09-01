package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cmd_TempMute extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    public static Set<Player> tempmutes = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }
        Player player = (Player) sender;
        if (!isAM(player)) {
            SendMessage(sender, cmd, "あなたの権限ではこのコマンドを使用することはできません。");
            return true;
        }

        if (tempmutes.contains(player)) {
            tempmutes.remove(player);
            SendMessage(sender, cmd, "すべてのチャットミュートを解除しました。");
        } else {
            tempmutes.add(player);
            SendMessage(sender, cmd, "一時的にすべてのチャットをミュートしました。リログすると解除されます。");
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "一時的にすべてのチャットをミュートするかどうかを設定します。Admin・Moderatorのみ使用できます。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/tempmute");
            }
        };
    }
}