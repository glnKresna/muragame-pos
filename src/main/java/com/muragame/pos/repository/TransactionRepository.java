package com.muragame.pos.repository;

import com.muragame.pos.model.Transaction_Order;
import com.muragame.pos.model.Invoice;
import com.muragame.pos.model.Payment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionRepository {
    private static final String FILE_PATH = "transactions.txt";

    public void save(Transaction_Order order, Payment payment) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            
            pw.println("==========================================");
            pw.println("TRANSAKSI: " + order.getIdOrder());
            pw.println("Waktu    : " + dtf.format(now));
            pw.println("Pelanggan: " + order.getCustomer().getNamaPemesan() + " (" + order.getCustomer().getType() + ")");
            pw.println("Layanan  : " + order.getLayanan().getTipeLayanan() + " (Rp " + order.getLayanan().getBiayaLayanan() + ")");
            pw.println("------------------------------------------");
            
            for (Invoice item : order.getInvoiceItems()) {
                pw.printf("%-20s x%-3d  Rp %,10.0f\n", 
                    item.getMenu().getNamaMenu(), 
                    item.getKuantitas(), 
                    item.getSubTotalItem()
                );
            }
            
            pw.println("------------------------------------------");
            pw.printf("Subtotal   : Rp %,10.0f\n", order.getSubTotal());
            pw.printf("Diskon     : Rp %,10.0f\n", order.getDiskon());
            pw.printf("Total      : Rp %,10.0f\n", order.getTotalBersih());
            pw.printf("Metode     : %s\n", payment.getMetode());
            pw.printf("Dibayar    : Rp %,10.0f\n", payment.getUangBayar());
            pw.printf("Kembalian  : Rp %,10.0f\n", payment.getKembalian());
            pw.println("Status     : " + payment.getStatus());
            pw.println("==========================================");
            pw.println();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
