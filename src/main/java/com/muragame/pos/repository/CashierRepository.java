package com.muragame.pos.repository;

import com.muragame.pos.database.DatabaseConnection;
import com.muragame.pos.model.Cashier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository untuk mengelola data kasir dari database MySQL.
 * Memuat data kasir dari tabel 'cashiers' di MySQL sebagai sumber utama,
 * dengan fallback ke file cashiers.txt jika koneksi DB tidak tersedia.
 */
public class CashierRepository {
    private static final String FILE_PATH = "/com/muragame/pos/cashiers.txt";

    /**
     * Memuat daftar kasir. Prioritas: Database MySQL → File cashiers.txt (fallback).
     * @return List<Cashier> berisi data kasir
     */
    public List<Cashier> loadCashiers() {
        List<Cashier> fromDb = loadCashiersFromDatabase();
        if (!fromDb.isEmpty()) {
            System.out.println("[CashierRepo] " + fromDb.size() + " kasir dimuat dari database MySQL.");
            return fromDb;
        }

        System.out.println("[CashierRepo] Database tidak tersedia, fallback ke file cashiers.txt.");
        return loadCashiersFromFile();
    }

    /**
     * Memuat daftar kasir dari tabel 'cashiers' di MySQL.
     * @return List<Cashier> — kosong jika koneksi gagal atau tabel kosong
     */
    public List<Cashier> loadCashiersFromDatabase() {
        List<Cashier> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("[CashierRepo] Koneksi DB null, tidak dapat memuat kasir dari database.");
            return list;
        }

        String sql = "SELECT id_kasir, nama_kasir, password, shift FROM cashiers ORDER BY id_kasir";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id    = rs.getString("id_kasir");
                String nama  = rs.getString("nama_kasir");
                String pass  = rs.getString("password");
                String shift = rs.getString("shift");
                list.add(new Cashier(id, nama, pass, shift));
            }

        } catch (SQLException e) {
            System.err.println("[CashierRepo] Gagal memuat kasir dari database: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Memuat daftar kasir dari file cashiers.txt (fallback offline).
     * Format setiap baris: id;nama;password;shift
     * @return List<Cashier>
     */
    private List<Cashier> loadCashiersFromFile() {
        List<Cashier> list = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(FILE_PATH)) {
            if (is == null) {
                System.err.println("[CashierRepo] File cashiers.txt tidak ditemukan di resources!");
                return list;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split(";");
                    if (parts.length >= 4) {
                        String id    = parts[0].trim();
                        String name  = parts[1].trim();
                        String pass  = parts[2].trim();
                        String shift = parts[3].trim();
                        list.add(new Cashier(id, name, pass, shift));
                    }
                }
            }
            System.out.println("[CashierRepo] " + list.size() + " kasir dimuat dari file cashiers.txt.");
        } catch (Exception e) {
            System.err.println("[CashierRepo] Gagal membaca data kasir dari file: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Menyinkronkan data kasir ke database (INSERT ... ON DUPLICATE KEY UPDATE).
     * Digunakan untuk seed awal dari file ke DB jika DB masih kosong.
     * @param cashiers daftar kasir yang akan disinkronkan
     */
    public void syncCashiersToDatabase(List<Cashier> cashiers) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.out.println("[CashierRepo] Skip sinkronisasi kasir karena koneksi offline.");
            return;
        }

        String sql = "INSERT INTO cashiers (id_kasir, nama_kasir, password, shift, is_active) "
                   + "VALUES (?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE "
                   + "  nama_kasir = VALUES(nama_kasir), "
                   + "  password   = VALUES(password), "
                   + "  shift      = VALUES(shift)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Cashier c : cashiers) {
                ps.setString(1, c.getIdKasir());
                ps.setString(2, c.getNamaKasir());
                ps.setString(3, c.getPassword());
                ps.setString(4, c.getShift());
                ps.setBoolean(5, false);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("[CashierRepo] Sinkronisasi " + cashiers.size() + " kasir ke database berhasil.");
        } catch (SQLException e) {
            System.err.println("[CashierRepo] Gagal menyinkronkan data kasir ke database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Memverifikasi login kasir langsung ke database MySQL.
     * Lebih aman daripada membandingkan di memori karena password bisa berubah di DB.
     * @param namaKasir nama kasir yang diinputkan
     * @param password  password yang diinputkan
     * @return Cashier jika login valid, null jika tidak ditemukan atau password salah
     */
    public Cashier verifyLoginFromDatabase(String namaKasir, String password) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("[CashierRepo] Koneksi DB null, tidak dapat verifikasi login.");
            return null;
        }

        String sql = "SELECT id_kasir, nama_kasir, password, shift "
                   + "FROM cashiers "
                   + "WHERE LOWER(nama_kasir) = LOWER(?) AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaKasir.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id    = rs.getString("id_kasir");
                    String nama  = rs.getString("nama_kasir");
                    String pass  = rs.getString("password");
                    String shift = rs.getString("shift");
                    return new Cashier(id, nama, pass, shift);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CashierRepo] Gagal verifikasi login dari database: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
