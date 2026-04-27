package uts1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;
import uts1.ui.*;

public class KRSForm extends PremiumPanel {

    private JComboBox<String> cbMahasiswa, cbSemester;
    private List<String[]> mahasiswaList = new ArrayList<>();

    private JTable tblAvailable;
    private DefaultTableModel mdlAvailable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private PremiumTextField txtSearch;

    private JTable tblKrs;
    private DefaultTableModel mdlKrs;

    private JLabel lblStatus, lblIpkInfo;
    private JComboBox<String> cbMatkulAtas, cbMatkulMengulang;
    private PremiumButton btnTambahAtas, btnTambahMengulang;

    private double currentIpk = 0.0;
    private int currentSemester = 1;
    private int selectedMhsIndex = -1;

    public KRSForm() {
        super();
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) {
                loadData();
            }
        });

        JLabel title = new JLabel("Kartu Rencana Studi (KRS)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Palette.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(title, BorderLayout.NORTH);

        // ── SELECTOR BAR ──
        PremiumPanel selectorBar = new PremiumPanel();
        selectorBar.setBackground(new Color(30, 33, 58));
        selectorBar.setLayout(new GridBagLayout());
        selectorBar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        GridBagConstraints sg = new GridBagConstraints();
        sg.insets = new Insets(4, 6, 4, 6);
        sg.fill = GridBagConstraints.HORIZONTAL;

        cbMahasiswa = new JComboBox<>();
        cbSemester  = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8"});
        styleCombo(cbMahasiswa, 300);
        styleCombo(cbSemester, 70);

        PremiumButton btnLoad = new PremiumButton("Tampilkan", Palette.PRIMARY, Palette.PRIMARY_HOVER);
        btnLoad.setPreferredSize(new Dimension(120, 34));

        lblIpkInfo = new JLabel("IPK: —");
        lblIpkInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblIpkInfo.setForeground(new Color(100, 210, 140));

        lblStatus = new JLabel("Pilih mahasiswa lalu klik Tampilkan.");
        lblStatus.setFont(Palette.FONT_SMALL);
        lblStatus.setForeground(new Color(140,145,175));

        // MK Atas row
        cbMatkulAtas = new JComboBox<>();
        styleCombo(cbMatkulAtas, 260);
        cbMatkulAtas.setEnabled(false);
        Color subtleTeal = new Color(45, 115, 95);
        Color subtleTealHover = new Color(55, 130, 110);
        btnTambahAtas = new PremiumButton("Ambil MK Atas", subtleTeal, subtleTealHover);
        btnTambahAtas.setPreferredSize(new Dimension(140, 34));
        btnTambahAtas.setEnabled(false);

        // MK Mengulang row
        cbMatkulMengulang = new JComboBox<>();
        styleCombo(cbMatkulMengulang, 260);
        cbMatkulMengulang.setEnabled(false);
        Color subtleMustard = new Color(130, 100, 40);
        Color subtleMustardHover = new Color(150, 115, 45);
        btnTambahMengulang = new PremiumButton("Ambil MK Mengulang", subtleMustard, subtleMustardHover);
        btnTambahMengulang.setPreferredSize(new Dimension(160, 34));
        btnTambahMengulang.setEnabled(false);

        // Row 0: mahasiswa selector
        sg.gridy=0; sg.gridx=0; sg.weightx=0; selectorBar.add(softLabel("Mahasiswa:"), sg);
        sg.gridx=1; sg.weightx=1; selectorBar.add(cbMahasiswa, sg);
        sg.gridx=2; sg.weightx=0; selectorBar.add(softLabel("Smt:"), sg);
        sg.gridx=3; sg.weightx=0; selectorBar.add(cbSemester, sg);
        sg.gridx=4; selectorBar.add(btnLoad, sg);
        sg.gridx=5; sg.weightx=0.3; selectorBar.add(lblIpkInfo, sg);
        sg.gridx=6; sg.weightx=1; selectorBar.add(lblStatus, sg);

        // Row 1: MK Atas (visible only when eligible)
        sg.gridy=1; sg.gridx=0; sg.weightx=0; selectorBar.add(softLabel("MK Atas:"), sg);
        sg.gridx=1; sg.weightx=1; sg.gridwidth=3; selectorBar.add(cbMatkulAtas, sg); sg.gridwidth=1;
        sg.gridx=4; sg.weightx=0; selectorBar.add(btnTambahAtas, sg);

        // Row 2: MK Mengulang
        sg.gridy=2; sg.gridx=0; sg.weightx=0; selectorBar.add(softLabel("MK Mengulang:"), sg);
        sg.gridx=1; sg.weightx=1; sg.gridwidth=3; selectorBar.add(cbMatkulMengulang, sg); sg.gridwidth=1;
        sg.gridx=4; sg.weightx=0; selectorBar.add(btnTambahMengulang, sg);

        // ── SPLIT PANE ──
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setOpaque(false);
        split.setDividerSize(6);
        split.setResizeWeight(0.55);
        split.setBorder(null);
        split.setDividerLocation(0.55);

        // TOP: Available MK
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);

        JLabel lblAvail = sectionLabel("Mata Kuliah Tersedia (Klik untuk memilih)");

        mdlAvailable = new DefaultTableModel(
            new Object[]{"Kode MK","Nama Mata Kuliah","SKS","Smt","Dosen","Hari","Mulai","Selesai","Ruangan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAvailable = new JTable(mdlAvailable);
        MainMenu.styleTable(tblAvailable);
        rowSorter = new TableRowSorter<>(mdlAvailable);
        tblAvailable.setRowSorter(rowSorter);

        // ── SEARCH BAR directly above the available MK table ──
        JPanel availTopBar = new JPanel(new BorderLayout(10, 0));
        availTopBar.setOpaque(false);
        availTopBar.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        txtSearch = new PremiumTextField();
        txtSearch.setPreferredSize(new Dimension(0, 34));
        txtSearch.putClientProperty("JTextField.placeholderText", "  🔍  Cari kode MK, nama, dosen...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        PremiumButton btnTambah = new PremiumButton("Tambah KRS", Palette.SUCCESS, Palette.SUCCESS.darker());
        btnTambah.setPreferredSize(new Dimension(190, 34));

        availTopBar.add(txtSearch, BorderLayout.CENTER);
        availTopBar.add(btnTambah, BorderLayout.EAST);

        JScrollPane scrollAvail = styledScroll(tblAvailable);

        topPanel.add(availTopBar, BorderLayout.CENTER);
        topPanel.add(scrollAvail, BorderLayout.SOUTH);
        // Make scroll fill the rest
        JPanel topOuter = new JPanel(new BorderLayout(0, 0));
        topOuter.setOpaque(false);
        topOuter.add(lblAvail, BorderLayout.NORTH);
        topOuter.add(availTopBar, BorderLayout.CENTER);
        topOuter.add(scrollAvail, BorderLayout.SOUTH);
        // Actually we need scroll to take most space — use a different layout
        JPanel topFull = new JPanel(new BorderLayout(0, 4));
        topFull.setOpaque(false);
        JPanel topHead = new JPanel(new BorderLayout(0, 4));
        topHead.setOpaque(false);
        topHead.add(lblAvail, BorderLayout.NORTH);
        topHead.add(availTopBar, BorderLayout.CENTER);
        topFull.add(topHead, BorderLayout.NORTH);
        topFull.add(scrollAvail, BorderLayout.CENTER);

        // BOTTOM: KRS mahasiswa
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
        bottomPanel.setOpaque(false);

        JLabel lblKrs = sectionLabel("KRS Mahasiswa Terpilih");
        mdlKrs = new DefaultTableModel(
            new Object[]{"No","Kode MK","Mata Kuliah","SKS","Smt","Nama Dosen","Hari","Mulai","Selesai","Ruangan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKrs = new JTable(mdlKrs);
        MainMenu.styleTable(tblKrs);

        JScrollPane scrollKrs = styledScroll(tblKrs);

        PremiumButton btnHapus = new PremiumButton("Batalkan KRS Terpilih", Palette.DANGER, Palette.DANGER.darker());
        btnHapus.setPreferredSize(new Dimension(200, 34));

        JPanel delBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        delBar.setOpaque(false);
        delBar.add(btnHapus);

        bottomPanel.add(lblKrs, BorderLayout.NORTH);
        bottomPanel.add(scrollKrs, BorderLayout.CENTER);
        bottomPanel.add(delBar, BorderLayout.SOUTH);

        split.setTopComponent(topFull);
        split.setBottomComponent(bottomPanel);

        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.setOpaque(false);
        mainContent.add(selectorBar, BorderLayout.NORTH);
        mainContent.add(split, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // Wire events
        btnLoad.addActionListener(e -> loadData());
        btnTambah.addActionListener(e -> tambahKRS());
        btnHapus.addActionListener(e -> hapusKRS());
        btnTambahAtas.addActionListener(e -> ambilMkAtas());
        btnTambahMengulang.addActionListener(e -> ambilMkMengulang());

        loadMahasiswa();
    }

    private JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(new Color(25, 28, 50));
        sp.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,10)));
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        return sp;
    }

    private void applyFilters() {
        String text = txtSearch.getText().trim().toLowerCase();
        if (text.isEmpty()) { rowSorter.setRowFilter(null); return; }
        rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> e) {
                for (int i = 0; i < e.getValueCount(); i++) {
                    if (e.getValue(i) != null && e.getValue(i).toString().toLowerCase().contains(text)) return true;
                }
                return false;
            }
        });
    }

    private void loadMahasiswa() {
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                return DatabaseConnection.restGet("Mahasiswa", "select=nim,nama,angkatan&order=nim", true);
            }
            @Override protected void done() {
                try {
                    String json = get();
                    mahasiswaList.clear(); cbMahasiswa.removeAllItems();
                    cbMahasiswa.addItem("— Pilih Mahasiswa —");
                    int n = DatabaseConnection.countRows(json);
                    for (int i = 0; i < n; i++) {
                        String nim = DatabaseConnection.getField(json, i, "nim");
                        String nama = DatabaseConnection.getField(json, i, "nama");
                        String angkatan = DatabaseConnection.getField(json, i, "angkatan");
                        int smt = getSemesterInt(angkatan);
                        mahasiswaList.add(new String[]{nim, nama, String.valueOf(smt)});
                        cbMahasiswa.addItem(nim + " - " + nama + " - Semester " + smt);
                    }
                } catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    public void loadData() {
        int idx = cbMahasiswa.getSelectedIndex();
        if (idx <= 0) { setStatus("⚠ Pilih mahasiswa.", Palette.WARNING); return; }
        selectedMhsIndex = idx;
        String nim = mahasiswaList.get(idx - 1)[0];
        try { currentSemester = Integer.parseInt((String) cbSemester.getSelectedItem()); }
        catch (Exception ex) { currentSemester = 1; }
        String semFilter = (String) cbSemester.getSelectedItem();
        setStatus("Memuat...", new Color(140,145,175));

        new SwingWorker<String[], Void>() {
            @Override protected String[] doInBackground() throws Exception {
                String nilaiUrl = "select=nilai_akhir,grade,kode_matakuliah&nim=eq." + DatabaseConnection.escape(nim);
                String krsUrl   = "select=id,kode_matakuliah&nim=eq." + DatabaseConnection.escape(nim);
                return new String[]{
                    DatabaseConnection.restGet("Matakuliah", "select=kode_matakuliah,nama_matakuliah,sks,hari,jam_mulai,jam_selesai,ruangan,kode_dosen,semester&order=kode_matakuliah", true),
                    DatabaseConnection.restGet("KRS", krsUrl, true),
                    DatabaseConnection.restGet("Dosen", "select=kode_dosen,nama", true),
                    DatabaseConnection.restGet("Nilai", nilaiUrl, true)
                };
            }
            @Override protected void done() {
                try {
                    String[] r = get();
                    String mkJson = r[0], krsJson = r[1], dosenJson = r[2], nilaiJson = r[3];

                    // Dosen map
                    Map<String, String> dosenMap = new HashMap<>();
                    int nd = DatabaseConnection.countRows(dosenJson);
                    for (int i = 0; i < nd; i++)
                        dosenMap.put(DatabaseConnection.getField(dosenJson, i, "kode_dosen"),
                                     DatabaseConnection.getField(dosenJson, i, "nama"));

                    // SKS map for IPK
                    Map<String, Integer> mkSks = new HashMap<>();
                    int nmkAll = DatabaseConnection.countRows(mkJson);
                    for (int i = 0; i < nmkAll; i++) {
                        try { mkSks.put(DatabaseConnection.getField(mkJson, i, "kode_matakuliah"),
                              Integer.parseInt(DatabaseConnection.getField(mkJson, i, "sks"))); }
                        catch (Exception ignored) {}
                    }

                    // Calculate IPK and find failed MK
                    int totalSks = 0; double totalVal = 0;
                    Set<String> failedMatkul = new HashSet<>();
                    int nn = DatabaseConnection.countRows(nilaiJson);
                    for (int i = 0; i < nn; i++) {
                        String kode = DatabaseConnection.getField(nilaiJson, i, "kode_matakuliah");
                        int sks = mkSks.getOrDefault(kode, 0);
                        try {
                            String grade = DatabaseConnection.getField(nilaiJson, i, "grade");
                            double ipkVal = 0;
                            if ("A".equals(grade)) ipkVal = 4;
                            else if ("B".equals(grade)) ipkVal = 3;
                            else if ("C".equals(grade)) ipkVal = 2;
                            else if ("D".equals(grade)) { ipkVal = 1; failedMatkul.add(kode); }
                            else if ("E".equals(grade)) { ipkVal = 0; failedMatkul.add(kode); }
                            
                            totalSks += sks; totalVal += (ipkVal * sks);
                        } catch (Exception ignored) {}
                    }
                    currentIpk = totalSks > 0 ? totalVal / totalSks : 0.0;
                    lblIpkInfo.setText(String.format("IPK: %.2f", currentIpk));

                    // Registered set
                    Set<String> registered = new HashSet<>();
                    int nk = DatabaseConnection.countRows(krsJson);
                    for (int i = 0; i < nk; i++)
                        registered.add(DatabaseConnection.getField(krsJson, i, "kode_matakuliah"));

                    // MK Atas
                    cbMatkulAtas.removeAllItems();
                    cbMatkulAtas.setEnabled(false); btnTambahAtas.setEnabled(false);
                    boolean eligibleAtas = (currentIpk >= 3.50 && currentSemester >= 2 && currentSemester <= 5);
                    cbMatkulAtas.addItem(eligibleAtas
                        ? "— MK Atas (Semester " + (currentSemester + 2) + ") —"
                        : "IPK < 3.50 atau Semester Tidak Sesuai");

                    // MK Mengulang
                    cbMatkulMengulang.removeAllItems();
                    cbMatkulMengulang.setEnabled(false); btnTambahMengulang.setEnabled(false);
                    if (failedMatkul.isEmpty()) {
                        cbMatkulMengulang.addItem("— Tidak Ada MK Mengulang —");
                    } else {
                        cbMatkulMengulang.addItem("— Pilih MK Mengulang —");
                    }

                    // Populate available MK table
                    mdlAvailable.setRowCount(0);
                    for (int i = 0; i < nmkAll; i++) {
                        String kode = DatabaseConnection.getField(mkJson, i, "kode_matakuliah");
                        String nmMk = DatabaseConnection.getField(mkJson, i, "nama_matakuliah");
                        if (failedMatkul.contains(kode)) {
                            cbMatkulMengulang.addItem(kode + " - " + nmMk);
                            cbMatkulMengulang.setEnabled(true); btnTambahMengulang.setEnabled(true);
                        }
                        
                        if (registered.contains(kode)) continue;
                        String sem = DatabaseConnection.getField(mkJson, i, "semester");
                        String kdosen = DatabaseConnection.getField(mkJson, i, "kode_dosen");
                        String dnama = dosenMap.getOrDefault(kdosen, kdosen);

                        if (sem.equals(semFilter)) {
                            mdlAvailable.addRow(new Object[]{
                                kode, nmMk,
                                DatabaseConnection.getField(mkJson, i, "sks"),
                                sem,
                                dnama,
                                DatabaseConnection.getField(mkJson, i, "hari"),
                                DatabaseConnection.getField(mkJson, i, "jam_mulai"),
                                DatabaseConnection.getField(mkJson, i, "jam_selesai"),
                                DatabaseConnection.getField(mkJson, i, "ruangan")
                            });
                        }

                        if (eligibleAtas && sem.equals(String.valueOf(currentSemester + 2))) {
                            cbMatkulAtas.addItem(kode + " - " + nmMk);
                            cbMatkulAtas.setEnabled(true); btnTambahAtas.setEnabled(true);
                        }
                    }

                    // Load KRS table
                    loadKRS(nim, mkJson, krsJson, dosenMap);
                    String namaMhs = mahasiswaList.get(selectedMhsIndex-1)[1];
                    setStatus("✓ " + nk + " KRS dimuat untuk " + namaMhs, new Color(70,200,120));
                } catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void loadKRS(String nim, String mkJson, String krsJson, Map<String, String> dosenMap) {
        try {
            // Build MK detail map
            Map<String, Object[]> mkMap = new HashMap<>();
            int nm = DatabaseConnection.countRows(mkJson);
            for (int i = 0; i < nm; i++) {
                String kode = DatabaseConnection.getField(mkJson, i, "kode_matakuliah");
                String kdosen = DatabaseConnection.getField(mkJson, i, "kode_dosen");
                mkMap.put(kode, new Object[]{
                    DatabaseConnection.getField(mkJson, i, "nama_matakuliah"),
                    DatabaseConnection.getField(mkJson, i, "sks"),
                    DatabaseConnection.getField(mkJson, i, "semester"),
                    dosenMap != null ? dosenMap.getOrDefault(kdosen, kdosen) : kdosen,
                    DatabaseConnection.getField(mkJson, i, "hari"),
                    DatabaseConnection.getField(mkJson, i, "jam_mulai"),
                    DatabaseConnection.getField(mkJson, i, "jam_selesai"),
                    DatabaseConnection.getField(mkJson, i, "ruangan")
                });
            }
            mdlKrs.setRowCount(0);
            int n = DatabaseConnection.countRows(krsJson);
            for (int i = 0; i < n; i++) {
                String kode = DatabaseConnection.getField(krsJson, i, "kode_matakuliah");
                Object[] mk = mkMap.getOrDefault(kode, new Object[]{"—","—","—","—","—","—","—","—"});
                mdlKrs.addRow(new Object[]{i+1, kode, mk[0], mk[1], mk[2], mk[3], mk[4], mk[5], mk[6], mk[7]});
            }
        } catch (Exception ignored) {}
    }

    private void tambahKRS() {
        int row = tblAvailable.getSelectedRow();
        if (row == -1) { setStatus("⚠ Klik baris mata kuliah yang ingin ditambah.", Palette.WARNING); return; }
        String kode = (String) mdlAvailable.getValueAt(tblAvailable.convertRowIndexToModel(row), 0);
        insertKrsRequest(kode);
    }

    private void ambilMkAtas() {
        if (cbMatkulAtas.getSelectedIndex() <= 0) { setStatus("⚠ Pilih MK atas terlebih dahulu.", Palette.WARNING); return; }
        String item = (String) cbMatkulAtas.getSelectedItem();
        if (item == null || item.contains("IPK") || item.contains("—")) return;
        String kode = item.split(" - ")[0].trim();
        insertKrsRequest(kode);
    }

    private void ambilMkMengulang() {
        if (cbMatkulMengulang.getSelectedIndex() <= 0) { setStatus("⚠ Pilih MK mengulang terlebih dahulu.", Palette.WARNING); return; }
        String item = (String) cbMatkulMengulang.getSelectedItem();
        if (item == null || item.contains("—")) return;
        String kode = item.split(" - ")[0].trim();
        insertKrsRequest(kode);
    }

    private void insertKrsRequest(String kode) {
        int idx = cbMahasiswa.getSelectedIndex();
        if (idx <= 0) { setStatus("⚠ Pilih mahasiswa.", Palette.WARNING); return; }
        String nim = mahasiswaList.get(idx - 1)[0];
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restPost("KRS",
                    String.format("{\"nim\":\"%s\",\"kode_matakuliah\":\"%s\"}",
                    DatabaseConnection.escape(nim), DatabaseConnection.escape(kode)));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ MK " + kode + " ditambahkan ke KRS.", new Color(70,200,120)); loadData(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void hapusKRS() {
        int row = tblKrs.getSelectedRow();
        if (row == -1) { setStatus("⚠ Klik baris KRS yang ingin dibatalkan.", Palette.WARNING); return; }
        String kode = (String) mdlKrs.getValueAt(row, 1);
        int idx = cbMahasiswa.getSelectedIndex();
        if (idx <= 0) return;
        String nim = mahasiswaList.get(idx - 1)[0];
        if (JOptionPane.showConfirmDialog(this, "Batalkan KRS " + kode + " untuk " + nim + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restDelete("KRS",
                    "nim=eq." + DatabaseConnection.escape(nim) + "&kode_matakuliah=eq." + DatabaseConnection.escape(kode));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ KRS " + kode + " berhasil dibatalkan.", new Color(70,200,120)); loadData(); }
                catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void styleCombo(JComboBox<?> cb, int minW) {
        cb.setBackground(new Color(35, 38, 65));
        cb.setForeground(new Color(200, 205, 230));
        cb.setFont(Palette.FONT_BODY);
        cb.setPreferredSize(new Dimension(minW, 34));
    }

    private JLabel softLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(Palette.FONT_BODY); l.setForeground(new Color(130, 138, 175)); return l;
    }

    private JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(Palette.TEXT_PRIMARY); return l;
    }
    
    private int getSemesterInt(String angkatan) {
        try { return Math.max(1, (2026 - Integer.parseInt(angkatan)) * 2); } catch (Exception e) { return 1; }
    }

    private void setStatus(String m, Color c) { lblStatus.setText(m); lblStatus.setForeground(c); }
}
