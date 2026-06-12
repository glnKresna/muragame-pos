package com.muragame.pos.repository;

import com.muragame.pos.database.DatabaseConnection;
import com.muragame.pos.model.Transaction_Order;
import com.muragame.pos.model.Invoice;
import com.muragame.pos.model.KustomisasiPesanan;
import com.muragame.pos.model.Payment;
import com.muragame.pos.model.QrisPayment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repository untuk menyimpan data transaksi.
 * Menyimpan ke MySQL database (utama) dan file teks (backup/fallback).
 */
public class TransactionRepository {
    private static final String FILE_PATH = "transactions.txt";

    /**
     * Menyimpan transaksi lengkap ke MySQL database dan file teks backup.
     */
    public void save(Transaction_Order order, Payment payment) {
        // Simpan ke MySQL (utama)
        boolean dbSuccess = saveToDatabase(order, payment);

        if (dbSuccess) {
            System.out.println("[REPO] Transaksi " + order.getIdOrder() + " berhasil disimpan ke database MySQL.");
        } else {
            System.err.println("[REPO] Gagal menyimpan ke database, menggunakan file teks sebagai fallback.");
        }

        // Simpan ke file teks (backup/fallback - selalu dilakukan)
        saveToFile(order, payment);
    }

    /**
     * Menyimpan transaksi ke MySQL database.
     * Menggunakan transaction (BEGIN/COMMIT) untuk memastikan konsistensi data.
     */
    private boolean saveToDatabase(Transaction_Order order, Payment payment) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("[DB] Koneksi database null, skip penyimpanan ke MySQL.");
                return false;
            }

            // Mulai transaction
            conn.setAutoCommit(false);

            // 1. INSERT ke tabel transactions
            String sqlTransaction = "INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih) " +
                                     "VALUES (?, NOW(), ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTransaction)) {
                ps.setString(1, order.getIdOrder());
                ps.setString(2, order.getCustomer().getIdCustomer());
                ps.setString(3, order.getLayanan().getIdLayanan());
                ps.setString(4, "K001"); // Default kasir
                ps.setDouble(5, order.getSubTotal());
                ps.setDouble(6, order.getDiskon());
                ps.setDouble(7, order.getTotalBersih());
                ps.executeUpdate();
            }

            // 2. INSERT ke tabel kustomisasi_pesanan & invoice_items
            for (Invoice item : order.getInvoiceItems()) {
                // Jika ada kustomisasi, simpan dulu
                KustomisasiPesanan kustomisasi = item.getKustomisasi();
                if (kustomisasi != null) {
                    String sqlKustomisasi = "INSERT INTO kustomisasi_pesanan (id_kustomisasi, tingkat_kepedasan, kuah, topping, catatan_umum, is_ramen) " +
                                             "VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlKustomisasi)) {
                        ps.setString(1, kustomisasi.getIdKustomisasi());
                        ps.setInt(2, kustomisasi.getTingkatKepedasan());
                        ps.setString(3, kustomisasi.getKuah());
                        ps.setString(4, kustomisasi.getTopping());
                        ps.setString(5, kustomisasi.getCatatanUmum());
                        ps.setBoolean(6, kustomisasi.isRamen());
                        ps.executeUpdate();
                    }
                }

                // Simpan invoice item
                String sqlInvoice = "INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item, id_kustomisasi) " +
                                     "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInvoice)) {
                    ps.setString(1, item.getIdInvoice());
                    ps.setString(2, order.getIdOrder());
                    ps.setString(3, item.getMenu().getIdMenu());
                    ps.setInt(4, item.getKuantitas());
                    ps.setDouble(5, item.getSubTotalItem());
                    if (kustomisasi != null) {
                        ps.setString(6, kustomisasi.getIdKustomisasi());
                    } else {
                        ps.setNull(6, java.sql.Types.VARCHAR);
                    }
                    ps.executeUpdate();
                }
            }

            // 3. INSERT ke tabel payments
            String sqlPayment = "INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status, qris_transaction_id) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPayment)) {
                ps.setString(1, payment.getIdPayment());
                ps.setString(2, order.getIdOrder());
                ps.setString(3, payment.getMetode());
                ps.setDouble(4, payment.getUangBayar());
                ps.setDouble(5, payment.getKembalian());
                ps.setString(6, payment.getStatus());
                if (payment instanceof QrisPayment) {
                    ps.setString(7, ((QrisPayment) payment).getTransactionId());
                } else {
                    ps.setNull(7, java.sql.Types.VARCHAR);
                }
                ps.executeUpdate();
            }

            // Commit transaction
            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            System.err.println("[DB] Error saat menyimpan transaksi: " + e.getMessage());
            e.printStackTrace();
            // Rollback jika terjadi error
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        }
    }

    /**
     * Mengambil riwayat transaksi dari database berdasarkan filter waktu.
     * @param filter "today", "week", "month", "year", "all"
     * @return JSON array string
     */
    public String getHistoryFromDB(String filter) {
        Connection conn = null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            if (conn == null) {
                sb.append("]");
                return sb.toString();
            }

            // Build date filter
            String dateCondition = "";
            switch (filter) {
                case "today":
                    dateCondition = " WHERE DATE(t.tanggal_waktu) = CURDATE()";
                    break;
                case "week":
                    dateCondition = " WHERE t.tanggal_waktu >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)";
                    break;
                case "month":
                    dateCondition = " WHERE MONTH(t.tanggal_waktu) = MONTH(CURDATE()) AND YEAR(t.tanggal_waktu) = YEAR(CURDATE())";
                    break;
                case "year":
                    dateCondition = " WHERE YEAR(t.tanggal_waktu) = YEAR(CURDATE())";
                    break;
                default: // "all"
                    dateCondition = "";
                    break;
            }

            String sql = "SELECT t.id_order, t.tanggal_waktu, t.sub_total, t.diskon, t.total_bersih, " +
                         "l.tipe_layanan, l.biaya_layanan, c.nama_pemesan " +
                         "FROM transactions t " +
                         "JOIN layanan l ON t.id_layanan = l.id_layanan " +
                         "JOIN customers c ON t.id_customer = c.id_customer" +
                         dateCondition +
                         " ORDER BY t.tanggal_waktu DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {

                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    String orderId = rs.getString("id_order");
                    String date = rs.getTimestamp("tanggal_waktu").toString();
                    // Trim nanoseconds: "2026-06-10 17:00:00.0" -> "2026-06-10 17:00:00"
                    if (date.contains(".")) date = date.substring(0, date.indexOf("."));
                    double subTotal = rs.getDouble("sub_total");
                    double diskon = rs.getDouble("diskon");
                    double totalBersih = rs.getDouble("total_bersih");
                    String serviceName = rs.getString("tipe_layanan");
                    double servicePrice = rs.getDouble("biaya_layanan");
                    String customerName = rs.getString("nama_pemesan");

                    // Fetch invoice items for this order
                    String sqlItems = "SELECT m.nama_menu, ii.kuantitas, ii.sub_total_item " +
                                      "FROM invoice_items ii JOIN menu m ON ii.id_menu = m.id_menu " +
                                      "WHERE ii.id_order = ?";
                    StringBuilder itemsSb = new StringBuilder();
                    itemsSb.append("[");
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlItems)) {
                        ps2.setString(1, orderId);
                        try (java.sql.ResultSet rs2 = ps2.executeQuery()) {
                            boolean firstItem = true;
                            while (rs2.next()) {
                                if (!firstItem) itemsSb.append(",");
                                firstItem = false;
                                itemsSb.append(String.format(
                                    "{\"name\":\"%s\",\"qty\":%d,\"total\":%.0f}",
                                    escapeJson(rs2.getString("nama_menu")),
                                    rs2.getInt("kuantitas"),
                                    rs2.getDouble("sub_total_item")
                                ));
                            }
                        }
                    }
                    itemsSb.append("]");

                    sb.append(String.format(
                        "{\"orderId\":\"%s\",\"date\":\"%s\",\"customerName\":\"%s\",\"subtotal\":%.0f,\"service\":%.0f,\"serviceName\":\"%s\",\"discount\":%.0f,\"total\":%.0f,\"items\":%s}",
                        escapeJson(orderId), escapeJson(date), escapeJson(customerName),
                        subTotal, servicePrice, escapeJson(serviceName), diskon, totalBersih,
                        itemsSb.toString()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error saat mengambil history: " + e.getMessage());
            e.printStackTrace();
        }

        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Menyimpan transaksi ke file teks (backup/fallback).
     * Format sama seperti versi sebelumnya.
     */
    private void saveToFile(Transaction_Order order, Payment payment) {
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
