package uts1;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.time.Duration;
import java.sql.*;
import java.util.Properties;

/**
 * Supabase connection via Java's built-in HTTP client.
 * 
 * Strategy: Use Supabase REST API (PostgREST) as primary.
 * The connection returned wraps an HTTP-based fake JDBC for simple queries,
 * but for full JDBC we tunnel through a JDBC-over-Supabase adapter.
 *
 * Actually: we expose getConnection() returning a real Connection via the
 * postgres wire protocol, but we need a working DB password.
 *
 * Since the JDBC pooler has a "Tenant or user not found" bug on Supabase's
 * shared infrastructure, we use the REST API directly from each form.
 */
public class DatabaseConnection {

    public static final String SUPABASE_URL  = requiredEnv("SUPABASE_URL");
    public static final String ANON_KEY      = requiredEnv("SUPABASE_ANON_KEY");
    public static final String SERVICE_KEY   = requiredEnv("SUPABASE_SERVICE_KEY");

    private static final HttpClient HTTP = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    private static String requiredEnv(String key) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return val;
    }

    /**
     * Execute a REST query against the Supabase PostgREST endpoint.
     * Returns the JSON response body as a String.
     *
     * @param table  table name (e.g. "users")
     * @param query  URL query params (e.g. "select=username,role&username=eq.admin")
     * @param useServiceKey true to bypass RLS (needed for reading users)
     */
    public static String restGet(String table, String query, boolean useServiceKey) throws Exception {
        String apiKey = useServiceKey ? SERVICE_KEY : ANON_KEY;
        String url = SUPABASE_URL + "/rest/v1/" + table + "?" + query;
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("apikey", apiKey)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .GET()
            .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("REST GET failed [" + resp.statusCode() + "]: " + resp.body());
        }
        return resp.body();
    }

    /**
     * POST (insert) a JSON body to a Supabase table.
     */
    public static String restPost(String table, String jsonBody) throws Exception {
        String url = SUPABASE_URL + "/rest/v1/" + table;
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("apikey", SERVICE_KEY)
            .header("Authorization", "Bearer " + SERVICE_KEY)
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation")
            .POST(BodyPublishers.ofString(jsonBody))
            .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 409) {
            if (resp.body().contains("duplicate key value")) {
                throw new Exception("Data ini sudah ada (Duplikat). Coba periksa kembali!");
            }
        }
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("REST POST failed [" + resp.statusCode() + "]: " + resp.body());
        }
        return resp.body();
    }

    /**
     * PATCH (update) rows matching a filter.
     * @param filter e.g. "nim=eq.2455201321"
     */
    public static String restPatch(String table, String filter, String jsonBody) throws Exception {
        String url = SUPABASE_URL + "/rest/v1/" + table + "?" + filter;
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("apikey", SERVICE_KEY)
            .header("Authorization", "Bearer " + SERVICE_KEY)
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation")
            .method("PATCH", BodyPublishers.ofString(jsonBody))
            .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 409) {
            if (resp.body().contains("duplicate key value")) {
                throw new Exception("Data ini sudah ada (Duplikat). Coba periksa kembali!");
            }
        }
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("REST PATCH failed [" + resp.statusCode() + "]: " + resp.body());
        }
        return resp.body();
    }

    /**
     * DELETE rows matching a filter.
     * @param filter e.g. "nim=eq.2455201321"
     */
    public static String restDelete(String table, String filter) throws Exception {
        String url = SUPABASE_URL + "/rest/v1/" + table + "?" + filter;
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("apikey", SERVICE_KEY)
            .header("Authorization", "Bearer " + SERVICE_KEY)
            .header("Content-Type", "application/json")
            .DELETE()
            .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 409) {
            if (resp.body().contains("foreign key constraint")) {
                throw new Exception("Data ini masih digunakan (mis. di KRS/Nilai). Hapus data terkait terlebih dahulu!");
            }
        }
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("REST DELETE failed [" + resp.statusCode() + "]: " + resp.body());
        }
        return resp.body();
    }

    /**
     * Simple JSON escape utility for building payloads.
     */
    public static String escape(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Parse a simple JSON array of objects and extract a specific field value.
     * This is a lightweight parser for our controlled use case.
     */
    public static String getField(String json, int objectIndex, String field) {
        // Handle empty array early
        if (json == null || json.trim().equals("[]")) return null;
        // Split by },{
        String[] objects = json.replace("[", "").replace("]", "").split("\\},\\s*\\{");
        if (objectIndex >= objects.length) return null;
        String obj = objects[objectIndex];
        String key = "\"" + field + "\":";
        int idx = obj.indexOf(key);
        if (idx == -1) return null;
        idx += key.length();
        if (idx < obj.length() && obj.charAt(idx) == '"') {
            int end = obj.indexOf('"', idx + 1);
            return obj.substring(idx + 1, end);
        } else {
            int end = obj.indexOf(',', idx);
            if (end == -1) end = obj.indexOf('}', idx);
            if (end == -1) end = obj.length();
            return obj.substring(idx, end).trim();
        }
    }

    /**
     * Count how many objects are in a JSON array.
     */
    public static int countRows(String json) {
        if (json == null || json.trim().equals("[]")) return 0;
        int count = 0;
        int i = -1;
        while ((i = json.indexOf('{', i + 1)) != -1) count++;
        return count;
    }
}
