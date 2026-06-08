package com.muragame.pos.model;

public abstract class Layanan {
    private String idLayanan;
    private String tipeLayanan;
    private double biayaLayanan;

    public Layanan(String idLayanan, String tipeLayanan, double biayaLayanan) {
        this.idLayanan = idLayanan;
        this.tipeLayanan = tipeLayanan;
        this.biayaLayanan = biayaLayanan;
    }

    public String getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(String idLayanan) {
        this.idLayanan = idLayanan;
    }

    public String getTipeLayanan() {
        return tipeLayanan;
    }

    public void setTipeLayanan(String tipeLayanan) {
        this.tipeLayanan = tipeLayanan;
    }

    public double getBiayaLayanan() {
        return biayaLayanan;
    }

    public void setBiayaLayanan(double biayaLayanan) {
        this.biayaLayanan = biayaLayanan;
    }
}
