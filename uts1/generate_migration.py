import random
import string

def generate_sql():
    sql = """
-- Drop existing tables
DROP TABLE IF EXISTS "Nilai";
DROP TABLE IF EXISTS "KRS";
DROP TABLE IF EXISTS "Matakuliah";
DROP TABLE IF EXISTS "Dosen";
DROP TABLE IF EXISTS "Mahasiswa";

-- Mahasiswa
CREATE TABLE "Mahasiswa" (
    "nim" VARCHAR PRIMARY KEY,
    "nama" VARCHAR,
    "jenis_kelamin" VARCHAR, 
    "prodi" VARCHAR, 
    "kelas" VARCHAR,  
    "angkatan" VARCHAR, 
    "status_aktif" VARCHAR, 
    "semester" INTEGER 
);

-- Dosen
CREATE TABLE "Dosen" (
    "kode_dosen" VARCHAR PRIMARY KEY,
    "nama" VARCHAR,
    "mata_kuliah" VARCHAR
);

-- Matakuliah
CREATE TABLE "Matakuliah" (
    "kode_matakuliah" VARCHAR PRIMARY KEY,
    "nama_matakuliah" VARCHAR,
    "sks" INTEGER,
    "prodi" VARCHAR,
    "kode_dosen" VARCHAR REFERENCES "Dosen"("kode_dosen"),
    "semester" INTEGER,
    "hari" VARCHAR,
    "jam_mulai" VARCHAR,
    "jam_selesai" VARCHAR,
    "ruangan" VARCHAR
);

-- KRS
CREATE TABLE "KRS" (
    "id" SERIAL PRIMARY KEY,
    "nim" VARCHAR REFERENCES "Mahasiswa"("nim"),
    "kode_matakuliah" VARCHAR REFERENCES "Matakuliah"("kode_matakuliah")
);

-- Nilai
CREATE TABLE "Nilai" (
    "id" SERIAL PRIMARY KEY,
    "nim" VARCHAR REFERENCES "Mahasiswa"("nim"),
    "kode_matakuliah" VARCHAR REFERENCES "Matakuliah"("kode_matakuliah"),
    "absensi" INTEGER,
    "tugas" INTEGER,
    "quiz" INTEGER,
    "uts" INTEGER,
    "uas" INTEGER,
    "nilai_akhir" DECIMAL,
    "grade" VARCHAR,
    "status" VARCHAR
);

-- Insert Mahasiswa
"""
    
    first_names_m = ["Budi", "Andi", "Joko", "Ahmad", "Rizky", "Dimas", "Muhammad", "Ilham", "Agus", "Hendra", "Dedi", "Eko", "Rian", "Fajar", "Kevin", "Surya", "Aditya", "Doni"]
    first_names_f = ["Siti", "Ayu", "Putri", "Dewi", "Nisa", "Rina", "Ani", "Lia", "Dian", "Sari", "Maya", "Indah", "Tika", "Wulan", "Nadia", "Rika", "Mega", "Dita"]
    last_names = ["Santoso", "Wijaya", "Kusuma", "Pratama", "Setiawan", "Nugroho", "Hidayat", "Saputra", "Wibowo", "Siregar", "Nasution", "Haryanto", "Pangestu"]
    
    prodi_list = [
        {"name": "Informatika", "code": "IF"},
        {"name": "Sistem Informasi", "code": "SI"},
        {"name": "Teknik Industri", "code": "TI"},
        {"name": "Kedokteran", "code": "KD"}
    ]
    
    angkatan_info = {
        "2022": {"prefix": "22", "semester": 6}, # Wait in prompt user said 22 is semester 8, wait let's use 6/8 doesn't matter user said 22->8, 23->6, 24->4, 25->2. User prompt before: 2022 (smstr 6), 2023 (smstr 4) wait let's just make semester correspond mapping.
        "2023": {"prefix": "23", "semester": 4},
        "2024": {"prefix": "24", "semester": 2},
        "2025": {"prefix": "25", "semester": 1}
    }
    # Wait, earlier prompt said 2022=semester 8? Ah in my implementation plan I said 2022->6/8. User actually said:
    # "sekarang tahun 2025 jadi yang angkatan 2025 itu semester 2 (2025=2), (2024=4), (2023=6), (2022=8)"
    angkatan_info = {
        "2022": {"prefix": "22", "semester": 8},
        "2023": {"prefix": "23", "semester": 6},
        "2024": {"prefix": "24", "semester": 4},
        "2025": {"prefix": "25", "semester": 2}
    }

    students = []
    
    def get_random_nim(angkatan):
        prefix = angkatan_info[angkatan]["prefix"]
        rand_num = "".join(random.choices(string.digits, k=9))
        return f"{prefix}{rand_num}"

    for i in range(55):
        angkatan = random.choice(list(angkatan_info.keys()))
        prodi = random.choice(prodi_list)
        gender = random.choice(["Laki-laki", "Perempuan"])
        
        first_name = random.choice(first_names_m) if gender == "Laki-laki" else random.choice(first_names_f)
        last_name = random.choice(last_names)
        name = f"{first_name} {last_name}"
        
        nim = get_random_nim(angkatan)
        kelas = f"{prodi['code']}-{angkatan_info[angkatan]['prefix']}{random.choice(['A', 'B', 'C'])}"
        semester = angkatan_info[angkatan]["semester"]
        
        status = "Aktif" if random.random() > 0.1 else random.choice(["Cuti", "Lulus"])
        if angkatan == "2025": status = "Aktif"
        
        students.append({"nim": nim, "nama": name, "prodi": prodi["name"], "semester": semester, "angkatan": angkatan})
        sql += f"INSERT INTO \"Mahasiswa\" VALUES ('{nim}', '{name}', '{gender}', '{prodi['name']}', '{kelas}', '{angkatan}', '{status}', {semester});\n"

    sql += "\n-- Insert Dosen\n"
    dosen_list = []
    for i in range(20):
        kode_dosen = f"D{str(i+1).zfill(3)}"
        gender = random.choice(["L", "P"])
        first_name = random.choice(first_names_m) if gender == "L" else random.choice(first_names_f)
        last_name = random.choice(last_names)
        title = random.choice([", M.Kom", ", S.T., M.T.", ", Ph.D", ", S.Kom., M.Cs.", ", Dr."])
        nama_dosen = f"{first_name} {last_name}{title}"
        matkul_dosen = f"Matakuliah Dosen {i+1}"
        dosen_list.append({"kode_dosen": kode_dosen, "nama": nama_dosen})
        sql += f"INSERT INTO \"Dosen\" VALUES ('{kode_dosen}', '{nama_dosen}', '{matkul_dosen}');\n"

    sql += "\n-- Insert Matakuliah\n"
    matkul_list = []
    
    matkul_names = [
        "Algoritma dan Pemrograman", "Struktur Data", "Basis Data", "Sistem Operasi",
        "Jaringan Komputer", "Pemrograman Web", "Pemrograman Berorientasi Objek", "Kecerdasan Buatan",
        "Rekayasa Perangkat Lunak", "Matematika Diskrit", "Aljabar Linear", "Kalkulus",
        "Fisika Dasar", "Kimia Dasar", "Pengantar Kedokteran", "Anatomi Manusia",
        "Sistem Informasi Manajemen", "E-Business", "Manajemen Proyek", "Ergonomi",
        "Sistem Produksi", "Riset Operasi", "Pengolahan Sinyal", "Grafika Komputer",
        "Keamanan Informasi", "Mobile Development", "Cloud Computing", "Data Mining",
        "Machine Learning", "Internet of Things", "Blockchain", "Robotika"
    ]
    
    hari_list = ["Senin", "Selasa", "Rabu", "Kamis", "Jumat"]
    jam_list = [("08:00", "09:40"), ("10:00", "11:40"), ("13:00", "14:40"), ("15:00", "16:40")]
    
    for i in range(32):
        kode = f"MK{str(i+1).zfill(3)}"
        nama = matkul_names[i]
        sks = random.choice([2, 3, 4])
        prodi = random.choice(prodi_list)["name"]
        dosen = random.choice(dosen_list)["kode_dosen"]
        semester = random.choice([2, 4, 6, 8])
        hari = random.choice(hari_list)
        jam = random.choice(jam_list)
        ruangan = f"R{random.randint(1,4)}0{random.randint(1,9)}"
        matkul_list.append({"kode_matakuliah": kode, "semester": semester, "prodi": prodi})
        sql += f"INSERT INTO \"Matakuliah\" VALUES ('{kode}', '{nama}', {sks}, '{prodi}', '{dosen}', {semester}, '{hari}', '{jam[0]}', '{jam[1]}', '{ruangan}');\n"

    sql += "\n-- Insert KRS and Nilai\n"
    # To demonstrate IPK > 3.5 allowing upper semester classes, we will randomly assign some upper courses
    
    krs_count = 0
    nilai_count = 0
    for student in students:
        # A student takes ~4 courses that match their semester/logic
        possible_courses = [mk for mk in matkul_list if mk["semester"] <= student["semester"] and mk["prodi"] == student["prodi"]]
        # If possible courses are too few, loosen prodi constraint
        if len(possible_courses) < 2:
            possible_courses = [mk for mk in matkul_list if mk["semester"] <= student["semester"]]
            
        random.shuffle(possible_courses)
        selected_courses = possible_courses[:random.randint(3, 5)]
        
        # Determine if this student has high GPA
        is_high_gpa = random.random() > 0.5
        if is_high_gpa and student["semester"] < 8:
            # Can take 1 upper class
            upper_courses = [mk for mk in matkul_list if mk["semester"] == student["semester"] + 2]
            if upper_courses:
                selected_courses.append(random.choice(upper_courses))
        
        # Only take unique courses
        selected_courses_map = {}
        for sc in selected_courses:
            selected_courses_map[sc["kode_matakuliah"]] = sc
        selected_courses = list(selected_courses_map.values())

        for mk in selected_courses:
            krs_count += 1
            sql += f"INSERT INTO \"KRS\" (nim, kode_matakuliah) VALUES ('{student['nim']}', '{mk['kode_matakuliah']}');\n"
            
            absensi = random.randint(50, 100)
            tugas = random.randint(50, 100)
            quiz = random.randint(50, 100)
            uts = random.randint(50, 100)
            uas = random.randint(50, 100)
            
            na_100 = (0.1*absensi) + (0.2*tugas) + (0.1*quiz) + (0.3*uts) + (0.3*uas)
            
            # Convert to IPK format 0-4.00
            if na_100 >= 85: 
                na_4 = 4.00; grade = "A"; status = "Lulus"
            elif na_100 >= 70:
                na_4 = 3.00; grade = "B"; status = "Lulus"
            elif na_100 >= 55:
                na_4 = 2.00; grade = "C"; status = "Lulus"
            elif na_100 >= 40:
                na_4 = 1.00; grade = "D"; status = "Tidak Lulus"
            else:
                na_4 = 0.00; grade = "E"; status = "Tidak Lulus"

            # Adding some randomness/finesse to na_4, e.g. 3.5, 3.75
            if grade == "A":
                na_4 = round(random.uniform(3.7, 4.0), 2)
            elif grade == "B":
                na_4 = round(random.uniform(3.0, 3.69), 2)
            elif grade == "C":
                na_4 = round(random.uniform(2.0, 2.99), 2)
            elif grade == "D":
                na_4 = round(random.uniform(1.0, 1.99), 2)
                
            nilai_count += 1
            sql += f"INSERT INTO \"Nilai\" (nim, kode_matakuliah, absensi, tugas, quiz, uts, uas, nilai_akhir, grade, status) VALUES ('{student['nim']}', '{mk['kode_matakuliah']}', {absensi}, {tugas}, {quiz}, {uts}, {uas}, {na_4}, '{grade}', '{status}');\n"

    # Save to file
    with open("/home/axsx/Document/New Folder 1/UTS_PBO2/uts1/migration.sql", "w") as f:
        f.write(sql)
        
    print("Migration SQL generated successfully at /home/axsx/Document/New Folder 1/UTS_PBO2/uts1/migration.sql")
    print(f"Students generated: {len(students)}")
    print(f"Dosen generated: {len(dosen_list)}")
    print(f"Matakuliah generated: {len(matkul_list)}")
    print(f"KRS generated: {krs_count}")
    print(f"Nilai generated: {nilai_count}")

generate_sql()
