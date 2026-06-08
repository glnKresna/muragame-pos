package com.muragame.pos.model;

public class RamenMenu extends Menu {
    private int tingkatKepedasan;
    private String teksturMi;
    private String toppingTambahan;
    private String detailKhusus;

    public RamenMenu(String idMenu, String namaMenu, double harga, String kategori, 
                     int tingkatKepedasan, String teksturMi, String toppingTambahan, String detailKhusus) {
        super(idMenu, namaMenu, harga, kategori);
        this.tingkatKepedasan = tingkatKepedasan;
        this.teksturMi = teksturMi;
        this.toppingTambahan = toppingTambahan;
        this.detailKhusus = detailKhusus;
    }

    public int getTingkatKepedasan() {
        return tingkatKepedasan;
    }

    public void setTingkatKepedasan(int tingkatKepedasan) {
        this.tingkatKepedasan = tingkatKepedasan;
    }

    public String getTeksturMi() {
        return teksturMi;
    }

    public void setTeksturMi(String teksturMi) {
        this.teksturMi = teksturMi;
    }

    public String getToppingTambahan() {
        return toppingTambahan;
    }

    public void setToppingTambahan(String toppingTambahan) {
        this.toppingTambahan = toppingTambahan;
    }

    public String getDetailKhusus() {
        return detailKhusus;
    }

    public void setDetailKhusus(String detailKhusus) {
        this.detailKhusus = detailKhusus;
    }

    @Override
    public String getDetailPesanan() {
        return "Pedas " + tingkatKepedasan + " · " + detailKhusus;
    }
}
