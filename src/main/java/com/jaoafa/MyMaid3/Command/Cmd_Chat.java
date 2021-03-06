package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Cmd_Chat extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }
        if (args.length < 2) {
            SendUsageMessage(sender, cmd);
            return true;
        }
        ChatColor color = ChatColor.GRAY;
        List<String> colors = Arrays.stream(args).filter(
                arg -> arg != null && arg.startsWith("color:")).collect(Collectors.toList());
        if (colors.size() != 0) {
            for (ChatColor cc : ChatColor.values()) {
                if (!cc.name().equalsIgnoreCase(colors.get(0).substring("color:".length()))) {
                    continue;
                }
                color = cc;
            }
        }
        List<String> texts = Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).filter(
                arg -> arg != null && !arg.startsWith("color:")).collect(Collectors.toList());
        String text = ChatColor.translateAlternateColorCodes('&', String.join(" ", texts));
        if (args[0].equalsIgnoreCase("jaotan")) {
            color = ChatColor.GOLD;
        }

        chatFake(color, args[0], text);
        return true;
    }

    @Override
    public String getDescription() {
        return "偽のプレイヤーをしゃべらせます。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/chat <FakePlayer> <Message>");
            }
        };
    }
}
