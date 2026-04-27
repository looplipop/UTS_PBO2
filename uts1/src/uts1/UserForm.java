package uts1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import uts1.ui.*;

/** User management (Admin only). */
public class UserForm extends PremiumPanel {

    private PremiumTextField txtUsername;
    private JPasswordField   txtPassword;
    private JComboBox<String> cbRole;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    public UserForm() {
        super();
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("👤 Manajemen User");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Palette.PRIMARY);
        add(title, BorderLayout.NORTH);

        PremiumPanel inputPanel = new PremiumPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtUsername = new PremiumTextField();
        txtPassword = new JPasswordField();
        txtPassword.setBackground(Palette.SURFACE_ELEVATED);
        txtPassword.setForeground(Palette.TEXT_PRIMARY);
        txtPassword.setCaretColor(Palette.TEXT_PRIMARY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Palette.SURFACE_HOVER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        cbRole = new JComboBox<>(new String[]{"Admin","Operator"});
        cbRole.setBackground(Palette.SURFACE_HOVER); cbRole.setForeground(Palette.TEXT_PRIMARY);

        gbc.gridx=0;gbc.gridy=0;gbc.weightx=0.3; inputPanel.add(label("Username"),gbc);
        gbc.gridx=1;gbc.weightx=0.7; inputPanel.add(txtUsername,gbc);
        gbc.gridx=0;gbc.gridy=1;gbc.weightx=0.3; inputPanel.add(label("Password"),gbc);
        gbc.gridx=1;gbc.weightx=0.7; inputPanel.add(txtPassword,gbc);
        gbc.gridx=0;gbc.gridy=2;gbc.weightx=0.3; inputPanel.add(label("Role"),gbc);
        gbc.gridx=1;gbc.weightx=0.7; inputPanel.add(cbRole,gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnPanel.setOpaque(false);
        PremiumButton btnLoad   = new PremiumButton("🔄 Load",  Palette.SURFACE_HOVER, Palette.SURFACE_ELEVATED);
        PremiumButton btnAdd    = new PremiumButton("➕ Tambah", Palette.SUCCESS,       Palette.SUCCESS.darker());
        PremiumButton btnUpdate = new PremiumButton("✏ Update",  Palette.PRIMARY,       Palette.PRIMARY_HOVER);
        PremiumButton btnDelete = new PremiumButton("🗑 Hapus",  Palette.DANGER,        Palette.DANGER.darker());
        PremiumButton btnClear  = new PremiumButton("✖ Clear",   Palette.SURFACE_HOVER, Palette.SURFACE_ELEVATED);
        for (JButton b : new JButton[]{btnLoad,btnAdd,btnUpdate,btnDelete,btnClear}) btnPanel.add(b);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(Palette.FONT_SMALL);
        lblStatus.setForeground(Palette.TEXT_SECONDARY);

        gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=2; inputPanel.add(btnPanel,gbc);
        gbc.gridy=4; inputPanel.add(lblStatus,gbc);

        tableModel = new DefaultTableModel(new Object[]{"ID","Username","Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        MainMenu.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Palette.BACKGROUND);
        scroll.setBorder(BorderFactory.createLineBorder(Palette.SURFACE_HOVER));

        add(inputPanel, BorderLayout.NORTH);
        add(scroll,     BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                txtUsername.setText((String) tableModel.getValueAt(r, 1));
                cbRole.setSelectedItem(tableModel.getValueAt(r, 2));
            }
        });

        loadData();
    }

    private void loadData() {
        setStatus("Memuat...", Palette.TEXT_SECONDARY);
        new SwingWorker<String,Void>() {
            @Override protected String doInBackground() throws Exception {
                return DatabaseConnection.restGet("users","select=id_user,username,role&order=id_user",true);
            }
            @Override protected void done() {
                try {
                    String json=get(); tableModel.setRowCount(0);
                    int n=DatabaseConnection.countRows(json);
                    for(int i=0;i<n;i++) tableModel.addRow(new Object[]{
                        DatabaseConnection.getField(json,i,"id_user"),
                        DatabaseConnection.getField(json,i,"username"),
                        DatabaseConnection.getField(json,i,"role")});
                    setStatus("✓ "+n+" user.", Palette.SUCCESS);
                } catch(Exception ex){setStatus("✗ "+ex.getMessage(),Palette.DANGER);}
            }
        }.execute();
    }

    private void addUser() {
        String user=txtUsername.getText().trim(),pass=new String(txtPassword.getPassword()).trim(),role=(String)cbRole.getSelectedItem();
        if(user.isEmpty()||pass.isEmpty()){setStatus("⚠ Username dan password wajib diisi.",Palette.WARNING);return;}
        new SwingWorker<Void,Void>(){
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restPost("users","{\"username\":\""+DatabaseConnection.escape(user)+"\",\"password\":\""+DatabaseConnection.escape(pass)+"\",\"role\":\""+role+"\"}");
                return null;
            }
            @Override protected void done(){try{get();setStatus("✓ User ditambahkan.",Palette.SUCCESS);loadData();clearForm();}catch(Exception ex){setStatus("✗ "+ex.getMessage(),Palette.DANGER);}}
        }.execute();
    }

    private void updateUser() {
        int row=table.getSelectedRow();if(row==-1){setStatus("⚠ Pilih baris.",Palette.WARNING);return;}
        String id=(String)tableModel.getValueAt(row,0);
        String user=txtUsername.getText().trim(),pass=new String(txtPassword.getPassword()).trim(),role=(String)cbRole.getSelectedItem();
        new SwingWorker<Void,Void>(){
            @Override protected Void doInBackground() throws Exception {
                String body="{\"username\":\""+DatabaseConnection.escape(user)+"\""+
                    (pass.isEmpty()?"":",\"password\":\""+DatabaseConnection.escape(pass)+"\"")+
                    ",\"role\":\""+role+"\"}";
                DatabaseConnection.restPatch("users","id_user=eq."+DatabaseConnection.escape(id),body);
                return null;
            }
            @Override protected void done(){try{get();setStatus("✓ Diperbarui.",Palette.SUCCESS);loadData();clearForm();}catch(Exception ex){setStatus("✗ "+ex.getMessage(),Palette.DANGER);}}
        }.execute();
    }

    private void deleteUser() {
        int row=table.getSelectedRow();if(row==-1){setStatus("⚠ Pilih baris.",Palette.WARNING);return;}
        String id=(String)tableModel.getValueAt(row,0),user=(String)tableModel.getValueAt(row,1);
        if(JOptionPane.showConfirmDialog(this,"Hapus user "+user+"?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        new SwingWorker<Void,Void>(){
            @Override protected Void doInBackground() throws Exception {
                DatabaseConnection.restDelete("users","id_user=eq."+DatabaseConnection.escape(id));
                return null;
            }
            @Override protected void done(){try{get();setStatus("✓ Dihapus.",Palette.SUCCESS);loadData();clearForm();}catch(Exception ex){setStatus("✗ "+ex.getMessage(),Palette.DANGER);}}
        }.execute();
    }

    private void clearForm(){txtUsername.setText("");txtPassword.setText("");cbRole.setSelectedIndex(0);table.clearSelection();}
    private JLabel label(String t){JLabel l=new JLabel(t);l.setFont(Palette.FONT_BODY);l.setForeground(Palette.TEXT_SECONDARY);return l;}
    private void setStatus(String m,Color c){lblStatus.setText(m);lblStatus.setForeground(c);}
}
