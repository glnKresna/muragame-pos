package com.muragame.pos.model;

public class GeneralMenu extends Menu {
    private boolean isCold;
    private String detailKhusus;

    public GeneralMenu(String idMenu, String namaMenu, double harga, String kategori, 
                       boolean isCold, String detailKhusus) {
        super(idMenu, namaMenu, harga, kategori);
        this.isCold = isCold;
        this.detailKhusus = detailKhusus;
    }

    public boolean isCold() {
        return isCold;
    }

    public void setCold(boolean cold) {
        isCold = cold;
    }

    public String getDetailKhusus() {
        return detailKhusus;
    }

    public void setDetailKhusus(String detailKhusus) {
        this.detailKhusus = detailKhusus;
    }

    @Override
    public String getDetailPesanan() {
        String temp = isCold ? "Dingin" : "Hangat";
        if (detailKhusus != null && !detailKhusus.isEmpty()) {
            return temp + " · " + detailKhusus;
        }
        return temp;
    }
}
