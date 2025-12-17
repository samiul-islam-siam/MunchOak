package com.example;

import com.example.menu.MenuServer;
import com.example.munchoak.Home;
import com.example.network.ChatServer;

public class AppLauncher {

    public static void main(String[] args) {

        boolean isServerMachine = Boolean.getBoolean("server");

        if (isServerMachine) {
            System.out.println("Running as SERVER");
            startServer(MenuServer.class, "MenuServer");
            startServer(ChatServer.class, "ChatServer");
        }

        // CLIENT UI (always)
        Home.main(args);
    }

    private static void startServer(Class<?> serverClass, String name) {
        Thread thread = new Thread(() -> {
            try {
                serverClass.getMethod("main", String[].class)
                        .invoke(null, (Object) new String[]{});
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.setName(name + "-Thread");
        thread.start();
    }
}
