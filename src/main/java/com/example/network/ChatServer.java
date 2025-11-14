package com.example.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Private chats: USER <-> ADMIN
 * - Users cannot see other users.
 * - Admin can see all users, select one, and reply privately.
 * - Per-user chat histories stored under resources/chat_history/<username>.txt
 */
public class ChatServer {
    private static final int PORT = 5050;

    // Store histories under resources path
    private static final String HISTORY_DIR = "src/main/resources/com/example/network/chat_history";

    // Active connections
    private static final Map<String, ClientHandler> users = new ConcurrentHashMap<>();  // username -> handler
    private static final Set<ClientHandler> admins = ConcurrentHashMap.newKeySet();

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
            e.printStackTrace();
        }
    }

    /* ---------------- Utilities ---------------- */

    private static void ensureHistoryDir() {
        File dir = new File(HISTORY_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("⚠️ Could not create history dir: " + HISTORY_DIR);
        }
    }

    private static File historyFileFor(String username) {
        return new File(HISTORY_DIR, username + ".txt");
    }

    private static void appendHistory(String username, String line) {
        File f = historyFileFor(username);
        try (FileWriter fw = new FileWriter(f, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("⚠️ Error saving history for " + username + ": " + e.getMessage());
        }
    }

    private static List<String> readHistory(String username) {
        File f = historyFileFor(username);
        if (!f.exists()) return Collections.emptyList();
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String ln;
            while ((ln = br.readLine()) != null) lines.add(ln);
        } catch (IOException e) {
            System.err.println("⚠️ Error reading history for " + username + ": " + e.getMessage());
        }
        return lines;
    }

    private static void notifyAdminsUserList() {
        String list = String.join(",", users.keySet().stream().sorted().collect(Collectors.toList()));
        for (ClientHandler admin : admins) {
            admin.send("USER_LIST|" + list);
        }
    }

    /* ---------------- Client Handler ---------------- */

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
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Expect AUTH|<username>|<role>
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

                // Enforce unique usernames for users
                if (!isAdmin) {
                    ClientHandler existing = users.putIfAbsent(username, this);
                    if (existing != null) {
                        send("AUTH_ERR|Username already in use");
                        close();
                        return;
                    }
                } else {
                    admins.add(this);
                }

                send("AUTH_OK");
                System.out.println("✅ " + (isAdmin ? "ADMIN" : "USER") + " connected: " + username);

                // For users: send their own history
                if (!isAdmin) {
                    for (String line : readHistory(username)) {
                        send("HIST|" + username + "|" + line);
                    }
                    send("HIST_END|" + username);
                }

                // For admins: send initial user list
                if (isAdmin) {
                    notifyAdminsUserList();
                } else {
                    // Tell admins that a user joined
                    for (ClientHandler admin : admins) {
                        admin.send("SYS|🟢 " + username + " joined");
                    }
                    notifyAdminsUserList();
                }

                // Main loop
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("BYE")) break;

                    if (line.startsWith("MSG|")) {
                        // USER -> ADMIN(s)
                        String text = line.substring(4);
                        handleUserMessageToAdmin(text);

                    } else if (line.startsWith("MSGTO|")) {
                        // ADMIN -> specific USER
                        if (!isAdmin) {
                            send("SYS|Only admin can send MSGTO");
                            continue;
                        }
                        String[] p = line.split("\\|", 3);
                        if (p.length < 3) {
                            send("SYS|Bad MSGTO format");
                            continue;
                        }
                        handleAdminMessageToUser(p[1].trim(), p[2]);

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
                        String target = p[1].trim();
                        sendHistoryToAdmin(target);

                    } else {
                        send("SYS|Unknown command");
                    }
                }
            } catch (IOException e) {
                // ignore, will be handled in finally
            } finally {
                close();
            }
        }

        private void handleUserMessageToAdmin(String text) {
            String display = username + ": " + text;

            // Save in user's history
            appendHistory(username, display);

            // Deliver only to admins
            if (admins.isEmpty()) {
                send("SYS|No admin online");
            } else {
                for (ClientHandler admin : admins) {
                    admin.send("INCOMING|" + username + "|" + text);
                }
            }
        }


        private void handleAdminMessageToUser(String targetUser, String text) {
            ClientHandler target = users.get(targetUser);
            if (target == null) {
                send("SYS|User not online: " + targetUser);
                return;
            }

            // Use the admin's actual username so the user sees who sent the message
            String adminName = this.username != null ? this.username : "ADMIN";
            String display = adminName + ": " + text;

            // Save in the target user's history
            appendHistory(targetUser, display);

            // Deliver to target user
            // Send INCOMING|<from>|<text> so clients can parse properly
            target.send("INCOMING|" + adminName + "|" + text);
        }


        private void sendHistoryToAdmin(String targetUser) {
            for (String line : readHistory(targetUser)) {
                send("HIST|" + targetUser + "|" + line);
            }
            send("HIST_END|" + targetUser);
        }

        private void send(String msg) {
            if (out != null) out.println(msg);
        }

        private void close() {
            try {
                if (!isAdmin && username != null) {
                    users.remove(username, this);
                    for (ClientHandler admin : admins) {
                        admin.send("SYS|🔴 " + username + " left");
                    }
                    notifyAdminsUserList();
                }
                if (isAdmin) {
                    admins.remove(this);
                }
                if (socket != null && !socket.isClosed()) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException ignored) {}
        }
    }
}
