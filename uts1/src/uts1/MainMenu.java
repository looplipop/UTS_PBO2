package uts1;

import uts1.ui.*;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.TreeSet;
import javax.swing.table.*;

public class MainMenu extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;
    private String currentRole = "Guest";
    private JLabel roleLabel;
    private java.util.Map<String, JButton> navButtons = new java.util.HashMap<>();
    
    // Forms
    private MahasiswaForm mahasiswaForm;
    private DosenForm dosenForm;
    private MataKuliahForm mataKuliahForm;
    private KRSForm krsForm;
    private NilaiForm nilaiForm;

    public MainMenu() {
        setTitle("AKADEMIK UTB — Premium Edition");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1100, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setupGlobalUI();
        initUI();
    }

    private void setupGlobalUI() {
        try {
            UIManager.put("ComboBoxUI", "uts1.ui.PremiumComboBoxUI");
        } catch (Exception e) {}
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Palette.BACKGROUND);

        // ── SIDEBAR ──────────────────────────────────────────────
        sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Palette.SIDEBAR_TOP,
                                                     0, getHeight(), Palette.SIDEBAR_BOTTOM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 16, 20, 16));

        // Logo area
        JLabel logo = new JLabel("⬡ AKADEMIK");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Palette.PRIMARY);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JLabel university = new JLabel("UTB Management");
        university.setFont(Palette.FONT_SMALL);
        university.setForeground(Palette.TEXT_MUTED);
        university.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep1 = new JSeparator();
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep1.setForeground(Palette.SURFACE_HOVER);

        roleLabel = new JLabel("Role: —");
        roleLabel.setFont(Palette.FONT_SMALL);
        roleLabel.setForeground(Palette.TEXT_MUTED);
        roleLabel.setAlignmentX(CENTER_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(university);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        sidebar.add(sep1);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // Nav items: label, cardKey
        String[][] navItems = {
            {"Dashboard",   "Dashboard"},
            {"Mahasiswa",   "Mahasiswa"},
            {"Dosen",       "Dosen"},
            {"Mata Kuliah",  "Mata Kuliah"},
            {"KRS",         "KRS"},
            {"Nilai",       "Nilai"},
            {"Ganti Password", "GantiPassword"},
        };

        for (String[] item : navItems) {
            String label = item[0];
            String key   = item[1];
            JButton navBtn = createNavButton(label, key);
            navButtons.put(key, navBtn);
            sidebar.add(navBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        sidebar.add(Box.createVerticalGlue());

        // Logout — bottom with door icon
        JButton btnLogout = createNavButton("🔓  Keluar", "Logout");
        btnLogout.setForeground(new Color(248, 113, 113)); // soft red
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── CONTENT AREA ──────────────────────────────────────────
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Palette.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Screens
        mahasiswaForm = new MahasiswaForm();
        dosenForm = new DosenForm();
        mataKuliahForm = new MataKuliahForm();
        krsForm = new KRSForm();
        nilaiForm = new NilaiForm();
        
        contentPanel.add(new LoginForm(this::onLoginSuccess),  "Login");
        contentPanel.add(buildDashboard(),                     "Dashboard");
        contentPanel.add(mahasiswaForm,                        "Mahasiswa");
        contentPanel.add(dosenForm,                            "Dosen");
        contentPanel.add(mataKuliahForm,                       "Mata Kuliah");
        contentPanel.add(krsForm,                              "KRS");
        contentPanel.add(nilaiForm,                            "Nilai");
        contentPanel.add(new GantiPasswordForm(this::getCurrentRole), "GantiPassword");

        sidebar.setVisible(false);
        cardLayout.show(contentPanel, "Login");

        root.add(sidebar,      BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Called by LoginForm after successful authentication */
    private void onLoginSuccess(String role) {
        this.currentRole = role;
        roleLabel.setText("Role: " + role);
        sidebar.setVisible(true);

        if (role.equalsIgnoreCase("Admin")) {
            if (navButtons.containsKey("KRS")) navButtons.get("KRS").setVisible(false);
            if (navButtons.containsKey("Nilai")) navButtons.get("Nilai").setVisible(false);
            if (navButtons.containsKey("Mahasiswa")) navButtons.get("Mahasiswa").setVisible(true);
            if (navButtons.containsKey("Dosen")) navButtons.get("Dosen").setVisible(true);
            if (navButtons.containsKey("Mata Kuliah")) navButtons.get("Mata Kuliah").setVisible(true);
        } else if (role.equalsIgnoreCase("Operator")) {
            if (navButtons.containsKey("KRS")) navButtons.get("KRS").setVisible(true);
            if (navButtons.containsKey("Nilai")) navButtons.get("Nilai").setVisible(true);
            if (navButtons.containsKey("Mahasiswa")) navButtons.get("Mahasiswa").setVisible(false);
            if (navButtons.containsKey("Dosen")) navButtons.get("Dosen").setVisible(false);
            if (navButtons.containsKey("Mata Kuliah")) navButtons.get("Mata Kuliah").setVisible(false);
        }

        cardLayout.show(contentPanel, "Dashboard");
        // Refresh dashboard stats
        rebuildDashboard();
    }

    String getCurrentRole() { return currentRole; }

    private JButton createNavButton(String label, String key) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(Palette.SURFACE_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Palette.TEXT_SECONDARY);
        btn.setFont(Palette.FONT_BODY);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(Palette.TEXT_PRIMARY); btn.repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Palette.TEXT_SECONDARY); btn.repaint(); }
        });

        btn.addActionListener(e -> {
            if ("Logout".equals(key)) {
                int ok = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    // Reset to login
                    sidebar.setVisible(false);
                    rebuildLoginCard();
                    cardLayout.show(contentPanel, "Login");
                }
            } else {
                cardLayout.show(contentPanel, key);
                
                // Explicitly reload data when switching tabs
                if ("Mahasiswa".equals(key)) mahasiswaForm.loadData();
                else if ("Dosen".equals(key)) dosenForm.loadAll();
                else if ("Mata Kuliah".equals(key)) mataKuliahForm.loadAll();
                else if ("KRS".equals(key)) krsForm.loadData();
                else if ("Nilai".equals(key)) nilaiForm.loadAll();
            }
        });
        return btn;
    }

    // ── DASHBOARD ────────────────────────────────────────────────────

    private JPanel dashboardHolder;

    private JPanel buildDashboard() {
        dashboardHolder = new JPanel(new BorderLayout());
        dashboardHolder.setOpaque(false);
        dashboardHolder.add(makeDashboardContent("SEMUA"), BorderLayout.CENTER);
        return dashboardHolder;
    }

    private void rebuildDashboard() {
        dashboardHolder.removeAll();
        dashboardHolder.add(makeDashboardContent("SEMUA"), BorderLayout.CENTER);
        dashboardHolder.revalidate();
        dashboardHolder.repaint();
    }

    private void rebuildLoginCard() {
        // Replace Login card with fresh one
        contentPanel.remove(0); // remove old login
        LoginForm newLogin = new LoginForm(this::onLoginSuccess);
        contentPanel.add(newLogin, "Login", 0);
        contentPanel.revalidate();
        // Re-enable all nav buttons
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setEnabled(true);
                btn.setForeground(Palette.TEXT_SECONDARY);
            }
        }
    }

    private JPanel makeDashboardContent(String selectedDept) {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header with Filter merged
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Statistik Akademik");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Palette.TEXT_PRIMARY);
        JLabel lblSubtitle = new JLabel("Halo, " + currentRole + "! Berikut ringkasan data " + 
            (selectedDept.equals("SEMUA") ? "seluruh jurusan" : "jurusan " + selectedDept));
        lblSubtitle.setFont(Palette.FONT_BODY);
        lblSubtitle.setForeground(Palette.TEXT_SECONDARY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);
        header.add(titlePanel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Pilih Jurusan:") {{ setForeground(Palette.TEXT_MUTED); }});
        
        JComboBox<String> cbFilter = new JComboBox<>(getDeptList());
        cbFilter.setSelectedItem(selectedDept);
        cbFilter.setPreferredSize(new Dimension(220, 36));
        cbFilter.setBackground(Palette.SURFACE_ELEVATED);
        cbFilter.setForeground(Palette.TEXT_PRIMARY);
        cbFilter.addActionListener(e -> {
            String sel = (String)cbFilter.getSelectedItem();
            dashboardHolder.removeAll();
            dashboardHolder.add(makeDashboardContent(sel), BorderLayout.CENTER);
            dashboardHolder.revalidate(); dashboardHolder.repaint();
        });
        filterPanel.add(cbFilter);
        header.add(filterPanel, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Stats cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        Map<String, Object> statsData = fetchDashboardStats(selectedDept);
        int totalMhs = (int) statsData.get("totalMhs");
        int totalKrs = (int) statsData.get("totalKrs");
        int totalNoKrs = totalMhs - totalKrs;
        double avgVal = (double) statsData.get("avgNilai");
        String avgLetter = pointsToGrade(avgVal);

        statsRow.add(makeStatCard("👨‍🎓", String.valueOf(totalMhs), "Total Mahasiswa", Palette.PRIMARY));
        statsRow.add(makeStatCard("✅", String.valueOf(totalKrs), "Sudah KRS", Palette.SUCCESS));
        statsRow.add(makeStatCard("⏳", String.valueOf(totalNoKrs), "Belum KRS", Palette.WARNING));
        statsRow.add(makeStatCard("🎓", avgLetter, "Rata-rata Grade", Palette.ACCENT_PURPLE));

        // Bottom section: Visualizations
        JPanel visualizationRow = new JPanel(new GridLayout(1, 2, 20, 0));
        visualizationRow.setOpaque(false);
        visualizationRow.setMinimumSize(new Dimension(400, 280));
        visualizationRow.setPreferredSize(new Dimension(0, 340));

        // Section 1: KRS per Department (Stacked)
        ChartComponents.StackedBarChart krsChart = new ChartComponents.StackedBarChart();
        krsChart.setData((Map<String, int[]>) statsData.get("deptKrsData"));
        
        PremiumPanel krsCard = wrapInCard("Pendaftaran per Departemen", krsChart);
        visualizationRow.add(krsCard);

        // Section 2: Grade Distribution (Donut)
        ChartComponents.DonutChart gradeChart = new ChartComponents.DonutChart();
        gradeChart.setData((Map<String, Integer>) statsData.get("gradeDistData"));
        
        PremiumPanel gradeCard = wrapInCard("Distribusi Grade Akademik", gradeChart);
        visualizationRow.add(gradeCard);

        JPanel center = new JPanel(new BorderLayout(0, 24));
        center.setOpaque(false);
        center.add(statsRow,           BorderLayout.NORTH);
        center.add(visualizationRow,   BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private PremiumPanel wrapInCard(String title, JComponent content) {
        PremiumPanel card = new PremiumPanel();
        card.setBackground(Palette.SURFACE);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Palette.TEXT_PRIMARY);
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(content,  BorderLayout.CENTER);
        return card;
    }

    private Map<String, Object> fetchDashboardStats(String filterDept) {
        Map<String, Object> res = new HashMap<>();
        try {
            // 1. Fetch Students and their Depts
            String studentJson = DatabaseConnection.restGet("Mahasiswa", "select=nim,prodi", true);
            int nMhsTotal = DatabaseConnection.countRows(studentJson);
            
            Map<String, String> mhsToDept = new HashMap<>(); // nim -> dept
            Map<String, int[]> deptKrsData = new LinkedHashMap<>(); // dept -> [sudah, belum]
            
            int filteredMhsCount = 0;
            for (int i = 0; i < nMhsTotal; i++) {
                String nim = DatabaseConnection.getField(studentJson, i, "nim");
                String dept = DatabaseConnection.getField(studentJson, i, "prodi");
                
                if (filterDept.equals("SEMUA") || filterDept.equals(dept)) {
                    mhsToDept.put(nim, dept);
                    deptKrsData.putIfAbsent(dept, new int[]{0, 0});
                    filteredMhsCount++;
                }
            }
            res.put("totalMhs", filteredMhsCount);

            // 2. Fetch KRS to see status
            String krsJson = DatabaseConnection.restGet("KRS", "select=nim", true);
            int nKrsRaw = DatabaseConnection.countRows(krsJson);
            Set<String> krsNims = new HashSet<>();
            for (int i = 0; i < nKrsRaw; i++) {
                krsNims.add(DatabaseConnection.getField(krsJson, i, "nim"));
            }

            // Populate Dept Stats for Stacked Chart
            int filteredKrsCount = 0;
            for (Map.Entry<String, String> entry : mhsToDept.entrySet()) {
                String dept = entry.getValue();
                boolean isSudah = krsNims.contains(entry.getKey());
                if (isSudah) filteredKrsCount++;
                deptKrsData.get(dept)[isSudah ? 0 : 1]++;
            }
            res.put("totalKrs", filteredKrsCount);
            res.put("deptKrsData", deptKrsData);

            // 3. Performance (Grades)
            String nilaiJson = DatabaseConnection.restGet("Nilai", "select=nim,grade", true);
            int nNilai = DatabaseConnection.countRows(nilaiJson);
            
            Map<String, Integer> gradeDistData = new LinkedHashMap<>();
            gradeDistData.put("A", 0); gradeDistData.put("B", 0); 
            gradeDistData.put("C", 0); gradeDistData.put("D", 0);
            
            double totalPoints = 0;
            int totalGradesCount = 0;

            for (int i = 0; i < nNilai; i++) {
                String nim = DatabaseConnection.getField(nilaiJson, i, "nim");
                if (!mhsToDept.containsKey(nim)) continue;

                String grade = DatabaseConnection.getField(nilaiJson, i, "grade");
                if (grade == null) continue;
                
                gradeDistData.put(grade, gradeDistData.getOrDefault(grade, 0) + 1);
                
                double points = gradeToPoints(grade);
                totalPoints += points;
                totalGradesCount++;
            }
            res.put("avgNilai", totalGradesCount == 0 ? 0 : totalPoints / totalGradesCount);
            res.put("gradeDistData", gradeDistData);

        } catch (Exception e) {
            e.printStackTrace();
            res.put("totalMhs", 0); res.put("totalKrs", 0); res.put("avgNilai", 0.0);
            res.put("deptKrsData", new HashMap<String, int[]>());
            res.put("gradeDistData", new HashMap<String, Integer>());
        }
        return res;
    }

    private String[] getDeptList() {
        try {
            String json = DatabaseConnection.restGet("Mahasiswa", "select=prodi", true);
            int n = DatabaseConnection.countRows(json);
            Set<String> depts = new TreeSet<>();
            depts.add("SEMUA");
            for (int i = 0; i < n; i++) depts.add(DatabaseConnection.getField(json, i, "prodi"));
            return depts.toArray(new String[0]);
        } catch (Exception e) { return new String[]{"SEMUA"}; }
    }

    private String pointsToGrade(double p) {
        if (p >= 3.5) return "A";
        if (p >= 2.5) return "B";
        if (p >= 1.5) return "C";
        if (p >= 0.5) return "D";
        return "E";
    }

    private double gradeToPoints(String grade) {
        if (grade == null) return 0;
        switch (grade.toUpperCase()) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            default:  return 0.0;
        }
    }

    private JPanel makeStatCard(String icon, String count, String label, Color accent) {
        PremiumPanel card = new PremiumPanel();
        card.setLayout(new BorderLayout(0, 8));
        card.setBackground(Palette.SURFACE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel countLbl = new JLabel(count);
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 38));
        countLbl.setForeground(accent);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(Palette.FONT_BODY);
        nameLbl.setForeground(Palette.TEXT_SECONDARY);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(iconLbl, BorderLayout.WEST);

        JPanel bottom = new JPanel(new GridLayout(2,1,0,4));
        bottom.setOpaque(false);
        bottom.add(countLbl);
        bottom.add(nameLbl);

        card.add(top,    BorderLayout.NORTH);
        card.add(bottom, BorderLayout.CENTER);

        // Left accent bar
        JPanel accent_bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        accent_bar.setPreferredSize(new Dimension(4, 0));
        accent_bar.setOpaque(false);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(accent_bar, BorderLayout.WEST);
        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }


    /** Shared method to apply consistent table styling */
    static void styleTable(JTable t) {
        t.setBackground(Palette.SURFACE);
        t.setForeground(Palette.TEXT_PRIMARY);
        t.setRowHeight(36);
        t.setFont(Palette.FONT_BODY);
        
        // Restore subtle grid
        t.setShowGrid(true);
        t.setGridColor(new Color(255, 255, 255, 10)); // Very subtle grid
        t.setIntercellSpacing(new Dimension(0, 1));
        
        t.setSelectionBackground(new Color(99, 102, 241, 60));
        t.setSelectionForeground(Color.WHITE);
        t.setBorder(null);

        // Header Styling - Restore Bold White
        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(25, 29, 52));
        header.setForeground(Color.WHITE); // Bold White
        header.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Palette.PRIMARY));

        // Custom renderer for header to add separators
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255,255,255,20)), // subtle separator
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBackground(new Color(25, 29, 52));
                l.setForeground(Color.WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                return l;
            }
        });

        // Remove the white line/border seen in some scrollpanes
        if (t.getParent() instanceof JViewport) {
            JViewport viewport = (JViewport) t.getParent();
            if (viewport.getParent() instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) viewport.getParent();
                scroll.setBorder(BorderFactory.createEmptyBorder());
                scroll.setViewportBorder(BorderFactory.createEmptyBorder());
                scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
                scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
