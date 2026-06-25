-- ============================================================
-- MuragamePOS - Database MySQL
-- Aplikasi Kasir Desktop Restoran Jepang (Muragame Resto)
-- ============================================================
-- Jalankan file ini di phpMyAdmin atau MySQL CLI
-- XAMPP → Start MySQL → http://localhost/phpmyadmin
-- ============================================================

-- Buat database
CREATE DATABASE IF NOT EXISTS muragame_pos
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE muragame_pos;

-- ============================================================
-- 1. TABEL CASHIERS (Kasir)
-- ============================================================
CREATE TABLE IF NOT EXISTS cashiers (
    id_kasir VARCHAR(20) NOT NULL PRIMARY KEY,
    nama_kasir VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    shift VARCHAR(20) NOT NULL DEFAULT 'Pagi',
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 2. TABEL CUSTOMERS (Pelanggan)
-- ============================================================
CREATE TABLE IF NOT EXISTS customers (
    id_customer VARCHAR(20) NOT NULL PRIMARY KEY,
    nama_pemesan VARCHAR(100) NOT NULL,
    tipe_customer ENUM('Regular', 'Member') NOT NULL DEFAULT 'Regular',
    discount_rate DOUBLE NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 3. TABEL MENU (Daftar Menu Restoran)
-- ============================================================
CREATE TABLE IF NOT EXISTS menu (
    id_menu VARCHAR(20) NOT NULL PRIMARY KEY,
    nama_menu VARCHAR(100) NOT NULL,
    harga DOUBLE NOT NULL,
    kategori ENUM('ramen', 'minuman', 'snack') NOT NULL,
    tipe_menu ENUM('ramen', 'general') NOT NULL,
    -- Kolom khusus RamenMenu
    tingkat_kepedasan INT DEFAULT NULL,
    tekstur_mi VARCHAR(50) DEFAULT NULL,
    topping_tambahan VARCHAR(255) DEFAULT NULL,
    -- Kolom khusus GeneralMenu
    is_cold BOOLEAN DEFAULT NULL,
    -- Kolom umum
    detail_khusus VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 4. TABEL LAYANAN (Tipe Layanan Restoran)
-- ============================================================
CREATE TABLE IF NOT EXISTS layanan (
    id_layanan VARCHAR(20) NOT NULL PRIMARY KEY,
    tipe_layanan VARCHAR(50) NOT NULL,
    biaya_layanan DOUBLE NOT NULL DEFAULT 0.0
) ENGINE=InnoDB;

-- ============================================================
-- 5. TABEL TRANSACTIONS (Header Transaksi)
-- ============================================================
CREATE TABLE IF NOT EXISTS transactions (
    id_order VARCHAR(50) NOT NULL PRIMARY KEY,
    tanggal_waktu DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_customer VARCHAR(20) NOT NULL,
    id_layanan VARCHAR(20) NOT NULL,
    id_kasir VARCHAR(20) DEFAULT NULL,
    sub_total DOUBLE NOT NULL DEFAULT 0.0,
    diskon DOUBLE NOT NULL DEFAULT 0.0,
    total_bersih DOUBLE NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_customer) REFERENCES customers(id_customer),
    FOREIGN KEY (id_layanan) REFERENCES layanan(id_layanan),
    FOREIGN KEY (id_kasir) REFERENCES cashiers(id_kasir)
) ENGINE=InnoDB;

-- ============================================================
-- 6. TABEL KUSTOMISASI_PESANAN (Detail Kustomisasi Ramen)
-- ============================================================
CREATE TABLE IF NOT EXISTS kustomisasi_pesanan (
    id_kustomisasi VARCHAR(50) NOT NULL PRIMARY KEY,
    tingkat_kepedasan INT NOT NULL DEFAULT 0,
    kuah VARCHAR(50) DEFAULT 'Original',
    topping VARCHAR(255) DEFAULT NULL,
    catatan_umum TEXT DEFAULT NULL,
    is_ramen BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 7. TABEL INVOICE_ITEMS (Detail Item Pesanan)
-- ============================================================
CREATE TABLE IF NOT EXISTS invoice_items (
    id_invoice VARCHAR(50) NOT NULL PRIMARY KEY,
    id_order VARCHAR(50) NOT NULL,
    id_menu VARCHAR(20) NOT NULL,
    kuantitas INT NOT NULL DEFAULT 1,
    sub_total_item DOUBLE NOT NULL DEFAULT 0.0,
    id_kustomisasi VARCHAR(50) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_order) REFERENCES transactions(id_order),
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu),
    FOREIGN KEY (id_kustomisasi) REFERENCES kustomisasi_pesanan(id_kustomisasi)
) ENGINE=InnoDB;

-- ============================================================
-- 8. TABEL PAYMENTS (Data Pembayaran)
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    id_payment VARCHAR(50) NOT NULL PRIMARY KEY,
    id_order VARCHAR(50) NOT NULL,
    metode ENUM('CASH', 'QRIS') NOT NULL,
    uang_bayar DOUBLE NOT NULL DEFAULT 0.0,
    kembalian DOUBLE NOT NULL DEFAULT 0.0,
    status ENUM('PENDING', 'LUNAS', 'GAGAL') NOT NULL DEFAULT 'PENDING',
    qris_transaction_id VARCHAR(50) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_order) REFERENCES transactions(id_order)
) ENGINE=InnoDB;

