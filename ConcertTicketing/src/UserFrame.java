import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class UserFrame extends JFrame {
    private int userId;
    private String username;

    private JTable tblConcerts, tblHistory;
    private JComboBox<String> cmbConcert;
    private JComboBox<String> ticketCategory;
    private JSpinner spnJumlah;
    private DefaultTableModel modelConcerts, modelHistory;

    private JPanel panelContent;

    public UserFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Aplikasi Tiket Konser - " + username);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ====== SIDEBAR ======
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(15, 30, 70));
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel lblLogo = new JLabel("🎟️ TiketKonser", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);

        sidebar.add(Box.createVerticalStrut(35));

        JButton btnConcerts = createMenuButton("Daftar Konser");
        JButton btnOrder = createMenuButton("Pemesanan Tiket");
        JButton btnHistory = createMenuButton("Riwayat Pemesanan");
        JButton btnLogout = createMenuButton("Keluar");

        sidebar.add(btnConcerts);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnOrder);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnHistory);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ====== PANEL KONTEN ======
        panelContent = new JPanel(new CardLayout());
        panelContent.setBackground(new Color(245, 247, 255));
        add(panelContent, BorderLayout.CENTER);

        // ====== PANEL 1: DAFTAR KONSER ======
        JPanel panelConcerts = new JPanel(new BorderLayout());
        JLabel lbl1 = createHeader("Daftar Konser");
        panelConcerts.add(lbl1, BorderLayout.NORTH);

        modelConcerts = new DefaultTableModel(new String[]{
                "ID", "Nama Konser", "Tanggal", "Lokasi", "Harga VIP", "Harga Regular", "Kuota", "Guest Star"}, 0);
        tblConcerts = new JTable(modelConcerts);
        styleTable(tblConcerts);
        JScrollPane scrollConcerts = new JScrollPane(tblConcerts);
        panelConcerts.add(scrollConcerts, BorderLayout.CENTER);

        JButton btnReload = styledButton("Refresh", new Color(80, 100, 150));
        JPanel bottom1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom1.setBackground(Color.WHITE);
        bottom1.add(btnReload);
        panelConcerts.add(bottom1, BorderLayout.SOUTH);

        btnReload.addActionListener(e -> loadConcerts());

        // ====== PANEL 2: PEMESANAN ======
        JPanel panelOrder = new JPanel(new GridBagLayout());
        panelOrder.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitleOrder = createHeader("Pemesanan Tiket");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelOrder.add(lblTitleOrder, gbc);

        gbc.gridwidth = 1;
        JLabel lblConcert = new JLabel("Pilih Konser:");
        JLabel lblJumlah = new JLabel("Jumlah Tiket:");
        JLabel lblCategory = new JLabel("Kategori Tiket:");

        cmbConcert = new JComboBox<>();
        ticketCategory = new JComboBox<>(new String[]{"VIP", "Regular"});
        spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        JButton btnPesan = styledButton("Pesan Tiket", new Color(35, 120, 220));

        gbc.gridx = 0; gbc.gridy = 1; panelOrder.add(lblConcert, gbc);
        gbc.gridx = 1; panelOrder.add(cmbConcert, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelOrder.add(lblJumlah, gbc);
        gbc.gridx = 1; panelOrder.add(spnJumlah, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelOrder.add(lblCategory, gbc);
        gbc.gridx = 1; panelOrder.add(ticketCategory, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelOrder.add(btnPesan, gbc);

        lblConcert.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCategory.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnPesan.addActionListener(e -> buatPesanan());

        // ====== PANEL 3: RIWAYAT ======
        JPanel panelHistory = new JPanel(new BorderLayout());
        JLabel lbl3 = createHeader("Riwayat Pemesanan");
        panelHistory.add(lbl3, BorderLayout.NORTH);

        modelHistory = new DefaultTableModel(new String[]{
                "ID Pesanan", "Konser", "Tanggal Pesan", "Jumlah", "Total"}, 0);
        tblHistory = new JTable(modelHistory);
        styleTable(tblHistory);

        JScrollPane scrollHistory = new JScrollPane(tblHistory);
        panelHistory.add(scrollHistory, BorderLayout.CENTER);

        JButton btnReloadHistory = styledButton("Refresh Riwayat", new Color(80, 100, 150));
        JPanel bottom3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom3.setBackground(Color.WHITE);
        bottom3.add(btnReloadHistory);
        panelHistory.add(bottom3, BorderLayout.SOUTH);
        btnReloadHistory.addActionListener(e -> loadHistory());

        // ====== MASUKKAN SEMUA KE CARD ======
        panelContent.add(panelConcerts, "concerts");
        panelContent.add(panelOrder, "order");
        panelContent.add(panelHistory, "history");

        // ====== NAVIGATION BUTTONS ======
        CardLayout cl = (CardLayout) panelContent.getLayout();
        btnConcerts.addActionListener(e -> cl.show(panelContent, "concerts"));
        btnOrder.addActionListener(e -> cl.show(panelContent, "order"));
        btnHistory.addActionListener(e -> cl.show(panelContent, "history"));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) dispose();
        });

        // ====== LOAD DATA ======
        loadConcerts();
        loadConcertOptions();
        loadHistory();

        setVisible(true);
    }

    // === COMPONENT STYLE ===
    private JButton createMenuButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(35, 70, 150));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { b.setBackground(new Color(50, 110, 200)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { b.setBackground(new Color(35, 70, 150)); }
        });
        return b;
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createHeader(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(new Color(20, 40, 90));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        return lbl;
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

    // === DATABASE FUNCTION ===
    private void loadConcerts() {
        modelConcerts.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM concerts");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelConcerts.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("location"),
                        rs.getDouble("vip_price"),
                        rs.getDouble("regular_price"),
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
            while (rs.next()) cmbConcert.addItem(rs.getInt("id") + " - " + rs.getString("name"));
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
            int kuota = 0;
            try (PreparedStatement ps = conn.prepareStatement("SELECT vip_price, regular_price, quota FROM concerts WHERE id=?")) {
                ps.setInt(1, concertId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    harga = category.equals("VIP") ? rs.getDouble("vip_price") : rs.getDouble("regular_price");
                    kuota = rs.getInt("quota");
                }
            }

            if (jumlah > kuota) {
                JOptionPane.showMessageDialog(this, "Kuota tidak mencukupi!");
                return;
            }

            double total = harga * jumlah;
            DecimalFormat df = new DecimalFormat("#,###");
            String formatted = df.format(total);

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

            JOptionPane.showMessageDialog(this, "Pesanan berhasil!\nTotal: Rp " + formatted);
            loadConcerts();
            loadHistory();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadHistory() {
        modelHistory.setRowCount(0);
        String sql = "SELECT o.id, c.name, o.order_date, o.quantity, o.total_price FROM orders o " +
                     "JOIN concerts c ON o.concert_id=c.id WHERE o.user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelHistory.addRow(new Object[]{
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