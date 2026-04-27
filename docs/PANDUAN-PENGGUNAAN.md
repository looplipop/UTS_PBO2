# Panduan Penggunaan UTS_PBO2 (Admin & Operator)

Dokumen ini menjelaskan cara menjalankan aplikasi dan alur penggunaan setiap menu secara praktis.

## 1. Persiapan

1. Pastikan Java terpasang (sesuai project: Java 24).
2. Siapkan database Supabase dengan tabel utama: `users`, `Mahasiswa`, `Dosen`, `Matakuliah`, `KRS`, `Nilai`.
3. Set environment variable:

```bash
export SUPABASE_URL="https://your-project.supabase.co"
export SUPABASE_ANON_KEY="your-anon-key"
export SUPABASE_SERVICE_KEY="your-service-role-key"
```

## 2. Menjalankan Aplikasi

### Opsi A - Dari release (rekomendasi)

```bash
# setelah download dan ekstrak uts1-dist-v1.0.0.zip
cd uts1-dist-v1.0.0
java -jar uts1.jar
```

### Opsi B - Build sendiri

```bash
cd uts1
ant clean test jar
java -jar dist/uts1.jar
```

## 3. Login dan Hak Akses

1. Buka aplikasi, isi `username` dan `password`, lalu klik **Masuk ke Dashboard**.
2. Sistem membaca role dari tabel `users` dan otomatis menampilkan menu sesuai role.

| Role | Menu Aktif |
|---|---|
| **Admin** | Dashboard, Mahasiswa, Dosen, Mata Kuliah, Ganti Password |
| **Operator** | Dashboard, KRS, Nilai, Ganti Password |

## 4. Alur Kerja Admin

### 4.1 Dashboard
- Lihat ringkasan data akademik (mahasiswa, KRS, nilai, distribusi grade).
- Gunakan sebagai monitoring cepat kondisi data.

### 4.2 Menu Mahasiswa
- Isi data mahasiswa, lalu gunakan tombol: **Tambah / Update / Hapus / Reset / Refresh**.
- Tersedia kolom pencarian untuk filter berdasarkan NIM, nama, atau prodi.

### 4.3 Menu Dosen
- Kelola data dosen (kode, nama, mata kuliah diampu).
- Aksi yang tersedia: **Tambah / Update / Hapus / Reset / Refresh** + pencarian.

### 4.4 Menu Mata Kuliah
- Kelola mata kuliah dan jadwal (kode, nama, SKS, prodi, dosen, semester, hari, jam, ruangan).
- Aksi: **Tambah / Update / Hapus / Reset / Refresh**.
- Bisa filter/pencarian untuk mempercepat pencarian data.

### 4.5 Ganti Password
- Isi password lama dan baru, lalu simpan.
- Gunakan menu ini secara berkala untuk keamanan akun.

## 5. Alur Kerja Operator

### 5.1 Dashboard
- Pantau ringkasan operasional (jumlah data, progres KRS/nilai, grade).

### 5.2 Menu KRS
1. Pilih mahasiswa dan semester.
2. Klik **Tampilkan** untuk memuat daftar mata kuliah.
3. Gunakan pencarian untuk menemukan mata kuliah.
4. Pilih mata kuliah lalu klik **Tambah KRS**.
5. Untuk membatalkan, pilih data KRS lalu klik **Batalkan KRS Terpilih**.
6. Jika memenuhi syarat akademik, operator dapat memakai fitur:
   - **Ambil MK Atas**
   - **Ambil MK Mengulang**

### 5.3 Menu Nilai
1. Pilih mahasiswa dan mata kuliah.
2. Isi komponen nilai: **Absensi, Tugas, Quiz, UTS, UAS**.
3. Sistem otomatis menghitung **Nilai Akhir** dan **Grade**.
4. Simpan dengan **Tambah**, koreksi dengan **Update**, hapus dengan **Hapus**.
5. Gunakan fitur pencarian dan filter grade untuk audit data nilai.

### 5.4 Ganti Password
- Operator juga dapat mengganti password akun lewat menu yang sama.

## 6. Troubleshooting Singkat

| Masalah | Solusi Cepat |
|---|---|
| Login gagal | Cek username/password pada tabel `users` dan pastikan role valid (`Admin`/`Operator`) |
| Gagal terhubung ke server | Cek `SUPABASE_URL`, `SUPABASE_ANON_KEY`, `SUPABASE_SERVICE_KEY` |
| Data tidak bisa dihapus | Pastikan data tidak direferensikan tabel lain (mis. KRS/Nilai) |
| Dropdown data kosong | Pastikan tabel master (`Mahasiswa`, `Dosen`, `Matakuliah`) sudah terisi |

