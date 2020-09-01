package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Cmd_Link extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(
                "linkコマンドはMinecraftサーバ内ではなくjMS Gamers ClubのDiscordサーバ内で実行してね！そこでコマンドが発行されるから、そのコマンドをMinecraftサーバ内で打ち込んでね！");
        return true;
    }

    @Override
    public String getDescription() {
        return "linkコマンドはjMS Gamers Clubで実行するのだ。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/link");
            }
        };
    }
}
