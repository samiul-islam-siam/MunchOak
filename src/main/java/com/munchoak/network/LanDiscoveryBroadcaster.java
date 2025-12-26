package com.munchoak.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * Broadcasts the server IP + ports on the LAN so clients can auto-discover it.
 *
 * Message format:
 *   MUNCHOAK|<ip>|<menuPort>|<chatPort>
 *
 * Clients should listen on UDP DISCOVERY_PORT and parse the message.
 */
public final class LanDiscoveryBroadcaster {
    private LanDiscoveryBroadcaster() {}

    // UDP discovery port (choose a port not used by anything else)
    public static final int DISCOVERY_PORT = 5051;

    // Broadcast interval
    private static final int INTERVAL_MS = 1500;

    public static void start(int menuPort, int chatPort) {
        Thread t = new Thread(() -> run(menuPort, chatPort), "LanDiscoveryBroadcaster");
        t.setDaemon(true);
        t.start();
    }

    private static void run(int menuPort, int chatPort) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            while (true) {
                String ip = getLanIPv4();
                String msg = "MUNCHOAK|" + ip + "|" + menuPort + "|" + chatPort;

                byte[] data = msg.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(
                        data,
                        data.length,
                        InetAddress.getByName("255.255.255.255"),
                        DISCOVERY_PORT
                );

                socket.send(packet);

                // Optional log
                // System.out.println("[Discovery] Broadcasting: " + msg + " (UDP " + DISCOVERY_PORT + ")");

                Thread.sleep(INTERVAL_MS);
            }
        } catch (Exception e) {
            System.err.println("[Discovery] Broadcaster stopped: " + e.getMessage());
        }
    }

    /**
     * Returns a likely LAN IPv4 address (e.g., 192.168.x.x / 10.x.x.x),
     * or 127.0.0.1 if none found.
     */
    private static String getLanIPv4() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                if (!nic.isUp() || nic.isLoopback() || nic.isVirtual()) continue;

                Enumeration<java.net.InetAddress> addrs = nic.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    java.net.InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();
                        if (ip.startsWith("169.254.")) continue; // skip link-local
                        return ip;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }
}