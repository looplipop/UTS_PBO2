# UTS_PBO2 - Sistem Informasi Akademik UTB

`UTS_PBO2` adalah aplikasi desktop Java Swing untuk pengelolaan data akademik kampus (mahasiswa, dosen, mata kuliah, KRS, dan nilai) dengan backend Supabase.

## Pengenalan Aplikasi

Aplikasi ini dirancang untuk memisahkan alur kerja berdasarkan peran pengguna agar operasional akademik lebih rapi:

1. **Admin** berfokus pada master data akademik dan monitoring dashboard.
2. **Operator** berfokus pada transaksi akademik (KRS dan nilai).

## Hak Akses Berdasarkan Role

| Role | Fitur yang Bisa Diakses |
|---|---|
| **Admin** | Dashboard, Mahasiswa, Dosen, Mata Kuliah, Ganti Password |
| **Operator** | KRS, Nilai, Ganti Password |

## Skema Visual Database

![Skema Visual](docs/images/skema-visual.png)

## Screenshot Aplikasi

### 1) Login Page
![Login Page](docs/images/1-login-page.png)

### 2) Dashboard Page (Admin)
![Dashboard Admin](docs/images/2-dashboard-admin.png)

### 3) Mahasiswa Page (Admin)
![Mahasiswa Admin](docs/images/3-mahasiswa-admin.png)

### 4) Dosen Page (Admin)
![Dosen Admin](docs/images/4-dosen-admin.png)

### 5) Mata Kuliah Page (Admin)
![Mata Kuliah Admin](docs/images/5-matakuliah-admin.png)

### 6) Ganti Password Page
![Ganti Password](docs/images/6-ganti-password.png)

### 7) KRS Page (Operator)
![KRS Operator](docs/images/7-krs-operator.png)

### 8) Nilai Page (Operator)
![Nilai Operator](docs/images/8-nilai-operator.png)

## Cara Menjalankan

Set environment variable terlebih dahulu:

```bash
export SUPABASE_URL="https://your-project.supabase.co"
export SUPABASE_ANON_KEY="your-anon-key"
export SUPABASE_SERVICE_KEY="your-service-role-key"
```

```bash
cd uts1
ant clean test
ant run
```

## Struktur Penting

- `uts1/` -> project utama Java Swing (NetBeans/Ant).
- `uts_pbo2.sql` -> dump/skema SQL awal.
- `docs/images/` -> skema visual dan screenshot antarmuka.
