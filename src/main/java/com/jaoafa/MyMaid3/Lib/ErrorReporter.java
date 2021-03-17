package com.jaoafa.MyMaid3.Lib;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class ErrorReporter {
    public static void report(Throwable exception) {
        exception.printStackTrace();
        if (MyMaidConfig.getReportChannel() == null) {
            System.out.println("Main.ReportChannel == null error.");
            return;
        }
        if (MyMaidConfig.getJDA() == null) {
            System.out.println("Main.getClient() == null error.");
            return;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("MyMaid3 Discord Error Reporter");
            builder.setColor(Color.RED);
            builder.addField("StackTrace", "```" + sw.toString() + "```", false);
            builder.addField("Message", "```" + exception.getMessage() + "```", false);
            builder.addField("Cause", "```" + exception.getCause() + "```", false);
            builder.setTimestamp(Instant.now());
            MyMaidConfig.getReportChannel().sendMessage(builder.build()).queue();
        } catch (Exception e) {
            String text = "MyMaid3 Discord Error Reporter (" + MyMaidLibrary.sdfFormat(new Date()) + ")\n"
                    + "---------- StackTrace ----------\n"
                    + sw.toString() + "\n"
                    + "---------- Message ----------\n"
                    + exception.getMessage() + "\n"
                    + "---------- Cause ----------\n"
                    + exception.getCause();
            InputStream stream = new ByteArrayInputStream(
                    text.getBytes(StandardCharsets.UTF_8));
            MyMaidConfig.getReportChannel().sendFile(stream, "Mainreport" + System.currentTimeMillis() + ".txt").queue();
        }

    }
}
