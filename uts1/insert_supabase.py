import random
import string
import requests
import json
import os

SUPABASE_URL = os.getenv("SUPABASE_URL", "")
SERVICE_KEY = os.getenv("SUPABASE_SERVICE_KEY", "")
if not SUPABASE_URL or not SERVICE_KEY:
    raise RuntimeError("Set SUPABASE_URL and SUPABASE_SERVICE_KEY before running this script.")

headers = {
    "apikey": SERVICE_KEY,
    "Authorization": f"Bearer {SERVICE_KEY}",
    "Content-Type": "application/json",
    "Prefer": "return=minimal" # Do not return rows, just insert
}

def insert_table(table_name, payload_list):
    # Supabase allows bulk inserts up to 1000 rows
    print(f"Inserting {len(payload_list)} rows into {table_name}...")
    url = f"{SUPABASE_URL}/rest/v1/{table_name}"
    resp = requests.post(url, json=payload_list, headers=headers)
    if resp.status_code >= 400:
        print(f"Error {resp.status_code} on {table_name}: {resp.text}")
        return False
    return True

def generate_and_insert():
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
        gender = random.choice(["L", "P"])
        
        first_name = random.choice(first_names_m) if gender == "L" else random.choice(first_names_f)
        last_name = random.choice(last_names)
        name = f"{first_name} {last_name}"
        
        nim = get_random_nim(angkatan)
        kelas = f"{prodi['code']}-{angkatan_info[angkatan]['prefix']}{random.choice(['A', 'B', 'C'])}"
        semester = angkatan_info[angkatan]["semester"]
        
        status = "Aktif" if random.random() > 0.1 else random.choice(["Cuti", "Lulus"])
        if angkatan == "2025": status = "Aktif"
        
        students.append({
            "nim": nim, "nama": name, "jenis_kelamin": gender, "prodi": prodi["name"],
            "kelas": kelas, "angkatan": angkatan, "status_aktif": status, "semester": semester
        })
        
    print("Sending Mahasiswa...")
    if not insert_table("Mahasiswa", students): return

    dosen_list = []
    for i in range(20):
        kode_dosen = f"D{str(i+1).zfill(3)}"
        gender = random.choice(["L", "P"])
        first_name = random.choice(first_names_m) if gender == "L" else random.choice(first_names_f)
        last_name = random.choice(last_names)
        title = random.choice([", M.Kom", ", S.T., M.T.", ", Ph.D", ", S.Kom., M.Cs.", ", Dr."])
        nama_dosen = f"{first_name} {last_name}{title}"
        matkul_dosen = f"Matakuliah Dosen {i+1}"
        dosen_list.append({"kode_dosen": kode_dosen, "nama": nama_dosen, "mata_kuliah": matkul_dosen})
        
    print("Sending Dosen...")
    if not insert_table("Dosen", dosen_list): return

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
        matkul_list.append({
            "kode_matakuliah": kode, "nama_matakuliah": nama, "sks": sks, "prodi": prodi,
            "kode_dosen": dosen, "semester": semester, "hari": hari, 
            "jam_mulai": jam[0], "jam_selesai": jam[1], "ruangan": ruangan
        })
        
    print("Sending Matakuliah...")
    if not insert_table("Matakuliah", matkul_list): return

    krs_payloads = []
    nilai_payloads = []
    
    for student in students:
        possible_courses = [mk for mk in matkul_list if mk["semester"] <= student["semester"] and mk["prodi"] == student["prodi"]]
        if len(possible_courses) < 2:
            possible_courses = [mk for mk in matkul_list if mk["semester"] <= student["semester"]]
            
        random.shuffle(possible_courses)
        selected_courses = possible_courses[:random.randint(3, 5)]
        
        is_high_gpa = random.random() > 0.5
        if is_high_gpa and student["semester"] < 8:
            upper_courses = [mk for mk in matkul_list if mk["semester"] == student["semester"] + 2]
            if upper_courses:
                selected_courses.append(random.choice(upper_courses))
        
        selected_courses_map = {}
        for sc in selected_courses:
            selected_courses_map[sc["kode_matakuliah"]] = sc
        selected_courses = list(selected_courses_map.values())

        for mk in selected_courses:
            krs_payloads.append({"nim": student["nim"], "kode_matakuliah": mk["kode_matakuliah"]})
            
            absensi = random.randint(50, 100)
            tugas = random.randint(50, 100)
            quiz = random.randint(50, 100)
            uts = random.randint(50, 100)
            uas = random.randint(50, 100)
            
            na_100 = (0.1*absensi) + (0.2*tugas) + (0.1*quiz) + (0.3*uts) + (0.3*uas)
            if na_100 >= 85: 
                na_4 = round(random.uniform(3.7, 4.0), 2)
                grade = "A"; status = "Lulus"
            elif na_100 >= 70:
                na_4 = round(random.uniform(3.0, 3.69), 2)
                grade = "B"; status = "Lulus"
            elif na_100 >= 55:
                na_4 = round(random.uniform(2.0, 2.99), 2)
                grade = "C"; status = "Lulus"
            elif na_100 >= 40:
                na_4 = round(random.uniform(1.0, 1.99), 2)
                grade = "D"; status = "Tidak Lulus"
            else:
                na_4 = round(random.uniform(0.0, 0.99), 2)
                grade = "E"; status = "Tidak Lulus"

            nilai_payloads.append({
                "nim": student["nim"], "kode_matakuliah": mk["kode_matakuliah"],
                "absensi": absensi, "tugas": tugas, "quiz": quiz, "uts": uts, "uas": uas,
                "nilai_akhir": float(na_4), "grade": grade, "status": status
            })

    print("Sending KRS...")
    if not insert_table("KRS", krs_payloads): return
    print("Sending Nilai...")
    if not insert_table("Nilai", nilai_payloads): return

    print("Data insertion complete!")

generate_and_insert()
