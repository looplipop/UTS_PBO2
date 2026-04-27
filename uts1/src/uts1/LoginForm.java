package uts1;

import uts1.ui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private PremiumButton btnLogin;
    private JLabel lblError;
    private final LoginCallback callback;

    public interface LoginCallback {
        void onSuccess(String role);
    }

    public LoginForm(LoginCallback callback) {
        this.callback = callback;
        setOpaque(true);
        setBackground(Palette.BACKGROUND);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel leftPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0,0,Palette.GRAD_START,0,getHeight(),Palette.GRAD_END);
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setPreferredSize(new Dimension(400, 600));

        JLabel appName = new JLabel("AKADEMIK");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 42));
        appName.setForeground(Color.WHITE);

        JLabel appSub = new JLabel("UTB Management System");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        appSub.setForeground(new Color(255,255,255,180));

        JLabel desc = new JLabel("<html><div style='text-align:center;'>Kelola mahasiswa, dosen,<br>mata kuliah & nilai secara terpadu<br>berbasis cloud Supabase.</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(255,255,255,150));
        desc.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx=0; lc.gridy=0; lc.insets=new Insets(0,20,10,20); leftPanel.add(appName,lc);
        lc.gridy=1; lc.insets=new Insets(0,20,30,20);              leftPanel.add(appSub,lc);
        lc.gridy=2; lc.insets=new Insets(0,20,0,20);               leftPanel.add(desc,lc);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Palette.SURFACE);
        rightPanel.setPreferredSize(new Dimension(440, 600));

        JLabel titleLbl = new JLabel("Selamat Datang!");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLbl.setForeground(Palette.TEXT_PRIMARY);

        JLabel subLbl = new JLabel("Masuk ke dashboard akademik");
        subLbl.setFont(Palette.FONT_BODY);
        subLbl.setForeground(Palette.TEXT_SECONDARY);

        txtUsername = makeTextField();
        txtPassword = new JPasswordField();
        stylePassField(txtPassword);

        lblError = new JLabel(" ");
        lblError.setForeground(Palette.DANGER);
        lblError.setFont(Palette.FONT_SMALL);

        btnLogin = new PremiumButton("Masuk ke Dashboard");
        btnLogin.setPreferredSize(new Dimension(340, 46));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());

        GridBagConstraints rc = new GridBagConstraints();
        rc.insets = new Insets(8,32,8,32); rc.fill = GridBagConstraints.HORIZONTAL; rc.gridx = 0;
        rc.gridy=0; rc.insets=new Insets(0,32,4,32);  rightPanel.add(titleLbl,rc);
        rc.gridy=1; rc.insets=new Insets(0,32,32,32); rightPanel.add(subLbl,rc);
        rc.gridy=2; rc.insets=new Insets(6,32,6,32);  rightPanel.add(makeLf("Username",txtUsername),rc);
        rc.gridy=3;                                     rightPanel.add(makeLf("Password",txtPassword),rc);
        rc.gridy=4; rc.insets=new Insets(2,32,12,32); rightPanel.add(lblError,rc);
        rc.gridy=5; rc.insets=new Insets(6,32,6,32);  rightPanel.add(btnLogin,rc);

        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createLineBorder(Palette.SURFACE_HOVER, 1));
        card.add(leftPanel,  BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);
        add(card);
    }

    private JTextField makeTextField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(340,42));
        f.setBackground(Palette.SURFACE_ELEVATED);
        f.setForeground(Palette.TEXT_PRIMARY);
        f.setCaretColor(Palette.TEXT_PRIMARY);
        f.setFont(Palette.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Palette.SURFACE_HOVER,1,true),
            BorderFactory.createEmptyBorder(6,12,6,12)));
        return f;
    }
    private void stylePassField(JPasswordField f) {
        f.setPreferredSize(new Dimension(340,42));
        f.setBackground(Palette.SURFACE_ELEVATED);
        f.setForeground(Palette.TEXT_PRIMARY);
        f.setCaretColor(Palette.TEXT_PRIMARY);
        f.setFont(Palette.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Palette.SURFACE_HOVER,1,true),
            BorderFactory.createEmptyBorder(6,12,6,12)));
    }
    private JPanel makeLf(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0,6)); p.setOpaque(false);
        JLabel l = new JLabel(label); l.setFont(Palette.FONT_SMALL); l.setForeground(Palette.TEXT_SECONDARY);
        p.add(l, BorderLayout.NORTH); p.add(field, BorderLayout.CENTER); return p;
    }

    private void doLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        lblError.setText(" ");
        if (user.isEmpty() || pass.isEmpty()) { lblError.setText("⚠  Harap isi username dan password."); return; }

        btnLogin.setText("Menghubungkan..."); btnLogin.setEnabled(false);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String json = DatabaseConnection.restGet(
                    "users",
                    "select=role&username=eq." + DatabaseConnection.escape(user)
                    + "&password=eq." + DatabaseConnection.escape(pass),
                    true);
                if (DatabaseConnection.countRows(json) > 0) {
                    return DatabaseConnection.getField(json, 0, "role");
                }
                return null;
            }
            @Override
            protected void done() {
                btnLogin.setText("Masuk ke Dashboard"); btnLogin.setEnabled(true);
                try {
                    String role = get();
                    if (role != null) {
                        if (callback != null) callback.onSuccess(role);
                    } else {
                        lblError.setText("✗  Username atau password salah.");
                    }
                } catch (Exception ex) {
                    lblError.setText("<html><font color='#f87171'>✗  Gagal terhubung ke server.</font></html>");
                }
            }
        }.execute();
    }
}
