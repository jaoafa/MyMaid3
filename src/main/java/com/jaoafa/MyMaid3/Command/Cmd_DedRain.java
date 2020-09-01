package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Task.Task_DedRain;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class Cmd_DedRain extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    static BukkitTask task;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }
        if (args.length == 0) {
            // /dedrain
            String dedrainStr = MyMaidConfig.isDedRaining() ? "オン" : "オフ";
            SendMessage(sender, cmd, "現在のDedRain設定は「" + dedrainStr + "」です。");
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                // /dedrain on
                MyMaidConfig.setDedRaining(true);
                SendMessage(sender, cmd, "現在のDedRain設定は「オン」です。");
                for (World world : Bukkit.getWorlds()) {
                    world.setThundering(false);
                    world.setStorm(false);
                }
                task.cancel();
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                // /dedrain off
                MyMaidConfig.setDedRaining(false);
                SendMessage(sender, cmd, "現在のDedRain設定は「オフ」です。");
                SendMessage(sender, cmd, "10分後、自動的にオンになります。");
                task = new Task_DedRain().runTaskLater(Main.getJavaPlugin(), 12000);
                return true;
            }
        }
        SendUsageMessage(sender, cmd);
        return true;
    }

    @Override
    public String getDescription() {
        return "雨を降らせたり降らせなかったりします。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/dedrain: 現在の設定状態を表示します。");
                add("/dedrain on: 雨を降らせないようにします。(デフォルト)");
                add("/dedrain off: 雨を降らせれるようにします。10分で自動的に無効化されます。");
            }
        };
    }
}
