package com.muragame.pos.model.payment;

public class CashPayment extends Payment {
    private double cashReceived;

    public CashPayment(double totalAmount, double cashReceived) {
        super("CASH", totalAmount);
        this.cashReceived = cashReceived;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public double getChange() {
        return cashReceived - getTotalAmount();
    }

    @Override
    public boolean isSuccess() {
        return cashReceived >= getTotalAmount();
    }
}
