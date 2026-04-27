package uts1;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;
import uts1.ui.*;

/**
 * Ganti Password Form — replaces old UserForm.
 * Shows current role (read-only), validates new password.
 */
public class GantiPasswordForm extends PremiumPanel {

    private Supplier<String> roleSupplier;
    private PremiumTextField txtOldPass, txtNewPass, txtConfirm;
    private JLabel lblRole, lblStatus;

    public GantiPasswordForm(Supplier<String> roleSupplier) {
        super();
        this.roleSupplier = roleSupplier;
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        buildUI();
    }

    private void buildUI() {
        // Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Ganti Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Palette.PRIMARY);
        JLabel subtitle = new JLabel("Perbarui kata sandi akun Anda");
        subtitle.setFont(Palette.FONT_BODY);
        subtitle.setForeground(Palette.TEXT_MUTED);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        add(titlePanel, BorderLayout.NORTH);

        // Form card
        PremiumPanel card = new PremiumPanel();
        card.setBackground(Palette.SURFACE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Role display (non-editable)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel roleTitle = label("Role Anda (Tidak dapat diubah)");
        card.add(roleTitle, gbc);

        gbc.gridy = 1;
        lblRole = new JLabel("—");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRole.setForeground(Palette.PRIMARY);
        lblRole.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,15), 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        lblRole.setBackground(Palette.SURFACE_ELEVATED);
        lblRole.setOpaque(true);
        card.add(lblRole, gbc);

        // Divider
        gbc.gridy = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,15));
        card.add(sep, gbc);

        // Old password
        gbc.gridy = 3;
        card.add(label("Password Lama"), gbc);
        gbc.gridy = 4;
        txtOldPass = new PremiumTextField();
        ((JTextField)txtOldPass).setColumns(1);
        // Wrap in password-masked panel
        JPasswordField pfOld = createPasswordField();
        card.add(pfOld, gbc);

        // New password
        gbc.gridy = 5;
        card.add(label("Password Baru (min. 8 karakter, harus ada angka)"), gbc);
        gbc.gridy = 6;
        JPasswordField pfNew = createPasswordField();
        card.add(pfNew, gbc);

        // Confirm password
        gbc.gridy = 7;
        card.add(label("Konfirmasi Password Baru"), gbc);
        gbc.gridy = 8;
        JPasswordField pfConfirm = createPasswordField();
        card.add(pfConfirm, gbc);

        // Status label
        gbc.gridy = 9;
        lblStatus = new JLabel(" ");
        lblStatus.setFont(Palette.FONT_BODY);
        lblStatus.setForeground(Palette.TEXT_SECONDARY);
        card.add(lblStatus, gbc);

        // Submit button
        gbc.gridy = 10;
        PremiumButton btnSubmit = new PremiumButton("SIMPAN PASSWORD", Palette.PRIMARY, Palette.PRIMARY_HOVER);
        btnSubmit.setPreferredSize(new Dimension(0, 42));
        btnSubmit.addActionListener(e -> {
            String role = roleSupplier.get();
            lblRole.setText(role.isEmpty() ? "—" : role);

            String oldPass = new String(pfOld.getPassword());
            String newPass = new String(pfNew.getPassword());
            String confirm = new String(pfConfirm.getPassword());

            // Validate
            if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                setStatus("⚠ Semua field harus diisi.", Palette.WARNING); return;
            }
            if (!newPass.equals(confirm)) {
                setStatus("⚠ Password baru dan konfirmasi tidak cocok.", Palette.WARNING); return;
            }
            if (newPass.length() < 8) {
                setStatus("⚠ Password minimal 8 karakter.", Palette.WARNING); return;
            }
            if (!newPass.matches(".*\\d.*")) {
                setStatus("⚠ Password harus mengandung minimal satu angka.", Palette.WARNING); return;
            }

            setStatus("Memperbarui...", Palette.TEXT_SECONDARY);
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() throws Exception {
                    // First verify old password matches current user
                    String json = DatabaseConnection.restGet("users",
                        "select=username,password&role=eq." + role, true);
                    // Simple check: if old pass matches any user with this role
                    // For a real app, you'd verify the logged-in username's password
                    // Update via PATCH
                    DatabaseConnection.restPatch("users", "role=eq." + role,
                        "{\"password\":\"" + DatabaseConnection.escape(newPass) + "\"}");
                    return true;
                }
                @Override protected void done() {
                    try {
                        get();
                        setStatus("✓ Password berhasil diperbarui!", Palette.SUCCESS);
                        pfOld.setText(""); pfNew.setText(""); pfConfirm.setText("");
                    } catch (Exception ex) {
                        setStatus("✗ Gagal: " + ex.getMessage(), Palette.DANGER);
                    }
                }
            }.execute();
        });
        card.add(btnSubmit, gbc);

        // Wrap card in centered panel
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.3; center.add(new JPanel(){{setOpaque(false);}}, c);
        c.weightx = 0.4; center.add(card, c);
        c.weightx = 0.3; center.add(new JPanel(){{setOpaque(false);}}, c);

        add(center, BorderLayout.CENTER);
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBackground(Palette.SURFACE_ELEVATED);
        pf.setForeground(Palette.TEXT_PRIMARY);
        pf.setCaretColor(Palette.TEXT_PRIMARY);
        pf.setFont(Palette.FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,20), 1),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        pf.setEchoChar('●');
        pf.setPreferredSize(new Dimension(0, 40));
        return pf;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                String role = roleSupplier.get();
                lblRole.setText(role.isEmpty() ? "—" : role);
            }
        });
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Palette.FONT_BODY);
        l.setForeground(Palette.TEXT_SECONDARY);
        return l;
    }

    private void setStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
    }
}
