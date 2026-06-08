package com.muragame.pos.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History_Harian {
    private String idHistory;
    private Date tanggal;
    private int jumlahTransaksi;
    private double totalPendapatan;
    private List<Transaction_Order> transactions;

    public History_Harian(String idHistory) {
        this.idHistory = idHistory;
        this.tanggal = new Date();
        this.transactions = new ArrayList<>();
        this.jumlahTransaksi = 0;
        this.totalPendapatan = 0.0;
    }

    public String getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(String idHistory) {
        this.idHistory = idHistory;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public int getJumlahTransaksi() {
        return jumlahTransaksi;
    }

    public double getTotalPendapatan() {
        return totalPendapatan;
    }

    public List<Transaction_Order> getTransactions() {
        return transactions;
    }

    public void tambahDataTransaksi(Transaction_Order order) {
        transactions.add(order);
        hitungJumlahTransaksi();
        hitungTotalPendapatan();
    }

    public void hitungJumlahTransaksi() {
        this.jumlahTransaksi = transactions.size();
    }

    public void hitungTotalPendapatan() {
        this.totalPendapatan = 0.0;
        for (Transaction_Order t : transactions) {
            this.totalPendapatan += t.getTotalBersih();
        }
    }

    public void generateLaporan() {
        System.out.println("Daily Report Generated for ID: " + idHistory);
    }

    public void tampilkanHistory() {
        System.out.println("Menampilkan riwayat transaksi harian.");
    }
}
