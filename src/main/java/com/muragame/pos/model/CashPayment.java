package com.muragame.pos.model;

public class CashPayment extends Payment {
    public CashPayment(String idPayment) {
        super(idPayment, "CASH");
    }

    @Override
    public boolean prosesPembayaran(double total, double bayar) {
        setUangBayar(bayar);
        if (bayar >= total) {
            setKembalian(bayar - total);
            setStatus("LUNAS");
            return true;
        } else {
            setKembalian(0.0);
            setStatus("GAGAL");
            return false;
        }
    }

    @Override
    public void printStrukPDF() {
        System.out.println("Mencetak Struk PDF untuk Pembayaran Tunai: " + getIdPayment());
    }
}
