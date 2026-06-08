package com.muragame.pos.model;

public abstract class Customer {
    private String idCustomer;
    private String namaPemesan;

    public Customer(String idCustomer, String namaPemesan) {
        this.idCustomer = idCustomer;
        this.namaPemesan = namaPemesan;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getNamaPemesan() {
        return namaPemesan;
    }

    public void setNamaPemesan(String namaPemesan) {
        this.namaPemesan = namaPemesan;
    }

    public abstract String getType();
    public abstract double getDiscountRate();
}
