package com.jaoafa.MyMaid3.Lib;

import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * killコマンド等で可変するプレイヤーを指定するための仕組み、セレクターを判定するクラス<br>
 * Minecraft Version 1.12.2対応
 *
 * @author mine_book000
 */
public class SelectorParser extends MyMaidLibrary {
    /*
     * @p	最寄りのプレイヤー1人
     * @r	ランダムなプレイヤー
     * @a	全てのプレイヤー
     * @e	全てのエンティティ
     * @s	コマンドを実行しているエンティティ
     */
    List<String> selector_list = Arrays.asList("p", "r", "a", "e", "s");
    /**
     * x, y, z	座標
     * r, rm	半径（最大、最小）
     * dx, dy, dz	範囲の大きさ
     * <p>
     * score_name	最大スコア (非対応)
     * score_name_min	最小スコア (非対応)
     * tag	スコアボードのタグ
     * team	チーム名
     * <p>
     * c	数
     * l, lm	経験値レベル（最大、最小）
     * m	ゲームモード
     * name	エンティティ名
     * rx, rxm	X 軸を中心とした向き（最大、最小）
     * ry, rym	Y 軸を中心とした向き （最大、最小）
     * type	エンティティの種類
     */
    List<String> argument_list = Arrays.asList(
            "x", "y", "z",
            "r", "rm",
            "dx", "dy", "dz",
            "tag",
            "team",
            "c",
            "l", "lm",
            "m",
            "name",
            "rx", "rxm",
            "ry", "rym",
            "type"
    );


    boolean valid = true;
    String selector;
    Map<String, String> args = new HashMap<>();

    /**
     * ParseSelectorクラスの作成
     *
     * @param SelectorText セレクター
     * @throws IllegalArgumentException 指定されたセレクターが適切でなかった場合に発生します。
     * @author mine_book000
     */
    public SelectorParser(String SelectorText) throws IllegalArgumentException {
        Pattern p = Pattern.compile("^@(.)(.*)$");
        Matcher m = p.matcher(SelectorText);
        if (!m.find()) {
            valid = false;
            throw new IllegalArgumentException("セレクターテキストがセレクターとして認識できません。");
        }

        selector = m.group(1);
        if (!selector_list.contains(selector)) {
            throw new IllegalArgumentException("セレクターが認識できません。");
        }

        if (m.group(2).equals("[]")) {
            throw new IllegalArgumentException("セレクターの引数が認識できません。");
        }

        p = Pattern.compile("^\\[(.+)]$");
        m = p.matcher(m.group(2));
        if (!m.find()) {
            return;
        }

        if (m.group(1).equals("")) {
            throw new IllegalArgumentException("セレクターの引数が認識できません。");
        }

        if (!m.group(1).contains(",")) {
            String arg = m.group(1);
            if (arg.contains("=")) {
                String[] key_value = arg.split("=");
                String key = key_value[0];
                String value = key_value[1];
                this.args.put(key, value);
            } else {
                throw new IllegalArgumentException("セレクターの1番目の引数が認識できません。");
            }
            return;
        }

        String[] args = m.group(1).split(",");
        int i = 0;
        for (String arg : args) {
            if (arg.contains("=")) {
                String[] key_value = arg.split("=");
                String key = key_value[0];
                String value = key_value[1];
                this.args.put(key, value);
                i++;
            } else {
                throw new IllegalArgumentException("セレクターの" + (i + 1) + "番目の引数が認識できません。");
            }
        }
    }

    /**
     * 引数が適当であるかを調べる
     *
     * @return 引数が適当であればtrue
     */
    public boolean isValidValues() {
        if (!valid) {
            return false;
        }
        if (args.containsKey("x") && !isInt(args.get("x"))) {
            return false;
        }
        if (args.containsKey("y") && !isInt(args.get("y"))) {
            return false;
        }
        if (args.containsKey("z") && !isInt(args.get("z"))) {
            return false;
        }
        if (args.containsKey("r") && !isInt(args.get("r"))) {
            return false;
        }
        if (args.containsKey("type")) {
            boolean TypeCheck = false;
            for (EntityType type : EntityType.values()) {
                if (!"Player".equalsIgnoreCase(args.get("type"))) {
                    if (type.getName() == null) {
                        continue;
                    }
                    if (type.getName().equalsIgnoreCase(args.get("type"))) {
                        TypeCheck = true;
                    }
                    if (type.getName().equalsIgnoreCase("!" + args.get("type"))) {
                        TypeCheck = true;
                    }
                }
                if (type.getName().equalsIgnoreCase("!player")) {
                    TypeCheck = true;
                }
            }
            return TypeCheck;
        }
        return true;
    }

    /**
     * どの引数が適当でないかを返す
     *
     * @return どの引数が適当でないか
     */
    public Set<String> getInValidValues() {
        Set<String> invalid = new HashSet<>();
        if (!valid) {
            invalid.add("ALL");
        }
        if (args.containsKey("x")) {
            if (!isInt(args.get("x"))) {
                invalid.add("x");
            }
        }
        if (args.containsKey("y")) {
            if (!isInt(args.get("y"))) {
                invalid.add("y");
            }
        }
        if (args.containsKey("z")) {
            if (!isInt(args.get("z"))) {
                invalid.add("z");
            }
        }
        if (args.containsKey("r")) {
            if (!isInt(args.get("r"))) {
                invalid.add("r");
            }
        }
        if (args.containsKey("type")) {
            boolean TypeCheck = false;
            for (EntityType type : EntityType.values()) {
                if (!"Player".equals(args.get("type"))) {
                    if (type.getName() == null) {
                        continue;
                    }
                    if (type.getName().equalsIgnoreCase(args.get("type"))) {
                        TypeCheck = true;
                    }
                }
            }
            if (!TypeCheck) invalid.add("TYPE:" + args.get("type"));
        }
        return invalid;
    }

    /**
     * セレクターを取得する(@付き)
     *
     * @return セレクター
     */
    public String getSelector() {
        return "@" + selector;
    }

    /**
     * セレクター引数を取得する
     *
     * @return セレクター引数
     */
    public Map<String, String> getArgs() {
        return args;
    }
}
