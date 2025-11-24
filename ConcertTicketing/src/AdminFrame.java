import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {

    private int adminId;
    private String username;

    private JTable tableConcerts;
    private JTable tableOrders;

    private DefaultTableModel modelConcerts;
    private DefaultTableModel modelOrders;

    public AdminFrame(int adminId, String username) {
        this.adminId = adminId;
        this.username = username;

        setTitle("Dashboard Admin - " + username);
        setSize(1000, 700); // Memperlebar ukuran
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // BACKGROUND PANEL
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 20, 60),
                        getWidth(), getHeight(), new Color(0, 80, 180)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel);

        // HEADER
        JLabel lblHeader = new JLabel("Dashboard Admin - Kelola Data Konser", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(220, 230, 255));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        bgPanel.add(lblHeader, BorderLayout.NORTH);

        // TAB PANEL
        Color tabActive = new Color(25, 70, 180);
        Color tabInactive = new Color(30, 40, 80);
        Color tabTextActive = new Color(235, 240, 255);
        Color tabTextInactive = new Color(180, 190, 220);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setOpaque(false);
        tabbedPane.setBorder(null);

        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets = new Insets(10, 10, 0, 10);
                contentBorderInsets = new Insets(0, 0, 0, 0);
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                                              int tabIndex, int x, int y, int w, int h,
                                              boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? tabActive : tabInactive);
                g2.fillRoundRect(x + 2, y + 4, w - 4, h - 6, 20, 20);
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font,
                                     FontMetrics metrics, int tabIndex,
                                     String title, Rectangle textRect,
                                     boolean isSelected) {
                g.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g.setColor(isSelected ? tabTextActive : tabTextInactive);
                int x = textRect.x;
                int y = textRect.y + metrics.getAscent();
                g.drawString(title, x, y);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement,
                                              int selectedIndex) {}
        });

        // PANEL UTAMA (kartu transparan)
        JPanel cardHolder = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 25, 25);
            }
        };
        cardHolder.setOpaque(false);
        cardHolder.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        cardHolder.add(tabbedPane, BorderLayout.CENTER);
        bgPanel.add(cardHolder, BorderLayout.CENTER);

        // --- TAB 1: KELOLA DATA KONSER --- 
        JPanel panelConcert = new JPanel(new BorderLayout());
        panelConcert.setOpaque(false);
        panelConcert.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelConcerts = new DefaultTableModel(new String[]{"ID", "Nama Konser", "Tanggal", "Lokasi", "VIP Price", "Regular Price", "Guest Star", "Kuota"}, 0);
        tableConcerts = new JTable(modelConcerts);
        JScrollPane scrollConcert = new JScrollPane(tableConcerts);
        panelConcert.add(scrollConcert, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton btnAdd = new JButton("Tambah Konser");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        JButton btnRefresh = new JButton("Refresh");
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        panelConcert.add(actionPanel, BorderLayout.SOUTH);

        // EVENT tombol
        btnAdd.addActionListener(e -> addConcert());
        btnEdit.addActionListener(e -> editConcert());
        btnDelete.addActionListener(e -> deleteConcert());
        btnRefresh.addActionListener(e -> loadConcerts());

        // --- TAB 2: LAPORAN PEMESANAN ---
        JPanel panelOrders = new JPanel(new BorderLayout());
        panelOrders.setOpaque(false);
        panelOrders.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelOrders = new DefaultTableModel(new String[]{"ID Order", "User", "Konser", "Tanggal", "Jumlah Tiket", "Total"}, 0);
        tableOrders = new JTable(modelOrders);
        JScrollPane scrollOrders = new JScrollPane(tableOrders);
        panelOrders.add(scrollOrders, BorderLayout.CENTER);

        JButton btnReloadOrders = new JButton("Refresh Laporan");
        panelOrders.add(btnReloadOrders, BorderLayout.SOUTH);
        btnReloadOrders.addActionListener(e -> loadOrders());

        // MASUKKAN KE TAB
        tabbedPane.addTab("Kelola Konser", panelConcert);
        tabbedPane.addTab("Laporan Pemesanan", panelOrders);

        // LOAD DATA
        loadConcerts();
        loadOrders();
    }

    // --- METHOD LOAD DATA ---
    private void loadConcerts() {
        modelConcerts.setRowCount(0);
        String sql = "SELECT * FROM concerts";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelConcerts.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("location"),
                        rs.getDouble("vip_price"),
                        rs.getDouble("regular_price"),
                        rs.getString("guest_star"), // Menampilkan guest star
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
                     "FROM orders o JOIN users u ON o.user_id=u.id " +
                     "JOIN concerts c ON o.concert_id=c.id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
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

    // --- CRUD KONSER ---
    private void addConcert() {
        JTextField nameField = new JTextField();
        JTextField dateField = new JTextField("2025-12-31");
        JTextField locationField = new JTextField();
        JTextField vipPriceField = new JTextField();
        JTextField regularPriceField = new JTextField();
        JTextField guestStarField = new JTextField(); // Kolom Guest Star
        JTextField quotaField = new JTextField();

        Object[] form = {
                "Nama Konser:", nameField,
                "Tanggal (YYYY-MM-DD):", dateField,
                "Lokasi:", locationField,
                "VIP Price:", vipPriceField,
                "Regular Price:", regularPriceField,
                "Guest Star:", guestStarField,  // Kolom Guest Star
                "Kuota:", quotaField
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Tambah Konser", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO concerts(name, date, location, vip_price, regular_price, guest_star, quota) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nameField.getText());
                ps.setString(2, dateField.getText());
                ps.setString(3, locationField.getText());
                ps.setDouble(4, Double.parseDouble(vipPriceField.getText()));
                ps.setDouble(5, Double.parseDouble(regularPriceField.getText()));
                ps.setString(6, guestStarField.getText());  // Mengambil data guest star
                ps.setInt(7, Integer.parseInt(quotaField.getText()));
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
        JTextField nameField = new JTextField(modelConcerts.getValueAt(row, 1).toString());
        JTextField dateField = new JTextField(modelConcerts.getValueAt(row, 2).toString());
        JTextField locationField = new JTextField(modelConcerts.getValueAt(row, 3).toString());
        JTextField vipPriceField = new JTextField(modelConcerts.getValueAt(row, 4).toString());
        JTextField regularPriceField = new JTextField(modelConcerts.getValueAt(row, 5).toString());
        JTextField guestStarField = new JTextField(modelConcerts.getValueAt(row, 6).toString());  // Mengambil data guest star
        JTextField quotaField = new JTextField(modelConcerts.getValueAt(row, 7).toString());

        Object[] form = {
                "Nama Konser:", nameField,
                "Tanggal:", dateField,
                "Lokasi:", locationField,
                "VIP Price:", vipPriceField,
                "Regular Price:", regularPriceField,
                "Guest Star:", guestStarField,  // Kolom Guest Star
                "Kuota:", quotaField
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Konser", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String sql = "UPDATE concerts SET name=?, date=?, location=?, vip_price=?, regular_price=?, guest_star=?, quota=? WHERE id=?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nameField.getText());
                ps.setString(2, dateField.getText());
                ps.setString(3, locationField.getText());
                ps.setDouble(4, Double.parseDouble(vipPriceField.getText()));
                ps.setDouble(5, Double.parseDouble(regularPriceField.getText()));
                ps.setString(6, guestStarField.getText());  // Mengupdate guest star
                ps.setInt(7, Integer.parseInt(quotaField.getText()));
                ps.setInt(8, id);
                ps.executeUpdate();
                loadConcerts();
                JOptionPane.showMessageDialog(this, "Data konser berhasil diperbarui!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error update: " + e.getMessage());
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
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus konser ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM concerts WHERE id=?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadConcerts();
                JOptionPane.showMessageDialog(this, "Konser berhasil dihapus!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error hapus konser: " + e.getMessage());
            }
        }
    }
}
