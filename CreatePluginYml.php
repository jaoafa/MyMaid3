<?php
define("CommandLocation", __DIR__ . "/src/main/java/com/jaoafa/MyMaid3/Command/"); // コマンドがあるディレクトリ (/で終わらせること)
define("YmlLocation", __DIR__ . "/src/main/resources/plugin.yml"); // plugin.ymlの位置(作成先)
define("PluginName", "MyMaid3"); // プラグイン名
define("MainClass", "com.jaoafa.MyMaid3.Main"); // メインクラス
define("Version", "0.0.1"); // バージョン
define("Description", "General Plugin Version 3"); // プラグインの説明
define("PermissionPrefix", "mymaid"); // パーミッションノードの接頭辞
define("Author", "mine_book000"); // プラグインの制作者
define("Softdepend", [
    "PermissionsEx",
    "LuckPerms",
    "GeoipAPI",
    "dynmap",
    "Votifier",
    "jaoSuperAchievement",
    "MinecraftJPVoteMissFiller",
]); // 依存関係

require_once(__DIR__ . "/spyc/Spyc.php");

$data = [
    "name" => PluginName,
    "main" => MainClass,
    "version" => Version,
    "description" => Description,
    "author" => Author,
    "database" => true,
    "softdepend" => Softdepend,
    "commands" => []
];

if (file_exists(CommandLocation) && is_dir(CommandLocation)) {
    if ($dh = opendir(CommandLocation)) {
        while (($file = readdir($dh)) !== false) {
            if ($file != "." && $file != ".." && is_file(CommandLocation . $file)) {
                $java = file_get_contents(CommandLocation . $file);
                if (preg_match("/public class Cmd_(.+) implements/", $java, $m) == 0 || !isset($m[1])) {
                    echo "[" . $file . "] commandName get error\n";
                }
                $commandName = mb_strtolower($m[1]);

                if (preg_match("/getDescription\(\)[\s\S]*?\{[\s\S]*?return \"([\s\S]+?)\";[\s\S]*?\}/", $java, $m) == 0 || !isset($m[1])) {
                    echo "[" . $file . "] description get error\n";
                }
                $description = $m[1];

                if (preg_match("/getUsage\(\)[\s\S]*?\{[\s\S]*?return \"([\s\S]+?)\";[\s\S]*?\}/", $java, $m) == 0 || !isset($m[1])) {
                    echo "[" . $file . "] usage get error\n";
                }
                $usage = $m[1];

                $permission = PermissionPrefix . "." . $commandName;

                echo "[" . $file . "] Name: " . $commandName . "\n";
                echo "[" . $file . "] Description: " . $description . "\n";
                echo "[" . $file . "] Usage: " . $usage . "\n";
                echo "[" . $file . "] PermissionNode: " . $permission . "\n";

                $data["commands"][$commandName] = [
                    "description" => $description,
                    "usage" => $usage,
                    "permission" => $permission
                ];
            }
        }
        closedir($dh);
    }
} else {
    echo "CommandLocation(" . CommandLocation . ") is not found or not dir!\n";
}

$yml = Spyc::YAMLDump($data);
$yml = mb_substr($yml, 4); // 最初のセパレーター(---)削除。こんな方法じゃない方がいいのだろうけど…

file_put_contents(YmlLocation, $yml);