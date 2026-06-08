package com.muragame.pos.model.payment;

public abstract class Payment {
    private String method;
    private double totalAmount;

    public Payment(String method, double totalAmount) {
        this.method = method;
        this.totalAmount = totalAmount;
    }

    public String getMethod() {
        return method;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public abstract boolean isSuccess();
}
