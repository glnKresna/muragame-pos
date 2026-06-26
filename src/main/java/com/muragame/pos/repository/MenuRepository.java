package com.muragame.pos.repository;

import com.muragame.pos.database.DatabaseConnection;
import com.muragame.pos.model.GeneralMenu;
import com.muragame.pos.model.Menu;
import com.muragame.pos.model.RamenMenu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository untuk mengelola data menu dari dan ke database MySQL.
 * Menyediakan operasi CRUD lengkap untuk tabel menu.
 */
public class MenuRepository {

    /**
     * Memuat semua menu dari database MySQL.
     * @return List menu (RamenMenu / GeneralMenu), atau list kosong jika gagal.
     */
    public List<Menu> loadMenusFromDatabase() {
        List<Menu> menus = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("[MenuRepo] Koneksi DB null, tidak dapat memuat menu.");
            return menus;
        }

        String sql = "SELECT * FROM menu ORDER BY id_menu";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id       = rs.getString("id_menu");
                String nama     = rs.getString("nama_menu");
                double harga    = rs.getDouble("harga");
                String kategori = rs.getString("kategori");
                String tipe     = rs.getString("tipe_menu");

                if ("ramen".equalsIgnoreCase(tipe)) {
                    int    pedas    = rs.getInt("tingkat_kepedasan");
                    String tekstur  = rs.getString("tekstur_mi");
                    String topping  = rs.getString("topping_tambahan");
                    String detail   = rs.getString("detail_khusus");
                    menus.add(new RamenMenu(id, nama, harga, kategori, pedas, tekstur, topping, detail));
                } else {
                    boolean isCold = rs.getBoolean("is_cold");
                    String  detail = rs.getString("detail_khusus");
                    menus.add(new GeneralMenu(id, nama, harga, kategori, isCold, detail));
                }
            }
            System.out.println("[MenuRepo] Berhasil memuat " + menus.size() + " menu dari database.");

        } catch (SQLException e) {
            System.err.println("[MenuRepo] Gagal memuat menu dari DB: " + e.getMessage());
            e.printStackTrace();
        }
        return menus;
    }

    /**
     * Menyimpan menu baru ke database MySQL.
     * @param menu objek Menu (RamenMenu / GeneralMenu)
     * @return true jika berhasil, false jika gagal
     */
    public boolean saveMenu(Menu menu) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("[MenuRepo] Koneksi DB null, tidak dapat menyimpan menu.");
            return false;
        }

        String sql = "INSERT INTO menu (id_menu, nama_menu, harga, kategori, tipe_menu, "
                   + "tingkat_kepedasan, tekstur_mi, topping_tambahan, is_cold, detail_khusus) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            fillMenuStatement(ps, menu);
            ps.executeUpdate();
            System.out.println("[MenuRepo] Menu " + menu.getIdMenu() + " berhasil disimpan ke DB.");
            return true;
        } catch (SQLException e) {
            System.err.println("[MenuRepo] Gagal menyimpan menu ke DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengupdate data menu yang sudah ada di database.
     * @param menu objek Menu dengan data terbaru
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateMenu(Menu menu) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("[MenuRepo] Koneksi DB null, tidak dapat mengupdate menu.");
            return false;
        }

        String sql = "UPDATE menu SET nama_menu = ?, harga = ?, kategori = ? WHERE id_menu = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, menu.getNamaMenu());
            ps.setDouble(2, menu.getHarga());
            ps.setString(3, menu.getKategori().toLowerCase());
            ps.setString(4, menu.getIdMenu());
            ps.executeUpdate();
            System.out.println("[MenuRepo] Menu " + menu.getIdMenu() + " berhasil diupdate di DB.");
            return true;
        } catch (SQLException e) {
            System.err.println("[MenuRepo] Gagal mengupdate menu di DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Menghapus menu dari database berdasarkan ID.
     * @param idMenu ID menu yang akan dihapus
     * @return true jika berhasil, false jika gagal (misal: masih ada FK reference)
     */
    public boolean deleteMenu(String idMenu) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("[MenuRepo] Koneksi DB null, tidak dapat menghapus menu.");
            return false;
        }

        String sql = "DELETE FROM menu WHERE id_menu = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idMenu);
            ps.executeUpdate();
            System.out.println("[MenuRepo] Menu " + idMenu + " berhasil dihapus dari DB.");
            return true;
        } catch (SQLException e) {
            System.err.println("[MenuRepo] Gagal menghapus menu dari DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Menyinkronkan list menu default ke database (INSERT IGNORE agar tidak overwrite data yang ada).
     * Digunakan sebagai inisialisasi jika tabel menu kosong.
     * @param menus list menu default
     */
    public void syncDefaultMenusToDatabase(List<Menu> menus) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            System.out.println("[MenuRepo] Skip sinkronisasi menu karena koneksi offline.");
            return;
        }

        String sql = "INSERT IGNORE INTO menu (id_menu, nama_menu, harga, kategori, tipe_menu, "
                   + "tingkat_kepedasan, tekstur_mi, topping_tambahan, is_cold, detail_khusus) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Menu menu : menus) {
                fillMenuStatement(ps, menu);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("[MenuRepo] Sinkronisasi " + menus.size() + " menu default ke DB selesai.");
        } catch (SQLException e) {
            System.err.println("[MenuRepo] Gagal menyinkronkan menu default ke DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mengisi PreparedStatement dengan data dari objek Menu.
     * Mendukung RamenMenu dan GeneralMenu secara polimorfis.
     */
    private void fillMenuStatement(PreparedStatement ps, Menu menu) throws SQLException {
        String tipe = (menu instanceof RamenMenu) ? "ramen" : "general";
        ps.setString(1, menu.getIdMenu());
        ps.setString(2, menu.getNamaMenu());
        ps.setDouble(3, menu.getHarga());
        ps.setString(4, menu.getKategori().toLowerCase());
        ps.setString(5, tipe);

        if (menu instanceof RamenMenu) {
            RamenMenu rm = (RamenMenu) menu;
            ps.setInt(6, rm.getTingkatKepedasan());
            ps.setString(7, rm.getTeksturMi());
            ps.setString(8, rm.getToppingTambahan());
            ps.setNull(9, java.sql.Types.BOOLEAN);
            ps.setString(10, rm.getDetailPesanan());
        } else if (menu instanceof GeneralMenu) {
            GeneralMenu gm = (GeneralMenu) menu;
            ps.setNull(6, java.sql.Types.INTEGER);
            ps.setNull(7, java.sql.Types.VARCHAR);
            ps.setNull(8, java.sql.Types.VARCHAR);
            ps.setBoolean(9, gm.isCold());
            ps.setString(10, gm.getDetailPesanan());
        }
    }
}
