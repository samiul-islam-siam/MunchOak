package com.example.munchoak;

import java.time.Instant;

public class Payment {
    private static int nextId = 1;
    private int id;
    private double amount;
    private boolean success;
    private String timestamp;

    public Payment(double amount) {
        this.id = nextId++;
        this.amount = amount;
        this.timestamp = Instant.now().toString();
        this.success = false;
    }

    public void processPayment() {
        // Dummy: always success
        this.success = true;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }
}
