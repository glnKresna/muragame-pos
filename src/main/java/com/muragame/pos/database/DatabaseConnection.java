package com.muragame.pos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class untuk mengelola koneksi JDBC ke MySQL.
 * Menggunakan XAMPP MySQL default (localhost:3306, user: root, tanpa password).
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/muragame_pos?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Koneksi ke database muragame_pos berhasil!");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DB] Gagal terhubung ke database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan instance singleton DatabaseConnection.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null || !isConnectionValid(instance)) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Mendapatkan objek Connection JDBC aktif.
     * @return Connection atau null jika koneksi gagal
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Koneksi database dipulihkan (reconnect).");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Gagal reconnect ke database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Menutup koneksi database.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error saat menutup koneksi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mengecek apakah koneksi pada instance masih valid.
     */
    private static boolean isConnectionValid(DatabaseConnection dbInstance) {
        try {
            return dbInstance.connection != null && !dbInstance.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
