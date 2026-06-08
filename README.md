# MuragamePOS - Aplikasi Kasir Desktop Restoran Jepang

MuragamePOS adalah aplikasi Point of Sale (POS) desktop kasir restoran Jepang (Muragame Resto) yang dirancang khusus untuk memenuhi kriteria tugas akhir mata kuliah **Pemrograman Berorientasi Objek (PBO)**. 

Aplikasi ini menggunakan arsitektur modern berkinerja tinggi berbasis **JavaFX WebView**, yang menggabungkan kekuatan logika backend terstruktur **Java** dengan visual antarmuka (frontend) **HTML5, CSS3, dan JavaScript** yang interaktif, responsif, dan kaya estetika.

---

## 🚀 Fitur Utama

1.  **Kustomisasi Pesanan Ramen**: Klik menu Ramen untuk memunculkan modal kustomisasi tingkat pedas (0-5), checkbox topping tambahan (Chashu, Tamago, Nori, Tempura, Narutomaki), dan catatan khusus untuk koki.
2.  **Variasi Kuah Otomatis**: Kuah ramen (*Shio*, *Shoyu*, *Miso*, *Paitan*) terintegrasi langsung sebagai variasi menu utama dan otomatis dideteksi ke dalam objek kustomisasi Java backend.
3.  **Manajemen Layanan**: Mendukung pilihan tipe layanan *Dine In* (servis +Rp 5.000), *Take Away* (+Rp 2.000), dan *Delivery* (+Rp 15.000).
4.  **Diskon Member Otomatis**: Integrasi klasifikasi pelanggan (*Regular* vs *Member*) dengan perhitungan potongan diskon otomatis sebesar 10% untuk pemegang kartu member.
5.  **Multi-Metode Pembayaran**: Simulasi pembayaran menggunakan Uang Tunai (dilengkapi kalkulator kembalian) dan QRIS (simulasi kode QR dinamis).
6.  **Riwayat Transaksi Harian & Cetak Ulang Struk**: Tab khusus yang mencatat total omset, jumlah transaksi, detail pesanan, dan tombol untuk memanggil kembali struk thermal lama (*Reprint Struk*).
7.  **Database Lokal Sederhana**: Setiap transaksi sukses secara otomatis tersimpan ke dalam file teks lokal `transactions.txt`.
8.  **Browser Mock Engine (Penting untuk Kolaborasi)**: File HTML utama dilengkapi mesin tiruan (*Mock Engine*) di sisi JavaScript. Jika Anda membuka file HTML langsung di Google Chrome/Edge (tanpa Java), fitur keranjang, kustomisasi, pembayaran, cetak struk, dan riwayat order akan tetap berjalan normal sehingga memudahkan tim desainer frontend menguji tampilan.

---

## 🛠️ Prasyarat Sistem

Sebelum menjalankan aplikasi, pastikan komputer Anda telah terpasang:
*   **Java Development Kit (JDK) 21 atau 22**
*   **Maven** (Opsional, karena proyek sudah menyertakan Maven Wrapper `mvnw`)

---

## 🏁 Panduan Setup & Menjalankan Aplikasi

Unduh atau klon repositori ini, lalu buka terminal di direktori proyek:

### 1. Kompilasi Proyek (Build)
Jalankan perintah berikut untuk mengunduh dependensi JavaFX dan mengompilasi file sumber Java serta menyalin aset HTML/JS:
```powershell
# Di Windows (PowerShell/CMD):
.\mvnw clean compile

# Di Linux / macOS:
./mvnw clean compile
```

### 2. Jalankan Aplikasi Native (JavaFX Desktop GUI)
Untuk meluncurkan aplikasi kasir desktop secara native di sistem operasi Anda:
```powershell
# Di Windows (PowerShell/CMD):
.\mvnw javafx:run

# Di Linux / macOS:
./mvnw javafx:run
```

