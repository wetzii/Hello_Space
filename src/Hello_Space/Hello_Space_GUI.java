package Hello_Space;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;
import java.util.Vector;

public class Hello_Space_GUI {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;

    // ── Colors ───────────────────────────────────────────────────────────
    private static final Color BG_DEEP     = new Color(4,   6,  20);
    private static final Color BG_MID      = new Color(8,  14,  40);
    private static final Color ACCENT_CYAN = new Color(0, 220, 255);
    private static final Color HAZARD_RED  = new Color(255,  60,  80);
    private static final Color SAFE_GREEN  = new Color( 50, 220, 130);
    private static final Color TEXT_BRIGHT = new Color(210, 240, 255);
    private static final Color TEXT_DIM    = new Color(100, 140, 180);
    private static final Color GRID_LINE   = new Color(30,  55,  90);
    private static final Color HEADER_BG   = new Color(10,  22,  55);

    public Hello_Space_GUI() {
        frame = new JFrame("☄ Hello Space – Asteroid Tracker");
        frame.setSize(1050, 680);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Sternenhimmel als Basis
        StarfieldPanel starfield = new StarfieldPanel();
        frame.setContentPane(starfield);
        starfield.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 30, 80, 210),
                        getWidth(), 0, new Color(0, 10, 40, 210));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Leuchtlinie unten
                g2.setColor(ACCENT_CYAN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                for (int i = 1; i <= 8; i++) {
                    g2.setColor(new Color(0, 220, 255, 28 - i*3));
                    g2.drawLine(0, getHeight()-1-i, getWidth(), getHeight()-1-i);
                }
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        // Glühender Titel (custom paint)
        JPanel titleBlock = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glow
                g2.setFont(new Font("Courier New", Font.BOLD, 28));
                for (int d = 4; d >= 1; d--) {
                    g2.setColor(new Color(0, 200, 255, 12 * d));
                    g2.drawString("ASTEROID TRACKER", 42 - d, 34 + d);
                    g2.drawString("ASTEROID TRACKER", 42 + d, 34 - d);
                }
                g2.setColor(ACCENT_CYAN);
                g2.drawString("ASTEROID TRACKER", 42, 34);
                // Emoji
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                g2.drawString("☄", 4, 36);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(480, 46); }
        };
        titleBlock.setOpaque(false);

        JLabel subtitle = new JLabel("Near-Earth Objects  ·  Live NASA Feed  ·  Today's Close Approaches");
        subtitle.setFont(new Font("Courier New", Font.PLAIN, 11));
        subtitle.setForeground(TEXT_DIM);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        titleStack.add(titleBlock);
        titleStack.add(Box.createVerticalStrut(2));
        titleStack.add(subtitle);
        headerPanel.add(titleStack, BorderLayout.WEST);

        // Legende rechts
        headerPanel.add(buildLegend(), BorderLayout.EAST);
        starfield.add(headerPanel, BorderLayout.NORTH);

        // ── Tabelle ───────────────────────────────────────────────────────────
        String[] columns = {"  NAME", "  ⌀ DIAMETER (km)", "  DISTANCE (km)", "  SPEED (km/h)", "  STATUS", "  NASA URL"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Courier New", Font.PLAIN, 12));
        table.setOpaque(false);
        table.setShowGrid(true);
        table.setGridColor(GRID_LINE);
        table.setBackground(new Color(0, 0, 0, 0));
        table.setForeground(TEXT_BRIGHT);
        table.setSelectionBackground(new Color(0, 100, 180, 140));
        table.setSelectionForeground(Color.WHITE);
        table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        int[] widths = {200, 150, 160, 150, 120, 260};
        
        for (int i = 0; i < widths.length; i++)
        	
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Header-Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Courier New", Font.BOLD, 11));
        header.setForeground(ACCENT_CYAN);
        header.setBackground(HEADER_BG);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
        header.setPreferredSize(new Dimension(0, 38));

        table.setDefaultRenderer(Object.class, new SpaceCellRenderer());

        // Scroll Things
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setBackground(BG_MID);

        // Table Wrapper
        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(5, 15, 45, 195));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(0, 100, 180, 70));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        
        
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        starfield.add(tableWrapper, BorderLayout.CENTER);

        // ── Statusleiste ──────────────────────────────────────────────────────
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 5)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(4, 10, 30, 220));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        //Some INFOS
        statusBar.setOpaque(false);
        JLabel statusLabel = new JLabel("● LIVE  ·  Source: NASA NeoWs API  ·  Click column headers to sort");
        statusLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(0, 190, 110));
        statusBar.add(statusLabel);
        starfield.add(statusBar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Legend
    private JPanel buildLegend() {
        JPanel p = new JPanel(new GridLayout(2, 1, 4, 8));
        p.setOpaque(false);
        JLabel h = new JLabel("  ⚠ HAZARDOUS");
        h.setFont(new Font("Courier New", Font.BOLD, 12));
        h.setForeground(HAZARD_RED);
        JLabel s = new JLabel("  ✔ SAFE APPROACH");
        s.setFont(new Font("Courier New", Font.BOLD, 12));
        s.setForeground(SAFE_GREEN);
        p.add(h);
        p.add(s);
        return p;
    }

    // ── add Asteroid  ───────────────────────────────────────────────────
    public void addAsteroid(Asteroid asteroid) {
        Vector<Object> row = new Vector<>();
        row.add(asteroid.getName());
        row.add(String.format("%.3f", asteroid.getEstimatedDiameter()));
        row.add(String.format("%,.0f", asteroid.getMissDistance()));
        row.add(String.format("%,.0f", asteroid.getKilometersPerHour()));
        row.add(asteroid.isPotentiallyHazardous() ? "⚠ HAZARDOUS" : "✔ SAFE");
        row.add(asteroid.getNasaJplUrl());
        model.addRow(row);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Starfireld panel
    // ══════════════════════════════════════════════════════════════════════════
    static class StarfieldPanel extends JPanel {
        private final int[][] stars;
        private final float[] sizes, alphas;

        StarfieldPanel() {
            setBackground(BG_DEEP);
            Random rng = new Random(7);
            int n = 300;
            stars  = new int[n][2];
            sizes  = new float[n];
            alphas = new float[n];
            for (int i = 0; i < n; i++) {
                stars[i][0] = rng.nextInt(1600);
                stars[i][1] = rng.nextInt(900);
                sizes[i]    = 0.4f + rng.nextFloat() * 2.2f;
                alphas[i]   = 0.25f + rng.nextFloat() * 0.75f;
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Hintergrundverlauf
            g2.setPaint(new GradientPaint(0, 0, BG_DEEP, 0, getHeight(), BG_MID));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Nebel-Glow oben rechts
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(getWidth() * 0.78f, getHeight() * 0.25f),
                    getWidth() * 0.38f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0, 40, 110, 35), new Color(0, 0, 0, 0)}
            ));
            g2.fillOval((int)(getWidth()*0.4f), 0, (int)(getWidth()*0.76f), (int)(getHeight()*0.6f));

            // Sterne
            for (int i = 0; i < stars.length; i++) {
                int sx = stars[i][0] * getWidth()  / 1600;
                int sy = stars[i][1] * getHeight() / 900;
                g2.setColor(new Color(1f, 1f, 1f, alphas[i]));
                float s = sizes[i];
                g2.fill(new Ellipse2D.Float(sx - s/2f, sy - s/2f, s, s));
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Zell-Renderer
    // ══════════════════════════════════════════════════════════════════════════
    class SpaceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);

            int modelRow = tbl.convertRowIndexToModel(row);
            Object statusVal = model.getValueAt(modelRow, 4);
            boolean hazardous = statusVal != null && statusVal.toString().contains("HAZARDOUS");

            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            if (isSelected) {
                setBackground(new Color(0, 80, 160, 180));
                setForeground(Color.WHITE);
                setFont(new Font("Courier New", Font.PLAIN, 12));
            } else if (hazardous) {
                setBackground(new Color(80, 8, 15, 200));
                setForeground(new Color(255, 150, 160));
                setFont(new Font("Courier New", Font.PLAIN, 12));
            } else {
                setBackground(new Color(5, 40, 25, 200));
                setForeground(new Color(130, 230, 170));
                setFont(new Font("Courier New", Font.PLAIN, 12));
            }

            // Col 4 status
            if (col == 4) {
                setFont(new Font("Courier New", Font.BOLD, 12));
                setForeground(hazardous ? HAZARD_RED : SAFE_GREEN);
            } 
  
            if (col == 0) {
                setFont(new Font("Courier New", Font.BOLD, 12));
                if (!isSelected)
                    setForeground(hazardous ? new Color(255, 180, 185) : TEXT_BRIGHT);
            }

            return this;
        }
    }                                                
}