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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Cmd_GetLookLoc extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }

        Player player = (Player) sender;
        Location loc = player.getTargetBlock(null, 50).getLocation();
        Component component = Component.text().append(
                Component.text("ここ")
                        .hoverEvent(HoverEvent.showText(
                                Component.text(String.format("文字列「%d %d %d」をコピーします", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
                        ))
                        .clickEvent(ClickEvent.copyToClipboard(String.format("%d %d %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())))
                        .style(Style.style(NamedTextColor.AQUA, TextDecoration.UNDERLINED)),
                Component.text("をクリックすると見ているブロックの座標がコピーされます。")
        ).build();
        SendMessage(player, cmd, component);
        return true;
    }

    @Override
    public String getDescription() {
        return "見ている先のブロック座標をコピーできるテキストを表示します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "getlookloc",
                new CmdUsage.Cmd("", getDescription())
        );
    }
}
