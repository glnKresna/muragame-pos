package com.muragame.pos.bridge;

import com.muragame.pos.model.*;
import com.muragame.pos.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JavaBridge {
    private List<Menu> menus;
    private Transaction_Order currentOrder;
    private History_Harian historyHarian;
    private TransactionRepository transactionRepository;

    public JavaBridge() {
        this.transactionRepository = new TransactionRepository();
        this.historyHarian = new History_Harian("HIST-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()));
        initializeMenus();
        startNewOrder();
    }

    private void initializeMenus() {
        menus = new ArrayList<>();
        
        boolean loadedFromDb = loadMenusFromDatabase();
        if (!loadedFromDb || menus.isEmpty()) {
            System.out.println("[DB] Menggunakan menu default (fallback) karena database kosong/gagal koneksi.");
            menus.clear();
            // 12 Ramen Menus (M001 - M012)
            menus.add(new RamenMenu("M001", "Shio Tori Ramen", 38000, "ramen", 0, "Sedang", "Chashu, Tamago, Nori", "Chashu · Tamago · Nori"));
            menus.add(new RamenMenu("M002", "Shoyu Tori Ramen", 38000, "ramen", 0, "Sedang", "Chashu, Tamago, Nori", "Chashu · Tamago · Nori"));
            menus.add(new RamenMenu("M003", "Miso Tori Ramen", 38000, "ramen", 0, "Sedang", "Chashu, Tamago, Nori", "Chashu · Tamago · Nori"));
            menus.add(new RamenMenu("M004", "Paitan Tori Ramen", 38000, "ramen", 0, "Sedang", "Chashu, Tamago, Nori", "Chashu · Tamago · Nori"));
            
            menus.add(new RamenMenu("M005", "Shio Beef Ramen", 45000, "ramen", 0, "Sedang", "Beef Slices, Tamago, Nori", "Beef Slices · Tamago · Nori"));
            menus.add(new RamenMenu("M006", "Shoyu Beef Ramen", 45000, "ramen", 0, "Sedang", "Beef Slices, Tamago, Nori", "Beef Slices · Tamago · Nori"));
            menus.add(new RamenMenu("M007", "Miso Beef Ramen", 45000, "ramen", 0, "Sedang", "Beef Slices, Tamago, Nori", "Beef Slices · Tamago · Nori"));
            menus.add(new RamenMenu("M008", "Paitan Beef Ramen", 45000, "ramen", 0, "Sedang", "Beef Slices, Tamago, Nori", "Beef Slices · Tamago · Nori"));
            
            menus.add(new RamenMenu("M009", "Shio Tempura Ramen", 42000, "ramen", 0, "Sedang", "Tempura, Tamago, Nori", "Tempura · Tamago · Nori"));
            menus.add(new RamenMenu("M010", "Shoyu Tempura Ramen", 42000, "ramen", 0, "Sedang", "Tempura, Tamago, Nori", "Tempura · Tamago · Nori"));
            menus.add(new RamenMenu("M011", "Miso Tempura Ramen", 42000, "ramen", 0, "Sedang", "Tempura, Tamago, Nori", "Tempura · Tamago · Nori"));
            menus.add(new RamenMenu("M012", "Paitan Tempura Ramen", 42000, "ramen", 0, "Sedang", "Tempura, Tamago, Nori", "Tempura · Tamago · Nori"));
            
            // 5 General Menus (M013 - M017)
            menus.add(new GeneralMenu("M013", "Es Matcha Latte", 22000, "minuman", true, "Less sugar available"));
            menus.add(new GeneralMenu("M014", "Es Teh Tarik", 15000, "minuman", true, "Creamy blend"));
            menus.add(new GeneralMenu("M015", "Gyoza (6 pcs)", 28000, "snack", false, "Pork Filling"));
            menus.add(new GeneralMenu("M016", "Karaage", 25000, "snack", false, "Chicken · Mayo sauce"));
            menus.add(new GeneralMenu("M017", "Takoyaki (6)", 23000, "snack", false, "Octopus · Bonito"));
        }
    }

    private boolean loadMenusFromDatabase() {
        try {
            java.sql.Connection conn = com.muragame.pos.database.DatabaseConnection.getInstance().getConnection();
            if (conn == null) return false;
            
            String sql = "SELECT * FROM menu ORDER BY id_menu";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    String id = rs.getString("id_menu");
                    String nama = rs.getString("nama_menu");
                    double harga = rs.getDouble("harga");
                    String kategori = rs.getString("kategori");
                    String tipe = rs.getString("tipe_menu");
                    
                    if ("ramen".equalsIgnoreCase(tipe)) {
                        int pedas = rs.getInt("tingkat_kepedasan");
                        String tekstur = rs.getString("tekstur_mi");
                        String topping = rs.getString("topping_tambahan");
                        String detail = rs.getString("detail_khusus");
                        menus.add(new RamenMenu(id, nama, harga, kategori, pedas, tekstur, topping, detail));
                    } else {
                        boolean isCold = rs.getBoolean("is_cold");
                        String detail = rs.getString("detail_khusus");
                        menus.add(new GeneralMenu(id, nama, harga, kategori, isCold, detail));
                    }
                }
                return true;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("[DB] Gagal meload menu dari DB: " + e.getMessage());
            return false;
        }
    }

    public void startNewOrder() {
        String nowStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        int rand = (int) (Math.random() * 900) + 100;
        this.currentOrder = new Transaction_Order("ORD-" + nowStr + "-" + rand);
    }

    public String getMenusJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < menus.size(); i++) {
            Menu m = menus.get(i);
            
            String tag = "";
            String tagClass = "tag-s";
            if (m instanceof RamenMenu) {
                RamenMenu rm = (RamenMenu) m;
                // Since base level in menu list is 0, tag defaults to the derived soup type from name
                if (m.getNamaMenu().toLowerCase().contains("shio")) {
                    tag = "Shio";
                    tagClass = "tag-s";
                } else if (m.getNamaMenu().toLowerCase().contains("shoyu")) {
                    tag = "Shoyu";
                    tagClass = "tag-s";
                } else if (m.getNamaMenu().toLowerCase().contains("miso")) {
                    tag = "Miso";
                    tagClass = "tag-g";
                } else {
                    tag = "Paitan";
                    tagClass = "tag-r";
                }
            } else if (m instanceof GeneralMenu) {
                GeneralMenu gm = (GeneralMenu) m;
                if (gm.getIdMenu().equals("M015")) {
                    tag = "Goreng";
                } else if (gm.getIdMenu().equals("M016")) {
                    tag = "Crispy";
                } else {
                    tag = gm.isCold() ? "Dingin" : "Hangat";
                }
                tagClass = gm.isCold() ? "tag-g" : "tag-s";
            }

            sb.append(String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"cat\":\"%s\",\"price\":%.0f,\"tag\":\"%s\",\"tagClass\":\"%s\",\"detail\":\"%s\"}",
                escapeJson(m.getIdMenu()), escapeJson(m.getNamaMenu()), escapeJson(m.getKategori()), m.getHarga(),
                escapeJson(tag), escapeJson(tagClass), escapeJson(m.getDetailPesanan())
            ));
            if (i < menus.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public String getOrderId() {
        return currentOrder.getIdOrder();
    }

    public void addItem(String menuId) {
        for (Menu m : menus) {
            if (m.getIdMenu().equals(menuId)) {
                String idInv = "INV-" + System.currentTimeMillis();
                Invoice item = new Invoice(idInv, m, 1);
                currentOrder.addInvoiceItem(item);
                break;
            }
        }
    }

    public void addRamenWithCustomization(String menuId, int tingkatKepedasan, String topping, String catatanUmum) {
        for (Menu m : menus) {
            if (m.getIdMenu().equals(menuId)) {
                // Determine soup base (kuah) from the menu name
                String kuah = "Original";
                String nameLower = m.getNamaMenu().toLowerCase();
                if (nameLower.contains("shio")) {
                    kuah = "Shio";
                } else if (nameLower.contains("shoyu")) {
                    kuah = "Shoyu";
                } else if (nameLower.contains("miso")) {
                    kuah = "Miso";
                } else if (nameLower.contains("paitan")) {
                    kuah = "Paitan";
                }

                String idCust = "CUST-" + System.currentTimeMillis();
                KustomisasiPesanan customization = new KustomisasiPesanan(idCust, tingkatKepedasan, kuah, topping, catatanUmum, true);
                
                String idInv = "INV-" + System.currentTimeMillis();
                Invoice item = new Invoice(idInv, m, 1, customization);
                currentOrder.addInvoiceItem(item);
                break;
            }
        }
    }

    public void changeQty(String invoiceId, int delta) {
        currentOrder.changeQtyByInvoice(invoiceId, delta);
    }

    public void selectSvc(String serviceName) {
        if (serviceName.equalsIgnoreCase("Take Away")) {
            currentOrder.setLayanan(new TakeAwayLayanan());
        } else if (serviceName.equalsIgnoreCase("Delivery")) {
            currentOrder.setLayanan(new DeliveryLayanan());
        } else {
            currentOrder.setLayanan(new DineInLayanan());
        }
    }

    public String getOrderSummaryJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"subtotal\":%.0f,", currentOrder.getSubTotal()));
        sb.append(String.format("\"svcName\":\"%s\",", escapeJson(currentOrder.getLayanan().getTipeLayanan())));
        sb.append(String.format("\"svcPrice\":%.0f,", currentOrder.getLayanan().getBiayaLayanan()));
        sb.append(String.format("\"discount\":%.0f,", currentOrder.getDiskon()));
        sb.append(String.format("\"total\":%.0f,", currentOrder.getTotalBersih()));
        sb.append(String.format("\"customerName\":\"%s\",", escapeJson(currentOrder.getCustomer().getNamaPemesan())));
        sb.append(String.format("\"customerType\":\"%s\",", escapeJson(currentOrder.getCustomer().getType() + " · diskon " + (int)(currentOrder.getCustomer().getDiscountRate() * 100) + "%")));
        
        sb.append("\"items\":[");
        List<Invoice> items = currentOrder.getInvoiceItems();
        for (int i = 0; i < items.size(); i++) {
            Invoice item = items.get(i);
            sb.append(String.format(
                "{\"invoiceId\":\"%s\",\"menuId\":\"%s\",\"name\":\"%s\",\"price\":%.0f,\"qty\":%d,\"lineTotal\":%.0f,\"detail\":\"%s\"}",
                escapeJson(item.getIdInvoice()),
                escapeJson(item.getMenu().getIdMenu()),
                escapeJson(item.getMenu().getNamaMenu()),
                item.getMenu().getHarga(),
                item.getKuantitas(),
                item.getSubTotalItem(),
                escapeJson(item.getDetail())
            ));
            if (i < items.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    public String processPayment(String method, double payAmount, String customerName, double discountAmount) {
        if (customerName == null || customerName.trim().isEmpty() || customerName.trim().split("\\s+").length > 1) {
            return "{\"success\":false,\"message\":\"Pembayaran gagal! Nama pelanggan harus berupa satu kata saja.\"}";
        }
        
        RegularCustomer regCust = new RegularCustomer("C-" + System.currentTimeMillis(), customerName.trim());
        regCust.setCustomDiscount(discountAmount);
        currentOrder.setCustomer(regCust);

        double total = currentOrder.getTotalBersih();
        String idPay = "PAY-" + System.currentTimeMillis();
        Payment payment;
        
        if (method.equalsIgnoreCase("CASH")) {
            payment = new CashPayment(idPay);
        } else {
            payment = new QrisPayment(idPay);
        }

        boolean success = payment.prosesPembayaran(total, payAmount);

        if (success) {
            transactionRepository.save(currentOrder, payment);
            payment.printStrukPDF();

            // Auto-append to history
            historyHarian.tambahDataTransaksi(currentOrder);

            String qrisId = "";
            if (payment instanceof QrisPayment) {
                qrisId = ((QrisPayment) payment).getTransactionId();
            }

            StringBuilder itemsJson = new StringBuilder();
            itemsJson.append("[");
            List<Invoice> items = currentOrder.getInvoiceItems();
            for (int i = 0; i < items.size(); i++) {
                Invoice item = items.get(i);
                itemsJson.append(String.format(
                    "{\"name\":\"%s\",\"qty\":%d,\"total\":%.0f}",
                    escapeJson(item.getMenu().getNamaMenu()), item.getKuantitas(), item.getSubTotalItem()
                ));
                if (i < items.size() - 1) {
                    itemsJson.append(",");
                }
            }
            itemsJson.append("]");

            String receiptJson = String.format(
                "{\"success\":true,\"orderId\":\"%s\",\"date\":\"%s\",\"customerName\":\"%s\",\"subtotal\":%.0f,\"service\":%.0f,\"discount\":%.0f,\"total\":%.0f,\"method\":\"%s\",\"paid\":%.0f,\"change\":%.0f,\"qrisId\":\"%s\",\"items\":%s}",
                escapeJson(currentOrder.getIdOrder()),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()),
                escapeJson(currentOrder.getCustomer().getNamaPemesan()),
                currentOrder.getSubTotal(),
                currentOrder.getLayanan().getBiayaLayanan(),
                currentOrder.getDiskon(),
                total,
                method,
                payment.getUangBayar(),
                payment.getKembalian(),
                qrisId,
                itemsJson.toString()
            );

            startNewOrder();
            return receiptJson;
        } else {
            return "{\"success\":false,\"message\":\"Pembayaran gagal! Jumlah uang tunai kurang.\"}";
        }
    }

    public String getHistoryJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        List<Transaction_Order> transactions = historyHarian.getTransactions();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction_Order t = transactions.get(i);
            
            StringBuilder itemsSb = new StringBuilder();
            itemsSb.append("[");
            List<Invoice> items = t.getInvoiceItems();
            for (int j = 0; j < items.size(); j++) {
                Invoice item = items.get(j);
                itemsSb.append(String.format(
                    "{\"name\":\"%s\",\"qty\":%d,\"total\":%.0f}",
                    escapeJson(item.getMenu().getNamaMenu()), item.getKuantitas(), item.getSubTotalItem()
                ));
                if (j < items.size() - 1) {
                    itemsSb.append(",");
                }
            }
            itemsSb.append("]");

            sb.append(String.format(
                "{\"orderId\":\"%s\",\"date\":\"%s\",\"customerName\":\"%s\",\"subtotal\":%.0f,\"service\":%.0f,\"serviceName\":\"%s\",\"discount\":%.0f,\"total\":%.0f,\"items\":%s}",
                escapeJson(t.getIdOrder()),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(t.getTanggalWaktu().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()),
                escapeJson(t.getCustomer().getNamaPemesan()),
                t.getSubTotal(),
                t.getLayanan().getBiayaLayanan(),
                escapeJson(t.getLayanan().getTipeLayanan()),
                t.getDiskon(),
                t.getTotalBersih(),
                itemsSb.toString()
            ));
            if (i < transactions.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void exitApp() {
        System.exit(0);
    }

    /**
     * Mengambil riwayat transaksi dari database MySQL berdasarkan filter waktu.
     * @param filter "today", "week", "month", "year", "all"
     * @return JSON array string
     */
    public String getHistoryFromDB(String filter) {
        return transactionRepository.getHistoryFromDB(filter);
    }

    public String addMenu(String id, String name, double price, String category) {
        for (Menu m : menus) {
            if (m.getIdMenu().equalsIgnoreCase(id)) {
                return "{\"success\":false,\"message\":\"ID Menu sudah digunakan!\"}";
            }
        }
        
        String tipe = category.equalsIgnoreCase("ramen") ? "ramen" : "general";
        Menu newMenu;
        if (tipe.equals("ramen")) {
            newMenu = new RamenMenu(id, name, price, category, 0, "Sedang", "Chashu, Tamago, Nori", "Chashu · Tamago · Nori");
        } else {
            boolean isCold = category.equalsIgnoreCase("minuman");
            String detail = category.equalsIgnoreCase("minuman") ? "Less sugar available" : "Japanese snack";
            newMenu = new GeneralMenu(id, name, price, category, isCold, detail);
        }
        
        menus.add(newMenu);
        
        try {
            java.sql.Connection conn = com.muragame.pos.database.DatabaseConnection.getInstance().getConnection();
            if (conn != null) {
                String sql = "INSERT INTO menu (id_menu, nama_menu, harga, kategori, tipe_menu, tingkat_kepedasan, tekstur_mi, topping_tambahan, is_cold, detail_khusus) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, id);
                    ps.setString(2, name);
                    ps.setDouble(3, price);
                    ps.setString(4, category.toLowerCase());
                    ps.setString(5, tipe);
                    if (tipe.equals("ramen")) {
                        ps.setInt(6, 0);
                        ps.setString(7, "Sedang");
                        ps.setString(8, "Chashu, Tamago, Nori");
                        ps.setNull(9, java.sql.Types.BOOLEAN);
                        ps.setString(10, "Chashu · Tamago · Nori");
                    } else {
                        ps.setNull(6, java.sql.Types.INTEGER);
                        ps.setNull(7, java.sql.Types.VARCHAR);
                        ps.setNull(8, java.sql.Types.VARCHAR);
                        ps.setBoolean(9, category.equalsIgnoreCase("minuman"));
                        ps.setString(10, category.equalsIgnoreCase("minuman") ? "Less sugar available" : "Japanese snack");
                    }
                    ps.executeUpdate();
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("[DB] Gagal menyimpan menu baru ke DB: " + e.getMessage());
        }
        
        return "{\"success\":true}";
    }

    public String editMenu(String id, String name, double price, String category) {
        Menu target = null;
        for (Menu m : menus) {
            if (m.getIdMenu().equalsIgnoreCase(id)) {
                target = m;
                break;
            }
        }
        if (target == null) {
            return "{\"success\":false,\"message\":\"Menu tidak ditemukan!\"}";
        }
        
        target.setNamaMenu(name);
        target.setHarga(price);
        target.setKategori(category);
        
        try {
            java.sql.Connection conn = com.muragame.pos.database.DatabaseConnection.getInstance().getConnection();
            if (conn != null) {
                String sql = "UPDATE menu SET nama_menu = ?, harga = ?, kategori = ? WHERE id_menu = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setDouble(2, price);
                    ps.setString(3, category.toLowerCase());
                    ps.setString(4, id);
                    ps.executeUpdate();
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("[DB] Gagal mengupdate menu di DB: " + e.getMessage());
        }
        
        return "{\"success\":true}";
    }

    public String deleteMenu(String id) {
        Menu target = null;
        for (Menu m : menus) {
            if (m.getIdMenu().equalsIgnoreCase(id)) {
                target = m;
                break;
            }
        }
        if (target == null) {
            return "{\"success\":false,\"message\":\"Menu tidak ditemukan!\"}";
        }
        
        try {
            java.sql.Connection conn = com.muragame.pos.database.DatabaseConnection.getInstance().getConnection();
            if (conn != null) {
                String sql = "DELETE FROM menu WHERE id_menu = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, id);
                    ps.executeUpdate();
                }
            }
            menus.remove(target);
        } catch (java.sql.SQLException e) {
            System.err.println("[DB] Gagal menghapus menu dari DB: " + e.getMessage());
            return "{\"success\":false,\"message\":\"Tidak dapat menghapus menu karena menu ini sudah memiliki riwayat transaksi.\"}";
        }
        
        return "{\"success\":true}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
