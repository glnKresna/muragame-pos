package com.muragame.pos.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction_Order {
    private String idOrder;
    private Date tanggalWaktu;
    private double subTotal;
    private double diskon;
    private double totalBersih;
    private List<Invoice> invoiceItems;
    private Customer customer;
    private Layanan layanan;

    public Transaction_Order(String idOrder) {
        this.idOrder = idOrder;
        this.tanggalWaktu = new Date();
        this.invoiceItems = new ArrayList<>();
        // Default customer: RegularCustomer Umum
        this.customer = new RegularCustomer("C-DEFAULT", "Umum");
        this.layanan = new DineInLayanan();
        recalculate();
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public Date getTanggalWaktu() {
        return tanggalWaktu;
    }

    public void setTanggalWaktu(Date tanggalWaktu) {
        this.tanggalWaktu = tanggalWaktu;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getDiskon() {
        return diskon;
    }

    public double getTotalBersih() {
        return totalBersih;
    }

    public List<Invoice> getInvoiceItems() {
        return invoiceItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        recalculate();
    }

    public Layanan getLayanan() {
        return layanan;
    }

    public void setLayanan(Layanan layanan) {
        this.layanan = layanan;
        recalculate();
    }

    public void addInvoiceItem(Invoice item) {
        for (Invoice existing : invoiceItems) {
            boolean sameMenu = existing.getMenu().getIdMenu().equals(item.getMenu().getIdMenu());
            boolean sameCustomization = false;

            if (existing.getKustomisasi() == null && item.getKustomisasi() == null) {
                sameCustomization = true;
            } else if (existing.getKustomisasi() != null && item.getKustomisasi() != null) {
                KustomisasiPesanan c1 = existing.getKustomisasi();
                KustomisasiPesanan c2 = item.getKustomisasi();
                sameCustomization = (c1.getTingkatKepedasan() == c2.getTingkatKepedasan())
                                    && c1.getKuah().equals(c2.getKuah())
                                    && c1.getTopping().equals(c2.getTopping())
                                    && c1.getCatatanUmum().equals(c2.getCatatanUmum());
            }

            if (sameMenu && sameCustomization) {
                existing.changeQty(item.getKuantitas());
                recalculate();
                return;
            }
        }
        invoiceItems.add(item);
        recalculate();
    }

    public void changeQtyByInvoice(String invoiceId, int delta) {
        Invoice toRemove = null;
        for (Invoice item : invoiceItems) {
            if (item.getIdInvoice().equals(invoiceId)) {
                item.changeQty(delta);
                if (item.getKuantitas() <= 0) {
                    toRemove = item;
                }
                break;
            }
        }
        if (toRemove != null) {
            invoiceItems.remove(toRemove);
        }
        recalculate();
    }

    public void hitungSubTotal() {
        this.subTotal = 0.0;
        for (Invoice item : invoiceItems) {
            this.subTotal += item.getSubTotalItem();
        }
    }

    public void terapkanDiskon(IDiscountable pelanggan) {
        if (pelanggan != null && this.subTotal > 0) {
            this.diskon = pelanggan.hitungDiskon(this.subTotal);
        } else {
            this.diskon = 0.0;
        }
    }

    public void hitungTotalBersih(double biayaLayanan) {
        if (this.subTotal <= 0) {
            this.totalBersih = 0.0;
            this.diskon = 0.0;
        } else {
            this.totalBersih = this.subTotal + biayaLayanan - this.diskon;
        }
    }

    public void recalculate() {
        hitungSubTotal();
        if (customer instanceof IDiscountable) {
            terapkanDiskon((IDiscountable) customer);
        } else {
            this.diskon = 0.0;
        }
        double fee = layanan != null ? layanan.getBiayaLayanan() : 0.0;
        hitungTotalBersih(fee);
    }
}
