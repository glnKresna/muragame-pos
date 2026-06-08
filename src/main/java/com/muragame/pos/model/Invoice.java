package com.muragame.pos.model;

public class Invoice {
    private String idInvoice;
    private Menu menu;
    private int kuantitas;
    private double subTotalItem;
    private KustomisasiPesanan kustomisasi;

    public Invoice() {}

    public Invoice(String idInvoice, Menu menu, int kuantitas) {
        this.idInvoice = idInvoice;
        this.menu = menu;
        this.kuantitas = kuantitas;
        hitungSubTotalItem(menu.getHarga());
    }

    public Invoice(String idInvoice, Menu menu, int kuantitas, KustomisasiPesanan kustomisasi) {
        this.idInvoice = idInvoice;
        this.menu = menu;
        this.kuantitas = kuantitas;
        this.kustomisasi = kustomisasi;
        hitungSubTotalItem(menu.getHarga());
    }

    public String getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(String idInvoice) {
        this.idInvoice = idInvoice;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) {
            hitungSubTotalItem(menu.getHarga());
        }
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
        if (menu != null) {
            hitungSubTotalItem(menu.getHarga());
        }
    }

    public void changeQty(int delta) {
        this.kuantitas += delta;
        if (menu != null) {
            hitungSubTotalItem(menu.getHarga());
        }
    }

    public void hitungSubTotalItem(double hargaMenu) {
        this.subTotalItem = hargaMenu * kuantitas;
    }

    public double getSubTotalItem() {
        return subTotalItem;
    }

    public KustomisasiPesanan getKustomisasi() {
        return kustomisasi;
    }

    public void setKustomisasi(KustomisasiPesanan kustomisasi) {
        this.kustomisasi = kustomisasi;
    }

    public String getDetail() {
        if (kustomisasi != null) {
            return kustomisasi.getDetail();
        }
        return menu != null ? menu.getDetailPesanan() : "";
    }
}
