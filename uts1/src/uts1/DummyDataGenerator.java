package uts1;

import java.util.*;

public class DummyDataGenerator {

    private static final String[] JURUSAN = {"Teknik Informatika", "Sistem Informasi", "Teknik Elektro", "Teknik Industri", "Manajemen Informatika"};
    private static final String[] GRADES = {"A", "B", "C", "D"};

    public static void main(String[] args) {
        System.out.println("🚀 Menghapus data lama dan mengisi 55 Mahasiswa Baru...");
        
        try {
            // Delete existing (Requires a WHERE clause in Supabase)
            DatabaseConnection.restDelete("nilai", "nim=neq.null");
            DatabaseConnection.restDelete("krs", "nim=neq.null");
            DatabaseConnection.restDelete("mahasiswa", "nim=neq.null");
            DatabaseConnection.restDelete("matakuliah", "kode_mk=eq.MK01");

            // Insert Dummy Matakuliah
            DatabaseConnection.restPost("matakuliah", "{\"kode_mk\":\"MK01\", \"nama_mk\":\"Mata Kuliah Dummy\", \"sks\":3}");

            Random rand = new Random();
            for (int i = 1; i <= 55; i++) {
                String nim = "230101" + String.format("%03d", i);
                String nama = "Mahasiswa " + i;
                String jurusan = JURUSAN[rand.nextInt(JURUSAN.length)];

                // Insert Mahasiswa
                String mhsJson = String.format("{\"nim\":\"%s\", \"nama\":\"%s\", \"jurusan\":\"%s\"}",
                                                nim, nama, jurusan);
                DatabaseConnection.restPost("mahasiswa", mhsJson);

                // Randomly assign KRS (approx 70% registered)
                if (rand.nextDouble() < 0.7) {
                    String krsJson = String.format("{\"nim\":\"%s\", \"kode_mk\":\"MK01\"}", nim);
                    DatabaseConnection.restPost("krs", krsJson);

                    // If KRS, assign random grade
                    String grade = GRADES[rand.nextInt(GRADES.length)];
                    String nilaiJson = String.format("{\"nim\":\"%s\", \"kode_mk\":\"MK01\", \"nilai_huruf\":\"%s\"}",
                                                      nim, grade);
                    DatabaseConnection.restPost("nilai", nilaiJson);
                }
            }
            
            System.out.println("✅ Berhasil menambahkan 55 data mahasiswa acak!");
        } catch (Exception e) {
            System.err.println("❌ Gagal membuat data dummy: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