### 3. Pengujian di Browser (Tanpa Menjalankan Java)
Jika Anda hanya ingin menyunting atau memverifikasi visual antarmuka tanpa menjalankan Java backend, cukup buka file berikut langsung menggunakan browser web (Chrome/Firefox/Edge):
```text
muragamepos_transaksi.html
```
*Javascript Mock Engine akan mendeteksi tidak adanya bridge Java dan mengaktifkan simulasi POS secara otomatis di browser.*

---

## 📐 Arsitektur Kelas OOP (Sesuai UML Proposal PBO)

Aplikasi ini mengimplementasikan prinsip-prinsip dasar PBO seperti *Inheritance*, *Polymorphism*, *Abstraction*, *Encapsulation*, dan *Interfaces* dengan struktur kelas sebagai berikut:

*   **Menu (Abstract Class)**: Superclass dari item restoran.
    *   `RamenMenu` (Subclass): Menyimpan detail kustomisasi mi dan topping tambahan.
    *   `GeneralMenu` (Subclass): Menyimpan detail minuman/snack non-ramen beserta opsi suhu (panas/dingin).
*   **Customer (Abstract Class)** & **IDiscountable (Interface)**:
    *   `MemberCustomer` (Subclass): Mengoverride metode `hitungDiskon()` untuk memotong subtotal sebesar 10%.
    *   `RegularCustomer` (Subclass): Diskon default 0%.
*   **Layanan (Abstract Class)**:
    *   `DineInLayanan`, `TakeAwayLayanan`, `DeliveryLayanan` (Subclasses): Mengoverride besaran biaya layanan tambahan restoran.
*   **Payment (Abstract Class)** & **IPrintable (Interface)**:
    *   `CashPayment` (Subclass): Logika pemrosesan uang tunai & validasi kembalian uang.
    *   `QrisPayment` (Subclass): Menghasilkan ID transaksi pembayaran QRIS dinamis.
*   **Invoice**: Mewakili baris pesanan tunggal (*order item*) beserta kuantitas dan referensi objek `KustomisasiPesanan`.
*   **Transaction_Order**: Objek penampung transaksi aktif (keranjang belanja) yang bertugas menghitung subtotal, diskon, biaya layanan, dan nominal bersih yang harus dibayar.
*   **History_Harian**: Kelas rekapitulasi data transaksi harian restoran.
*   **JavaBridge**: Penghubung (*Controller Bridge*) antara eksekusi JavaScript WebView dengan objek Java Backend.

---

## 📁 Struktur Direktori Penting

```text
muragame-pos/
│
├── src/main/java/com/muragame/pos/
│   ├── Main.java                 # Entrypoint classpath program
│   ├── App.java                  # Launcher window JavaFX
│   ├── bridge/
│   │   └── JavaBridge.java       # Jembatan komunikasi JS <-> Java
│   ├── model/                    # Logika domain OOP (Menu, Customer, dll)
│   └── repository/
│       └── TransactionRepository.java # Penyimpanan transaksi ke file teks
│
├── src/main/resources/com/muragame/pos/
│   ├── muragamepos_transaksi.html # Halaman utama WebView POS
│   └── js/components/            # Modul Web Components terpisah:
│       ├── SidebarKiri.js        # Komponen navigasi & logo
│       ├── MenuSection.js        # Komponen filter & grid menu
│       ├── DetailPesanan.js      # Komponen kalkulator keranjang
│       ├── PopupKustomisasi.js   # Komponen modal kustom ramen
│       └── PopupPembayaran.js    # Komponen modal kasir & print struk
│
├── muragamepos_transaksi.html    # Salinan HTML untuk pengujian browser lokal
├── js/components/                # Salinan komponen JS untuk pengujian browser lokal
├── transactions.txt              # Database log transaksi (dibuat otomatis)
├── pom.xml                       # Konfigurasi Maven dependencies
└── README.md                     # Panduan penggunaan proyek
```
