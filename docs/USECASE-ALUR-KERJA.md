# Use Case & Alur Kerja UTS_PBO2 (Visual)

Dokumen ini menyajikan use case dan alur kerja utama aplikasi dalam bentuk visual (Mermaid) agar mudah dipahami di halaman GitHub.

## 1) Use Case Utama

```mermaid
flowchart LR
    Admin[Actor: Admin] --> UC1[Login]
    Admin --> UC2[Dashboard]
    Admin --> UC3[Kelola Mahasiswa]
    Admin --> UC4[Kelola Dosen]
    Admin --> UC5[Kelola Mata Kuliah]
    Admin --> UC6[Ganti Password]

    Operator[Actor: Operator] --> UC1
    Operator --> UC2
    Operator --> UC7[Kelola KRS]
    Operator --> UC8[Kelola Nilai]
    Operator --> UC6
```

## 2) Activity Flow Admin

```mermaid
flowchart TD
    A0([Start]) --> A1[Login Admin]
    A1 --> A2[Dashboard: monitoring statistik]
    A2 --> A3{Pilih modul}
    A3 -->|Mahasiswa| A4[CRUD Data Mahasiswa]
    A3 -->|Dosen| A5[CRUD Data Dosen]
    A3 -->|Mata Kuliah| A6[CRUD Data MK & Jadwal]
    A4 --> A7[Simpan ke Database]
    A5 --> A7
    A6 --> A7
    A7 --> A8{Ganti password?}
    A8 -->|Ya| A9[Ganti Password]
    A8 -->|Tidak| A10([End])
    A9 --> A10
```

## 3) Activity Flow Operator

```mermaid
flowchart TD
    O0([Start]) --> O1[Login Operator]
    O1 --> O2[Dashboard: monitoring operasional]
    O2 --> O3[Modul KRS]
    O3 --> O4[Pilih Mahasiswa + Semester]
    O4 --> O5[Tampilkan daftar MK]
    O5 --> O6[Tambah/Batalkan KRS]
    O6 --> O7[Modul Nilai]
    O7 --> O8[Pilih Mahasiswa + Mata Kuliah]
    O8 --> O9[Input komponen nilai]
    O9 --> O10[Hitung nilai akhir + grade otomatis]
    O10 --> O11[Simpan/Update/Hapus nilai]
    O11 --> O12{Ganti password?}
    O12 -->|Ya| O13[Ganti Password]
    O12 -->|Tidak| O14([End])
    O13 --> O14
```

## 4) Data Flow Ringkas Dashboard

```mermaid
flowchart LR
    DB[(Supabase Tables)]
    DB --> M[Mahasiswa]
    DB --> K[KRS]
    DB --> N[Nilai]

    M --> S1[Total Mahasiswa]
    M --> S2[Filter Jurusan]
    K --> S3[Sudah/Belum KRS]
    N --> S4[Rata-rata Grade]
    N --> S5[Distribusi Grade]

    S1 --> UI[Dashboard UI]
    S2 --> UI
    S3 --> UI
    S4 --> UI
    S5 --> UI
```

## 5) Catatan Implementasi Visual

- UI dibangun dengan **Java Swing**.
- Visualisasi dashboard memakai **custom Java2D drawing** (tanpa chart library eksternal):
  - `ChartComponents.StackedBarChart`
  - `ChartComponents.DonutChart`
- Perhitungan data dashboard berada di `MainMenu.fetchDashboardStats(...)`.

