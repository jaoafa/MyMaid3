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
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Cmd_G extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
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
            GameMode beforeGameMode = player.getGameMode();

            if (player.getGameMode() == GameMode.SPECTATOR) {
                // スペクテイターならクリエイティブにする
                player.setGameMode(GameMode.CREATIVE);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    SendMessage(sender, cmd, "ゲームモードの変更ができませんでした。");
                    return true;
                }
                SendMessage(sender, cmd, beforeGameMode.name() + " -> " + GameMode.CREATIVE.name());
                return true;
            } else if (player.getGameMode() == GameMode.CREATIVE) {
                // クリエイティブならスペクテイターにする
                player.setGameMode(GameMode.SPECTATOR);
                if (player.getGameMode() != GameMode.SPECTATOR) {
                    SendMessage(sender, cmd, "ゲームモードの変更ができませんでした。");
                    return true;
                }
                SendMessage(sender, cmd, beforeGameMode.name() + " -> " + GameMode.SPECTATOR.name());
                return true;
            } else {
                // それ以外(サバイバル・アドベンチャー)ならクリエイティブにする
                player.setGameMode(GameMode.CREATIVE);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    SendMessage(sender, cmd, "ゲームモードの変更ができませんでした。");
                    return true;
                }
                SendMessage(sender, cmd, beforeGameMode.name() + " -> " + GameMode.CREATIVE.name());
                return true;
            }
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
                return true;
            }
            Player player = (Player) sender;
            GameMode beforeGameMode = player.getGameMode();

            int i;
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                SendMessage(sender, cmd, "引数には数値を指定してください。");
                return true;
            }

            @SuppressWarnings("deprecation")
            GameMode gm = GameMode.getByValue(i);
            if (gm == null) {
                SendMessage(sender, cmd, "指定された引数からゲームモードが取得できませんでした。");
                return true;
            }

            player.setGameMode(gm);
            if (player.getGameMode() != gm) {
                SendMessage(sender, cmd, "ゲームモードの変更ができませんでした。");
                return true;
            }
            SendMessage(sender, cmd, beforeGameMode.name() + " -> " + gm.name());
            return true;
        } else if (args.length == 2) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!isAMR(player)) {
                    SendMessage(sender, cmd,
                            "あなたの権限では他のユーザーのゲームモードを変更することはできません。自身のゲームモードを変更する場合はプレイヤー名を入れずに入力してください。");
                    return true;
                }
            }

            int i;
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                SendMessage(sender, cmd, "引数には数値を指定してください。");
                return true;
            }

            @SuppressWarnings("deprecation")
            GameMode gm = GameMode.getByValue(i);
            if (gm == null) {
                SendMessage(sender, cmd, "指定された引数からゲームモードが取得できませんでした。");
                return true;
            }

            String playername = args[1];
            Player player = Bukkit.getPlayerExact(playername);
            if (player == null) {
                SendMessage(sender, cmd, "指定されたプレイヤー「" + playername + "」は見つかりませんでした。");

                Player any_chance_player = Bukkit.getPlayer(playername);
                if (any_chance_player != null) {
                    SendMessage(sender, cmd, "もしかして: " + any_chance_player.getName());
                }
                return true;
            }

            GameMode beforeGameMode = player.getGameMode();

            player.setGameMode(gm);
            if (player.getGameMode() != gm) {
                SendMessage(sender, cmd, "ゲームモードの変更ができませんでした。");
                return true;
            }
            SendMessage(sender, cmd, player.getName() + ": " + beforeGameMode.name() + " -> " + gm.name());
            return true;
        }
        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    @Override
    public String getDescription() {
        return "ゲームモードを変更します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "g",
                new CmdUsage.Cmd("", "クリエイティブモードならスペクテイターモードに、スペクテイターモードならクリエイティブモードに、それ以外ならクリエイティブモードに変更します。"),
                new CmdUsage.Cmd("<0-3>", "指定された数値に合うゲームモードに変更します。"),
                new CmdUsage.Cmd("<0-3> <Player>", "指定したプレイヤーのゲームモードを指定された数値に合うゲームモードに変更します。")
        );
    }
}
