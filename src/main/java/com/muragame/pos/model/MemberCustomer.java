package com.muragame.pos.model;

public class MemberCustomer extends Customer implements IDiscountable {
    public MemberCustomer(String idCustomer, String namaPemesan) {
        super(idCustomer, namaPemesan);
    }

    @Override
    public double hitungDiskon(double subTotal) {
        return Math.round(subTotal * 0.10);
    }

    @Override
    public String getType() {
        return "Member";
    }

    @Override
    public double getDiscountRate() {
        return 0.10;
    }
}
