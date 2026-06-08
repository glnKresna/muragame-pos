package com.muragame.pos.model;

public abstract class Payment implements IPrintable {
    private String idPayment;
    private double uangBayar;
    private double kembalian;
    private String status;
    private String metode;

    public Payment(String idPayment, String metode) {
        this.idPayment = idPayment;
        this.metode = metode;
        this.status = "PENDING";
    }

    public String getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(String idPayment) {
        this.idPayment = idPayment;
    }

    public double getUangBayar() {
        return uangBayar;
    }

    public void setUangBayar(double uangBayar) {
        this.uangBayar = uangBayar;
    }

    public double getKembalian() {
        return kembalian;
    }

    public void setKembalian(double kembalian) {
        this.kembalian = kembalian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetode() {
        return metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public abstract boolean prosesPembayaran(double total, double bayar);
}
