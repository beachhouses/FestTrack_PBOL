import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class UserFrame extends JFrame {

    private int userId;
    private String username;

    private JTable tblConcerts, tblHistory;
    private JComboBox<String> cmbConcert;
    private JSpinner spnJumlah;
    private JComboBox<String> ticketCategory;  // ComboBox untuk kategori tiket (VIP / Regular)
    private DefaultTableModel modelConcerts, modelHistory;

    public UserFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Aplikasi Tiket Konser - User: " + username);
        setSize(950, 650);
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

        JLabel lblHeader = new JLabel("Dashboard Pengguna - Tiket Konser", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(230, 235, 255));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        bgPanel.add(lblHeader, BorderLayout.NORTH);

        // TAB PANEL
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setOpaque(false);
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(isSelected ? new Color(25, 70, 180) : new Color(30, 40, 80));
                g2.fillRoundRect(x + 2, y + 4, w - 4, h - 6, 20, 20);
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                     int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g.setColor(isSelected ? Color.WHITE : new Color(190, 200, 240));
                g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
        });

        bgPanel.add(tabs, BorderLayout.CENTER);

        // TAB 1: DAFTAR KONSER
        JPanel panelConcerts = new JPanel(new BorderLayout());
        panelConcerts.setOpaque(false);

        modelConcerts = new DefaultTableModel(new String[]{"ID", "Nama Konser", "Tanggal", "Lokasi", "Harga VIP", "Harga Regular", "Kuota", "Guest Star"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblConcerts = new JTable(modelConcerts);
        styleTable(tblConcerts);

        panelConcerts.add(new JScrollPane(tblConcerts), BorderLayout.CENTER);

        JButton btnReload = new JButton("Refresh");
        btnReload.addActionListener(e -> loadConcerts());
        JPanel bottom1 = new JPanel();
        bottom1.setOpaque(false);
        bottom1.add(btnReload);
        panelConcerts.add(bottom1, BorderLayout.SOUTH);

        // TAB 2: PEMESANAN
        JPanel panelOrder = new JPanel(new GridBagLayout());
        panelOrder.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblConcert = new JLabel("Pilih Konser:");
        lblConcert.setForeground(Color.WHITE);
        JLabel lblJumlah = new JLabel("Jumlah Tiket:");
        lblJumlah.setForeground(Color.WHITE);
        JLabel lblCategory = new JLabel("Kategori Tiket:");
        lblCategory.setForeground(Color.WHITE);

        cmbConcert = new JComboBox<>();
        cmbConcert.setPreferredSize(new Dimension(250, 30));
        spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        // ComboBox untuk memilih kategori tiket (VIP / Regular)
        ticketCategory = new JComboBox<>(new String[]{"VIP", "Regular"});
        ticketCategory.setPreferredSize(new Dimension(250, 30));

        JButton btnPesan = new JButton("Pesan Tiket");
        btnPesan.setBackground(new Color(30, 100, 230));
        btnPesan.setForeground(Color.WHITE);
        btnPesan.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 0; panelOrder.add(lblConcert, gbc);
        gbc.gridx = 1; panelOrder.add(cmbConcert, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelOrder.add(lblJumlah, gbc);
        gbc.gridx = 1; panelOrder.add(spnJumlah, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelOrder.add(lblCategory, gbc);
        gbc.gridx = 1; panelOrder.add(ticketCategory, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelOrder.add(btnPesan, gbc);

        btnPesan.addActionListener(e -> buatPesanan());

        // TAB 3: RIWAYAT
        JPanel panelHistory = new JPanel(new BorderLayout());
        panelHistory.setOpaque(false);

        modelHistory = new DefaultTableModel(
                new String[]{"ID Pesanan", "Konser", "Tanggal Pesan", "Jumlah", "Total"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        tblHistory = new JTable(modelHistory);
        styleTable(tblHistory);

        JButton btnHistoryReload = new JButton("Refresh Riwayat");
        btnHistoryReload.addActionListener(e -> loadHistory());

        panelHistory.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        JPanel bottom2 = new JPanel();
        bottom2.setOpaque(false);
        bottom2.add(btnHistoryReload);
        panelHistory.add(bottom2, BorderLayout.SOUTH);

        // ADD TABS
        tabs.addTab("Daftar Konser", panelConcerts);
        tabs.addTab("Pemesanan Tiket", panelOrder);
        tabs.addTab("Riwayat Pemesanan", panelHistory);

        // LOAD DATA
        loadConcerts();
        loadConcertOptions();
        loadHistory();
    }

    private void styleTable(JTable t) {
        t.setRowHeight(28);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setSelectionBackground(new Color(30, 70, 180));
        t.setSelectionForeground(Color.WHITE);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(20, 40, 90));
        h.setForeground(Color.WHITE);
    }

    private void loadConcerts() {
        modelConcerts.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM concerts");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelConcerts.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("location"),
                        rs.getDouble("vip_price"),  // Harga VIP
                        rs.getDouble("regular_price"),  // Harga Regular
                        rs.getInt("quota"),
                        rs.getString("guest_star")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load konser: " + e.getMessage());
        }
    }

    private void loadConcertOptions() {
        cmbConcert.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM concerts");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbConcert.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load pilihan konser: " + e.getMessage());
        }
    }

    private void buatPesanan() {
        if (cmbConcert.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih konser terlebih dahulu!");
            return;
        }

        String selected = cmbConcert.getSelectedItem().toString();
        int concertId = Integer.parseInt(selected.split(" - ")[0]);
        int jumlah = (int) spnJumlah.getValue();
        String category = ticketCategory.getSelectedItem().toString();

        try (Connection conn = DBConnection.getConnection()) {
            double harga = 0;
            try (PreparedStatement ps = conn.prepareStatement("SELECT vip_price, regular_price, quota FROM concerts WHERE id=?")) {
                ps.setInt(1, concertId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if ("VIP".equals(category)) {
                        harga = rs.getDouble("vip_price");
                    } else {
                        harga = rs.getDouble("regular_price");
                    }

                    int kuota = rs.getInt("quota");
                    if (jumlah > kuota) {
                        JOptionPane.showMessageDialog(this, "Kuota tidak cukup!");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Konser tidak ditemukan");
                    return;
                }
            }

            double total = harga * jumlah;
            // Format total harga menjadi mata uang
            DecimalFormat formatter = new DecimalFormat("Rp #,###");
            String formattedTotal = formatter.format(total);

            String sqlInsert = "INSERT INTO orders(user_id, concert_id, order_date, quantity, total_price) VALUES (?,?,CURRENT_DATE,?,?)";
            try (PreparedStatement ps2 = conn.prepareStatement(sqlInsert)) {
                ps2.setInt(1, userId);
                ps2.setInt(2, concertId);
                ps2.setInt(3, jumlah);
                ps2.setDouble(4, total);
                ps2.executeUpdate();
            }

            String sqlUpdate = "UPDATE concerts SET quota = quota - ? WHERE id=?";
            try (PreparedStatement ps3 = conn.prepareStatement(sqlUpdate)) {
                ps3.setInt(1, jumlah);
                ps3.setInt(2, concertId);
                ps3.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Pesanan berhasil! Total: Rp " + total);
            loadConcerts();
            loadHistory();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat pemesanan: " + e.getMessage());
        }
    }

    private void loadHistory() {
        modelHistory.setRowCount(0);
        String sql = "SELECT o.id, c.name, o.order_date, o.quantity, o.total_price " +
                     "FROM orders o JOIN concerts c ON o.concert_id = c.id WHERE o.user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelHistory.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("order_date"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load riwayat: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserFrame(1, "tester").setVisible(true));
    }
}