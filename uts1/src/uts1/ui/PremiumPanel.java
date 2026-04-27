package uts1.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Gradient-background PremiumPanel with rounded corners.
 */
public class PremiumPanel extends JPanel {
    private int radius = 20;
    private boolean useGradient = false;

    public PremiumPanel() {
        super();
        setOpaque(false);
        setBackground(Palette.SURFACE);
    }

    public PremiumPanel(boolean useGradient) {
        this();
        this.useGradient = useGradient;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (useGradient) {
            GradientPaint gp = new GradientPaint(
                0, 0, Palette.GRAD_START,
                getWidth(), getHeight(), Palette.GRAD_END);
            g2.setPaint(gp);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
