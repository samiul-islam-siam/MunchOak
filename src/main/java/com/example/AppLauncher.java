package com.example;

import com.example.menu.MenuServer;
import com.example.munchoak.Home;
import com.example.network.ChatServer;

import java.io.IOException;
import java.net.Socket;

public class AppLauncher {

    public static void main(String[] args) {

        boolean isMenuServerRunning = isPortInUse(MenuServer.getServerIP(), 8080);
        boolean isChatServerRunning = isPortInUse(MenuServer.getServerIP(), 5050);

        if (!isMenuServerRunning && !isChatServerRunning) {
            System.out.println("Both servers are not running. Launching both...");
            startServer(MenuServer.class, "MenuServer");
            startServer(ChatServer.class, "ChatServer");

        } else {
            if (!isMenuServerRunning) {
                System.out.println("Menu server not running. Launching...");
                startServer(MenuServer.class, "MenuServer");
            }

            if (!isChatServerRunning) {
                System.out.println("Chat server not running. Launching...");
                startServer(ChatServer.class, "ChatServer");
            }

            if (isMenuServerRunning && isChatServerRunning) {
                System.out.println("Servers already running. Launching Home only...");
            }
        }

        // Home UI
        Home.main(args);
    }


    private static boolean isPortInUse(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;  // Connection successful → server running
        } catch (IOException e) {
            return false; // Cannot connect → server not running
        }
    }

    private static void startServer(Class<?> serverClass, String name) {
        Thread thread = new Thread(() -> {
            try {
                serverClass.getMethod("main", String[].class)
                        .invoke(null, (Object) new String[]{});
            } catch (Exception e) {
                System.err.println("IOException: " + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.setName(name + "-Thread");
        thread.start();
    }
}
