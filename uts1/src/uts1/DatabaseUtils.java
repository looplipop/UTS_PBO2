package uts1;

/**
 * Utility class — now uses REST API for all DB operations.
 * Legacy JDBC methods removed since we use Supabase PostgREST.
 */
public class DatabaseUtils {

    /** Count rows in a table via REST API. Returns -1 on error. */
    public static int countRows(String tableName) {
        try {
            String json = DatabaseConnection.restGet(tableName, "select=*", true);
            return DatabaseConnection.countRows(json);
        } catch (Exception e) {
            System.out.println("Error counting " + tableName + ": " + e.getMessage());
            return -1;
        }
    }

    /** Check if a table is empty. */
    public static boolean isTableEmpty(String tableName) {
        return countRows(tableName) == 0;
    }
}
