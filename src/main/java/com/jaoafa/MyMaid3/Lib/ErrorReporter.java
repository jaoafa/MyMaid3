package com.jaoafa.MyMaid3.Lib;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.jaoafa.MyMaid3.Main;

import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class ErrorReporter {
	public static void report(Throwable exception) {
		exception.printStackTrace();
		if (Main.ReportChannel == null) {
			System.out.println("Main.ReportChannel == null error.");
			return;
		}
		if (Main.getDiscordClient() == null) {
			System.out.println("Main.getClient() == null error.");
			return;
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);

		try {
			EmbedBuilder builder = new EmbedBuilder();
			builder.withTitle("MyMaid3 Discord Error Reporter");
			builder.withColor(Color.RED);
			builder.appendField("StackTrace", "```" + sw.toString() + "```", false);
			builder.appendField("Message", "```" + exception.getMessage() + "```", false);
			builder.appendField("Cause", "```" + exception.getCause() + "```", false);
			builder.withTimestamp(System.currentTimeMillis());
			RequestBuffer.request(() -> {
				try {
					Main.ReportChannel.sendMessage(builder.build());
				} catch (DiscordException discordexception) {
					Main.DiscordExceptionError(ErrorReporter.class, Main.ReportChannel, discordexception);
				}
			});
		} catch (Exception e) {
			try {
				String text = "MyMaid3 Discord Error Reporter (" + MyMaidLibrary.sdfFormat(new Date()) + ")\n"
						+ "---------- StackTrace ----------\n"
						+ sw.toString() + "\n"
						+ "---------- Message ----------\n"
						+ exception.getMessage() + "\n"
						+ "---------- Cause ----------\n"
						+ exception.getCause();
				InputStream stream = new ByteArrayInputStream(
						text.getBytes("utf-8"));
				RequestBuffer.request(() -> {
					try {
						Main.ReportChannel.sendFile("MyMaid3 Discord Error Reporter", stream,
								"Mainreport" + System.currentTimeMillis() + ".txt");
					} catch (DiscordException discordexception) {
						Main.DiscordExceptionError(ErrorReporter.class, Main.ReportChannel,
								discordexception);
					}
				});
			} catch (UnsupportedEncodingException ex) {
				return;
			}
		}

	}
}
