package com.jaoafa.MyMaid3.HttpServer;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MyMaidServer extends BukkitRunnable {
    @Override
    public void run() {
        int port = 31001;
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", exc -> {
                final String response = "{\"status\":true,\"message\":\"Hello world.\"}";
                exc.sendResponseHeaders(200, response.length());
                OutputStream os = exc.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.createContext("/votefill", new Http_VoteFill());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
