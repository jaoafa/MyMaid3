<?php
date_default_timezone_set("Asia/Tokyo");
define("CommandLocation", __DIR__ . "/src/main/java/com/jaoafa/MyMaid3/Command/"); // コマンドがあるディレクトリ (/で終わらせること)
define("YmlLocation", __DIR__ . "/src/main/resources/plugin.yml"); // plugin.ymlの位置(作成先)
define("PluginName", "MyMaid3"); // プラグイン名
define("MainClass", "com.jaoafa.MyMaid3.Main"); // メインクラス
if (file_exists(__DIR__ . "/src/main/resources/COMMIT")) {
    $COMMIT = "_" . mb_substr(file_get_contents(__DIR__ . "/src/main/resources/COMMIT"), 0, 7);
} else {
    $COMMIT = "";
}
define("Version", date("Y.m.d_H.i") . $COMMIT); // バージョン
define("Description", "General Plugin Version 3"); // プラグインの説明
define("PermissionPrefix", "mymaid"); // パーミッションノードの接頭辞
define("Author", "mine_book000"); // プラグインの制作者
$Softdepend = [
    "PermissionsEx",
    "LuckPerms",
    "GeoipAPI",
    "dynmap",
    "Votifier",
    "jaoSuperAchievement",
    "MinecraftJPVoteMissFiller",
    "ViaVersion",
    "LunaChat"
]; // 依存関係

require_once(__DIR__ . "/spyc/Spyc.php");

$data = [
    "name" => PluginName,
    "main" => MainClass,
    "version" => Version,
    "description" => Description,
    "author" => Author,
    "database" => true,
    "softdepend" => $Softdepend,
    "commands" => []
];

if (file_exists(CommandLocation) && is_dir(CommandLocation)) {
    if ($dh = opendir(CommandLocation)) {
        while (($file = readdir($dh)) !== false) {
            if ($file != "." && $file != ".." && is_file(CommandLocation . $file)) {
                $java = file_get_contents(CommandLocation . $file);
                if (preg_match("/public class Cmd_(.+?) /", $java, $m) == 0 || !isset($m[1])) {
                    echo "[" . $file . "] commandName get error\n";
                }
                $commandName = mb_strtolower($m[1]);

                if (preg_match("/getDescription\(\)[\s\S]*?\{[\s\S]*?return \"([\s\S]+?)\";[\s\S]*?\}/", $java, $m) == 0 || !isset($m[1])) {
                    echo "[" . $file . "] description get error\n";
                }
                $description = $m[1];

                if (preg_match("/getUsage\(\)[\s\S]*?\{([\s\S]*)\}/", $java, $m) == 0 || !isset($m[1])) {
                    echo "[" . $file . "] usage get error\n";
                }
                if (preg_match_all("/add\(\"(.+?)\"\);/", $m[1], $u) == 0 || !isset($u[1])) {
                    echo "[" . $file . "] usage(add) get error\n";
                }
                $usage = "";
                foreach ($u[1] as $value) {
                    $usage .= $value . "\n";
                }
                $usage = trim($usage);

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
