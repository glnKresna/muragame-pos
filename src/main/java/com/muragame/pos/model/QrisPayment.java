package com.muragame.pos.model;

import java.util.UUID;

public class QrisPayment extends Payment {
    private String transactionId;

    public QrisPayment(String idPayment) {
        super(idPayment, "QRIS");
        this.transactionId = "QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean prosesPembayaran(double total, double bayar) {
        setUangBayar(total);
        setKembalian(0.0);
        setStatus("LUNAS");
        return true;
    }

    @Override
    public void printStrukPDF() {
        System.out.println("Mencetak Struk PDF untuk Pembayaran QRIS: " + getIdPayment());
    }
}
