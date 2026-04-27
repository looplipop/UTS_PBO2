import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExecuteSQL {
    public static void main(String[] args) {
        String url = System.getenv("SUPABASE_JDBC_URL");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Set SUPABASE_JDBC_URL before running ExecuteSQL.");
        }
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Reading migration.sql...");
            String sql = new String(Files.readAllBytes(Paths.get("/home/axsx/Document/New Folder 1/UTS_PBO2/uts1/migration.sql")));
            
            System.out.println("Executing SQL statements...");
            stmt.execute(sql);
            System.out.println("PostgreSQL Migration Successful!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
