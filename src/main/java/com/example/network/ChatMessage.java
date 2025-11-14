package com.example.network;

public class ChatMessage {
    public final String text;
    public final boolean isSelf;

    public ChatMessage(String text, boolean isSelf) {
        this.text = text;
        this.isSelf = isSelf;
    }

    // ListView sometimes tries to display "" + object
    @Override
    public String toString() {
        return text;
    }
}
