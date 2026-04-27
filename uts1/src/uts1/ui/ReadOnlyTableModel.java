package uts1.ui;

import javax.swing.table.DefaultTableModel;

/**
 * A DefaultTableModel that prevents cells from being editable.
 * This replaces anonymous inner classes to bypass NetBeans compilation bugs.
 */
public class ReadOnlyTableModel extends DefaultTableModel {
    
    public ReadOnlyTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
