package uts1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import uts1.ui.*;

public class DosenForm extends PremiumPanel {

    private PremiumTextField txtKode, txtNama, txtSearch;
    private JComboBox<String> cbMatkul;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel lblStatus;

    public DosenForm() {
        super();
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) {
                loadAll();
            }
        });

        JLabel title = new JLabel("Data Dosen");
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

        txtKode = new PremiumTextField();
        txtNama = new PremiumTextField();
        cbMatkul = new JComboBox<>();
        styleCombo(cbMatkul);

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.12; inputPanel.add(softLabel("Kode Dosen"),gbc);
        gbc.gridx=1; gbc.weightx=0.38; inputPanel.add(txtKode, gbc);
        gbc.gridx=2; gbc.weightx=0.12; inputPanel.add(softLabel("Nama Dosen"),gbc);
        gbc.gridx=3; gbc.weightx=0.38; inputPanel.add(txtNama, gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.12; inputPanel.add(softLabel("Mata Kuliah Diampu"),gbc);
        gbc.gridx=1; gbc.weightx=0.38; gbc.gridwidth=3; inputPanel.add(cbMatkul, gbc); gbc.gridwidth=1;

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

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=4; inputPanel.add(btnPanel, gbc);
        gbc.gridy=3; inputPanel.add(lblStatus, gbc);

        // ── TABLE ──
        tableModel = new DefaultTableModel(new Object[]{"No","Kode Dosen","Nama Dosen","Mata Kuliah"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        MainMenu.styleTable(table);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Search bar directly above table
        JPanel tableTopBar = new JPanel(new BorderLayout(10, 0));
        tableTopBar.setOpaque(false);
        tableTopBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));

        txtSearch = new PremiumTextField();
        txtSearch.setPreferredSize(new Dimension(0, 34));
        txtSearch.putClientProperty("JTextField.placeholderText", "  🔍  Cari kode, nama dosen, mata kuliah...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        tableTopBar.add(txtSearch, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(25, 28, 50));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,10)));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(tableTopBar, BorderLayout.NORTH);
        tableArea.add(scroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(tableArea, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadAll());
        btnAdd.addActionListener(e -> addData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.convertRowIndexToModel(table.getSelectedRow());
                txtKode.setText((String) tableModel.getValueAt(r, 1));
                txtNama.setText((String) tableModel.getValueAt(r, 2));
                String mk = (String) tableModel.getValueAt(r, 3);
                for (int i = 0; i < cbMatkul.getItemCount(); i++) {
                    if (cbMatkul.getItemAt(i).contains(mk)) { cbMatkul.setSelectedIndex(i); break; }
                }
            }
        });
        loadAll();
    }

    private void applyFilters() {
        String text = txtSearch.getText().trim().toLowerCase();
        if (text.isEmpty()) { rowSorter.setRowFilter(null); return; }
        rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> e) {
                for (int i = 1; i < e.getValueCount(); i++) {
                    if (e.getValue(i) != null && e.getValue(i).toString().toLowerCase().contains(text)) return true;
                }
                return false;
            }
        });
    }

    public void loadAll() {
        setStatus("Memuat...", new Color(140,145,175));
        new SwingWorker<String[], Void>() {
            @Override protected String[] doInBackground() throws Exception {
                return new String[]{
                    DatabaseConnection.restGet("Dosen", "select=kode_dosen,nama,mata_kuliah&order=kode_dosen", true),
                    DatabaseConnection.restGet("Matakuliah", "select=kode_matakuliah,nama_matakuliah", true)
                };
            }
            @Override protected void done() {
                try {
                    String[] r = get();
                    cbMatkul.removeAllItems();
                    cbMatkul.addItem("— Pilih Mata Kuliah —");
                    int nm = DatabaseConnection.countRows(r[1]);
                    for (int i = 0; i < nm; i++) {
                        cbMatkul.addItem(DatabaseConnection.getField(r[1], i, "kode_matakuliah")
                            + " - " + DatabaseConnection.getField(r[1], i, "nama_matakuliah"));
                    }
                    tableModel.setRowCount(0);
                    int n = DatabaseConnection.countRows(r[0]);
                    for (int i = 0; i < n; i++) {
                        tableModel.addRow(new Object[]{
                            i + 1,
                            DatabaseConnection.getField(r[0], i, "kode_dosen"),
                            DatabaseConnection.getField(r[0], i, "nama"),
                            DatabaseConnection.getField(r[0], i, "mata_kuliah")
                        });
                    }
                    setStatus("✓ " + n + " dosen dimuat.", new Color(70,200,120));
                } catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void addData() {
        String kode = txtKode.getText().trim(), nama = txtNama.getText().trim();
        if (kode.isEmpty() || nama.isEmpty()) { setStatus("⚠ Kode dan Nama wajib diisi.", Palette.WARNING); return; }
        String matkulFull = cbMatkul.getSelectedIndex() > 0 ? (String) cbMatkul.getSelectedItem() : "";
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restPost("Dosen",
                    String.format("{\"kode_dosen\":\"%s\",\"nama\":\"%s\",\"mata_kuliah\":\"%s\"}",
                    DatabaseConnection.escape(kode), DatabaseConnection.escape(nama), DatabaseConnection.escape(matkulFull)));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Dosen ditambahkan.", new Color(70,200,120)); loadAll(); clearForm(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void updateData() {
        int row = table.getSelectedRow();
        if (row == -1) { setStatus("⚠ Pilih baris.", Palette.WARNING); return; }
        String kode = txtKode.getText().trim(), nama = txtNama.getText().trim();
        String matkulFull = cbMatkul.getSelectedIndex() > 0 ? (String) cbMatkul.getSelectedItem() : "";
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restPatch("Dosen", "kode_dosen=eq." + DatabaseConnection.escape(kode),
                    String.format("{\"nama\":\"%s\",\"mata_kuliah\":\"%s\"}",
                    DatabaseConnection.escape(nama), DatabaseConnection.escape(matkulFull)));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Data diperbarui.", new Color(70,200,120)); loadAll(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void deleteData() {
        int row = table.getSelectedRow();
        if (row == -1) { setStatus("⚠ Pilih baris.", Palette.WARNING); return; }
        String kode = (String) tableModel.getValueAt(table.convertRowIndexToModel(row), 1);
        if (JOptionPane.showConfirmDialog(this, "Hapus dosen " + kode + "?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restDelete("Dosen", "kode_dosen=eq." + DatabaseConnection.escape(kode)); return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Dihapus.", new Color(70,200,120)); loadAll(); clearForm(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void clearForm() {
        txtKode.setText(""); txtNama.setText(""); cbMatkul.setSelectedIndex(0); table.clearSelection();
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(new Color(35, 38, 65));
        cb.setForeground(new Color(200, 205, 230));
        cb.setFont(Palette.FONT_BODY);
        cb.setPreferredSize(new Dimension(0, 34));
    }

    private JLabel softLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(Palette.FONT_BODY); l.setForeground(new Color(130, 138, 175)); return l;
    }

    private void setStatus(String m, Color c) { lblStatus.setText(m); lblStatus.setForeground(c); }
}
