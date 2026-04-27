import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConn {
    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");

        String host_pooler = "aws-0-ap-southeast-1.pooler.supabase.com";
        String project_ref = "nentjnbdwxrzylotwptw";
        String password    = System.getenv("SUPABASE_DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Set SUPABASE_DB_PASSWORD before running TestConn.");
        }

        // Username formats to try
        String[][] tests = {
            // {url, user}
            {"jdbc:postgresql://" + host_pooler + ":5432/postgres?sslmode=require",
             "postgres." + project_ref},
            {"jdbc:postgresql://" + host_pooler + ":6543/postgres",
             "postgres." + project_ref},
            {"jdbc:postgresql://" + host_pooler + ":5432/postgres?sslmode=require",
             "postgres"},
            {"jdbc:postgresql://" + host_pooler + ":5432/" + project_ref + "?sslmode=require",
             "postgres." + project_ref},
        };

        for (int i = 0; i < tests.length; i++) {
            String url  = tests[i][0];
            String user = tests[i][1];
            System.out.println("=== Test " + (i+1) + " ===");
            System.out.println("  URL:  " + url);
            System.out.println("  User: " + user);
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT 1 as ok");
                rs.next();
                System.out.println("  RESULT: SUCCESS! 1=" + rs.getInt("ok"));
                conn.close();
            } catch (Exception e) {
                System.out.println("  RESULT: FAIL — " + e.getMessage());
            }
        }
    }
}
