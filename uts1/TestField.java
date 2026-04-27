package uts1;
public class TestField {
    public static void main(String[] args) throws Exception {
        String json = DatabaseConnection.restGet("matakuliah","select=kode_mk,nama_mk,sks&order=kode_mk",true);
        System.out.println("JSON: " + json);
        int n = DatabaseConnection.countRows(json);
        System.out.println("Rows: " + n);
        for(int i=0;i<n;i++) {
            System.out.println("MK: " + DatabaseConnection.getField(json, i, "kode_mk") + 
                ", " + DatabaseConnection.getField(json, i, "nama_mk") + 
                ", " + DatabaseConnection.getField(json, i, "sks"));
        }
    }
}
