import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {

    private int adminId;
    private String username;
    private JTable tableConcerts, tableOrders;
    private DefaultTableModel modelConcerts, modelOrders;
    private JPanel panelContent;

    public AdminFrame(int adminId, String username) {
        this.adminId = adminId;
        this.username = username;

        setTitle("FestTrack Admin - " + username);
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== BACKGROUND DASHBOARD =====
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 20, 60),
                        getWidth(), getHeight(), new Color(25, 100, 210)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        add(bgPanel, BorderLayout.CENTER);

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblLogo = new JLabel("FestTrack Admin", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(40));

        JButton btnKonser = createMenuButton("Kelola Konser");
        JButton btnLaporan = createMenuButton("Laporan Pemesanan");
        JButton btnLogout = createMenuButton("Keluar");

        sidebar.add(btnKonser);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnLaporan);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        bgPanel.add(sidebar, BorderLayout.WEST);

        // ===== PANEL UTAMA =====
        panelContent = new JPanel(new CardLayout());
        panelContent.setOpaque(false);
        panelContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        bgPanel.add(panelContent, BorderLayout.CENTER);

        // ===== PANEL KELOLA KONSER =====
        JPanel panelConcert = createCardPanel();
        JLabel lblHeader = createGradientTitle("Kelola Data Konser");
        panelConcert.add(lblHeader, BorderLayout.NORTH);

        modelConcerts = new DefaultTableModel(
                new String[]{"ID", "Nama Konser", "Tanggal", "Lokasi", "VIP Price", "Regular Price", "Guest Star", "Kuota"}, 0);
        tableConcerts = new JTable(modelConcerts);
        styleTable(tableConcerts);
        JScrollPane scrollConcert = createRoundedScrollPane(tableConcerts);
        scrollConcert.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));
        panelConcert.add(scrollConcert, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setOpaque(false);
        JButton btnAdd = styledButton("Tambah Konser", new Color(66, 163, 95));
        JButton btnEdit = styledButton("Edit", new Color(70, 125, 210));
        JButton btnDelete = styledButton("Hapus", new Color(200, 70, 70));
        JButton btnRefresh = styledButton("Refresh", new Color(120, 120, 140));
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        panelConcert.add(actionPanel, BorderLayout.SOUTH);

        // ===== PANEL LAPORAN =====
        JPanel panelOrders = createCardPanel();
        JLabel lblLaporan = createGradientTitle("Laporan Pemesanan Tiket");
        panelOrders.add(lblLaporan, BorderLayout.NORTH);

        modelOrders = new DefaultTableModel(
                new String[]{"ID Order", "User", "Konser", "Tanggal", "Jumlah Tiket", "Total"}, 0);
        tableOrders = new JTable(modelOrders);
        styleTable(tableOrders);
        JScrollPane scrollOrders = createRoundedScrollPane(tableOrders);
        scrollOrders.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));
        panelOrders.add(scrollOrders, BorderLayout.CENTER);

        JButton btnReloadOrders = styledButton("Refresh Laporan", new Color(120, 120, 140));
        JPanel pnlReload = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlReload.setOpaque(false);
        pnlReload.add(btnReloadOrders);
        panelOrders.add(pnlReload, BorderLayout.SOUTH);

        // ===== ADD TO CONTENT =====
        panelContent.add(panelConcert, "konser");
        panelContent.add(panelOrders, "laporan");

        CardLayout cl = (CardLayout) panelContent.getLayout();
        btnKonser.addActionListener(e -> cl.show(panelContent, "konser"));
        btnLaporan.addActionListener(e -> cl.show(panelContent, "laporan"));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) dispose();
        });

        // ===== CRUD EVENT =====
        btnAdd.addActionListener(e -> addConcert());
        btnEdit.addActionListener(e -> editConcert());
        btnDelete.addActionListener(e -> deleteConcert());
        btnRefresh.addActionListener(e -> loadConcerts());
        btnReloadOrders.addActionListener(e -> loadOrders());

        loadConcerts();
        loadOrders();

        setVisible(true);
    }

    // ===== UTIL UI =====
    private JPanel createCardPanel() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 80),
                        0, getHeight(), new Color(255, 255, 255, 40)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(255, 255, 255, 90));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));
        return card;
    }

    private JLabel createGradientTitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(240, 245, 255),
                        getWidth(), 0, new Color(160, 190, 255)
                );
                g2.setPaint(gp);
                g2.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        label.setPreferredSize(new Dimension(0, 70));
        return label;
    }

    private JScrollPane createRoundedScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(new Color(255, 255, 255, 70));
        return scroll;
    }

    private JButton createMenuButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(32, 60, 140));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(200, 45));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(55, 100, 190)); }
            public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(new Color(32, 60, 140)); }
        });
        return b;
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bgColor.brighter()); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(new Color(255, 255, 255, 200));
        table.setSelectionBackground(new Color(30, 90, 180));
        table.setSelectionForeground(Color.WHITE);
        table.setOpaque(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        header.setBackground(new Color(25, 50, 120));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setOpaque(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0
                            ? new Color(250, 250, 255, 230)
                            : new Color(235, 240, 250, 220));
                    c.setForeground(new Color(30, 30, 50));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    // ===== CRUD & LOAD DATA =====
    private void addConcert() {
        JTextField name = new JTextField();
        JTextField date = new JTextField("2025-12-31");
        JTextField location = new JTextField();
        JTextField vip = new JTextField();
        JTextField regular = new JTextField();
        JTextField guest = new JTextField();
        JTextField quota = new JTextField();

        Object[] form = {"Nama Konser:", name, "Tanggal:", date, "Lokasi:", location,
                "VIP Price:", vip, "Regular Price:", regular, "Guest Star:", guest, "Kuota:", quota};

        if (JOptionPane.showConfirmDialog(this, form, "Tambah Konser", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO concerts(name, date, location, vip_price, regular_price, guest_star, quota) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name.getText());
                ps.setString(2, date.getText());
                ps.setString(3, location.getText());
                ps.setDouble(4, Double.parseDouble(vip.getText()));
                ps.setDouble(5, Double.parseDouble(regular.getText()));
                ps.setString(6, guest.getText());
                ps.setInt(7, Integer.parseInt(quota.getText()));
                ps.executeUpdate();
                loadConcerts();
                JOptionPane.showMessageDialog(this, "Konser berhasil ditambahkan!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error tambah konser: " + e.getMessage());
            }
        }
    }

    private void editConcert() {
        int row = tableConcerts.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih konser yang ingin diedit!");
            return;
        }

        int id = (int) modelConcerts.getValueAt(row, 0);
        JTextField name = new JTextField(modelConcerts.getValueAt(row, 1).toString());
        JTextField date = new JTextField(modelConcerts.getValueAt(row, 2).toString());
        JTextField location = new JTextField(modelConcerts.getValueAt(row, 3).toString());
        JTextField vip = new JTextField(modelConcerts.getValueAt(row, 4).toString());
        JTextField regular = new JTextField(modelConcerts.getValueAt(row, 5).toString());
        JTextField guest = new JTextField(modelConcerts.getValueAt(row, 6).toString());
        JTextField quota = new JTextField(modelConcerts.getValueAt(row, 7).toString());

        Object[] form = {"Nama Konser:", name, "Tanggal:", date, "Lokasi:", location,
                "VIP Price:", vip, "Regular Price:", regular, "Guest Star:", guest, "Kuota:", quota};

        if (JOptionPane.showConfirmDialog(this, form, "Edit Konser", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            String sql = "UPDATE concerts SET name=?, date=?, location=?, vip_price=?, regular_price=?, guest_star=?, quota=? WHERE id=?";
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name.getText());
                ps.setString(2, date.getText());
                ps.setString(3, location.getText());
                ps.setDouble(4, Double.parseDouble(vip.getText()));
                ps.setDouble(5, Double.parseDouble(regular.getText()));
                ps.setString(6, guest.getText());
                ps.setInt(7, Integer.parseInt(quota.getText()));
                ps.setInt(8, id);
                ps.executeUpdate();
                loadConcerts();
                JOptionPane.showMessageDialog(this, "Data konser diperbarui!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error update konser: " + e.getMessage());
            }
        }
    }

    private void deleteConcert() {
        int row = tableConcerts.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih konser yang ingin dihapus!");
            return;
        }
        int id = (int) modelConcerts.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus konser ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement("DELETE FROM concerts WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadConcerts();
                JOptionPane.showMessageDialog(this, "Konser berhasil dihapus!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error hapus konser: " + e.getMessage());
            }
        }
    }

    private void loadConcerts() {
        modelConcerts.setRowCount(0);
        String sql = "SELECT * FROM concerts";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelConcerts.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("location"),
                        rs.getDouble("vip_price"),
                        rs.getDouble("regular_price"),
                        rs.getString("guest_star"),
                        rs.getInt("quota")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load konser: " + e.getMessage());
        }
    }

    private void loadOrders() {
        modelOrders.setRowCount(0);
        String sql = "SELECT o.id, u.username, c.name, o.order_date, o.quantity, o.total_price " +
                     "FROM orders o JOIN users u ON o.user_id = u.id " +
                     "JOIN concerts c ON o.concert_id = c.id";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelOrders.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("order_date"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load laporan: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminFrame(1, "admin"));
    }
}