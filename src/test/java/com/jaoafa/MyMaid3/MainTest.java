package com.jaoafa.MyMaid3;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.junit.Test;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.jaoSuperAchievement2.Lib.ClassFinder;

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
						@SuppressWarnings("unused")
						CommandExecutor cmdExecutor = (CommandExecutor) instance;
						System.out.println(clazz.getSimpleName() + " command registable");
					} catch (ClassCastException e) {
						// commandexecutor not implemented
						System.out.println("! " + commandName + ": commandexecutor not implemented [1]");
						fail();
						continue;
					}
				} else {
					System.out.println("! " + commandName + ": commandexecutor not implemented [2]");
					fail();
					continue;
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

				Constructor<?> construct = (Constructor<?>) clazz.getConstructor();
				Object instance = construct.newInstance();

				if (instance instanceof Listener) {
					try {
						@SuppressWarnings("unused")
						Listener listener = (Listener) instance;
						System.out.println(clazz.getSimpleName() + " event registable");
					} catch (ClassCastException e) {
						// commandexecutor not implemented
						System.out.println("! " + clazz.getSimpleName() + ": Listener not implemented [1]");
						fail();
						continue;
					}
				} else {
					System.out.println("! " + clazz.getSimpleName() + ": Listener not implemented [2]");
					fail();
					continue;
				}
			}
		} catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
			e.printStackTrace();
			fail();
			return;
		}
	}
}
