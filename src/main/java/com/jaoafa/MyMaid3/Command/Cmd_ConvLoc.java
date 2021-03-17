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

import com.google.common.collect.Sets;
import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cmd_ConvLoc extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    final Pattern LOC_PATTERN = Pattern.compile("^(~?)(-?)([.0-9]+)$");
    final Pattern SELECTOR_PATTERN = Pattern.compile("^@[praes]\\[.*?]$");
    final Pattern XYZ_SELECTOR_PATTERN = Pattern.compile("[^d]([xyz])=([~.\\-0-9]+)");

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

        Set<Material> materials = Sets.newHashSet(Material.values());
        materials.remove(Material.COMMAND_BLOCK);
        materials.remove(Material.CHAIN_COMMAND_BLOCK);
        materials.remove(Material.REPEATING_COMMAND_BLOCK);
        Block targetBlock = player.getTargetBlock(materials, 10);
        if (targetBlock.getType() != Material.COMMAND_BLOCK && targetBlock.getType() != Material.CHAIN_COMMAND_BLOCK && targetBlock.getType() != Material.REPEATING_COMMAND_BLOCK) {
            SendMessage(sender, cmd, "対象のコマンドブロックを見てください。(" + targetBlock.getType().name() + " / " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ() + ")");
            return true;
        }

        if (!(targetBlock.getState() instanceof CommandBlock)) {
            SendMessage(sender, cmd, "コマンドブロック情報を正常に取得できませんでした。(" + targetBlock.getType().name() + " / " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ() + ")");
            return true;
        }

        CommandBlock cb = (CommandBlock) targetBlock.getState();
        String command = cb.getCommand();

        if (!command.contains(" ")) {
            SendMessage(sender, cmd, targetBlock.getWorld().getName() + " " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
            SendMessage(sender, cmd, "BEFORE: " + command);
            SendMessage(sender, cmd, "AFTER : " + command);
            return true;
        }

        if (args.length == 0) {
            String replaced = replaceProcess(targetBlock.getLocation(), command, true);
            SendMessage(sender, cmd, targetBlock.getWorld().getName() + " " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
            SendMessage(sender, cmd, "BEFORE: " + command);
            SendMessage(sender, cmd, "AFTER : " + replaced);
            return true;
        } else if (args.length == 1) {
            if ("relative".startsWith(args[0])) {
                String replaced = replaceProcess(targetBlock.getLocation(), command, true);
                if (replaced == null) replaced = command;
                SendMessage(sender, cmd, targetBlock.getWorld().getName() + " " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                SendMessage(sender, cmd, "BEFORE: " + command);
                SendMessage(sender, cmd, "AFTER : " + replaced);
                return true;
            } else if ("absolute".startsWith(args[0])) {
                String replaced = replaceProcess(targetBlock.getLocation(), command, false);
                if (replaced == null) replaced = command;
                SendMessage(sender, cmd, targetBlock.getWorld().getName() + " " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                SendMessage(sender, cmd, "BEFORE: " + command);
                SendMessage(sender, cmd, "AFTER : " + replaced);
                return true;
            }
            return true;
        }
        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    @Override
    public String getDescription() {
        return "コマンドブロックのコマンドの座標指定を「絶対座標」と「相対座標」で相互変換します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "convLoc",
                new CmdUsage.Cmd("", "見ているコマンドブロックのコマンドを「相対座標」に変換します。"),
                new CmdUsage.Cmd("<relative|absolute>", "見ているコマンドブロックのコマンドを「相対座標(relative)」か「絶対座標(absolute)」のいずれかに変換します。<relative|absolute>は短縮できます。")
        );
    }

    String replaceProcess(Location loc, String command, boolean toRelative) {
        String _baseCommand = command.split(" ")[0].trim();
        String baseCommand = _baseCommand;
        if (baseCommand.charAt(0) == '$') baseCommand = baseCommand.substring(1);
        if (baseCommand.charAt(0) == '/') baseCommand = baseCommand.substring(1);
        List<String> args = Arrays.asList(Arrays.copyOfRange(command.split(" "), 1, command.split(" ").length));
        try {
            LinkedList<String> new_args = new LinkedList<>();
            List<String> lines = Files.readAllLines(Paths.get(Main.getJavaPlugin().getDataFolder().getAbsolutePath(), "command_sheet.txt"));
            List<String> sheet_args = null;
            for (String line : lines) {
                String sheet_baseCommand = line.split(" ")[0].trim();
                List<String> _sheet_args = Arrays.asList(Arrays.copyOfRange(line.split(" "), 1, line.split(" ").length));

                if (!baseCommand.equalsIgnoreCase(sheet_baseCommand)) {
                    continue;
                }
                sheet_args = _sheet_args;
                break;
            }
            for (int i = 0; i < args.size(); i++) {
                String arg = args.get(i);
                if (SELECTOR_PATTERN.matcher(arg).matches()) {
                    // セレクター
                    Matcher xyz = XYZ_SELECTOR_PATTERN.matcher(arg);
                    while (xyz.find()) {
                        String selector_key = xyz.group(1);
                        String selector_value = xyz.group(2);

                        if (selector_key.equalsIgnoreCase("x")) {
                            String replaced = toRelative ? toRelative(selector_value, loc.getBlockX()) : toAbsolute(selector_value, loc.getBlockX());
                            arg = arg.replace(selector_key + "=" + selector_value, selector_key + "=" + replaced);
                        } else if (selector_key.equalsIgnoreCase("y")) {
                            String replaced = toRelative ? toRelative(selector_value, loc.getBlockY()) : toAbsolute(selector_value, loc.getBlockY());
                            arg = arg.replace(selector_key + "=" + selector_value, selector_key + "=" + replaced);
                        } else if (selector_key.equalsIgnoreCase("z")) {
                            String replaced = toRelative ? toRelative(selector_value, loc.getBlockZ()) : toAbsolute(selector_value, loc.getBlockZ());
                            arg = arg.replace(selector_key + "=" + selector_value, selector_key + "=" + replaced);
                        }
                    }
                }
                if (sheet_args == null) {
                    new_args.add(arg);
                    continue;
                }
                if (i >= sheet_args.size()) {
                    new_args.add(arg);
                    continue;
                }
                String sheet_arg = sheet_args.get(i);
                if (sheet_arg.equals("%N")) {
                    // skip
                    new_args.add(arg);
                    continue;
                }
                if (sheet_arg.equals("%X")) {
                    // x
                    String replaced = toRelative ? toRelative(arg, loc.getBlockX()) : toAbsolute(arg, loc.getBlockX());
                    new_args.add(replaced);
                    continue;
                }
                if (sheet_arg.equals("%Y")) {
                    // y
                    String replaced = toRelative ? toRelative(arg, loc.getBlockY()) : toAbsolute(arg, loc.getBlockY());
                    new_args.add(replaced);
                    continue;
                }
                if (sheet_arg.equals("%Z")) {
                    // z
                    String replaced = toRelative ? toRelative(arg, loc.getBlockZ()) : toAbsolute(arg, loc.getBlockZ());
                    new_args.add(replaced);
                    continue;
                }
                new_args.add(arg);
            }
            return _baseCommand + " " + String.join(" ", new_args);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 絶対座標数値から相対座標数値に変換する。
     *
     * @param xyz     絶対座標数値 (103)
     * @param cmb_xyz コマンドブロックの位置座標 (100)
     * @return 相対座標数値 (~3)
     */
    String toRelative(String xyz, int cmb_xyz) {
        Matcher matcher = LOC_PATTERN.matcher(xyz);
        if (!matcher.matches()) {
            return xyz;
        }
        if (matcher.groupCount() != 3) {
            return xyz;
        }
        if (matcher.group(1).equals("~")) {
            // 既に相対座標数値
            return xyz;
        }
        int i = Integer.parseInt(matcher.group(2) + matcher.group(3));

        if (("~" + (i - cmb_xyz)).equals("~0")) return "~";
        return "~" + (i - cmb_xyz);
    }

    /**
     * 相対座標数値から絶対座標数値に変換する。
     *
     * @param xyz     相対座標数値 (~100)
     * @param cmb_xyz コマンドブロックの位置座標 (103)
     * @return 絶対座標数値 (3)
     */
    String toAbsolute(String xyz, int cmb_xyz) {
        Matcher matcher = LOC_PATTERN.matcher(xyz);
        if (!matcher.matches()) {
            return xyz;
        }
        if (matcher.groupCount() != 3) {
            return xyz;
        }
        if (matcher.group(1).equals("")) {
            // 既に絶対座標数値
            return xyz;
        }
        int i = matcher.group(3).equals("") ? 0 : Integer.parseInt(matcher.group(3));
        if (matcher.group(2).equals("-")) {
            i = cmb_xyz - i;
        } else {
            i = cmb_xyz + i;
        }

        return String.valueOf(i);
    }
}
