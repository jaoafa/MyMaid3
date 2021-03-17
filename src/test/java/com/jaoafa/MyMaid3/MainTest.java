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

package com.jaoafa.MyMaid3;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.jaoSuperAchievement2.Lib.ClassFinder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.fail;

public class MainTest {
    @Test
    public void ListenerTest() {
        // Command
        try {
            ClassFinder classFinder = new ClassFinder();
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.MyMaid3.Command")) {
                if (!clazz.getName().startsWith("com.jaoafa.MyMaid3.Command.Cmd_")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }
                String commandName = clazz.getName().substring("com.jaoafa.MyMaid3.Command.Cmd_".length())
                        .toLowerCase();
                Constructor<?> construct = clazz.getConstructor();
                Object instance = construct.newInstance();
                if (instance instanceof CommandPremise) {
                    System.out.println(clazz.getSimpleName() + " instanceof CommandPremise");
                } else {
                    System.out.println("! " + commandName + ": CommandPremise not implemented");
                    fail();
                    continue;
                }

                if (instance instanceof CommandExecutor) {
                    try {
                        CommandExecutor cmdExecutor = (CommandExecutor) instance;
                        System.out.println(clazz.getSimpleName() + " command registable");
                    } catch (ClassCastException e) {
                        // commandexecutor not implemented
                        System.out.println("! " + commandName + ": commandexecutor not implemented [1]");
                        fail();
                    }
                } else {
                    System.out.println("! " + commandName + ": commandexecutor not implemented [2]");
                    fail();
                }
            }
        } catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
            e.printStackTrace();
            return;
        }

        // Event
        try {
            ClassFinder classFinder = new ClassFinder();
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.MyMaid3.Event")) {
                if (!clazz.getName().startsWith("com.jaoafa.MyMaid3.Event.Event_")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }

                Constructor<?> construct = clazz.getConstructor();
                Object instance = construct.newInstance();

                if (instance instanceof Listener) {
                    try {
                        Listener listener = (Listener) instance;
                        System.out.println(clazz.getSimpleName() + " event registable");
                    } catch (ClassCastException e) {
                        // commandexecutor not implemented
                        System.out.println("! " + clazz.getSimpleName() + ": Listener not implemented [1]");
                        fail();
                    }
                } else {
                    System.out.println("! " + clazz.getSimpleName() + ": Listener not implemented [2]");
                    fail();
                }
            }
        } catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
            e.printStackTrace();
            fail();
        }
    }
}
