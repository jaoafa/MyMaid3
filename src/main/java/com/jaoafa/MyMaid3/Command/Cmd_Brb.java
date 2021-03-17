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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class Cmd_Brb extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                MyMaidLibrary.SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
                return true;
            }
            Player player = (Player) sender;

            ItemStack is = new ItemStack(Material.BARRIER);

            PlayerInventory inv = player.getInventory();
            ItemStack main = inv.getItemInMainHand();

            inv.setItemInMainHand(is);
            MyMaidLibrary.SendMessage(sender, cmd, "バリアブロックをメインハンドのアイテムと置きかえました。");

            if (main.getType() != Material.AIR) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.getLocation().getWorld().dropItem(player.getLocation(), main);
                    MyMaidLibrary.SendMessage(sender, cmd, "インベントリがいっぱいだったため、既に持っていたアイテムはあなたの足元にドロップしました。");
                } else {
                    inv.addItem(main);
                }
            }
            return true;
        } else if (args.length == 1) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                MyMaidLibrary.SendMessage(sender, cmd, "指定されたプレイヤー「" + args[0] + "」は見つかりませんでした。");

                Player any_chance_player = Bukkit.getPlayer(args[0]);
                if (any_chance_player != null)
                    MyMaidLibrary.SendMessage(sender, cmd, "もしかして: " + any_chance_player.getName());
                return true;
            }

            ItemStack is = new ItemStack(Material.BARRIER);

            PlayerInventory inv = player.getInventory();
            ItemStack main = inv.getItemInMainHand();

            inv.setItemInMainHand(is);
            MyMaidLibrary.SendMessage(sender, cmd, "バリアブロックをプレイヤー「" + player.getName() + "」のメインハンドのアイテムと置きかえました。");

            if (main.getType() != Material.AIR) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.getLocation().getWorld().dropItem(player.getLocation(), main);
                    MyMaidLibrary.SendMessage(player, cmd, "インベントリがいっぱいだったため、既に持っていたアイテムはあなたの足元にドロップしました。");
                } else {
                    inv.addItem(main);
                }
            }
            return true;
        }
        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    @Override
    public String getDescription() {
        return "バリアブロックをプレイヤーのメインハンドのアイテムと置き換えます。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "brb",
                new CmdUsage.Cmd("", "バリアブロックをコマンド実行者のメインハンドのアイテムと置き換えます。"),
                new CmdUsage.Cmd("<Player>", "バリアブロックを指定したプレイヤーのメインハンドのアイテムと置き換えます。")
        );
    }
}
