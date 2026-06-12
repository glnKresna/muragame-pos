-- ============================================================
-- SEED DATA: Dummy Transaksi untuk Testing Filter History
-- Jalankan setelah muragame_pos.sql
-- ============================================================
USE muragame_pos;

-- ============================================================
-- HARI INI (2 transaksi)
-- ============================================================

-- Transaksi 1: Hari Ini
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-TODAY-001', NOW() - INTERVAL 2 HOUR, 'C001', 'L001', 'K001', 76000, 7600, 73400);

INSERT INTO kustomisasi_pesanan (id_kustomisasi, tingkat_kepedasan, kuah, topping, catatan_umum, is_ramen)
VALUES ('CUST-T1-001', 2, 'Shoyu', 'Chashu, Tamago, Nori', 'Pedas sedang', TRUE);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item, id_kustomisasi)
VALUES ('INV-T1-001', 'ORD-TODAY-001', 'M002', 2, 76000, 'CUST-T1-001');

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-T1-001', 'ORD-TODAY-001', 'CASH', 80000, 6600, 'LUNAS');

-- Transaksi 2: Hari Ini
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-TODAY-002', NOW() - INTERVAL 1 HOUR, 'C002', 'L002', 'K001', 60000, 0, 62000);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item)
VALUES ('INV-T2-001', 'ORD-TODAY-002', 'M013', 1, 22000, NULL),
       ('INV-T2-002', 'ORD-TODAY-002', 'M001', 1, 38000, NULL);

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-T2-001', 'ORD-TODAY-002', 'QRIS', 62000, 0, 'LUNAS');

-- ============================================================
-- MINGGU INI tapi bukan hari ini (2 transaksi)
-- ============================================================

-- Transaksi 3: 2 hari lalu
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-WEEK-001', NOW() - INTERVAL 2 DAY, 'C001', 'L001', 'K001', 90000, 9000, 86000);

INSERT INTO kustomisasi_pesanan (id_kustomisasi, tingkat_kepedasan, kuah, topping, catatan_umum, is_ramen)
VALUES ('CUST-W1-001', 3, 'Miso', 'Chashu, Nori, Tempura', 'Extra pedas', TRUE);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item, id_kustomisasi)
VALUES ('INV-W1-001', 'ORD-WEEK-001', 'M007', 2, 90000, 'CUST-W1-001');

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-W1-001', 'ORD-WEEK-001', 'CASH', 100000, 14000, 'LUNAS');

-- Transaksi 4: 3 hari lalu
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-WEEK-002', NOW() - INTERVAL 3 DAY, 'C002', 'L003', 'K001', 53000, 0, 68000);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item)
VALUES ('INV-W2-001', 'ORD-WEEK-002', 'M015', 1, 28000, NULL),
       ('INV-W2-002', 'ORD-WEEK-002', 'M016', 1, 25000, NULL);

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-W2-001', 'ORD-WEEK-002', 'CASH', 70000, 2000, 'LUNAS');

-- ============================================================
-- BULAN INI tapi bukan minggu ini (2 transaksi)
-- ============================================================

-- Transaksi 5: 10 hari lalu
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-MONTH-001', NOW() - INTERVAL 10 DAY, 'C001', 'L002', 'K001', 130000, 13000, 119000);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item)
VALUES ('INV-M1-001', 'ORD-MONTH-001', 'M005', 2, 90000, NULL),
       ('INV-M1-002', 'ORD-MONTH-001', 'M009', 1, 42000, NULL);

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-M1-001', 'ORD-MONTH-001', 'QRIS', 119000, 0, 'LUNAS');

-- Transaksi 6: 15 hari lalu
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-MONTH-002', NOW() - INTERVAL 15 DAY, 'C002', 'L001', 'K001', 61000, 0, 66000);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item)
VALUES ('INV-M2-001', 'ORD-MONTH-002', 'M001', 1, 38000, NULL),
       ('INV-M2-002', 'ORD-MONTH-002', 'M017', 1, 23000, NULL);

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-M2-001', 'ORD-MONTH-002', 'CASH', 70000, 4000, 'LUNAS');

-- ============================================================
-- TAHUN INI tapi bukan bulan ini (3 transaksi)
-- ============================================================

-- Transaksi 7: 2 bulan lalu
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-YEAR-001', NOW() - INTERVAL 2 MONTH, 'C001', 'L001', 'K001', 45000, 4500, 45500);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item)
VALUES ('INV-Y1-001', 'ORD-YEAR-001', 'M006', 1, 45000, NULL);

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-Y1-001', 'ORD-YEAR-001', 'CASH', 50000, 4500, 'LUNAS');

-- Transaksi 8: 3 bulan lalu
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-YEAR-002', NOW() - INTERVAL 3 MONTH, 'C002', 'L003', 'K001', 84000, 0, 99000);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item)
VALUES ('INV-Y2-001', 'ORD-YEAR-002', 'M009', 2, 84000, NULL);

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-Y2-001', 'ORD-YEAR-002', 'QRIS', 99000, 0, 'LUNAS');

-- Transaksi 9: 5 bulan lalu
INSERT INTO transactions (id_order, tanggal_waktu, id_customer, id_layanan, id_kasir, sub_total, diskon, total_bersih)
VALUES ('ORD-YEAR-003', NOW() - INTERVAL 5 MONTH, 'C001', 'L002', 'K001', 108000, 10800, 99200);

INSERT INTO invoice_items (id_invoice, id_order, id_menu, kuantitas, sub_total_item)
VALUES ('INV-Y3-001', 'ORD-YEAR-003', 'M003', 2, 76000, NULL),
       ('INV-Y3-002', 'ORD-YEAR-003', 'M014', 1, 15000, NULL),
       ('INV-Y3-003', 'ORD-YEAR-003', 'M017', 1, 23000, NULL);

INSERT INTO payments (id_payment, id_order, metode, uang_bayar, kembalian, status)
VALUES ('PAY-Y3-001', 'ORD-YEAR-003', 'CASH', 100000, 800, 'LUNAS');

-- ============================================================
-- SELESAI! 9 transaksi dummy berhasil ditambahkan.
-- Hari Ini: 2, Minggu Ini: +2, Bulan Ini: +2, Tahun Ini: +3
-- ============================================================
