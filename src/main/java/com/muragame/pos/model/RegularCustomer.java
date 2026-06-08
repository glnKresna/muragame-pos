package com.muragame.pos.model;

public class RegularCustomer extends Customer implements IDiscountable {
    public RegularCustomer(String idCustomer, String namaPemesan) {
        super(idCustomer, namaPemesan);
    }

    @Override
    public double hitungDiskon(double subTotal) {
        return 0.0;
    }

    @Override
    public String getType() {
        return "Regular";
    }

    @Override
    public double getDiscountRate() {
        return 0.0;
    }
}
