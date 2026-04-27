import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class migrate {
    public static void main(String[] args) {
        String url = System.getenv("SUPABASE_JDBC_URL");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Set SUPABASE_JDBC_URL before running migrate.");
        }
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            // Drop tables if exist
            stmt.execute("DROP TABLE IF EXISTS nilai CASCADE;");
            stmt.execute("DROP TABLE IF EXISTS krs CASCADE;");
            stmt.execute("DROP TABLE IF EXISTS dosen CASCADE;");
            stmt.execute("DROP TABLE IF EXISTS matakuliah CASCADE;");
            stmt.execute("DROP TABLE IF EXISTS mahasiswa CASCADE;");
            // Also drop user in case it was created previously
            stmt.execute("DROP TABLE IF EXISTS \"user\" CASCADE;");
            stmt.execute("DROP TABLE IF EXISTS users CASCADE;");

            // Users
            stmt.execute("CREATE TABLE users (" +
                         "id_user SERIAL PRIMARY KEY, " +
                         "username VARCHAR(50) NOT NULL, " +
                         "password VARCHAR(50) NOT NULL, " +
                         "role VARCHAR(20) NOT NULL);");
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('mahesa', 'esa', 'Admin');");

            // Mahasiswa
            stmt.execute("CREATE TABLE mahasiswa (" +
                         "nim VARCHAR(10) PRIMARY KEY, " +
                         "nama VARCHAR(50) NOT NULL, " +
                         "jurusan VARCHAR(30) NOT NULL);");
            stmt.execute("INSERT INTO mahasiswa (nim, nama, jurusan) VALUES ('2455201321', 'Mahesa Satria Darussalam', 'Teknik Informatika');");

            // Mata Kuliah
            stmt.execute("CREATE TABLE matakuliah (" +
                         "kode_mk VARCHAR(10) PRIMARY KEY, " +
                         "nama_mk VARCHAR(50) NOT NULL, " +
                         "sks INTEGER NOT NULL);");
            stmt.execute("INSERT INTO matakuliah (kode_mk, nama_mk, sks) VALUES " +
                         "('1', 'PBO1', 2), ('2', 'AGAMA', 3), ('3', 'STATISTIKA', 3);");

            // Dosen
            stmt.execute("CREATE TABLE dosen (" +
                         "kode_dosen VARCHAR(10) PRIMARY KEY, " +
                         "nama_dosen VARCHAR(50) NOT NULL);");
            stmt.execute("INSERT INTO dosen (kode_dosen, nama_dosen) VALUES " +
                         "('1', 'gia'), ('2', 'muslim'), ('3', 'gia');");

            // KRS
            stmt.execute("CREATE TABLE krs (" +
                         "id_krs SERIAL PRIMARY KEY, " +
                         "nim VARCHAR(10) REFERENCES mahasiswa(nim), " +
                         "kode_mk VARCHAR(10) REFERENCES matakuliah(kode_mk));");
            stmt.execute("INSERT INTO krs (nim, kode_mk) VALUES " +
                         "('2455201321', '1'), ('2455201321', '2'), ('2455201321', '3');");

            // Nilai
            stmt.execute("CREATE TABLE nilai (" +
                         "id_nilai SERIAL PRIMARY KEY, " +
                         "nim VARCHAR(10) REFERENCES mahasiswa(nim), " +
                         "kode_mk VARCHAR(10) REFERENCES matakuliah(kode_mk), " +
                         "nilai_huruf VARCHAR(1) CHECK (nilai_huruf IN ('A','B','C','D','E')));");
            // Fix invalid NIM referencing 24552011321 to 2455201321
            stmt.execute("INSERT INTO nilai (nim, kode_mk, nilai_huruf) VALUES " +
                         "('2455201321', '1', 'A'), ('2455201321', '2', 'A'), ('2455201321', '3', 'A');");

            System.out.println("PostgreSQL Migration Successful!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
