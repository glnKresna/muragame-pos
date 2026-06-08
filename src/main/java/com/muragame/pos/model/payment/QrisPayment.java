package com.muragame.pos.model.payment;

import java.util.UUID;

public class QrisPayment extends Payment {
    private String transactionId;

    public QrisPayment(double totalAmount) {
        super("QRIS", totalAmount);
        this.transactionId = "QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean isSuccess() {
        return true; // Simulating successful digital QRIS transaction
    }
}
