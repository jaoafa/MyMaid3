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

public class Cmd_Cmdb extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
                return true;
            }
            Player player = (Player) sender;

            ItemStack is = new ItemStack(Material.COMMAND_BLOCK);

            PlayerInventory inv = player.getInventory();
            ItemStack main = inv.getItemInMainHand();

            inv.setItemInMainHand(is);
            SendMessage(sender, cmd, "コマンドブロックをメインハンドのアイテムと置きかえました。");

            if (main.getType() != Material.AIR) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.getLocation().getWorld().dropItem(player.getLocation(), main);
                    SendMessage(sender, cmd, "インベントリがいっぱいだったため、既に持っていたアイテムはあなたの足元にドロップしました。");
                } else {
                    inv.addItem(main);
                }
            }
            return true;
        } else if (args.length == 1) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                SendMessage(sender, cmd, "指定されたプレイヤー「" + args[0] + "」は見つかりませんでした。");

                Player any_chance_player = Bukkit.getPlayer(args[0]);
                if (any_chance_player != null) {
                    SendMessage(sender, cmd, "もしかして: " + any_chance_player.getName());
                }
                return true;
            }

            ItemStack is = new ItemStack(Material.COMMAND_BLOCK);

            PlayerInventory inv = player.getInventory();
            ItemStack main = inv.getItemInMainHand();

            inv.setItemInMainHand(is);
            SendMessage(sender, cmd, "コマンドブロックをプレイヤー「" + player.getName() + "」のメインハンドのアイテムと置きかえました。");

            if (main.getType() != Material.AIR) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.getLocation().getWorld().dropItem(player.getLocation(), main);
                    SendMessage(player, cmd, "インベントリがいっぱいだったため、既に持っていたアイテムはあなたの足元にドロップしました。");
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
        return "コマンドブロックをプレイヤーのメインハンドのアイテムと置き換えます。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "cmdb",
                new CmdUsage.Cmd("", "コマンドブロックをコマンド実行者のメインハンドのアイテムと置き換えます。"),
                new CmdUsage.Cmd("<Player>", "コマンドブロックを指定したプレイヤーのメインハンドのアイテムと置き換えます。")
        );
    }
}
