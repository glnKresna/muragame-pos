package com.muragame.pos.model;

public class RegularCustomer extends Customer implements IDiscountable {
    private double customDiscount = 0.0;

    public RegularCustomer(String idCustomer, String namaPemesan) {
        super(idCustomer, namaPemesan);
    }

    public void setCustomDiscount(double customDiscount) {
        this.customDiscount = customDiscount;
    }

    @Override
    public double hitungDiskon(double subTotal) {
        return customDiscount;
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
