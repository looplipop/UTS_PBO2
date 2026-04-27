package uts1.ui;

import java.awt.Color;
import java.awt.Font;

public class Palette {
    // Next-gen Minimalist Dark Theme with Beautiful Gradients
    // Base colors - Deep Midnight Blue
    public static final Color BACKGROUND       = new Color(10, 12, 20);      // #0a0c14
    public static final Color SURFACE          = new Color(18, 21, 36);      // #121524
    public static final Color SURFACE_HOVER    = new Color(30, 35, 60);      // #1e233c
    public static final Color SURFACE_ELEVATED = new Color(24, 28, 48);      // #181c30
    public static final Color DROPDOWN_ARROW   = new Color(148, 163, 184);   // Slate 400
    
    // Accent - Royal Blue to Violet Gradient tones
    public static final Color PRIMARY          = new Color(99, 102, 241);    // #6366f1 Indigo
    public static final Color PRIMARY_HOVER    = new Color(129, 132, 255);   // #8184ff
    public static final Color ACCENT_PURPLE    = new Color(168, 85, 247);    // #a855f7
    public static final Color ACCENT_CYAN      = new Color(34, 211, 238);    // #22d3ee

    // Text
    public static final Color TEXT_PRIMARY     = new Color(241, 245, 249);   // #f1f5f9
    public static final Color TEXT_SECONDARY   = new Color(148, 163, 184);   // #94a3b8
    public static final Color TEXT_MUTED       = new Color(71, 85, 105);     // #475569

    // Status
    public static final Color SUCCESS          = new Color(16, 185, 129);    // #10b981
    public static final Color WARNING          = new Color(245, 158, 11);    // #f59e0b
    public static final Color DANGER           = new Color(239, 68, 68);     // #ef4444

    // Chart Colors (Department Specific)
    public static final Color CHART_INF = new Color(59, 130, 246); // Blue
    public static final Color CHART_EL  = new Color(139, 92, 246); // Purple
    public static final Color CHART_SI  = new Color(20, 184, 166); // Teal
    public static final Color CHART_IND = new Color(236, 72, 153); // Pink

    // Dynamic Chart Colors
    private static final Color[] EXTENDED_COLORS = {
        new Color(59, 130, 246), new Color(139, 92, 246), new Color(20, 184, 166),
        new Color(236, 72, 153), new Color(245, 158, 11), new Color(16, 185, 129),
        new Color(244, 63, 94),  new Color(100, 116, 139), new Color(14, 165, 233)
    };

    public static Color getDeptColor(String name) {
        if (name == null) return EXTENDED_COLORS[0];
        int hash = Math.abs(name.hashCode());
        return EXTENDED_COLORS[hash % EXTENDED_COLORS.length];
    }

    public static Color getGradeColor(String grade) {
        if (grade == null) return TEXT_MUTED;
        switch (grade.toUpperCase()) {
            case "A": return SUCCESS;
            case "B": return CHART_INF;
            case "C": return WARNING;
            case "D": return DANGER;
            default: return TEXT_MUTED;
        }
    }
    public static final Color GRAD_START       = new Color(99, 102, 241);    // Indigo
    public static final Color GRAD_END         = new Color(168, 85, 247);    // Purple

    // Sidebar gradient
    public static final Color SIDEBAR_TOP      = new Color(15, 17, 30);      // #0f111e
    public static final Color SIDEBAR_BOTTOM   = new Color(20, 24, 45);      // #14182d

    // Typography
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADER       = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY         = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 12);
}
