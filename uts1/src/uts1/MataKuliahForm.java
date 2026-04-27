package uts1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import uts1.ui.*;

public class MataKuliahForm extends PremiumPanel {

    private PremiumTextField txtKode, txtNama, txtSks, txtRuangan, txtSearch;
    private JComboBox<String> cbSemester, cbHari, cbJamMulai, cbJamSelesai;
    private JComboBox<String> cbDosen, cbProdi, cbFilterJurusan;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel lblStatus;
    private java.util.List<String[]> dosenList = new java.util.ArrayList<>();

    public MataKuliahForm() {
        super();
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) {
                loadAll();
            }
        });

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Data Mata Kuliah");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Palette.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        cbFilterJurusan = new JComboBox<>(new String[]{"Semua Jurusan","Informatika","Sistem Informasi","Teknik Industri","Kedokteran"});
        styleCombo(cbFilterJurusan);
        cbFilterJurusan.addItemListener(e -> { if(e.getStateChange() == ItemEvent.SELECTED) applyFilters(); });

        txtSearch = new PremiumTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "  🔍  Cari kode MK, nama, prodi...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        PremiumPanel ip = new PremiumPanel();
        ip.setBackground(new Color(30, 33, 58));
        ip.setLayout(new GridBagLayout());
        ip.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 5, 8);

        txtKode    = new PremiumTextField();
        txtNama    = new PremiumTextField();
        txtSks     = new PremiumTextField();
        txtRuangan = new PremiumTextField();

        String[] semesters = {"1","2","3","4","5","6","7","8"};
        String[] hari = {"Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};
        String[] jams = {"07:00","07:30","08:00","08:30","09:00","09:30","10:00","10:30",
                         "11:00","11:30","12:00","13:00","13:30","14:00","14:30","15:00",
                         "15:30","16:00","16:30","17:00","18:00","19:00","19:30","20:00"};
        String[] prodis = {"Informatika","Sistem Informasi","Teknik Industri","Kedokteran"};

        cbSemester  = new JComboBox<>(semesters);
        cbHari      = new JComboBox<>(hari);
        cbJamMulai  = new JComboBox<>(jams);
        cbJamSelesai = new JComboBox<>(jams);
        cbDosen     = new JComboBox<>();
        cbProdi     = new JComboBox<>(prodis);

        for (JComboBox<?> cb : new JComboBox[]{cbSemester, cbHari, cbJamMulai, cbJamSelesai, cbDosen, cbProdi}) {
            styleCombo(cb);
        }

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.12; ip.add(label("Kode MK"),gbc);
        gbc.gridx=1; gbc.weightx=0.28; ip.add(txtKode,gbc);
        gbc.gridx=2; gbc.weightx=0.12; ip.add(label("Nama MK"),gbc);
        gbc.gridx=3; gbc.weightx=0.28; ip.add(txtNama,gbc);
        gbc.gridx=4; gbc.weightx=0.08; ip.add(label("SKS"),gbc);
        gbc.gridx=5; gbc.weightx=0.12; ip.add(txtSks,gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.12; ip.add(label("Semester"),gbc);
        gbc.gridx=1; gbc.weightx=0.28; ip.add(cbSemester,gbc);
        gbc.gridx=2; gbc.weightx=0.12; ip.add(label("Program Studi"),gbc);
        gbc.gridx=3; gbc.weightx=0.48; gbc.gridwidth=3; ip.add(cbProdi,gbc); gbc.gridwidth=1;

        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.12; ip.add(label("Hari"),gbc);
        gbc.gridx=1; gbc.weightx=0.28; ip.add(cbHari,gbc);
        gbc.gridx=2; gbc.weightx=0.12; ip.add(label("Jam Mulai"),gbc);
        gbc.gridx=3; gbc.weightx=0.28; ip.add(cbJamMulai,gbc);
        gbc.gridx=4; gbc.weightx=0.08; ip.add(label("Jam Selesai"),gbc);
        gbc.gridx=5; gbc.weightx=0.12; ip.add(cbJamSelesai,gbc);

        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.12; ip.add(label("Ruangan"),gbc);
        gbc.gridx=1; gbc.weightx=0.28; ip.add(txtRuangan,gbc);
        gbc.gridx=2; gbc.weightx=0.12; ip.add(label("Dosen"),gbc);
        gbc.gridx=3; gbc.weightx=0.48; gbc.gridwidth=3; ip.add(cbDosen,gbc); gbc.gridwidth=1;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnPanel.setOpaque(false);
        Color neutralBtn = new Color(75, 82, 115);
        Color neutralBtnHover = new Color(90, 98, 135);
        PremiumButton bLoad = new PremiumButton("Refresh", neutralBtn, neutralBtnHover);
        PremiumButton bAdd  = new PremiumButton("Tambah",  Palette.SUCCESS,       Palette.SUCCESS.darker());
        PremiumButton bEdit = new PremiumButton("Update",  Palette.PRIMARY,       Palette.PRIMARY_HOVER);
        PremiumButton bDel  = new PremiumButton("Hapus",   Palette.DANGER,        Palette.DANGER.darker());
        PremiumButton bClr  = new PremiumButton("Reset",   neutralBtn, neutralBtnHover);
        for (JButton b : new JButton[]{bLoad, bAdd, bEdit, bDel, bClr}) {
            b.setPreferredSize(new Dimension(90, 36)); btnPanel.add(b);
        }

        lblStatus = new JLabel("Status: Idle");
        lblStatus.setFont(Palette.FONT_BODY);
        lblStatus.setForeground(Palette.TEXT_SECONDARY);

        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=6; ip.add(btnPanel,gbc);
        gbc.gridy=5; ip.add(lblStatus,gbc);

        // Requested Columns: no, kode_matakuliah, nama_matakuliah, sks, nama_prodi, nama_dosen
        // We will keep extra columns in model but hide them from view.
        tableModel = new DefaultTableModel(
            new Object[]{
                "No","Kode MK","Nama MK","SKS","Program Studi","Nama Dosen", // Visible
                "Semester","Hari","Jam Mulai","Jam Selesai","Ruangan" // Hidden
            }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        MainMenu.styleTable(table);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Remove columns 6 to 10 from the view (so only 0-5 show)
        for (int i = 10; i >= 6; i--) {
            table.getColumnModel().removeColumn(table.getColumnModel().getColumn(i));
        }

        // ── SEARCH + FILTER bar directly above table ──
        JPanel tableTopBar = new JPanel(new BorderLayout(10, 0));
        tableTopBar.setOpaque(false);
        tableTopBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
        tableTopBar.add(txtSearch, BorderLayout.CENTER);
        JPanel filterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterRight.setOpaque(false);
        filterRight.add(cbFilterJurusan);
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
        bAdd.addActionListener(e -> addData());
        bEdit.addActionListener(e -> updateData());
        bDel.addActionListener(e -> deleteData());
        bClr.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.convertRowIndexToModel(table.getSelectedRow());
                txtKode.setText(str(tableModel.getValueAt(r,1)));
                txtNama.setText(str(tableModel.getValueAt(r,2)));
                txtSks.setText(str(tableModel.getValueAt(r,3)));
                cbProdi.setSelectedItem(str(tableModel.getValueAt(r,4)));
                
                String dosenName = str(tableModel.getValueAt(r,5));
                for(int i=0; i<cbDosen.getItemCount(); i++) {
                    if (cbDosen.getItemAt(i).contains(dosenName)) {
                        cbDosen.setSelectedIndex(i); break;
                    }
                }
                
                cbSemester.setSelectedItem(str(tableModel.getValueAt(r,6)));
                cbHari.setSelectedItem(str(tableModel.getValueAt(r,7)));
                cbJamMulai.setSelectedItem(str(tableModel.getValueAt(r,8)));
                cbJamSelesai.setSelectedItem(str(tableModel.getValueAt(r,9)));
                txtRuangan.setText(str(tableModel.getValueAt(r,10)));
            }
        });
        loadAll();
    }

    private void applyFilters() {
        String text = txtSearch.getText().trim().toLowerCase();
        String jur = cbFilterJurusan.getSelectedItem().toString();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                boolean matchesText = true;
                if (!text.isEmpty()) {
                    matchesText = false;
                    for (int i = 1; i <= 5; i++) { // Only search visible cols
                        if (str(entry.getValue(i)).toLowerCase().contains(text)) {
                            matchesText = true;
                            break;
                        }
                    }
                }
                boolean matchesJur = true;
                if (!jur.equals("Semua Jurusan")) {
                    matchesJur = str(entry.getValue(4)).toLowerCase().contains(jur.toLowerCase().replace("teknik ", ""));
                }
                return matchesText && matchesJur;
            }
        };
        rowSorter.setRowFilter(rf);
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    public void loadAll() {
        setStatus("Memuat...", Palette.TEXT_SECONDARY);
        new SwingWorker<String[], Void>() {
            @Override protected String[] doInBackground() throws Exception {
                // To get Nama Dosen, we fetch all Dosen.
                // Supabase supports `select=*,Dosen(nama)`.
                return new String[]{
                    DatabaseConnection.restGet("Matakuliah",
                        "select=kode_matakuliah,nama_matakuliah,sks,semester,prodi,kode_dosen,hari,jam_mulai,jam_selesai,ruangan&order=kode_matakuliah", true),
                    DatabaseConnection.restGet("Dosen", "select=kode_dosen,nama", true)
                };
            }
            @Override protected void done() {
                try {
                    String[] r = get();
                    dosenList.clear();
                    cbDosen.removeAllItems();
                    cbDosen.addItem("— Pilih Dosen —");
                    int nd = DatabaseConnection.countRows(r[1]);
                    for (int i = 0; i < nd; i++) {
                        String kd = DatabaseConnection.getField(r[1], i, "kode_dosen");
                        String nd2 = DatabaseConnection.getField(r[1], i, "nama");
                        dosenList.add(new String[]{kd, nd2});
                        cbDosen.addItem(kd + " - " + nd2);
                    }
                    
                    tableModel.setRowCount(0);
                    int n = DatabaseConnection.countRows(r[0]);
                    for (int i = 0; i < n; i++) {
                        String dkode = DatabaseConnection.getField(r[0], i, "kode_dosen");
                        String dnama = dkode;
                        for(String[] d : dosenList) { if (d[0].equals(dkode)) dnama = d[1]; }
                        
                        tableModel.addRow(new Object[]{
                            i+1,
                            DatabaseConnection.getField(r[0], i, "kode_matakuliah"),
                            DatabaseConnection.getField(r[0], i, "nama_matakuliah"),
                            DatabaseConnection.getField(r[0], i, "sks"),
                            DatabaseConnection.getField(r[0], i, "prodi"),
                            dnama,
                            DatabaseConnection.getField(r[0], i, "semester"),
                            DatabaseConnection.getField(r[0], i, "hari"),
                            DatabaseConnection.getField(r[0], i, "jam_mulai"),
                            DatabaseConnection.getField(r[0], i, "jam_selesai"),
                            DatabaseConnection.getField(r[0], i, "ruangan")
                        });
                    }
                    setStatus("✓ " + n + " mata kuliah dimuat.", Palette.SUCCESS);
                } catch (Exception ex) { setStatus("✗ " + ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void addData() {
        String kode=txtKode.getText().trim(), nama=txtNama.getText().trim(), sks=txtSks.getText().trim();
        if (kode.isEmpty()||nama.isEmpty()||sks.isEmpty()) { setStatus("⚠ Kode, Nama, SKS wajib diisi.",Palette.WARNING); return; }
        String prodi=(String)cbProdi.getSelectedItem(), semester=str(cbSemester.getSelectedItem());
        String hari=str(cbHari.getSelectedItem()), mulai=str(cbJamMulai.getSelectedItem()), selesai=str(cbJamSelesai.getSelectedItem());
        String ruangan=txtRuangan.getText().trim();
        String kdosen = cbDosen.getSelectedIndex() > 0 ? ((String)cbDosen.getSelectedItem()).split(" - ")[0] : null;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                String kd = kdosen!=null ? "\""+DatabaseConnection.escape(kdosen)+"\"" : "null";
                DatabaseConnection.restPost("Matakuliah",
                    String.format("{\"kode_matakuliah\":\"%s\",\"nama_matakuliah\":\"%s\",\"sks\":%s,\"semester\":%s,\"prodi\":\"%s\",\"kode_dosen\":%s,\"hari\":\"%s\",\"jam_mulai\":\"%s\",\"jam_selesai\":\"%s\",\"ruangan\":\"%s\"}",
                    DatabaseConnection.escape(kode),DatabaseConnection.escape(nama),sks,semester,
                    DatabaseConnection.escape(prodi),kd,hari,mulai,selesai,
                    DatabaseConnection.escape(ruangan)));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ MK ditambahkan.", Palette.SUCCESS); loadAll(); clearForm(); }
                catch (Exception ex) { setStatus("✗ "+ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void updateData() {
        int row=table.getSelectedRow(); if (row==-1) { setStatus("⚠ Pilih baris.", Palette.WARNING); return; }
        String kode=txtKode.getText().trim(), nama=txtNama.getText().trim(), sks=txtSks.getText().trim();
        String prodi=(String)cbProdi.getSelectedItem(), semester=str(cbSemester.getSelectedItem());
        String hari=str(cbHari.getSelectedItem()), mulai=str(cbJamMulai.getSelectedItem()), selesai=str(cbJamSelesai.getSelectedItem());
        String ruangan=txtRuangan.getText().trim();
        String kdosen = cbDosen.getSelectedIndex() > 0 ? ((String)cbDosen.getSelectedItem()).split(" - ")[0] : null;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                String kd = kdosen!=null ? "\""+DatabaseConnection.escape(kdosen)+"\"" : "null";
                DatabaseConnection.restPatch("Matakuliah","kode_matakuliah=eq."+DatabaseConnection.escape(kode),
                    String.format("{\"nama_matakuliah\":\"%s\",\"sks\":%s,\"semester\":%s,\"prodi\":\"%s\",\"kode_dosen\":%s,\"hari\":\"%s\",\"jam_mulai\":\"%s\",\"jam_selesai\":\"%s\",\"ruangan\":\"%s\"}",
                    DatabaseConnection.escape(nama),sks,semester,DatabaseConnection.escape(prodi),
                    kd,hari,mulai,selesai,DatabaseConnection.escape(ruangan)));
                return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Data diperbarui.", Palette.SUCCESS); loadAll(); }
                catch (Exception ex) { setStatus("✗ "+ex.getMessage(), Palette.DANGER); }
            }
        }.execute();
    }

    private void deleteData() {
        int row=table.getSelectedRow(); if(row==-1){setStatus("⚠ Pilih baris.",Palette.WARNING);return;}
        String kode=(String)tableModel.getValueAt(table.convertRowIndexToModel(row),1);
        if (JOptionPane.showConfirmDialog(this,"Hapus MK "+kode+"?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        new SwingWorker<Void,Void>(){
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restDelete("Matakuliah","kode_matakuliah=eq."+DatabaseConnection.escape(kode)); return null;
            }
            @Override protected void done() {
                try { get(); setStatus("✓ Dihapus.",Palette.SUCCESS); loadAll(); clearForm(); }
                catch (Exception ex){setStatus("✗ "+ex.getMessage(),Palette.DANGER);}
            }
        }.execute();
    }

    private void clearForm() {
        txtKode.setText(""); txtNama.setText(""); txtSks.setText(""); txtRuangan.setText("");
        cbSemester.setSelectedIndex(0); cbDosen.setSelectedIndex(0); cbHari.setSelectedIndex(0);
        cbJamMulai.setSelectedIndex(0); cbJamSelesai.setSelectedIndex(0); cbProdi.setSelectedIndex(0);
        table.clearSelection();
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t); l.setFont(Palette.FONT_BODY); l.setForeground(Palette.TEXT_SECONDARY); return l;
    }
    
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(Palette.SURFACE_ELEVATED);
        cb.setForeground(Palette.TEXT_PRIMARY);
        cb.setFont(Palette.FONT_BODY);
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 36));
    }

    private void setStatus(String m, Color c) { lblStatus.setText(m); lblStatus.setForeground(c); }
}
