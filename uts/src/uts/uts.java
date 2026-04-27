package uts;

/**
 *
 * @author Kemas M
 */
public class uts {
    public static void main(String[] args) {
        // Jalankan MainMenu
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new inputnilai().setVisible(true);
            }
        });
    }
}