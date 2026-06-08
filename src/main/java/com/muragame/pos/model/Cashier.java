package com.muragame.pos.model;

public class Cashier {
    private String idKasir;
    private String namaKasir;
    private String password;
    private String shift;
    private boolean isActive;

    public Cashier(String idKasir, String namaKasir, String password, String shift) {
        this.idKasir = idKasir;
        this.namaKasir = namaKasir;
        this.password = password;
        this.shift = shift;
        this.isActive = false;
    }

    public String getIdKasir() {
        return idKasir;
    }

    public void setIdKasir(String idKasir) {
        this.idKasir = idKasir;
    }

    public String getNamaKasir() {
        return namaKasir;
    }

    public void setNamaKasir(String namaKasir) {
        this.namaKasir = namaKasir;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean login(String pass) {
        if (this.password.equals(pass)) {
            this.isActive = true;
            return true;
        }
        return false;
    }

    public void logout() {
        this.isActive = false;
    }
}
