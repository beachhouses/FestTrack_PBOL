import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        JPanel panelConcert = createCardPanel("Kelola Data Konser");
        modelConcerts = new DefaultTableModel(
                new String[]{"ID", "Nama Konser", "Tanggal", "Lokasi",
                             "VIP Price", "Regular Price",
                             "Kuota VIP", "Kuota Regular", "Guest Star"}, 0);
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
        JPanel panelOrders = createCardPanel("Laporan Pemesanan Tiket");
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
    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        label.setForeground(new Color(240, 245, 255));
        label.setPreferredSize(new Dimension(0, 70));
        card.add(label, BorderLayout.NORTH);

        return card;
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
        table.setSelectionBackground(new Color(30, 90, 180));
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        header.setBackground(new Color(25, 50, 120));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }

    // ===== CRUD & LOAD DATA =====
    private void addConcert() {
        JTextField name = new JTextField();
        JTextField location = new JTextField();
        JTextField vip = new JTextField();
        JTextField regular = new JTextField();
        JTextField vipQuota = new JTextField();
        JTextField regQuota = new JTextField();
        JTextField guest = new JTextField();

        // Date picker
        JTextField txtDate = new JTextField();
        txtDate.setEditable(false);
        JButton btnPick = new JButton("📅 Pilih");
        btnPick.addActionListener(e -> {
            DatePicker dp = new DatePicker(this);
            Date d = dp.pickDate();
            if (d != null) {
                txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(d));
            }
        });
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(txtDate, BorderLayout.CENTER);
        datePanel.add(btnPick, BorderLayout.EAST);

        Object[] form = {
                "Nama Konser:", name,
                "Tanggal:", datePanel,
                "Lokasi:", location,
                "VIP Price:", vip,
                "Regular Price:", regular,
                "Kuota VIP:", vipQuota,
                "Kuota Regular:", regQuota,
                "Guest Star:", guest
        };

        if (JOptionPane.showConfirmDialog(this, form, "Tambah Konser", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO concerts(name, date, location, vip_price, regular_price, vip_quota, regular_quota, guest_star) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name.getText());
                ps.setString(2, txtDate.getText());
                ps.setString(3, location.getText());
                ps.setDouble(4, Double.parseDouble(vip.getText()));
                ps.setDouble(5, Double.parseDouble(regular.getText()));
                ps.setInt(6, Integer.parseInt(vipQuota.getText()));
                ps.setInt(7, Integer.parseInt(regQuota.getText()));
                ps.setString(8, guest.getText());
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
        JTextField location = new JTextField(modelConcerts.getValueAt(row, 3).toString());
        JTextField vip = new JTextField(modelConcerts.getValueAt(row, 4).toString());
        JTextField regular = new JTextField(modelConcerts.getValueAt(row, 5).toString());
        JTextField vipQuota = new JTextField(modelConcerts.getValueAt(row, 6).toString());
        JTextField regQuota = new JTextField(modelConcerts.getValueAt(row, 7).toString());
        JTextField guest = new JTextField(modelConcerts.getValueAt(row, 8).toString());

        // date picker
        JTextField txtDate = new JTextField(modelConcerts.getValueAt(row, 2).toString());
        txtDate.setEditable(false);
        JButton btnPick = new JButton("📅 Pilih");
        btnPick.addActionListener(e -> {
            DatePicker dp = new DatePicker(this);
            Date d = dp.pickDate();
            if (d != null) txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(d));
        });
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(txtDate, BorderLayout.CENTER);
        datePanel.add(btnPick, BorderLayout.EAST);

        Object[] form = {
                "Nama Konser:", name,
                "Tanggal:", datePanel,
                "Lokasi:", location,
                "VIP Price:", vip,
                "Regular Price:", regular,
                "Kuota VIP:", vipQuota,
                "Kuota Regular:", regQuota,
                "Guest Star:", guest
        };

        if (JOptionPane.showConfirmDialog(this, form, "Edit Konser", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            String sql = "UPDATE concerts SET name=?, date=?, location=?, vip_price=?, regular_price=?, vip_quota=?, regular_quota=?, guest_star=? WHERE id=?";
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name.getText());
                ps.setString(2, txtDate.getText());
                ps.setString(3, location.getText());
                ps.setDouble(4, Double.parseDouble(vip.getText()));
                ps.setDouble(5, Double.parseDouble(regular.getText()));
                ps.setInt(6, Integer.parseInt(vipQuota.getText()));
                ps.setInt(7, Integer.parseInt(regQuota.getText()));
                ps.setString(8, guest.getText());
                ps.setInt(9, id);
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
                        rs.getInt("vip_quota"),
                        rs.getInt("regular_quota"),
                        rs.getString("guest_star")
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