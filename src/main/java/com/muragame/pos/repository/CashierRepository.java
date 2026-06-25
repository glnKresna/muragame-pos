package com.muragame.pos.repository;

import com.muragame.pos.database.DatabaseConnection;
import com.muragame.pos.model.Cashier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CashierRepository {
    private static final String FILE_PATH = "/com/muragame/pos/cashiers.txt";

    public List<Cashier> loadCashiers() {
        List<Cashier> list = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(FILE_PATH)) {
            if (is == null) {
                System.err.println("[REPO] File cashiers.txt tidak ditemukan di resources!");
                return list;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    String[] parts = line.split(";");
                    if (parts.length >= 4) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        String pass = parts[2].trim();
                        String shift = parts[3].trim();
                        list.add(new Cashier(id, name, pass, shift));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[REPO] Gagal membaca data kasir: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public void syncCashiersToDatabase(List<Cashier> cashiers) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            if (conn == null) {
                System.out.println("[DB] Skip sinkronisasi kasir ke database karena koneksi offline.");
                return;
            }
            
            String sql = "INSERT INTO cashiers (id_kasir, nama_kasir, password, shift, is_active) " +
                         "VALUES (?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE nama_kasir = VALUES(nama_kasir), password = VALUES(password), shift = VALUES(shift)";
                         
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
                System.out.println("[REPO] Sinkronisasi " + cashiers.size() + " kasir ke database berhasil.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Gagal menyinkronkan data kasir ke database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
