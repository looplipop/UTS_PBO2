package uts1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import uts1.ui.*;

public class MahasiswaForm extends PremiumPanel {

    private PremiumTextField txtNimSuffix, txtNama, txtKelas, txtSearch;
    private JLabel lblNimPrefix;
    private JComboBox<String> cbJurusan, cbJK, cbStatus, cbAngkatan;
    private JComboBox<String> cbFilterJurusan, cbFilterAngkatan;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel lblStatus;

    public MahasiswaForm() {
        super();
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) {
                loadData();
            }
        });

        // ── TITLE ──
        JLabel title = new JLabel("Data Mahasiswa");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Palette.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        add(title, BorderLayout.NORTH);

        // ── INPUT PANEL ──
        PremiumPanel inputPanel = new PremiumPanel();
        inputPanel.setBackground(new Color(30, 33, 58));
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 7, 5, 7);

        // NIM custom input
        JPanel nimPanel = new JPanel(new BorderLayout(4, 0));
        nimPanel.setOpaque(false);
        lblNimPrefix = new JLabel("");
        lblNimPrefix.setFont(Palette.FONT_BODY);
        lblNimPrefix.setForeground(Palette.TEXT_SECONDARY);
        lblNimPrefix.setOpaque(true);
        lblNimPrefix.setBackground(new Color(35, 38, 65));
        lblNimPrefix.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,22), 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        txtNimSuffix = new PremiumTextField();
        nimPanel.add(lblNimPrefix, BorderLayout.WEST);
        nimPanel.add(txtNimSuffix, BorderLayout.CENTER);

        txtNama     = new PremiumTextField();
        txtKelas    = new PremiumTextField();
        cbJK       = new JComboBox<>(new String[]{"L", "P"});
        cbJurusan  = new JComboBox<>(new String[]{"Informatika", "Sistem Informasi", "Teknik Industri", "Kedokteran"});
        cbAngkatan = new JComboBox<>(new String[]{"2025", "2024", "2023", "2022"});
        cbStatus   = new JComboBox<>(new String[]{"Aktif", "Cuti", "Lulus"});
        for (JComboBox<?> cb : new JComboBox[]{cbJK, cbJurusan, cbAngkatan, cbStatus}) styleCombo(cb);

        cbAngkatan.addItemListener(e -> updateNimPrefix());

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.12; inputPanel.add(softLabel("Angkatan"),gbc);
        gbc.gridx=1; gbc.weightx=0.3; inputPanel.add(cbAngkatan,gbc);
        gbc.gridx=2; gbc.weightx=0.12; inputPanel.add(softLabel("NIM"),gbc);
        gbc.gridx=3; gbc.weightx=0.46; inputPanel.add(nimPanel, gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.12; inputPanel.add(softLabel("Nama"),gbc);
        gbc.gridx=1; gbc.weightx=0.3; inputPanel.add(txtNama, gbc);
        gbc.gridx=2; gbc.weightx=0.12; inputPanel.add(softLabel("J/K"),gbc);
        gbc.gridx=3; gbc.weightx=0.46; inputPanel.add(cbJK, gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.12; inputPanel.add(softLabel("Program Studi"),gbc);
        gbc.gridx=1; gbc.weightx=0.3; inputPanel.add(cbJurusan, gbc);
        gbc.gridx=2; gbc.weightx=0.12; inputPanel.add(softLabel("Kelas"),gbc);
        gbc.gridx=3; gbc.weightx=0.46; inputPanel.add(txtKelas, gbc);

        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.12; inputPanel.add(softLabel("Status"),gbc);
        gbc.gridx=1; gbc.weightx=0.3; inputPanel.add(cbStatus, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        Color neutralBtn = new Color(75, 82, 115);
        Color neutralBtnHover = new Color(90, 98, 135);
        PremiumButton btnLoad   = new PremiumButton("Refresh", neutralBtn, neutralBtnHover);
        PremiumButton btnAdd    = new PremiumButton("Tambah",  Palette.SUCCESS,       Palette.SUCCESS.darker());
        PremiumButton btnUpdate = new PremiumButton("Update",  Palette.PRIMARY,       Palette.PRIMARY_HOVER);
        PremiumButton btnDelete = new PremiumButton("Hapus",   Palette.DANGER,        Palette.DANGER.darker());
        PremiumButton btnClear  = new PremiumButton("Reset",   neutralBtn, neutralBtnHover);
        for (JButton b : new JButton[]{btnLoad, btnAdd, btnUpdate, btnDelete, btnClear}) {
            b.setPreferredSize(new Dimension(90, 34)); btnPanel.add(b);
        }

        lblStatus = new JLabel("Status: Idle");
        lblStatus.setFont(Palette.FONT_SMALL);
        lblStatus.setForeground(new Color(140, 145, 175));

        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=4; inputPanel.add(btnPanel, gbc);
        gbc.gridy=5; inputPanel.add(lblStatus, gbc);

        // ── TABLE AREA ──
        tableModel = new DefaultTableModel(
            new Object[]{"No","NIM","Nama","J/K","Program Studi","Kelas","Angkatan","Status Aktif","Semester"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        MainMenu.styleTable(table);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Search bar + filters ABOVE table
        JPanel tableTopBar = new JPanel(new BorderLayout(10, 0));
        tableTopBar.setOpaque(false);
        tableTopBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));

        txtSearch = new PremiumTextField();
        txtSearch.setPreferredSize(new Dimension(0, 34));
        txtSearch.putClientProperty("JTextField.placeholderText", "  🔍  Cari nama, NIM, prodi...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        cbFilterJurusan = new JComboBox<>(new String[]{"Semua Jurusan","Informatika","Sistem Informasi","Teknik Industri","Kedokteran"});
        cbFilterAngkatan = new JComboBox<>(new String[]{"Semua Angkatan","2022","2023","2024","2025"});
        styleCombo(cbFilterJurusan); styleCombo(cbFilterAngkatan);
        cbFilterJurusan.addItemListener(e -> { if(e.getStateChange()==ItemEvent.SELECTED) applyFilters(); });
        cbFilterAngkatan.addItemListener(e -> { if(e.getStateChange()==ItemEvent.SELECTED) applyFilters(); });

        JPanel filterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterRight.setOpaque(false);
        filterRight.add(cbFilterJurusan);
        filterRight.add(cbFilterAngkatan);

        tableTopBar.add(txtSearch, BorderLayout.CENTER);
        tableTopBar.add(filterRight, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(25, 28, 50));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,10)));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Hide scrollbar visually but keep scroll functionality
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scroll.getVerticalScrollBar().setBackground(new Color(30,33,58));
        scroll.getVerticalScrollBar().setForeground(new Color(70,75,120));

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(tableTopBar, BorderLayout.NORTH);
        tableArea.add(scroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(tableArea, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Wire actions
        btnLoad.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> addData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.convertRowIndexToModel(table.getSelectedRow());
                String nimFull = str(tableModel.getValueAt(r, 1));
                if (nimFull.length() >= 2) txtNimSuffix.setText(nimFull.substring(2));
                else txtNimSuffix.setText(nimFull);
                txtNama.setText(str(tableModel.getValueAt(r, 2)));
                cbJK.setSelectedItem(str(tableModel.getValueAt(r, 3)));
                cbJurusan.setSelectedItem(str(tableModel.getValueAt(r, 4)));
                txtKelas.setText(str(tableModel.getValueAt(r, 5)));
                cbAngkatan.setSelectedItem(str(tableModel.getValueAt(r, 6)));
                cbStatus.setSelectedItem(str(tableModel.getValueAt(r, 7)));
            }
        });

        updateNimPrefix();
        loadData();
    }

    private void applyFilters() {
        String text = txtSearch.getText().trim().toLowerCase();
        String jur  = cbFilterJurusan.getSelectedItem().toString();
        String ang  = cbFilterAngkatan.getSelectedItem().toString();
        rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> e) {
                boolean okText = true;
                if (!text.isEmpty()) {
                    okText = false;
                    for (int i = 1; i <= 7; i++) {
                        if (str(e.getValue(i)).toLowerCase().contains(text)) { okText = true; break; }
                    }
                }
                boolean okJur = jur.equals("Semua Jurusan") || str(e.getValue(4)).toLowerCase().contains(jur.toLowerCase());
                boolean okAng = ang.equals("Semua Angkatan") || str(e.getValue(6)).equals(ang);
                return okText && okJur && okAng;
            }
        });
    }

    private void updateNimPrefix() {
        String a = (String) cbAngkatan.getSelectedItem();
        if (a != null && a.length() == 4) lblNimPrefix.setText(a.substring(2));
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    public void loadData() {
        setStatus("Memuat...", new Color(140,145,175));
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                // Not ordering by NIM ensures new data appends to the bottom as requested
                return DatabaseConnection.restGet("Mahasiswa",
                    "select=nim,nama,jenis_kelamin,prodi,kelas,angkatan,status_aktif", true);
            }
            @Override protected void done() {
                try {
                    String json = get();
                    tableModel.setRowCount(0);
                    int n = DatabaseConnection.countRows(json);
                    for (int i = 0; i < n; i++) {
                        tableModel.addRow(new Object[]{
                            i + 1,
                            DatabaseConnection.getField(json, i, "nim"),
                            DatabaseConnection.getField(json, i, "nama"),
                            DatabaseConnection.getField(json, i, "jenis_kelamin"),
                            DatabaseConnection.getField(json, i, "prodi"),
                            DatabaseConnection.getField(json, i, "kelas"),
                            DatabaseConnection.getField(json, i, "angkatan"),
                            DatabaseConnection.getField(json, i, "status_aktif"),
                            getSemesterInt(DatabaseConnection.getField(json, i, "angkatan"))
                        });
                    }
                    setStatus("✓ " + n + " mahasiswa dimuat.", new Color(70,200,120));
                } catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void addData() {
        String nim = lblNimPrefix.getText() + txtNimSuffix.getText().trim();
        String nama = txtNama.getText().trim();
        if (txtNimSuffix.getText().trim().isEmpty() || nama.isEmpty()) { setStatus("⚠ NIM dan Nama wajib diisi.", Palette.WARNING); return; }
        String jk = (String) cbJK.getSelectedItem();
        String prodi = (String) cbJurusan.getSelectedItem();
        String kelas = txtKelas.getText().trim();
        String angkatan = (String) cbAngkatan.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();
        int smt = semesterFromAngkatan(angkatan);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restPost("Mahasiswa",
                    String.format("{\"nim\":\"%s\",\"nama\":\"%s\",\"jenis_kelamin\":\"%s\",\"prodi\":\"%s\",\"kelas\":\"%s\",\"angkatan\":\"%s\",\"status_aktif\":\"%s\",\"semester\":%d}",
                    DatabaseConnection.escape(nim), DatabaseConnection.escape(nama), jk,
                    DatabaseConnection.escape(prodi), DatabaseConnection.escape(kelas),
                    DatabaseConnection.escape(angkatan), DatabaseConnection.escape(status), smt));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Mahasiswa ditambahkan.", new Color(70,200,120)); loadData(); clearForm(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void updateData() {
        int row = table.getSelectedRow();
        if (row == -1) { setStatus("⚠ Pilih baris.", Palette.WARNING); return; }
        String nim = lblNimPrefix.getText() + txtNimSuffix.getText().trim();
        String nama = txtNama.getText().trim();
        String jk = (String) cbJK.getSelectedItem();
        String prodi = (String) cbJurusan.getSelectedItem();
        String kelas = txtKelas.getText().trim();
        String angkatan = (String) cbAngkatan.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();
        int smt = semesterFromAngkatan(angkatan);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restPatch("Mahasiswa", "nim=eq." + DatabaseConnection.escape(nim),
                    String.format("{\"nama\":\"%s\",\"jenis_kelamin\":\"%s\",\"prodi\":\"%s\",\"kelas\":\"%s\",\"angkatan\":\"%s\",\"status_aktif\":\"%s\",\"semester\":%d}",
                    DatabaseConnection.escape(nama), jk, DatabaseConnection.escape(prodi),
                    DatabaseConnection.escape(kelas), DatabaseConnection.escape(angkatan),
                    DatabaseConnection.escape(status), smt));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Data diperbarui.", new Color(70,200,120)); loadData(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void deleteData() {
        int row = table.getSelectedRow();
        if (row == -1) { setStatus("⚠ Pilih baris.", Palette.WARNING); return; }
        int r = table.convertRowIndexToModel(row);
        String nim = (String) tableModel.getValueAt(r, 1);
        if (JOptionPane.showConfirmDialog(this, "Hapus mahasiswa " + nim + "?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restDelete("Mahasiswa", "nim=eq." + DatabaseConnection.escape(nim)); return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Dihapus.", new Color(70,200,120)); loadData(); clearForm(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void clearForm() {
        txtNimSuffix.setText(""); txtNama.setText(""); txtKelas.setText("");
        cbJK.setSelectedIndex(0); cbJurusan.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0); cbAngkatan.setSelectedIndex(0);
        table.clearSelection();
    }

    private int semesterFromAngkatan(String a) {
        if ("2025".equals(a)) return 2;
        if ("2024".equals(a)) return 4;
        if ("2023".equals(a)) return 6;
        if ("2022".equals(a)) return 8;
        return 1;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(new Color(35, 38, 65));
        cb.setForeground(new Color(200, 205, 230));
        cb.setFont(Palette.FONT_BODY);
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 34));
    }

    private JLabel softLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Palette.FONT_BODY);
        l.setForeground(new Color(130, 138, 175));
        return l;
    }

    private void setStatus(String m, Color c) { lblStatus.setText(m); lblStatus.setForeground(c); }

    // Dynamically compute Semester based on Angkatan mapping (Assuming 2026 is Year 4, Semester 8)
    private int getSemesterInt(String angkatan) {
        try { return Math.max(1, (2026 - Integer.parseInt(angkatan)) * 2); } catch (Exception e) { return 1; }
    }
}
