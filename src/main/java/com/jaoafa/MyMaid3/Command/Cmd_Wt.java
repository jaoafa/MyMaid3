/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Cmd_Wt extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    final Map<String, String> worlds = new HashMap<String, String>() {
        {
            put("1", "Jao_Afa");
            put("2", "Jao_Afa_nether");
            put("3", "SandBox");
        }
    };

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
                return true;
            }
            Player player = (Player) sender;
            if (worlds.containsKey(args[0])) {
                String worldName = worlds.get(args[0]);
                World world = Bukkit.getServer().getWorld(worldName);
                if (world == null) {
                    SendMessage(sender, cmd, String.format("「%s」ワールドの取得に失敗しました。", worldName));
                    return true;
                }
                Location loc = new Location(world, 0, 0, 0, 0, 0);
                int y = getGroundPos(loc);
                loc = new Location(world, 0, y, 0, 0, 0);
                loc.add(0.5f, 0f, 0.5f);
                player.teleport(loc);
                SendMessage(sender, cmd, String.format("「%s」ワールドにテレポートしました。", worldName));
            } else {
                World world = Bukkit.getServer().getWorld(args[0]);
                if (world == null) {
                    SendMessage(sender, cmd, "指定されたワールドは存在しません。");
                } else {
                    Location loc = new Location(world, 0, 0, 0, 0, 0);
                    int y = getGroundPos(loc);
                    loc = new Location(world, 0, y, 0, 0, 0);
                    loc.add(0.5f, 0f, 0.5f);
                    player.teleport(loc);
                    SendMessage(sender, cmd, "「" + world.getName() + "」ワールドにテレポートしました。");
                }
            }
            return true;
        } else if (args.length == 2) {
            Player play = null;
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(args[0])) {
                    play = p;
                }
            }
            if (play == null) {
                SendMessage(sender, cmd, "ユーザーが見つかりませんでした。");
                return true;
            }
            if (worlds.containsKey(args[1])) {
                String worldName = worlds.get(args[1]);
                World world = Bukkit.getServer().getWorld(worldName);
                if (world == null) {
                    SendMessage(sender, cmd, String.format("「%s」ワールドの取得に失敗しました。", worldName));
                    return true;
                }
                Location loc = new Location(world, 0, 0, 0, 0, 0);
                int y = getGroundPos(loc);
                loc = new Location(world, 0, y, 0, 0, 0);
                loc.add(0.5f, 0f, 0.5f);
                play.teleport(loc);
                SendMessage(sender, cmd, String.format("「%s」ワールドにテレポートしました。", worldName));
            } else {
                World world = Bukkit.getServer().getWorld(args[1]);
                if (world == null) {
                    SendMessage(sender, cmd, "指定されたワールドは存在しません。");
                } else {
                    Location loc = new Location(world, 0, 0, 0, 0, 0);
                    int y = getGroundPos(loc);
                    loc = new Location(world, 0, y, 0, 0, 0);
                    loc.add(0.5f, 0f, 0.5f);
                    play.teleport(loc);
                    SendMessage(sender, cmd, String.format("%sが「%s」ワールドにテレポートしました。", play.getName(), world.getName()));
                    SendMessage(play, cmd, String.format("「%s」ワールドにテレポートしました。", world.getName()));
                }
            }
            return true;
        }
        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    @Override
    public String getDescription() {
        return "他ワールドにテレポートします。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "wt",
                new CmdUsage.Cmd("1", "ワールド「Jao_Afa」にテレポートします。"),
                new CmdUsage.Cmd("2", "ワールド「Jao_Afa_nether」にテレポートします。"),
                new CmdUsage.Cmd("3", "ワールド「SandBox」にテレポートします。"),
                new CmdUsage.Cmd("<WorldName>", "指定されたワールド名のワールドのスポーン地点にテレポートします。")
        );
    }
}
