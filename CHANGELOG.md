# Changelog

Semua perubahan penting pada proyek SafeKuy akan dicatat dalam file ini.

Format pencatatan berdasarkan pedoman [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
dan proyek ini menganut sistem [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-07-01

### Added (Ditambahkan)
- **Fitur Baru: Daily Income Split (Bagi Hasil)**
  - Pengguna dapat membagi pendapatan harian secara otomatis berdasarkan kategori (Default: Cicilan, Bensin, Tabungan, Dana Cadangan).
  - **Kategori Dinamis**: Pengguna kini dapat menambahkan kategori baru, mengubah nama kategori (misal: "Sumbangan"), dan mengubah persentasenya secara bebas di menu Pengaturan.
  - Sistem dilengkapi validasi agar total seluruh persentase wajib **100%**.
  - Menyediakan *checklist* dan riwayat *dropdown* lokasi penyimpanan (Tunai, Dana, GoPay, SeaBank, dll) untuk tiap-tiap kategori.
  - Data pembagian otomatis tersimpan secara lokal dan aman.
- **Halaman Riwayat (History)**
  - Menyimpan riwayat pembagian pendapatan harian di masa lalu lengkap dengan catatan dan status.
- **Halaman Statistik (Statistics)**
  - Menampilkan ringkasan total pendapatan dan grafik *Pie Chart* interaktif untuk melihat sebaran alokasi dana secara visual.
- **Halaman Pengaturan (Settings)**
  - Pengguna dapat menyesuaikan persentase alokasi masing-masing kategori secara bebas (Total wajib 100%).

### Changed (Diubah)
- Menambahkan tab navigasi baru di *Bottom Navigation Bar* (Bagi Hasil).
- Upgrade *Room Database* dari versi 2 ke versi 3 dengan tambahan tabel (Entity) `DailySplit`. Migrasi berjalan aman, data lama (versi 1.0.2) tidak terhapus.

---

## [1.0.2] - Rilis Sebelumnya

### Added (Ditambahkan)
- Fitur pencatatan transaksi masuk dan keluar (Pemasukan & Pengeluaran).
- Tampilan Laporan (Report) dan visualisasi transaksi dasar.
- Profil Pengguna (Profile).
- Dukungan *Widget* untuk penambahan data transaksi dengan cepat.

*(Catatan: Versi-versi 1.0.x merupakan versi rilis awal yang stabil, mencakup bugfix minor dari 1.0.0).*
