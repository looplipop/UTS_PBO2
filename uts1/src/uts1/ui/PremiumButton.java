package uts1.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern gradient PremiumButton with rounded corners and hover effects.
 */
public class PremiumButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private boolean useGradient = false;
    private Color gradStart;
    private Color gradEnd;
    private int radius = 14;
    private boolean pressed = false;

    /** Solid color button */
    public PremiumButton(String text, Color bg, Color hover) {
        super(text);
        this.normalColor = bg;
        this.hoverColor = hover;
        init();
        setBackground(normalColor);
    }

    /** Gradient button */
    public PremiumButton(String text) {
        super(text);
        this.useGradient = true;
        this.gradStart = Palette.GRAD_START;
        this.gradEnd = Palette.GRAD_END;
        this.normalColor = Palette.PRIMARY;
        this.hoverColor = Palette.PRIMARY_HOVER;
        init();
    }

    private void init() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Palette.TEXT_PRIMARY);
        setFont(Palette.FONT_BUTTON);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 20, 10, 20));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { 
                if (!useGradient) setBackground(hoverColor); 
                repaint(); 
            }
            @Override public void mouseExited(MouseEvent e)   { 
                if (!useGradient) setBackground(normalColor); 
                pressed = false; 
                repaint(); 
            }
            @Override public void mousePressed(MouseEvent e)  { pressed = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
        });
    }

    public void setRadius(int r) { this.radius = r; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (useGradient) {
            float alpha = pressed ? 0.75f : 1.0f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            GradientPaint gp = new GradientPaint(0, 0, gradStart, w, 0, gradEnd);
            g2.setPaint(gp);
        } else {
            Color color = pressed ? getBackground().darker() : getBackground();
            g2.setColor(color);
        }

        // Fill the main shape
        g2.fillRoundRect(0, 0, w, h, radius, radius);

        // Optional: Suble border/glow for premium feel
        if (getModel().isRollover()) {
            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