-- ============================================================
-- DATA AWAL (SEED DATA)
-- ============================================================

-- -----------------------------------------------------------
-- Seed: Kasir Default
-- -----------------------------------------------------------
INSERT INTO cashiers (id_kasir, nama_kasir, password, shift, is_active) VALUES
('K001', 'Admin Kasir', 'admin123', 'Pagi', TRUE);

-- -----------------------------------------------------------
-- Seed: Customer Contoh
-- -----------------------------------------------------------
INSERT INTO customers (id_customer, nama_pemesan, tipe_customer, discount_rate) VALUES
('C001', 'Budi Santoso', 'Member', 0.10),
('C002', 'Pelanggan Umum', 'Regular', 0.00);

-- -----------------------------------------------------------
-- Seed: Layanan
-- -----------------------------------------------------------
INSERT INTO layanan (id_layanan, tipe_layanan, biaya_layanan) VALUES
('L001', 'Dine In', 0.0),
('L002', 'Take Away', 2000.0),
('L003', 'Delivery', 15000.0);

-- -----------------------------------------------------------
-- Seed: Menu Ramen (12 item)
-- -----------------------------------------------------------
INSERT INTO menu (id_menu, nama_menu, harga, kategori, tipe_menu, tingkat_kepedasan, tekstur_mi, topping_tambahan, is_cold, detail_khusus) VALUES
-- Tori Ramen (Ayam) - Rp 38.000
('M001', 'Shio Tori Ramen',    38000, 'ramen', 'ramen', 0, 'Sedang', 'Chashu, Tamago, Nori', NULL, 'Chashu · Tamago · Nori'),
('M002', 'Shoyu Tori Ramen',   38000, 'ramen', 'ramen', 0, 'Sedang', 'Chashu, Tamago, Nori', NULL, 'Chashu · Tamago · Nori'),
('M003', 'Miso Tori Ramen',    38000, 'ramen', 'ramen', 0, 'Sedang', 'Chashu, Tamago, Nori', NULL, 'Chashu · Tamago · Nori'),
('M004', 'Paitan Tori Ramen',  38000, 'ramen', 'ramen', 0, 'Sedang', 'Chashu, Tamago, Nori', NULL, 'Chashu · Tamago · Nori'),

-- Beef Ramen - Rp 45.000
('M005', 'Shio Beef Ramen',    45000, 'ramen', 'ramen', 0, 'Sedang', 'Beef Slices, Tamago, Nori', NULL, 'Beef Slices · Tamago · Nori'),
('M006', 'Shoyu Beef Ramen',   45000, 'ramen', 'ramen', 0, 'Sedang', 'Beef Slices, Tamago, Nori', NULL, 'Beef Slices · Tamago · Nori'),
('M007', 'Miso Beef Ramen',    45000, 'ramen', 'ramen', 0, 'Sedang', 'Beef Slices, Tamago, Nori', NULL, 'Beef Slices · Tamago · Nori'),
('M008', 'Paitan Beef Ramen',  45000, 'ramen', 'ramen', 0, 'Sedang', 'Beef Slices, Tamago, Nori', NULL, 'Beef Slices · Tamago · Nori'),

-- Tempura Ramen - Rp 42.000
('M009', 'Shio Tempura Ramen',   42000, 'ramen', 'ramen', 0, 'Sedang', 'Tempura, Tamago, Nori', NULL, 'Tempura · Tamago · Nori'),
('M010', 'Shoyu Tempura Ramen',  42000, 'ramen', 'ramen', 0, 'Sedang', 'Tempura, Tamago, Nori', NULL, 'Tempura · Tamago · Nori'),
('M011', 'Miso Tempura Ramen',   42000, 'ramen', 'ramen', 0, 'Sedang', 'Tempura, Tamago, Nori', NULL, 'Tempura · Tamago · Nori'),
('M012', 'Paitan Tempura Ramen', 42000, 'ramen', 'ramen', 0, 'Sedang', 'Tempura, Tamago, Nori', NULL, 'Tempura · Tamago · Nori');

-- -----------------------------------------------------------
-- Seed: Menu General (5 item - Minuman & Snack)
-- -----------------------------------------------------------
INSERT INTO menu (id_menu, nama_menu, harga, kategori, tipe_menu, tingkat_kepedasan, tekstur_mi, topping_tambahan, is_cold, detail_khusus) VALUES
('M013', 'Es Matcha Latte', 22000, 'minuman', 'general', NULL, NULL, NULL, TRUE,  'Less sugar available'),
('M014', 'Es Teh Tarik',    15000, 'minuman', 'general', NULL, NULL, NULL, TRUE,  'Creamy blend'),
('M015', 'Gyoza (6 pcs)',   28000, 'snack',   'general', NULL, NULL, NULL, FALSE, 'Pork Filling'),
('M016', 'Karaage',         25000, 'snack',   'general', NULL, NULL, NULL, FALSE, 'Chicken · Mayo sauce'),
('M017', 'Takoyaki (6)',    23000, 'snack',   'general', NULL, NULL, NULL, FALSE, 'Octopus · Bonito');

-- ============================================================
-- SELESAI! Database muragame_pos siap digunakan.
-- ============================================================
