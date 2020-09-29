package com.jaoafa.MyMaid3.Command;

import com.google.common.collect.Sets;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cmd_ConvLoc extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    Pattern LOC_PATTERN = Pattern.compile("^(~?)(-?)([0-9]+)$");
    Pattern SELECTOR_PATTERN = Pattern.compile("^@[praes]\\[.*?]$");
    Pattern XYZ_SELECTOR_PATTERN = Pattern.compile("([xyz])=([~\\-0-9]+)");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }

        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }
        Player player = (Player) sender;

        Set<Material> materials = Sets.newHashSet(Material.values());
        materials.remove(Material.COMMAND);
        materials.remove(Material.COMMAND_CHAIN);
        materials.remove(Material.COMMAND_REPEATING);
        Block targetBlock = player.getTargetBlock(materials, 10);
        if (targetBlock == null) {
            SendMessage(sender, cmd, "対象のコマンドブロックを見てください。(1)");
            return true;
        }
        if (targetBlock.getType() != Material.COMMAND && targetBlock.getType() != Material.COMMAND_CHAIN && targetBlock.getType() != Material.COMMAND_REPEATING) {
            SendMessage(sender, cmd, "対象のコマンドブロックを見てください。(2 / " + targetBlock.getType().name() + " / " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ() + ")");
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
            if (args[0].startsWith("relative")) {
                String replaced = replaceProcess(targetBlock.getLocation(), command, true);
                if (replaced == null) replaced = command;
                SendMessage(sender, cmd, targetBlock.getWorld().getName() + " " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                SendMessage(sender, cmd, "BEFORE: " + command);
                SendMessage(sender, cmd, "AFTER : " + replaced);
                return true;
            } else if (args[0].startsWith("absolute")) {
                String replaced = replaceProcess(targetBlock.getLocation(), command, false);
                if (replaced == null) replaced = command;
                SendMessage(sender, cmd, targetBlock.getWorld().getName() + " " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                SendMessage(sender, cmd, "BEFORE: " + command);
                SendMessage(sender, cmd, "AFTER : " + replaced);
                return true;
            }
            return true;
        }
        SendUsageMessage(sender, cmd);
        return true;
    }

    @Override
    public String getDescription() {
        return "コマンドブロックのコマンドの座標指定を「絶対座標」と「相対座標」で相互変換します。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/convloc: 見ているコマンドブロックのコマンドを「相対座標」に変換します。");
                add("/convloc <relative|absolute>: 見ているコマンドブロックのコマンドを「相対座標(relative)」か「絶対座標(absolute)」のいずれかに変換します。<relative|absolute>は短縮できます。");
            }
        };
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
            for (String line : lines) {
                String sheet_baseCommand = line.split(" ")[0].trim();
                List<String> sheet_args = Arrays.asList(Arrays.copyOfRange(line.split(" "), 1, line.split(" ").length));

                boolean canEnd = false;
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
                                arg = arg.replace(xyz.group(0), xyz.group(1) + "=" + replaced);
                            } else if (selector_key.equalsIgnoreCase("y")) {
                                String replaced = toRelative ? toRelative(selector_value, loc.getBlockY()) : toAbsolute(selector_value, loc.getBlockY());
                                arg = arg.replace(xyz.group(0), xyz.group(1) + "=" + replaced);
                            } else if (selector_key.equalsIgnoreCase("z")) {
                                String replaced = toRelative ? toRelative(selector_value, loc.getBlockZ()) : toAbsolute(selector_value, loc.getBlockZ());
                                arg = arg.replace(xyz.group(0), xyz.group(1) + "=" + replaced);
                            }
                        }
                    }
                    if (!baseCommand.equalsIgnoreCase(sheet_baseCommand)) {
                        new_args.add(arg);
                        continue;
                    }
                    canEnd = true;
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
                if (canEnd) return _baseCommand + " " + String.join(" ", new_args);
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
