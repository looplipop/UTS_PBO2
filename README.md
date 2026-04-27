# UTS_PBO2 - Sistem Informasi Akademik UTB

`UTS_PBO2` adalah aplikasi desktop **Java Swing** untuk pengelolaan data akademik kampus (mahasiswa, dosen, mata kuliah, KRS, dan nilai) dengan backend **Supabase**.

## Fitur Utama

1. Login multi-role (`admin` dan `operator`).
2. Dashboard ringkasan data akademik.
3. Manajemen data mahasiswa.
4. Manajemen data dosen.
5. Manajemen data mata kuliah.
6. Pengelolaan KRS.
7. Pengelolaan nilai (absensi, tugas, quiz, UTS, UAS, nilai akhir, grade, status).
8. Ganti password user.

## Struktur Folder Project

```text
UTS_PBO2/
├── README.md
├── .gitignore
├── uts_pbo2.sql                 # skema SQL awal
├── docs/
│   └── images/                  # screenshot UI + skema visual database
├── uts1/                        # project utama Java Swing (NetBeans/Ant)
│   ├── src/uts1/                # source code aplikasi
│   ├── lib/                     # dependency JDBC
│   ├── supabase/migrations/     # migration SQL
│   ├── build.xml                # build script Ant
│   └── manifest.mf
└── uts/                         # project lama/arsip
```

## Tugas & Hak Akses Role

| Role | Menu yang Bisa Diakses | Tugas Utama |
|---|---|---|
| **Admin** | Dashboard, Mahasiswa, Dosen, Mata Kuliah, Ganti Password | Monitoring data akademik, kelola master data mahasiswa/dosen/mata kuliah, maintenance akun sendiri |
| **Operator** | Dashboard, KRS, Nilai, Ganti Password | Input dan update transaksi KRS, input komponen nilai sampai nilai akhir, maintenance akun sendiri |

## Cara Menggunakan

### 1) Pakai file `.jar` (siap pakai)

1. Download rilis terbaru di: `https://github.com/looplipop/UTS_PBO2/releases/latest`
2. Ambil file `uts1-dist-v1.0.0.zip` lalu ekstrak.
3. Set environment variable Supabase:

```bash
export SUPABASE_URL="https://your-project.supabase.co"
export SUPABASE_ANON_KEY="your-anon-key"
export SUPABASE_SERVICE_KEY="your-service-role-key"
```

4. Jalankan aplikasi:

```bash
cd uts1-dist-v1.0.0
java -jar uts1.jar
```

### 2) Build dari source code

```bash
cd uts1
ant clean test jar
java -jar dist/uts1.jar
```

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
