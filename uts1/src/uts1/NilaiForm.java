package uts1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import uts1.ui.*;

public class NilaiForm extends PremiumPanel {

    private JComboBox<String> cbMahasiswa, cbMataKuliah;
    private PremiumTextField txtAbsensi, txtTugas, txtQuiz, txtUts, txtUas, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel lblStatus, lblNilaiAkhir, lblIpkInfo;
    private java.util.ArrayList<String[]> mahasiswaList = new java.util.ArrayList<>();
    private java.util.ArrayList<String[]> matkulList = new java.util.ArrayList<>();
    private Map<Integer, String> rowToKodeMk = new HashMap<>();

    public NilaiForm() {
        super();
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) {
                loadAll();
            }
        });

        JLabel title = new JLabel("Data Nilai");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Palette.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        add(title, BorderLayout.NORTH);

        // ── INPUT PANEL ──
        PremiumPanel ip = new PremiumPanel();
        ip.setBackground(new Color(30, 33, 58));
        ip.setLayout(new GridBagLayout());
        ip.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 7, 5, 7);

        cbMahasiswa  = new JComboBox<>();
        cbMataKuliah = new JComboBox<>();
        styleCombo(cbMahasiswa); styleCombo(cbMataKuliah);

        txtAbsensi = field("0"); txtTugas = field("0");
        txtQuiz = field("0"); txtUts = field("0"); txtUas = field("0");

        lblNilaiAkhir = new JLabel("—");
        lblNilaiAkhir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNilaiAkhir.setForeground(new Color(120, 190, 255));

        DocumentListener calc = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcNilai(); }
            public void removeUpdate(DocumentEvent e) { calcNilai(); }
            public void changedUpdate(DocumentEvent e) { calcNilai(); }
        };
        for (PremiumTextField f : new PremiumTextField[]{txtAbsensi, txtTugas, txtQuiz, txtUts, txtUas})
            f.getDocument().addDocumentListener(calc);

        // Row 0: Mahasiswa | MataKuliah
        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.1; ip.add(softLabel("Mahasiswa"),gbc);
        gbc.gridx=1; gbc.weightx=0.4; gbc.gridwidth=3; ip.add(cbMahasiswa,gbc); gbc.gridwidth=1;
        gbc.gridx=4; gbc.weightx=0.1; ip.add(softLabel("Mata Kuliah"),gbc);
        gbc.gridx=5; gbc.weightx=0.4; gbc.gridwidth=3; ip.add(cbMataKuliah,gbc); gbc.gridwidth=1;

        // Row 1: score fields
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.08; ip.add(softLabel("Absensi"),gbc);
        gbc.gridx=1; gbc.weightx=0.12; ip.add(txtAbsensi,gbc);
        gbc.gridx=2; gbc.weightx=0.08; ip.add(softLabel("Tugas"),gbc);
        gbc.gridx=3; gbc.weightx=0.12; ip.add(txtTugas,gbc);
        gbc.gridx=4; gbc.weightx=0.08; ip.add(softLabel("Quiz"),gbc);
        gbc.gridx=5; gbc.weightx=0.12; ip.add(txtQuiz,gbc);
        gbc.gridx=6; gbc.weightx=0.08; ip.add(softLabel("UTS"),gbc);
        gbc.gridx=7; gbc.weightx=0.12; ip.add(txtUts,gbc);

        // Row 2: UAS + preview + IPK
        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.08; ip.add(softLabel("UAS"),gbc);
        gbc.gridx=1; gbc.weightx=0.12; ip.add(txtUas,gbc);
        gbc.gridx=2; gbc.weightx=0.08; ip.add(softLabel("Nilai Akhir"),gbc);
        gbc.gridx=3; gbc.weightx=0.22; ip.add(lblNilaiAkhir,gbc);
        
        lblIpkInfo = new JLabel("IPK: —");
        lblIpkInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblIpkInfo.setForeground(new Color(255, 180, 100));
        gbc.gridx=4; gbc.weightx=0.08; ip.add(softLabel("Info Mahasiswa"),gbc);
        gbc.gridx=5; gbc.weightx=0.42; gbc.gridwidth=3; ip.add(lblIpkInfo,gbc); gbc.gridwidth=1;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        Color neutralBtn = new Color(75, 82, 115);
        Color neutralBtnHover = new Color(90, 98, 135);
        PremiumButton bLoad = new PremiumButton("Refresh", neutralBtn, neutralBtnHover);
        PremiumButton bAdd  = new PremiumButton("Tambah",  Palette.SUCCESS,       Palette.SUCCESS.darker());
        PremiumButton bEdit = new PremiumButton("Update",  Palette.PRIMARY,       Palette.PRIMARY_HOVER);
        PremiumButton bDel  = new PremiumButton("Hapus",   Palette.DANGER,        Palette.DANGER.darker());
        PremiumButton bClr  = new PremiumButton("Reset",   neutralBtn, neutralBtnHover);
        for (JButton b : new JButton[]{bLoad, bAdd, bEdit, bDel, bClr}) { b.setPreferredSize(new Dimension(90,34)); btnPanel.add(b); }

        lblStatus = new JLabel("Status: Idle");
        lblStatus.setFont(Palette.FONT_SMALL);
        lblStatus.setForeground(new Color(140,145,175));

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=8; ip.add(btnPanel,gbc);
        gbc.gridy=4; ip.add(lblStatus,gbc);

        // ── TABLE ──
        tableModel = new DefaultTableModel(
            new Object[]{"No","NIM","Nama Mahasiswa","Mata Kuliah","Absensi","Tugas","Quiz","UTS","UAS","Nilai Akhir","Grade"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        MainMenu.styleTable(table);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Search + filter bar directly above table
        JPanel tableTopBar = new JPanel(new BorderLayout(10, 0));
        tableTopBar.setOpaque(false);
        tableTopBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));

        txtSearch = new PremiumTextField();
        txtSearch.setPreferredSize(new Dimension(0, 34));
        txtSearch.putClientProperty("JTextField.placeholderText", "  🔍  Cari NIM, nama, grade, status...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        JComboBox<String> cbFilterGrade = new JComboBox<>(new String[]{"Semua Grade","A","B","C","D","E"});
        styleCombo(cbFilterGrade);
        cbFilterGrade.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                String g = cbFilterGrade.getSelectedItem().toString();
                if (g.equals("Semua Grade")) { rowSorter.setRowFilter(null); return; }
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + g, 10));
            }
        });

        JPanel filterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterRight.setOpaque(false);
        filterRight.add(cbFilterGrade);

        tableTopBar.add(txtSearch, BorderLayout.CENTER);
        tableTopBar.add(filterRight, BorderLayout.EAST);

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
        centerPanel.add(ip, BorderLayout.NORTH);
        centerPanel.add(tableArea, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        bLoad.addActionListener(e -> loadAll());
        bAdd.addActionListener(e -> addNilai());
        bEdit.addActionListener(e -> updateNilai());
        bDel.addActionListener(e -> deleteNilai());
        
        cbMahasiswa.addActionListener(e -> updateIpkDisplay());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.convertRowIndexToModel(table.getSelectedRow());
                String nim = str(tableModel.getValueAt(r, 1));
                for (int i = 0; i < mahasiswaList.size(); i++) {
                    if (mahasiswaList.get(i)[0].equals(nim)) { cbMahasiswa.setSelectedIndex(i+1); break; }
                }
                String kodeMk = rowToKodeMk.get(r);
                if (kodeMk != null) {
                    for (int i = 0; i < matkulList.size(); i++) {
                        if (matkulList.get(i)[0].equals(kodeMk)) { cbMataKuliah.setSelectedIndex(i+1); break; }
                    }
                }
                txtAbsensi.setText(formatNum(tableModel.getValueAt(r, 4)));
                txtTugas.setText(formatNum(tableModel.getValueAt(r, 5)));
                txtQuiz.setText(formatNum(tableModel.getValueAt(r, 6)));
                txtUts.setText(formatNum(tableModel.getValueAt(r, 7)));
                txtUas.setText(formatNum(tableModel.getValueAt(r, 8)));
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

    private void calcNilai() {
        try {
            double abs = parseD(txtAbsensi), tgs = parseD(txtTugas), qz = parseD(txtQuiz),
                   uts = parseD(txtUts),    uas = parseD(txtUas);
            double total = 0.10*abs + 0.15*tgs + 0.15*qz + 0.30*uts + 0.30*uas;
            String grade = nilaiToGrade(total);
            lblNilaiAkhir.setText(String.format("%.1f  (%s)", total, grade));
            lblNilaiAkhir.setForeground(Palette.getGradeColor(grade));
        } catch (NumberFormatException ignored) {}
    }

    private double parseD(PremiumTextField f) {
        try { return Double.parseDouble(f.getText().trim().isEmpty() ? "0" : f.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
    
    private String formatNum(Object v) {
        if (v == null) return "0";
        try {
            double num = Double.parseDouble(v.toString());
            if (num == (int)num) return String.valueOf((int)num);
            return String.valueOf(num);
        } catch(Exception e) { return v.toString(); }
    }

    private String nilaiToGrade(double v) {
        if (v >= 80) return "A"; if (v >= 70) return "B";
        if (v >= 55) return "C"; if (v >= 40) return "D"; return "E";
    }

    public void loadAll() {
        setStatus("Memuat...", new Color(140,145,175));
        new SwingWorker<String[], Void>() {
            @Override protected String[] doInBackground() throws Exception {
                return new String[]{
                    DatabaseConnection.restGet("Mahasiswa", "select=nim,nama,angkatan&order=nim", true),
                    DatabaseConnection.restGet("Matakuliah", "select=kode_matakuliah,nama_matakuliah", true),
                    DatabaseConnection.restGet("Nilai", "select=id,nim,kode_matakuliah,absensi,tugas,quiz,uts,uas,nilai_akhir,grade&order=id", true)
                };
            }
            @Override protected void done() {
                try {
                    String[] r = get();
                    mahasiswaList.clear(); cbMahasiswa.removeAllItems(); cbMahasiswa.addItem("— Pilih Mahasiswa —");
                    int nm = DatabaseConnection.countRows(r[0]);
                    for (int i = 0; i < nm; i++) {
                        String nim = DatabaseConnection.getField(r[0], i, "nim");
                        String nama = DatabaseConnection.getField(r[0], i, "nama");
                        String angkatan = DatabaseConnection.getField(r[0], i, "angkatan");
                        int smt = getSemesterInt(angkatan);
                        mahasiswaList.add(new String[]{nim, nama, String.valueOf(smt)});
                        cbMahasiswa.addItem(nim + " - " + nama + " - Semester " + smt);
                    }
                    matkulList.clear(); cbMataKuliah.removeAllItems(); cbMataKuliah.addItem("— Pilih Mata Kuliah —");
                    int nk = DatabaseConnection.countRows(r[1]);
                    for (int i = 0; i < nk; i++) {
                        String kode = DatabaseConnection.getField(r[1], i, "kode_matakuliah");
                        String nama = DatabaseConnection.getField(r[1], i, "nama_matakuliah");
                        matkulList.add(new String[]{kode, nama});
                        cbMataKuliah.addItem(kode + " - " + nama);
                    }
                    Map<String, String> nimToNama = new HashMap<>();
                    for (String[] m : mahasiswaList) nimToNama.put(m[0], m[1]);
                    Map<String, String> kodeToNama = new HashMap<>();
                    for (String[] m : matkulList) kodeToNama.put(m[0], m[1]);
                    tableModel.setRowCount(0); rowToKodeMk.clear();
                    int n = DatabaseConnection.countRows(r[2]);
                    for (int i = 0; i < n; i++) {
                        String nim = DatabaseConnection.getField(r[2], i, "nim");
                        String kodeMk = DatabaseConnection.getField(r[2], i, "kode_matakuliah");
                        rowToKodeMk.put(i, kodeMk);
                        tableModel.addRow(new Object[]{ i+1, nim, nimToNama.getOrDefault(nim,"—"), kodeToNama.getOrDefault(kodeMk,"—"),
                            DatabaseConnection.getField(r[2], i, "absensi"),
                            DatabaseConnection.getField(r[2], i, "tugas"),
                            DatabaseConnection.getField(r[2], i, "quiz"),
                            DatabaseConnection.getField(r[2], i, "uts"),
                            DatabaseConnection.getField(r[2], i, "uas"),
                            DatabaseConnection.getField(r[2], i, "nilai_akhir"),
                            DatabaseConnection.getField(r[2], i, "grade")
                        });
                    }
                    setStatus("✓ " + n + " nilai dimuat.", new Color(70,200,120));
                } catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void addNilai() {
        int mi = cbMahasiswa.getSelectedIndex(), mk = cbMataKuliah.getSelectedIndex();
        if (mi <= 0 || mk <= 0) { setStatus("⚠ Pilih mahasiswa dan mata kuliah.", Palette.WARNING); return; }
        String nim = mahasiswaList.get(mi-1)[0], kode = matkulList.get(mk-1)[0];
        try {
            double abs=parseD(txtAbsensi), tgs=parseD(txtTugas), qz=parseD(txtQuiz), uts=parseD(txtUts), uas=parseD(txtUas);
            double total = 0.10*abs+0.15*tgs+0.15*qz+0.30*uts+0.30*uas;
            String grade = nilaiToGrade(total);
            new SwingWorker<Void,Void>() {
                @Override protected Void doInBackground() throws Exception {
                    DatabaseConnection.restPost("Nilai",
                        String.format("{\"nim\":\"%s\",\"kode_matakuliah\":\"%s\",\"absensi\":%d,\"tugas\":%d,\"quiz\":%d,\"uts\":%d,\"uas\":%d,\"nilai_akhir\":%s,\"grade\":\"%s\"}",
                        DatabaseConnection.escape(nim),DatabaseConnection.escape(kode),(int)Math.round(abs),(int)Math.round(tgs),(int)Math.round(qz),
                        (int)Math.round(uts),(int)Math.round(uas),total,DatabaseConnection.escape(grade)));
                    return null;
                }
                @Override protected void done() {
                    try { get(); setStatus("✓ Nilai ditambahkan.", new Color(70,200,120)); loadAll(); }
                    catch (Exception ex) { setStatus("✗ "+ex.getMessage(), Palette.DANGER); }
                }
            }.execute();
        } catch (Exception e) { setStatus("⚠ Nilai harus berupa angka.", Palette.WARNING); }
    }

    private void updateNilai() {
        int row = table.getSelectedRow();
        if (row==-1) { setStatus("⚠ Pilih baris.", Palette.WARNING); return; }
        String nim = str(tableModel.getValueAt(table.convertRowIndexToModel(row), 1));
        String kode = rowToKodeMk.get(table.convertRowIndexToModel(row));
        try {
            double abs=parseD(txtAbsensi), tgs=parseD(txtTugas), qz=parseD(txtQuiz), uts=parseD(txtUts), uas=parseD(txtUas);
            double total=0.10*abs+0.15*tgs+0.15*qz+0.30*uts+0.30*uas;
            String grade=nilaiToGrade(total);
            new SwingWorker<Void,Void>() {
                @Override protected Void doInBackground() throws Exception {
                    DatabaseConnection.restPatch("Nilai","nim=eq."+DatabaseConnection.escape(nim)+"&kode_matakuliah=eq."+DatabaseConnection.escape(kode),
                        String.format("{\"absensi\":%d,\"tugas\":%d,\"quiz\":%d,\"uts\":%d,\"uas\":%d,\"nilai_akhir\":%s,\"grade\":\"%s\"}",
                        (int)Math.round(abs),(int)Math.round(tgs),(int)Math.round(qz),(int)Math.round(uts),(int)Math.round(uas),total,DatabaseConnection.escape(grade)));
                    return null;
                }
                @Override protected void done() {
                    try { get(); setStatus("✓ Nilai diperbarui.", new Color(70,200,120)); loadAll(); }
                    catch (Exception ex) { setStatus("✗ "+ex.getMessage(), Palette.DANGER); }
                }
            }.execute();
        } catch (Exception e) { setStatus("⚠ Nilai harus angka.", Palette.WARNING); }
    }

    private void deleteNilai() {
        int row = table.getSelectedRow();
        if (row==-1) { setStatus("⚠ Pilih baris.", Palette.WARNING); return; }
        int r = table.convertRowIndexToModel(row);
        String nim = str(tableModel.getValueAt(r, 1));
        String kode = rowToKodeMk.get(r);
        if (JOptionPane.showConfirmDialog(this,"Hapus nilai "+nim+" - "+kode+"?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restDelete("Nilai","nim=eq."+nim+"&kode_matakuliah=eq."+kode); return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Dihapus.", new Color(70,200,120)); loadAll(); }
                catch (Exception ex) { setStatus("✗ "+ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private PremiumTextField field(String def) { PremiumTextField f = new PremiumTextField(); f.setText(def); return f; }
    private String str(Object o) { return o == null ? "" : o.toString(); }
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(new Color(35, 38, 65)); cb.setForeground(new Color(200, 205, 230));
        cb.setFont(Palette.FONT_BODY); cb.setPreferredSize(new Dimension(0, 34));
    }
    private JLabel softLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(Palette.FONT_BODY); l.setForeground(new Color(130, 138, 175)); return l;
    }
    private void setStatus(String m, Color c) { lblStatus.setText(m); lblStatus.setForeground(c); }

    private void updateIpkDisplay() {
        int idx = cbMahasiswa.getSelectedIndex();
        if (idx <= 0) { lblIpkInfo.setText("IPK: —"); return; }
        String nim = mahasiswaList.get(idx - 1)[0];
        
        double totalPts = 0;
        int count = 0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (nim.equals(tableModel.getValueAt(i, 1))) {
                String grade = str(tableModel.getValueAt(i, 10));
                double pt = 0.0;
                switch(grade) {
                    case "A": pt = 4.0; break; case "B": pt = 3.0; break;
                    case "C": pt = 2.0; break; case "D": pt = 1.0; break;
                    case "E": pt = 0.0; break;
                }
                totalPts += pt;
                count++;
            }
        }
        
        if (count == 0) lblIpkInfo.setText("IPK: 0.00 (0 MK)");
        else lblIpkInfo.setText(String.format("IPK: %.2f (%d MK)", totalPts/count, count));
    }
    
    private int getSemesterInt(String angkatan) {
        try { return Math.max(1, (2026 - Integer.parseInt(angkatan)) * 2); } catch (Exception e) { return 1; }
    }
}
