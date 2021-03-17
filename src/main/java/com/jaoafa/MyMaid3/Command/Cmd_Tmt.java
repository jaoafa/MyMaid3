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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Cmd_Tmt extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはサーバ内から実行できます。");
            return true;
        }
        Player player = (Player) sender;
        Location loc = player.getLocation();
        PlayerInventory inv = player.getInventory();

        dropOrAddItem(loc, inv, inv.getHelmet());
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("32ff7cdc-a1b4-450a-aa7e-6af75fe8c37c")));
        skull.setItemMeta(skullMeta);
        inv.setHelmet(skull);

        dropOrAddItem(loc, inv, inv.getChestplate());
        inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));

        dropOrAddItem(loc, inv, inv.getLeggings());
        inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));

        dropOrAddItem(loc, inv, inv.getBoots());
        inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));

        player.updateInventory();

        SendMessage(sender, cmd, "はいどうぞ。");
        return true;
    }

    void dropOrAddItem(Location loc, PlayerInventory inv, ItemStack is) {
        if (is != null && is.getType() != Material.AIR) {
            if (inv.firstEmpty() != -1) {
                inv.addItem(is);
            } else {
                loc.getWorld().dropItem(loc, is);
            }
        }
    }

    @Override
    public String getDescription() {
        return "あなたはmine_book000です。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "tmt",
                new CmdUsage.Cmd("", getDescription())
        );
    }
}
