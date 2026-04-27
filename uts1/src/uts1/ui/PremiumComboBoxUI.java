package uts1.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

/**
 * Modern, minimalist ComboBox UI.
 * - No arrow icon (hidden button with zero size)
 * - Soft rounded corners
 * - Custom popup with dark theme
 */
public class PremiumComboBoxUI extends BasicComboBoxUI {

    @Override
    protected JButton createArrowButton() {
        // Completely hidden — no icon, no clickable area
        JButton btn = new JButton();
        btn.setBorder(null);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setMinimumSize(new Dimension(0, 0));
        btn.setMaximumSize(new Dimension(0, 0));
        return btn;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        comboBox.setOpaque(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = c.getWidth();
        int h = c.getHeight();

        // Rounded filled background
        g2.setColor(Palette.SURFACE_ELEVATED);
        g2.fillRoundRect(0, 0, w, h, 10, 10);

        // Subtle border
        g2.setColor(new Color(255, 255, 255, 20));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);

        // Small chevron indicator on right side
        int cx = w - 18;
        int cy = h / 2;
        g2.setColor(Palette.TEXT_MUTED);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
        g2.drawLine(cx, cy + 2, cx + 4, cy - 2);

        g2.dispose();
        super.paint(g, c);
    }

    @Override
    protected ListCellRenderer<Object> createRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(7, 12, 7, 12));
                label.setFont(Palette.FONT_BODY);
                label.setForeground(Palette.TEXT_PRIMARY);
                label.setBackground(isSelected ? Palette.SURFACE_HOVER : Palette.SURFACE);
                label.setOpaque(true);
                return label;
            }
        };
    }

    @Override
    protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
            @Override
            protected void configurePopup() {
                super.configurePopup();
                setBackground(Palette.SURFACE);
                setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1));
            }

            @Override
            protected JScrollPane createScroller() {
                JScrollPane scroller = super.createScroller();
                scroller.setBackground(Palette.SURFACE);
                scroller.getViewport().setBackground(Palette.SURFACE);
                scroller.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
                scroller.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
                return scroller;
            }
        };
    }
}
