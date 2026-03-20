package Hello_Space;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Hello_Space_GUI {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JSpinner dateSpinner;
    private JLabel statusLabel;
    private ChartPanel chartPanel;

    // list of asteroids for chart
    private final List<Asteroid> asteroids = new ArrayList<>();

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
        frame.setSize(1100, 720);
        frame.setMinimumSize(new Dimension(850, 550));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        StarfieldPanel starfield = new StarfieldPanel();
        frame.setContentPane(starfield);
        starfield.setLayout(new BorderLayout());

        // ── Header ───────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 30, 80, 210),
                        getWidth(), 0, new Color(0, 10, 40, 210));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
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
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));

        // Titel
        JPanel titleBlock = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Courier New", Font.BOLD, 28));
                for (int d = 4; d >= 1; d--) {
                    g2.setColor(new Color(0, 200, 255, 12 * d));
                    g2.drawString("ASTEROID TRACKER", 42 - d, 34 + d);
                    g2.drawString("ASTEROID TRACKER", 42 + d, 34 - d);
                }
                g2.setColor(ACCENT_CYAN);
                g2.drawString("ASTEROID TRACKER", 42, 34);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                g2.drawString("☄", 4, 36);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(480, 46); }
        };
        titleBlock.setOpaque(false);

        JLabel subtitle = new JLabel("Near-Earth Objects  ·  Live NASA Feed  ·  Close Approaches");
        subtitle.setFont(new Font("Courier New", Font.PLAIN, 11));
        subtitle.setForeground(TEXT_DIM);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        titleStack.add(titleBlock);
        titleStack.add(Box.createVerticalStrut(2));
        titleStack.add(subtitle);
        headerPanel.add(titleStack, BorderLayout.WEST);

        // ── Center: Legende + DatePicker ─────────────────────────────────
        JPanel rightControls = new JPanel();
        rightControls.setLayout(new BoxLayout(rightControls, BoxLayout.Y_AXIS));
        rightControls.setOpaque(false);
        rightControls.add(buildLegend());
        rightControls.add(Box.createVerticalStrut(8));
        rightControls.add(buildDatePicker());

        headerPanel.add(rightControls, BorderLayout.EAST);
        starfield.add(headerPanel, BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.setFont(new Font("Courier New", Font.BOLD, 12));
        tabs.setForeground(ACCENT_CYAN);
        tabs.setBackground(BG_MID);
        UIManager.put("TabbedPane.selected", new Color(10, 30, 70));
        UIManager.put("TabbedPane.contentAreaColor", new Color(5, 15, 45, 200));

        // Tab 1: Tabelle
        tabs.addTab("  ◉ TABLE  ", buildTablePanel());

        // Tab 2: Chart
        chartPanel = new ChartPanel();
        JPanel chartWrapper = new JPanel(new BorderLayout());
        chartWrapper.setOpaque(false);
        chartWrapper.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        chartWrapper.add(chartPanel, BorderLayout.CENTER);
        tabs.addTab("  ▦ DISTANCE CHART  ", chartWrapper);

        // Tab wrapper
        JPanel tabsWrapper = new JPanel(new BorderLayout());
        tabsWrapper.setOpaque(false);
        tabsWrapper.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));
        tabsWrapper.add(tabs, BorderLayout.CENTER);
        starfield.add(tabsWrapper, BorderLayout.CENTER);

        // ── Statusleiste ─────────────────────────────────────────────────
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 5)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(4, 10, 30, 220));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        statusBar.setOpaque(false);
        statusLabel = new JLabel("● LIVE  ·  Source: NASA NeoWs API  ·  Click column headers to sort  ·  Click NASA URL to open in browser");
        statusLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(0, 190, 110));
        statusBar.add(statusLabel);
        starfield.add(statusBar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // ── Date Picker ───────────────────────────────────────────────────────
    private JPanel buildDatePicker() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        p.setOpaque(false);

        JLabel lbl = new JLabel("DATE:");
        lbl.setFont(new Font("Courier New", Font.BOLD, 11));
        lbl.setForeground(TEXT_DIM);

        // Spinner with date model
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        dateSpinner.setFont(new Font("Courier New", Font.PLAIN, 12));
        dateSpinner.setPreferredSize(new Dimension(120, 28));
        styleSpinner(dateSpinner);

        JButton loadBtn = new JButton("LOAD ▶");
        loadBtn.setFont(new Font("Courier New", Font.BOLD, 11));
        loadBtn.setForeground(BG_DEEP);
        loadBtn.setBackground(ACCENT_CYAN);
        loadBtn.setFocusPainted(false);
        loadBtn.setBorderPainted(false);
        loadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loadBtn.addActionListener(e -> reloadForDate());

        p.add(lbl);
        p.add(dateSpinner);
        p.add(loadBtn);
        return p;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setBackground(new Color(10, 25, 60));
        spinner.setForeground(ACCENT_CYAN);
        JFormattedTextField tf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        tf.setBackground(new Color(10, 25, 60));
        tf.setForeground(ACCENT_CYAN);
        tf.setCaretColor(ACCENT_CYAN);
        tf.setBorder(BorderFactory.createLineBorder(new Color(0, 80, 140)));
    }

    private void reloadForDate() {
        java.util.Date selected = (java.util.Date) dateSpinner.getValue();
        LocalDate ld = selected.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
        String dateStr = ld.toString();

        // Clear table and chart
        model.setRowCount(0);
        asteroids.clear();
        chartPanel.repaint();

        statusLabel.setText("⟳ Loading data for " + dateStr + " ...");
        statusLabel.setForeground(new Color(220, 180, 0));

        // Fetch in background thread so GUI stays responsive
        new Thread(() -> {
            try {
                AsteroidFetcher.fetch(this, dateStr);
                SwingUtilities.invokeLater(() -> {
                    chartPanel.repaint();
                    statusLabel.setText("● " + dateStr + "  ·  " + asteroids.size()
                            + " objects  ·  Source: NASA NeoWs  ·  Click NASA URL to open in browser");
                    statusLabel.setForeground(new Color(0, 190, 110));
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("✖ Error: " + ex.getMessage());
                    statusLabel.setForeground(HAZARD_RED);
                });
            }
        }).start();
    }

    // ── Table Panel ───────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
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
        table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        int[] widths = {200, 150, 160, 150, 120, 260};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Courier New", Font.BOLD, 11));
        header.setForeground(ACCENT_CYAN);
        header.setBackground(HEADER_BG);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
        header.setPreferredSize(new Dimension(0, 38));

        table.setDefaultRenderer(Object.class, new SpaceCellRenderer());

        // ── Clickable NASA URL ─────────────────────────────────────────
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (col == 5 && row >= 0) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Object url = model.getValueAt(modelRow, 5);
                    if (url != null && Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI(url.toString().trim()));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Could not open URL:\n" + url,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 5
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 5
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setBackground(BG_MID);

        JPanel wrapper = new JPanel(new BorderLayout()) {
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
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Legend ────────────────────────────────────────────────────────────
    private JPanel buildLegend() {
        JPanel p = new JPanel(new GridLayout(2, 1, 4, 4));
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

    // ── Add Asteroid ──────────────────────────────────────────────────────
    public void addAsteroid(Asteroid asteroid) {
        asteroids.add(asteroid);
        Vector<Object> row = new Vector<>();
        row.add(asteroid.getName());
        row.add(String.format("%.3f", asteroid.getEstimatedDiameter()));
        row.add(String.format("%,.0f", asteroid.getMissDistance()));
        row.add(String.format("%,.0f", asteroid.getKilometersPerHour()));
        row.add(asteroid.isPotentiallyHazardous() ? "⚠ HAZARDOUS" : "✔ SAFE");
        row.add(asteroid.getNasaJplUrl());
        SwingUtilities.invokeLater(() -> {
            model.addRow(row);
            chartPanel.repaint();
        });
    }

    // ══════════════════════════════════════════════════════════════════════
    // Distance Bar Chart Panel
    // ══════════════════════════════════════════════════════════════════════
    class ChartPanel extends JPanel {

        ChartPanel() {
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Background
            g2.setColor(new Color(5, 15, 45, 210));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(new Color(0, 100, 180, 60));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);

            if (asteroids.isEmpty()) {
                g2.setFont(new Font("Courier New", Font.PLAIN, 14));
                g2.setColor(TEXT_DIM);
                String msg = "No data — load a date first";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                g2.dispose();
                return;
            }

            int pad     = 60;
            int padTop  = 40;
            int padBot  = 80;
            int chartW  = getWidth()  - pad * 2;
            int chartH  = getHeight() - padTop - padBot;

            // Title
            g2.setFont(new Font("Courier New", Font.BOLD, 13));
            g2.setColor(ACCENT_CYAN);
            g2.drawString("MISS DISTANCE FROM EARTH (km)", pad, padTop - 12);

            // Max distance for scaling
            double maxDist = asteroids.stream()
                    .mapToDouble(Asteroid::getMissDistance)
                    .max().orElse(1);

            int n    = asteroids.size();
            int barW = Math.max(8, (chartW - 10) / n - 4);

            // Horizontal grid lines
            g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1f, new float[]{4, 6}, 0));
            g2.setFont(new Font("Courier New", Font.PLAIN, 10));
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int y = padTop + chartH - (int)(chartH * i / (double) gridLines);
                g2.setColor(GRID_LINE);
                g2.drawLine(pad, y, pad + chartW, y);
                double val = maxDist * i / gridLines;
                g2.setColor(TEXT_DIM);
                String label = formatDistance(val);
                g2.drawString(label, pad - 58, y + 4);
            }
            g2.setStroke(new BasicStroke(1f));

            // Axes
            g2.setColor(new Color(0, 100, 180, 120));
            g2.drawLine(pad, padTop, pad, padTop + chartH);
            g2.drawLine(pad, padTop + chartH, pad + chartW, padTop + chartH);

            // Bars
            for (int i = 0; i < n; i++) {
                Asteroid a   = asteroids.get(i);
                int barH     = (int)(chartH * (a.getMissDistance() / maxDist));
                int x        = pad + i * (chartW / n) + (chartW / n - barW) / 2;
                int y        = padTop + chartH - barH;
                boolean haz  = a.isPotentiallyHazardous();

                // Bar gradient
                Color top    = haz ? new Color(200, 40, 60) : new Color(0, 180, 100);
                Color bot    = haz ? new Color(80, 10, 20)  : new Color(0, 60, 40);
                g2.setPaint(new GradientPaint(x, y, top, x, y + barH, bot));
                g2.fillRoundRect(x, y, barW, barH, 4, 4);

                // Glow top of bar
                g2.setPaint(new GradientPaint(x, y, new Color(
                        top.getRed(), top.getGreen(), top.getBlue(), 120),
                        x, y + 12, new Color(0, 0, 0, 0)));
                g2.fillRect(x, y, barW, 12);

                // Name label (rotated)
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("Courier New", Font.PLAIN, 9));
                Graphics2D gr = (Graphics2D) g2.create();
                gr.translate(x + barW / 2 + 4, padTop + chartH + 6);
                gr.rotate(Math.PI / 4);
                // Shorten name for display
                String name = a.getName().replaceAll("[()]", "").trim();
                if (name.length() > 14) name = name.substring(0, 13) + "…";
                gr.drawString(name, 0, 0);
                gr.dispose();
            }

            // Moon distance reference line (384,400 km)
            double moonDist = 384400;
            if (moonDist <= maxDist) {
                int moonY = padTop + chartH - (int)(chartH * (moonDist / maxDist));
                g2.setColor(new Color(255, 220, 80, 160));
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        1f, new float[]{6, 4}, 0));
                g2.drawLine(pad, moonY, pad + chartW, moonY);
                g2.setStroke(new BasicStroke(1f));
                g2.setFont(new Font("Courier New", Font.PLAIN, 10));
                g2.setColor(new Color(255, 220, 80));
                g2.drawString("◄ Moon distance (384,400 km)", pad + chartW - 210, moonY - 4);
            }

            g2.dispose();
        }

        private String formatDistance(double km) {
            if (km >= 1_000_000) return String.format("%.1fM", km / 1_000_000);
            if (km >= 1_000)     return String.format("%.0fk", km / 1_000);
            return String.format("%.0f", km);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Starfield Panel
    // ══════════════════════════════════════════════════════════════════════
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
            g2.setPaint(new GradientPaint(0, 0, BG_DEEP, 0, getHeight(), BG_MID));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(getWidth() * 0.78f, getHeight() * 0.25f),
                    getWidth() * 0.38f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0, 40, 110, 35), new Color(0, 0, 0, 0)}
            ));
            g2.fillOval((int)(getWidth()*0.4f), 0, (int)(getWidth()*0.76f), (int)(getHeight()*0.6f));
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

    // ══════════════════════════════════════════════════════════════════════
    // Cell Renderer
    // ══════════════════════════════════════════════════════════════════════
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

            if (col == 4) {
                setFont(new Font("Courier New", Font.BOLD, 12));
                setForeground(hazardous ? HAZARD_RED : SAFE_GREEN);
            }
            if (col == 0) {
                setFont(new Font("Courier New", Font.BOLD, 12));
                if (!isSelected)
                    setForeground(hazardous ? new Color(255, 180, 185) : TEXT_BRIGHT);
            }
            // URL column — underline style
            if (col == 5) {
                setFont(new Font("Courier New", Font.PLAIN, 11));
                setForeground(isSelected ? Color.WHITE : new Color(80, 160, 255));
                if (value != null)
                    setText("<html><u>" + value + "</u></html>");
            }

            return this;
        }
    }
}