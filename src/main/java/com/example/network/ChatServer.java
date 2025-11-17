package com.example.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChatServer {
    private static final int PORT = 5050;
    private static final String HISTORY_DIR = "src/main/resources/com/example/network/Chats";

    private static final Map<String, ClientHandler> users = new ConcurrentHashMap<>();
    private static final Set<ClientHandler> admins = ConcurrentHashMap.newKeySet();

    /*-------------------- main method --------------------*/
    // Thread per client method
    public static void main(String[] args) {
        ensureHistoryDir();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server running on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler ch = new ClientHandler(socket);
                new Thread(ch, "ClientHandler-" + socket.getRemoteSocketAddress()).start();
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    /*-------------------- History Utils --------------------*/
    // History directory
    private static void ensureHistoryDir() {
        File dir = new File(HISTORY_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("⚠️ Could not create history dir: " + HISTORY_DIR);
        }
    }

    // Separate file for each user
    private static File historyFileFor(String username) {
        return new File(HISTORY_DIR, username + ".dat");
    }

    private static void appendHistory(String username, String role, String text) {
        File appendFile = historyFileFor(username);
        // dos = Data Output Stream
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(appendFile, true)))) {

            dos.writeUTF(role); // "USER" or "ADMIN"
            dos.writeUTF(text); // content
            dos.writeLong(System.currentTimeMillis()); // timestamp
        } catch (IOException e) {
            System.err.println("Error saving history for " + username + ": " + e.getMessage());
        }
    }

    private static List<String> readHistory(String username) {
        File readFile = historyFileFor(username);
        if (!readFile.exists()) return Collections.emptyList();

        List<String> lines = new ArrayList<>();
        // dis = Data Input Stream
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(readFile)))) {

            while (true) {
                try {
                    String role = dis.readUTF();
                    String text = dis.readUTF();

                    lines.add(role + "|" + text); // message: ROLE|TEXT
                } catch (EOFException eof) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading history for " + username + ": " + e.getMessage());
        }
        return lines;
    }

    // A comma-separated list of all current online users
    private static void notifyAdminsUserList() {
        String list = users.keySet().stream().sorted().collect(Collectors.joining(","));
        // Sends the list to every admin
        for (ClientHandler admin : admins) {
            admin.send("USER_LIST|" + list);
        }
    }

    /* ---------------- ClientHandler ---------------- */
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        private String username;
        private boolean isAdmin;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                /*-------------------- Authentication --------------------*/
                // First message: AUTH|username|role
                String auth = in.readLine();
                if (auth == null || !auth.startsWith("AUTH|")) {
                    send("AUTH_ERR|Missing AUTH");
                    close();
                    return;
                }

                String[] parts = auth.split("\\|", 3);
                if (parts.length < 3) {
                    send("AUTH_ERR|Bad AUTH format");
                    close();
                    return;
                }

                username = parts[1].trim();
                String role = parts[2].trim().toUpperCase(Locale.ROOT);
                isAdmin = "ADMIN".equals(role);

                if (!isAdmin) {
                    ClientHandler existing = users.putIfAbsent(username, this);
                    if (existing != null) {
                        send("AUTH_ERR|Username already in use"); // username must be unique
                        close();
                        return;
                    }
                } else {
                    admins.add(this);
                }

                send("AUTH_OK");
                System.out.println("✅ " + (isAdmin ? "ADMIN" : "USER") + " connected: " + username);

                if (!isAdmin) {
                    // Send user history
                    for (String line : readHistory(username)) {
                        String[] p = line.split("\\|", 2);
                        if (p.length == 2) {
                            send("INCOMING|" + p[0] + "|" + p[1]); // INCOMING|ROLE|message
                        }
                    }
                }

                if (isAdmin) {
                    notifyAdminsUserList(); // updated current online user list
                } else {
                    for (ClientHandler admin : admins) {
                        admin.send("SYS|🟢 " + username + " joined");
                    }
                    notifyAdminsUserList();
                }

                /*-------------------- Message Handling --------------------*/
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("BYE")) break;

                    if (line.startsWith("MSG|")) {
                        handleUserMessageToAdmin(line.substring(4)); // User to Admin
                    } else if (line.startsWith("MSGTO|")) {
                        if (!isAdmin) {
                            send("SYS|Only admin can send MSGTO");
                            continue;
                        }
                        String[] p = line.split("\\|", 3);
                        if (p.length < 3) {
                            send("SYS|Bad MSGTO format");
                            continue;
                        }
                        handleAdminMessageToUser(p[1].trim(), p[2]); // Admin to User
                    } else if (line.startsWith("GETHIST|")) {
                        if (!isAdmin) {
                            send("SYS|Only admin can request history");
                            continue;
                        }
                        String[] p = line.split("\\|", 2);
                        if (p.length < 2) {
                            send("SYS|Bad GETHIST format");
                            continue;
                        }
                        sendHistoryToAdmin(p[1].trim()); // When admin request history
                    } else {
                        send("SYS|Unknown command");
                    }
                }
            } catch (IOException ignored) {
            } finally {
                close();
            }
        }

        /*-------------------- Message Handling Utils --------------------*/
        private void handleUserMessageToAdmin(String text) {
            appendHistory(username, "USER", text);
            if (admins.isEmpty()) {
                send("SYS|No admin online");
            } else {
                admins.forEach(admin -> admin.send("INCOMING|" + username + "|" + text));
            }
        }

        private void handleAdminMessageToUser(String targetUser, String text) {
            ClientHandler target = users.get(targetUser);
            if (target == null) {
                send("SYS|User not online: " + targetUser);
                return;
            }
            appendHistory(targetUser, "ADMIN", text);
            target.send("INCOMING|ADMIN|" + text);
        }

        private void sendHistoryToAdmin(String targetUser) {
            List<String> lines = readHistory(targetUser);
            for (String line : lines) {
                String[] p = line.split("\\|", 2);
                if (p.length == 2) {
                    send("HIST|" + targetUser + "|" + p[0] + "|" + p[1]);
                }
            }
            send("HIST_END|" + targetUser);
        }

        private void send(String msg) {
            if (out != null) out.println(msg);
        }

        /*-------------------- close method --------------------*/
        private void close() {
            try {
                if (!isAdmin && username != null) {
                    users.remove(username, this);
                    admins.forEach(admin -> admin.send("SYS|🔴 " + username + " left"));
                    notifyAdminsUserList();
                }
                if (isAdmin) admins.remove(this);
                if (socket != null && !socket.isClosed()) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException ignored) {
            }
        }
    }
}
