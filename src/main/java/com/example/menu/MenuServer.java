package com.example.menu;

import com.example.manager.FileStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.net.InetAddress;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

public class MenuServer {

    private static final int PORT = 8080;

    // Store clients safely
    private static final List<ClientWrapper> clients =
            Collections.synchronizedList(new ArrayList<>());

    // Cached data for new joiners
    private static byte[] latestMenu = null;
    private static String latestMenuName = "menu.dat";
    private static final Map<String, byte[]> latestImages = new HashMap<>();
    private static byte[] latestUserFile = null;
    private static byte[] latestReservationFile = null;
    private static byte[] latestReservationStatusFile = null;
    private static String latestReservationStatusName = "reservation_status.dat";
    private static byte[] latestMessageFile = null;
    private static String latestMessageFileName = "messages.dat";


    private static String serverIP = "0.0.0.0"; // default bind

    public static void setServerIP(String ip) {
        serverIP = ip;
    }

    public static String getServerIP() {
        return serverIP;
    }

    public static void main(String[] args) {
        try  {
            if (args.length > 0) {
                setServerIP(args[0]);
            }

            ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(serverIP));
            System.out.println("Menu Server running on port " + PORT);

            while (true) {
                Socket socket = server.accept();
                ClientWrapper cw = new ClientWrapper(socket);
                clients.add(cw);

                System.out.println("Client connected.");

                sendInitialSync(cw);

                new Thread(() -> handleClient(cw), "ClientHandler").start();
            }

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // --------------------------
    // SEND INITIAL DATA TO LATE JOINERS
    // --------------------------
    private static void sendInitialSync(ClientWrapper cw) {
        try {

            if (latestMenu != null) {
                sendSafe(cw, () -> {
                    cw.out.writeUTF("UPDATE_MENU");
                    cw.out.writeUTF(latestMenuName);
                    cw.out.writeInt(latestMenu.length);
                    cw.out.write(latestMenu);
                });
            }

            for (var entry : latestImages.entrySet()) {
                sendSafe(cw, () -> {
                    cw.out.writeUTF("UPDATE_IMAGE");
                    cw.out.writeUTF(entry.getKey());
                    cw.out.writeInt(entry.getValue().length);
                    cw.out.write(entry.getValue());
                });
            }

            if (latestUserFile != null) {
                sendSafe(cw, () -> {
                    cw.out.writeUTF("UPDATE_USERFILE");
                    cw.out.writeUTF("users.dat");
                    cw.out.writeInt(latestUserFile.length);
                    cw.out.write(latestUserFile);
                });
            }
            if (latestReservationFile != null) {
                sendSafe(cw, () -> {
                    cw.out.writeUTF("UPDATE_RESERVATIONS");
                    cw.out.writeUTF("reservations.dat");
                    cw.out.writeInt(latestReservationFile.length);
                    cw.out.write(latestReservationFile);
                });
            }
            if (latestReservationStatusFile != null) {
                sendSafe(cw, () -> {
                    cw.out.writeUTF("UPDATE_RESERVATION_STATUS");
                    cw.out.writeUTF(latestReservationStatusName);
                    cw.out.writeInt(latestReservationStatusFile.length);
                    cw.out.write(latestReservationStatusFile);
                });
            }
            if (latestMessageFile != null) {
                sendSafe(cw, () -> {
                    cw.out.writeUTF("UPDATE_MESSAGES");
                    cw.out.writeUTF(latestMessageFileName);
                    cw.out.writeInt(latestMessageFile.length);
                    cw.out.write(latestMessageFile);
                });
            }




        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // --------------------------
    // SAFE WRITING (THE REAL FIX)
    // --------------------------
    private static void sendSafe(ClientWrapper cw, IOBlock block) {
        synchronized (cw.out) {
            try {
                block.run();
                cw.out.flush();
            } catch (Exception ignored) {
            }
        }
    }

    private interface IOBlock {
        void run() throws Exception;
    }

    // --------------------------
    // HANDLE CLIENT INPUT
    // --------------------------
    private static void handleClient(ClientWrapper cw) {
        try {

            while (true) {

                String cmd = cw.in.readUTF();

                // ----- UPDATE MENU -----
                if (cmd.equals("UPDATE_MENU")) {

                    String filename = cw.in.readUTF();
                    int size = cw.in.readInt();

                    byte[] data = new byte[size];
                    cw.in.readFully(data);

                    latestMenuName = filename;
                    latestMenu = data;

                    File menuDir = new File("src/main/resources/com/example/manager/data/");
                    menuDir.mkdirs();
                    write(new File(menuDir, filename).toPath(), data);

                    broadcastMenu(filename, data, cw);
                }

                // ----- UPDATE IMAGE -----
                else if (cmd.equals("UPDATE_IMAGE")) {

                    String filename = cw.in.readUTF();
                    int size = cw.in.readInt();

                    byte[] img = new byte[size];
                    cw.in.readFully(img);

                    File imgDir = new File("src/main/resources/com/example/manager/images/");
                    imgDir.mkdirs();
                    write(new File(imgDir, filename).toPath(), img);

                    latestImages.put(filename, img);

                    broadcastImage(filename, img, cw);
                }

                // ----- REGISTER USER -----
                else if (cmd.equals("REGISTER_USER")) {

                    String username = cw.in.readUTF();
                    String email = cw.in.readUTF();
                    String contactNo = cw.in.readUTF();
                    String pwd = cw.in.readUTF();

                    File userFile = new File("src/main/resources/com/example/manager/data/users.dat");
                    userFile.getParentFile().mkdirs();
                    userFile.createNewFile();

                    FileStorage.appendUser(username, email,contactNo, pwd);

                    byte[] fullUserData = readAllBytes(userFile.toPath());
                    latestUserFile = fullUserData;

                    broadcastUserFile("users.dat", fullUserData);

                    sendSafe(cw, () -> {
                        cw.out.writeUTF("REGISTER_RESPONSE");
                        cw.out.writeBoolean(true);
                        cw.out.writeUTF("Registration successful.");
                    });
                }
                else if (cmd.equals("UPDATE_RESERVATIONS")) {

                    String filename = cw.in.readUTF();
                    int size = cw.in.readInt();

                    byte[] data = new byte[size];
                    cw.in.readFully(data);

                    File dir = new File("src/main/resources/com/example/manager/data/");
                    dir.mkdirs();

                    write(new File(dir, filename).toPath(), data);

                    latestReservationFile = data;

                    broadcastReservations(filename, data);
                }
                else if (cmd.equals("UPDATE_RESERVATION_STATUS")) {

                    String filename = cw.in.readUTF();
                    int size = cw.in.readInt();

                    byte[] data = new byte[size];
                    cw.in.readFully(data);

                    File dir = new File("src/main/resources/com/example/manager/data/");
                    dir.mkdirs();

                    write(new File(dir, filename).toPath(), data);

                    // ✅ CACHE IT FOR LATE JOINERS
                    latestReservationStatusFile = data;
                    latestReservationStatusName = filename;
                    broadcastReservationStatus(filename, data);
                }
                else if (cmd.equals("UPDATE_MESSAGES")) {

                    String filename = cw.in.readUTF();
                    int size = cw.in.readInt();

                    byte[] data = new byte[size];
                    cw.in.readFully(data);

                    File dir = new File("src/main/resources/com/example/manager/data/");
                    dir.mkdirs();

                    write(new File(dir, filename).toPath(), data);

                    // ✅ cache for late joiners
                    latestMessageFile = data;
                    latestMessageFileName = filename;

                    broadcastMessages(filename, data);
                }



            }

        } catch (Exception e) {
            System.out.println("Client disconnected.");
        } finally {
            clients.remove(cw);
        }
    }

    // --------------------------
    // BROADCAST HELPERS
    // --------------------------
    private static void broadcastMenu(String filename, byte[] data, ClientWrapper exclude) {
        for (ClientWrapper cw : clients) {
            if (cw == exclude) continue;

            sendSafe(cw, () -> {
                cw.out.writeUTF("UPDATE_MENU");
                cw.out.writeUTF(filename);
                cw.out.writeInt(data.length);
                cw.out.write(data);
            });
        }
    }
    private static void broadcastReservationStatus(String filename, byte[] data) {
        for (ClientWrapper cw : clients) {
            sendSafe(cw, () -> {
                cw.out.writeUTF("UPDATE_RESERVATION_STATUS");
                cw.out.writeUTF(filename);
                cw.out.writeInt(data.length);
                cw.out.write(data);
            });
        }
    }

    private static void broadcastReservations(String filename, byte[] data) {
        for (ClientWrapper cw : clients) {
            sendSafe(cw, () -> {
                cw.out.writeUTF("UPDATE_RESERVATIONS");
                cw.out.writeUTF(filename);
                cw.out.writeInt(data.length);
                cw.out.write(data);
            });
        }
    }
    private static void broadcastMessages(String filename, byte[] data) {
        for (ClientWrapper cw : clients) {
            sendSafe(cw, () -> {
                cw.out.writeUTF("UPDATE_MESSAGES");
                cw.out.writeUTF(filename);
                cw.out.writeInt(data.length);
                cw.out.write(data);
            });
        }
    }

    private static void broadcastImage(String filename, byte[] data, ClientWrapper exclude) {
        for (ClientWrapper cw : clients) {
            if (cw == exclude) continue;

            sendSafe(cw, () -> {
                cw.out.writeUTF("UPDATE_IMAGE");
                cw.out.writeUTF(filename);
                cw.out.writeInt(data.length);
                cw.out.write(data);
            });
        }
    }

    private static void broadcastUserFile(String filename, byte[] data) {
        for (ClientWrapper cw : clients) {

            sendSafe(cw, () -> {
                cw.out.writeUTF("UPDATE_USERFILE");
                cw.out.writeUTF(filename);
                cw.out.writeInt(data.length);
                cw.out.write(data);
            });
        }
    }

    // --------------------------
    // CLIENT WRAPPER CLASS
    // --------------------------
    private static class ClientWrapper {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        ClientWrapper(Socket s) throws Exception {
            socket = s;
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
        }
    }
}
