package com.munchoak.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

/**
 * Client-side helper to discover the server host/ports on the LAN.
 */
public final class LanDiscoveryClient {
    private LanDiscoveryClient() {
    }

    public static final class Result {
        public final String host;
        public final int menuPort;
        public final int chatPort;

        public Result(String host, int menuPort, int chatPort) {
            this.host = host;
            this.menuPort = menuPort;
            this.chatPort = chatPort;
        }
    }

    /**
     * Listens for a single UDP broadcast from the server.
     *
     * @param timeoutMs how long to wait
     */
    public static Result discover(int timeoutMs) throws Exception {
        try (DatagramSocket socket = new DatagramSocket(LanDiscoveryBroadcaster.DISCOVERY_PORT)) {
            socket.setSoTimeout(timeoutMs);

            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

            String[] p = msg.split("\\|");
            if (p.length != 4 || !p[0].equals("MUNCHOAK")) {
                throw new IllegalArgumentException("Bad discovery message: " + msg);
            }

            return new Result(
                    p[1].trim(),
                    Integer.parseInt(p[2].trim()),
                    Integer.parseInt(p[3].trim())
            );
        }
    }

}