package uts1.ui;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PremiumTextField extends JTextField {

    private int radius = 10;
    private Color borderColor = new Color(255, 255, 255, 20);
    private Color focusColor = Palette.PRIMARY;
    private Color bgColor = Palette.SURFACE_ELEVATED;
    private boolean isFocused = false;

    public PremiumTextField() {
        super();
        setOpaque(false);
        setBackground(bgColor);
        setForeground(Palette.TEXT_PRIMARY);
        setCaretColor(Palette.TEXT_PRIMARY);
        setFont(Palette.FONT_BODY);
        setBorder(new EmptyBorder(9, 12, 9, 12));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        // Draw Border
        g2.setColor(isFocused ? focusColor : borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
