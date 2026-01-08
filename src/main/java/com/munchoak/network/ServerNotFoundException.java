package com.munchoak.network;

public class ServerNotFoundException extends Exception {
    public ServerNotFoundException() {
        super("No LAN server found.");
    }
}