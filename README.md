<div align="center">
  <br />
    <img src="app/src/main/res/mipmap-xxhdpi/ic_launcher.webp" alt="Logo" width="80" height="80" onerror="this.onerror=null; this.src='https://cdn-icons-png.flaticon.com/512/3135/3135715.png'">
  <h1 align="center">💸 SafeKuy</h1>
  <p align="center">
    <strong>Catat keuanganmu dengan cerdas, aman, dan tanpa ribet!</strong>
    <br />
    Aplikasi pencatatan keuangan pintar bertenaga AI untuk gaya hidup masa kini.
    <br />
    <br />
    <a href="#about-the-project">Tentang Project</a>
    ·
    <a href="#fitur-unggulan">Fitur</a>
    ·
    <a href="#tech-stack">Tech Stack</a>
    ·
    <a href="#getting-started">Mulai Install</a>
  </p>
</div>

<hr />

## 📖 About The Project

Bosan dengan aplikasi pencatatan keuangan yang kaku dan ribet? Kenalan yuk sama **SafeKuy**! 🚀

**SafeKuy** (gabungan dari kata *Safe/Save* dan *Kuy/Yuk*) adalah aplikasi tracker keuangan berbasis Android yang didesain buat kamu yang pengen ngatur duit dengan cara yang asik, cepat, dan modern. Nggak cuma sekadar nyatet pemasukan dan pengeluaran, SafeKuy juga terintegrasi dengan **Google Gemini AI** untuk kasih kamu pengalaman mencatat keuangan yang jauh lebih *smart*.

Nggak perlu pusing mikirin di mana duit kamu menguap tiap bulannya, biarkan SafeKuy yang bantuin kamu nge-track semuanya dengan tampilan antarmuka yang rapi, laporan yang mudah dibaca, dan bahkan widget untuk nambahin transaksi secepat kilat dari *homescreen*.

Kuy, mulai sehat finansial dari sekarang! 💰✨

## ✨ Fitur Unggulan

- 🤖 **Smart AI Powered:** Dilengkapi integrasi **Google Gemini AI SDK** yang bikin pencatatan keuangan kamu lebih pintar.
- ⚡ **Quick Add Widget:** Tambah transaksi langsung dari *homescreen* hp kamu tanpa perlu buka aplikasi pakai *SafeKuy Widget*.
- 📊 **Insightful Reports:** Pantau *cashflow* bulanan kamu lewat *ReportFragment* dengan visualisasi yang asik dan gampang dipahami.
- 📅 **Calendar View:** Cek riwayat transaksi per hari dengan tampilan kalender yang interaktif.
- 🔒 **Local First & Secure:** Data kamu aman tersimpan secara lokal menggunakan Room Database. Cepat, tanpa nunggu *loading* muter-muter.
- 🎨 **Modern UI/UX:** Dibangun pakai *ViewBinding* dan *Material Components* buat ngasih *feel* navigasi yang mulus nan memanjakan mata.

## 🛠️ Tech Stack

SafeKuy dibangun dengan teknologi Android terkini (*Modern Android Development*) biar performanya tetap *ngebut* dan stabil:

*   **Language:** [Kotlin](https://kotlinlang.org/) 💜
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Local Database:** [Room](https://developer.android.com/training/data-storage/room)
*   **Networking:** [Retrofit2](https://square.github.io/retrofit/) & Gson
*   **Asynchronous:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & Flow
*   **AI Integration:** [Google Gemini AI SDK](https://ai.google.dev/)
*   **UI Components:** ViewBinding, Material Design, RecyclerView

## 🚀 Getting Started

Mau coba *run* SafeKuy di mesin kamu sendiri? Gampang banget, tinggal ikutin *step-by-step* di bawah ini:

### Prerequisites
*   Android Studio (versi terbaru direkomendasikan)
*   Android SDK API Level 35
*   **API Key Google Gemini** (Dapetin dari [Google AI Studio](https://aistudio.google.com/))

### Installation

1.  **Clone repo ini**
    ```bash
    git clone https://github.com/RizalMuhammad12/Mobile-Safekuy.git
    ```
2.  **Buka project di Android Studio**
    Buka Android Studio > *Open* > Pilih folder `SafeKuy` yang baru aja di-clone.
3.  **Setup API Key Gemini**
    Buka file `local.properties` di *root directory* project (bikin filenya kalau belum ada), terus tambahin baris ini:
    ```properties
    API_KEY=masukin_api_key_gemini_kamu_di_sini
    ```
    *Notes: Tenang aja, file `local.properties` udah di-ignore kok sama Git, jadi API Key kamu dijamin nggak akan bocor ke publik! 🤫*
4.  **Build & Run**
    Tunggu Gradle kelar nge-sync, terus klik tombol ▶️ **Run** (atau pencet `Shift + F10`) buat ngejalanin aplikasinya di emulator atau *device* fisik kamu.

## 🤝 Contributing

Punya ide brilian buat bikin SafeKuy makin kece? *Pull request* selalu terbuka lebar!

1. Fork Project ini
2. Bikin Feature Branch kamu (`git checkout -b feature/FiturKece`)
3. Commit perubahan kamu (`git commit -m 'Add FiturKece'`)
4. Push ke Branch (`git push origin feature/FiturKece`)
5. Buka Pull Request

## 💌 Kontak & Support

Kalo nemu *bug* atau mau ngasih masukan, langsung aja bikin **Issue** di repo ini. 
Jangan lupa kasih ⭐ (Star) buat repo ini kalau kamu ngerasa SafeKuy ngebantu kamu!

## ⚖️ Lisensi & Hak Cipta

Secara default, semua hak cipta atas kode dan konsep aplikasi ini adalah milik kreator (kamu). Dilarang keras menyalin, mendistribusikan, atau menggunakan kode dari repository ini untuk kepentingan komersial tanpa izin tertulis. **All Rights Reserved.**

---
<div align="center">
  Dibuat dengan ☕ dan 💻 untuk masa depan finansial yang lebih cerah!
</div>
