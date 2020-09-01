package com.jaoafa.MyMaid3.Lib;

import com.jaoafa.MyMaid3.Main;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TeleportAlias {
    static Map<String, String> alias = new HashMap<String, String>();

    public static boolean setAlias(String target, String replacement) {
        try {
            Load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (alias.containsKey(target)) {
            return false;
        }
        alias.put(target, replacement);
        try {
            Save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean removeAlias(String target) {
        try {
            Load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!alias.containsKey(target)) {
            return false;
        }
        alias.remove(target);
        try {
            Save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getReplaceAlias(String target) {
        try {
            Load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (alias.containsKey(target)) {
            return alias.get(target);
        }
        return null;
    }

    public static Map<String, String> getAlias() {
        try {
            Load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alias;
    }

    public static void Save() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("data", new JSONObject(alias));
        FileWriter fw = new FileWriter(new File(Main.getJavaPlugin().getDataFolder(), "teleportAlias.json"));
        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
        pw.write(obj.toString());
        pw.close();
        fw.close();
    }

    public static void Load() throws IOException {
        FileReader fr = new FileReader(new File(Main.getJavaPlugin().getDataFolder(), "teleportAlias.json"));
        BufferedReader br = new BufferedReader(fr);
        String str = "";
        String data;
        while ((data = br.readLine()) != null) {
            str += data;
        }
        JSONObject obj = new JSONObject(str);
        if (obj.has("data")) {
            alias = toMap(obj.getJSONObject("data"));
        }
        br.close();
        fr.close();
    }

    static Map<String, String> toMap(JSONObject json) {
        Map<String, String> tmp = new HashMap<String, String>();
        for (String target : json.keySet()) {
            tmp.put(target, json.getString(target));
        }
        return tmp;
    }
}
