import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {

    private int adminId;
    private String username;

    private JTable tableConcerts;
    private JTable tableOrders;
    private DefaultTableModel modelConcerts;
    private DefaultTableModel modelOrders;

    private JPanel panelContent; // untuk ganti antar menu

    public AdminFrame(int adminId, String username) {
        this.adminId = adminId;
        this.username = username;

        setTitle("Dashboard Admin - " + username);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== BACKGROUND DASHBOARD =====
        JPanel bgPanel = new JPanel(new BorderLayout());
        bgPanel.setBackground(new Color(12, 25, 60));
        add(bgPanel, BorderLayout.CENTER);

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBackground(new Color(18, 35, 90));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel lblLogo = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);

        sidebar.add(Box.createVerticalStrut(40));

        JButton btnKonser = new JButton("Kelola Konser");
        JButton btnLaporan = new JButton("Laporan Pemesanan");
        JButton btnLogout = new JButton("Keluar");

        JButton[] menuButtons = {btnKonser, btnLaporan, btnLogout};
        for (JButton b : menuButtons) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setForeground(Color.WHITE);
            b.setBackground(new Color(35, 70, 150));
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(200, 45));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setIconTextGap(10);
            sidebar.add(b);
            sidebar.add(Box.createVerticalStrut(15));
        }

        bgPanel.add(sidebar, BorderLayout.WEST);

        // ===== PANEL KONTEN (berubah dinamis) =====
        panelContent = new JPanel(new CardLayout());
        panelContent.setBackground(new Color(245, 247, 255));
        bgPanel.add(panelContent, BorderLayout.CENTER);

        // ===== HEADER =====
        JLabel lblHeader = new JLabel("Dashboard Admin - Kelola Data Konser", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(20, 40, 90));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // PANEL KELOLA KONSER
        JPanel panelConcert = new JPanel(new BorderLayout());
        panelConcert.setBackground(Color.WHITE);
        panelConcert.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panelConcert.add(lblHeader, BorderLayout.NORTH);

        modelConcerts = new DefaultTableModel(
                new String[]{"ID", "Nama Konser", "Tanggal", "Lokasi", "VIP Price", "Regular Price", "Guest Star", "Kuota"}, 0);
        tableConcerts = new JTable(modelConcerts);
        tableConcerts.setRowHeight(28);
        tableConcerts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollConcert = new JScrollPane(tableConcerts);
        panelConcert.add(scrollConcert, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setBackground(Color.WHITE);
        JButton btnAdd = styledButton("Tambah Konser", new Color(80, 170, 100));
        JButton btnEdit = styledButton("Edit", new Color(80, 140, 220));
        JButton btnDelete = styledButton("Hapus", new Color(210, 70, 70));
        JButton btnRefresh = styledButton("Refresh", new Color(100, 100, 120));
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        panelConcert.add(actionPanel, BorderLayout.SOUTH);

        // PANEL LAPORAN PEMESANAN
        JPanel panelOrders = new JPanel(new BorderLayout());
        panelOrders.setBackground(Color.WHITE);
        panelOrders.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel lblLaporan = new JLabel("Laporan Pemesanan Tiket", SwingConstants.CENTER);
        lblLaporan.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLaporan.setForeground(new Color(20, 40, 90));
        panelOrders.add(lblLaporan, BorderLayout.NORTH);

        modelOrders = new DefaultTableModel(
                new String[]{"ID Order", "User", "Konser", "Tanggal", "Jumlah Tiket", "Total"}, 0);
        tableOrders = new JTable(modelOrders);
        tableOrders.setRowHeight(28);
        tableOrders.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollOrders = new JScrollPane(tableOrders);
        panelOrders.add(scrollOrders, BorderLayout.CENTER);

        JButton btnReloadOrders = styledButton("Refresh Laporan", new Color(100, 100, 120));
        JPanel pnlReload = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlReload.setBackground(Color.WHITE);
        pnlReload.add(btnReloadOrders);
        panelOrders.add(pnlReload, BorderLayout.SOUTH);

        // TAMBAHKAN KE CARD PANEL
        panelContent.add(panelConcert, "konser");
        panelContent.add(panelOrders, "laporan");

        // ==== ACTION LISTENER SIDEBAR ====
        CardLayout cl = (CardLayout) panelContent.getLayout();
        btnKonser.addActionListener(e -> cl.show(panelContent, "konser"));
        btnLaporan.addActionListener(e -> cl.show(panelContent, "laporan"));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        // ==== EVENT CRUD ====
        btnAdd.addActionListener(e -> addConcert());
        btnEdit.addActionListener(e -> editConcert());
        btnDelete.addActionListener(e -> deleteConcert());
        btnRefresh.addActionListener(e -> loadConcerts());
        btnReloadOrders.addActionListener(e -> loadOrders());

        loadConcerts();
        loadOrders();

        setVisible(true);
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // === METHOD LOAD DATA (sama seperti versi kamu) ===
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

    // === CRUD (sama seperti di kode kamu) ===
    private void addConcert() { /* copy dari kode kamu */ }
    private void editConcert() { /* copy dari kode kamu */ }
    private void deleteConcert() { /* copy dari kode kamu */ }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminFrame(1, "admin"));
    }
}