package uts1.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ChartComponents {

    /**
     * Stacked Bar Chart for Registration Status por Department
     */
    public static class StackedBarChart extends JPanel {
        private Map<String, int[]> data = new LinkedHashMap<>(); // Dept -> [Sudah, Belum]
        private String[] legend = {"Sudah KRS", "Belum KRS"};
        private Color[] colors = {Palette.SUCCESS, Palette.DANGER};

        public StackedBarChart() {
            setOpaque(false);
        }

        public void setData(Map<String, int[]> d) {
            this.data = d;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margin = 40;
            int chartW = w - margin * 2;
            int chartH = h - margin * 2;

            // X and Y Axes
            g2.setColor(Palette.TEXT_MUTED);
            g2.drawLine(margin, margin, margin, h - margin); // Y
            g2.drawLine(margin, h - margin, w - margin, h - margin); // X

            int maxTotal = 0;
            for (int[] v : data.values()) maxTotal = Math.max(maxTotal, v[0] + v[1]);
            if (maxTotal == 0) maxTotal = 1;

            // Fixed bar width and spacing for consistency
            int barWidth = 40;
            int spacing = 100;
            
            // Calculate starting X to center the bars
            int totalChartsW = data.size() * spacing;
            int startX = margin + (chartW - totalChartsW) / 2 + (spacing - barWidth) / 2;
            int x = startX;

            int i = 0;
            for (Map.Entry<String, int[]> entry : data.entrySet()) {
                int[] vals = entry.getValue();
                String dept = entry.getKey();

                // Already Registered (Sudah)
                int barH1 = (vals[0] * chartH) / maxTotal;
                g2.setColor(colors[0]);
                g2.fillRoundRect(x, h - margin - barH1, barWidth, barH1, 6, 6);

                // Not Registered (Belum)
                int barH2 = (vals[1] * chartH) / maxTotal;
                g2.setColor(colors[1]);
                g2.fillRoundRect(x, h - margin - barH1 - barH2, barWidth, barH2, 6, 6);
                
                // Total label on top
                g2.setColor(Palette.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String totalStr = String.valueOf(vals[0] + vals[1]);
                FontMetrics fmTotal = g2.getFontMetrics();
                g2.drawString(totalStr, x + (barWidth - fmTotal.stringWidth(totalStr)) / 2, h - margin - barH1 - barH2 - 5);

                // Label
                g2.setColor(Palette.TEXT_SECONDARY);
                g2.setFont(Palette.FONT_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                String shortName = dept.replace("Teknik ", "");
                g2.drawString(shortName, x + (barWidth - fm.stringWidth(shortName)) / 2, h - margin + 20);

                x += spacing;
            }

            // Legend
            int lx = w - margin - 120;
            int ly = margin / 2;
            for (int j = 0; j < 2; j++) {
                g2.setColor(colors[j]);
                g2.fillOval(lx, ly, 10, 10);
                g2.setColor(Palette.TEXT_SECONDARY);
                g2.drawString(legend[j], lx + 15, ly + 10);
                lx -= 100;
            }

            g2.dispose();
        }
    }

    /**
     * Donut Chart for Grade Distribution
     */
    public static class DonutChart extends JPanel {
        private Map<String, Integer> data = new LinkedHashMap<>(); // Grade (A,B,C,D) -> Count
        private int total = 0;

        public DonutChart() {
            setOpaque(false);
        }

        public void setData(Map<String, Integer> d) {
            this.data = d;
            this.total = d.values().stream().mapToInt(Integer::intValue).sum();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 60;
            int x = (w - size) / 2 - 40; // Shift left for legend
            int y = (h - size) / 2;

            if (data.isEmpty()) {
                g2.dispose();
                return;
            }

            if (total == 0) {
                // Draw empty donut ring but keep legends
                g2.setStroke(new BasicStroke(15));
                g2.setColor(Palette.SURFACE_HOVER);
                g2.drawOval(x+10, y+10, size-20, size-20);
                
                int lx = x + size + 40;
                int ly = y + (size - (data.size() * 25)) / 2;
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    g2.setColor(Palette.getGradeColor(entry.getKey()));
                    g2.fillOval(lx, ly, 10, 10);
                    g2.setColor(Palette.TEXT_PRIMARY);
                    g2.setFont(Palette.FONT_BODY);
                    g2.drawString("Grade " + entry.getKey() + ": 0", lx + 20, ly + 10);
                    ly += 25;
                }
                
                // Center text
                g2.setColor(Palette.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                String pctStr = "0%";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(pctStr, x + (size - fm.stringWidth(pctStr)) / 2, y + size / 2 + 10);

                g2.dispose();
                return;
            }

            double startAngle = 90;
            String topGrade = "";
            double maxPct = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double arcAngle = (entry.getValue() * 360.0) / total;
                g2.setColor(Palette.getGradeColor(entry.getKey()));
                g2.fillArc(x, y, size, size, (int) startAngle, (int) -arcAngle);
                
                if (entry.getValue() / (double)total > maxPct) {
                    maxPct = entry.getValue() / (double)total;
                    topGrade = entry.getKey();
                }
                startAngle -= arcAngle;
            }

            // Hole in center
            g2.setColor(Palette.SURFACE);
            int holeSize = (int) (size * 0.65);
            int hx = x + (size - holeSize) / 2;
            int hy = y + (size - holeSize) / 2;
            g2.fillOval(hx, hy, holeSize, holeSize);

            // Text in center (Percentage of top grade)
            g2.setColor(Palette.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
            String pctStr = (int)(maxPct * 100) + "%";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(pctStr, hx + (holeSize - fm.stringWidth(pctStr)) / 2, hy + holeSize / 2 + 10);
            
            g2.setFont(Palette.FONT_SMALL);
            fm = g2.getFontMetrics();
            g2.drawString("Grade " + topGrade, hx + (holeSize - fm.stringWidth("Grade " + topGrade)) / 2, hy + holeSize / 2 + 30);

            // Legend on the right
            int lx = x + size + 40;
            int ly = y + (size - (data.size() * 25)) / 2;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                g2.setColor(Palette.getGradeColor(entry.getKey()));
                g2.fillOval(lx, ly, 10, 10);
                
                g2.setColor(Palette.TEXT_PRIMARY);
                g2.setFont(Palette.FONT_BODY);
                g2.drawString("Grade " + entry.getKey() + ": " + entry.getValue(), lx + 20, ly + 10);
                ly += 25;
            }

            g2.dispose();
        }
    }
}
