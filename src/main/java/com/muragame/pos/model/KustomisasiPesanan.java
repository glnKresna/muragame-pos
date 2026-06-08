package com.muragame.pos.model;

public class KustomisasiPesanan {
    private String idKustomisasi;
    private int tingkatKepedasan;
    private String kuah;
    private String topping;
    private String catatanUmum;
    private boolean isRamen;

    public KustomisasiPesanan() {}

    public KustomisasiPesanan(String idKustomisasi, int tingkatKepedasan, String kuah, 
                              String topping, String catatanUmum, boolean isRamen) {
        this.idKustomisasi = idKustomisasi;
        this.tingkatKepedasan = tingkatKepedasan;
        this.kuah = kuah;
        this.topping = topping;
        this.catatanUmum = catatanUmum;
        this.isRamen = isRamen;
    }

    public String getIdKustomisasi() {
        return idKustomisasi;
    }

    public void setIdKustomisasi(String idKustomisasi) {
        this.idKustomisasi = idKustomisasi;
    }

    public int getTingkatKepedasan() {
        return tingkatKepedasan;
    }

    public void setTingkatKepedasan(int tingkatKepedasan) {
        this.tingkatKepedasan = tingkatKepedasan;
    }

    public String getKuah() {
        return kuah;
    }

    public void setKuah(String kuah) {
        this.kuah = kuah;
    }

    public String getTopping() {
        return topping;
    }

    public void setTopping(String topping) {
        this.topping = topping;
    }

    public String getCatatanUmum() {
        return catatanUmum;
    }

    public void setCatatanUmum(String catatanUmum) {
        this.catatanUmum = catatanUmum;
    }

    public boolean isRamen() {
        return isRamen;
    }

    public void setRamen(boolean ramen) {
        isRamen = ramen;
    }

    public String getDetail() {
        if (isRamen) {
            return getRamenDetail();
        } else {
            return getCatatanUmum();
        }
    }

    public void applyToOrder(Transaction_Order order) {
    }

    public boolean isValid() {
        return tingkatKepedasan >= 0 && tingkatKepedasan <= 5;
    }

    public String getRamenDetail() {
        StringBuilder sb = new StringBuilder();
        sb.append("Kuah: ").append(kuah);
        sb.append(" · Pedas ").append(tingkatKepedasan);
        if (topping != null && !topping.trim().isEmpty()) {
            sb.append(" · Topping: ").append(topping);
        }
        if (catatanUmum != null && !catatanUmum.trim().isEmpty()) {
            sb.append(" (").append(catatanUmum).append(")");
        }
        return sb.toString();
    }
}
