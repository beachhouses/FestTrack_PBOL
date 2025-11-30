import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class UserFrame extends JFrame {
    private int userId;
    private String username;

    private JTable tblConcerts, tblHistory;
    private JComboBox<String> cmbConcert, ticketCategory;
    private JSpinner spnJumlah;
    private DefaultTableModel modelConcerts, modelHistory;
    private JPanel panelContent;

    public UserFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;

        // ==== Frame setup ====
        setTitle("FestTrack User - " + username);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==== Background Gradient ====
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

        // ==== Sidebar ====
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel lblLogo = new JLabel("FestTrack User", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
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
        bgPanel.add(sidebar, BorderLayout.WEST);

        // ==== Main Content Panel ====
        panelContent = new JPanel(new CardLayout());
        panelContent.setOpaque(false);
        panelContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bgPanel.add(panelContent, BorderLayout.CENTER);

        // ==== Panel 1: Daftar Konser ====
        JPanel cardConcerts = createCardPanel();
        JLabel lbl1 = createGradientHeader("Daftar Konser");
        cardConcerts.add(lbl1, BorderLayout.NORTH);

        modelConcerts = new DefaultTableModel(new String[]{
                "ID", "Nama Konser", "Tanggal", "Lokasi",
                "Harga VIP", "Harga Regular", "Kuota", "Guest Star"
        }, 0);
        tblConcerts = new JTable(modelConcerts);
        styleTable(tblConcerts);
        JScrollPane scrollConcerts = createRoundedScrollPane(tblConcerts);
        cardConcerts.add(scrollConcerts, BorderLayout.CENTER);

        JButton btnReload = styledButton("Refresh", new Color(80, 100, 150));
        JPanel bottom1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom1.setOpaque(false);
        bottom1.add(btnReload);
        cardConcerts.add(bottom1, BorderLayout.SOUTH);
        btnReload.addActionListener(e -> loadConcerts());

        // ==== Panel 2: Pemesanan Tiket ====
        JPanel cardOrder = createCardPanel();
        JLabel lbl2 = createGradientHeader("Pemesanan Tiket");
        cardOrder.add(lbl2, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblConcert = new JLabel("Pilih Konser:");
        JLabel lblJumlah = new JLabel("Jumlah Tiket:");
        JLabel lblCategory = new JLabel("Kategori Tiket:");
        lblConcert.setForeground(Color.DARK_GRAY);
        lblJumlah.setForeground(Color.DARK_GRAY);
        lblCategory.setForeground(Color.DARK_GRAY);

        cmbConcert = new JComboBox<>();
        ticketCategory = new JComboBox<>(new String[]{"VIP", "Regular"});
        spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        JButton btnPesan = styledButton("Pesan Tiket", new Color(35, 120, 220));

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblConcert, gbc);
        gbc.gridx = 1; formPanel.add(cmbConcert, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblJumlah, gbc);
        gbc.gridx = 1; formPanel.add(spnJumlah, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblCategory, gbc);
        gbc.gridx = 1; formPanel.add(ticketCategory, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnPesan, gbc);
        cardOrder.add(formPanel, BorderLayout.CENTER);
        btnPesan.addActionListener(e -> buatPesanan());

        // ==== Panel 3: Riwayat Pemesanan ====
        JPanel cardHistory = createCardPanel();
        JLabel lbl3 = createGradientHeader("Riwayat Pemesanan");
        cardHistory.add(lbl3, BorderLayout.NORTH);

        modelHistory = new DefaultTableModel(new String[]{
                "ID Pesanan", "Konser", "Tanggal Pesan", "Jumlah", "Total"
        }, 0);
        tblHistory = new JTable(modelHistory);
        styleTable(tblHistory);
        JScrollPane scrollHistory = createRoundedScrollPane(tblHistory);
        cardHistory.add(scrollHistory, BorderLayout.CENTER);

        JButton btnReloadHistory = styledButton("Refresh Riwayat", new Color(80, 100, 150));
        JButton btnCetak = styledButton("Cetak Tiket", new Color(66, 163, 95));
        JPanel bottom3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom3.setOpaque(false);
        bottom3.add(btnCetak);
        bottom3.add(btnReloadHistory);
        cardHistory.add(bottom3, BorderLayout.SOUTH);

        btnReloadHistory.addActionListener(e -> loadHistory());
        btnCetak.addActionListener(e -> cetakTiketHTML());

        // ==== Add to content ====
        panelContent.add(cardConcerts, "concerts");
        panelContent.add(cardOrder, "order");
        panelContent.add(cardHistory, "history");

        CardLayout cl = (CardLayout) panelContent.getLayout();
        btnConcerts.addActionListener(e -> cl.show(panelContent, "concerts"));
        btnOrder.addActionListener(e -> cl.show(panelContent, "order"));
        btnHistory.addActionListener(e -> cl.show(panelContent, "history"));
        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) dispose();
        });

        // ==== Load data ====
        loadConcerts();
        loadConcertOptions();
        loadHistory();

        setVisible(true);
    }

    // ==== UI Helpers ====
    private JPanel createCardPanel() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        return p;
    }

    private JLabel createGradientHeader(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        lbl.setForeground(new Color(230, 240, 255));
        lbl.setPreferredSize(new Dimension(0, 70));
        return lbl;
    }

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
            public void mouseEntered(java.awt.event.MouseEvent evt) { b.setBackground(new Color(55, 110, 200)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { b.setBackground(new Color(35, 70, 150)); }
        });
        return b;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JScrollPane createRoundedScrollPane(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    private void styleTable(JTable t) {
        t.setRowHeight(30);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setSelectionBackground(new Color(30, 70, 180));
        t.setSelectionForeground(Color.WHITE);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(20, 40, 90));
        h.setForeground(Color.WHITE);
    }

    // ==== Cetak Tiket ke HTML ====
    private void cetakTiketHTML() {
        int row = tblHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu pesanan untuk dicetak!");
            return;
        }

        String id = modelHistory.getValueAt(row, 0).toString();
        String konser = modelHistory.getValueAt(row, 1).toString();
        String tanggal = modelHistory.getValueAt(row, 2).toString();
        String jumlah = modelHistory.getValueAt(row, 3).toString();
        String total = modelHistory.getValueAt(row, 4).toString();

        try {
            String namaFile = "Tiket_" + id + ".html";
            java.io.PrintWriter writer = new java.io.PrintWriter(namaFile);

            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><meta charset='UTF-8'>");
            sb.append("<title>FestTrack Ticket</title>");
            sb.append("<style>");
            sb.append("body { font-family: 'Segoe UI', sans-serif; background: #eef3ff; color: #222; }");
            sb.append(".ticket { width: 520px; margin: 50px auto; padding: 30px;");
            sb.append("background: white; border-radius: 20px;");
            sb.append("box-shadow: 0 6px 16px rgba(0,0,0,0.2); }");
            sb.append("h1 { text-align: center; color: #1e40af; }");
            sb.append(".info { margin-top: 20px; font-size: 15px; }");
            sb.append(".info div { margin: 8px 0; }");
            sb.append(".footer { margin-top: 30px; text-align: center; color: #555; font-size: 13px; }");
            sb.append("</style></head><body>");
            sb.append("<div class='ticket'>");
            sb.append("<h1>🎟 FESTTRACK TICKET</h1>");
            sb.append("<div class='info'>");
            sb.append("<div><b>ID Pesanan:</b> ").append(id).append("</div>");
            sb.append("<div><b>Nama Konser:</b> ").append(konser).append("</div>");
            sb.append("<div><b>Tanggal Pesan:</b> ").append(tanggal).append("</div>");
            sb.append("<div><b>Jumlah Tiket:</b> ").append(jumlah).append("</div>");
            sb.append("<div><b>Total Bayar:</b> Rp ").append(total).append("</div>");
            sb.append("</div>");
            sb.append("<div class='footer'>Terima kasih telah menggunakan FestTrack!<br>");
            sb.append("Simpan tiket ini sebagai bukti pemesanan.</div>");
            sb.append("</div></body></html>");

            writer.print(sb.toString());
            writer.close();

            JOptionPane.showMessageDialog(this, "Tiket berhasil dicetak!\nFile: " + namaFile);
            java.awt.Desktop.getDesktop().browse(new java.io.File(namaFile).toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak tiket: " + e.getMessage());
        }
    }

    // ==== Database ====
    private void loadConcerts() {
        modelConcerts.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM concerts")) {
            while (r.next()) {
                modelConcerts.addRow(new Object[]{
                        r.getInt("id"), r.getString("name"), r.getString("date"), r.getString("location"),
                        r.getDouble("vip_price"), r.getDouble("regular_price"), r.getInt("quota"), r.getString("guest_star")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load konser: " + e.getMessage());
        }
    }

    private void loadConcertOptions() {
        cmbConcert.removeAllItems();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT id, name FROM concerts")) {
            while (r.next()) cmbConcert.addItem(r.getInt("id") + " - " + r.getString("name"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load pilihan konser: " + e.getMessage());
        }
    }

    private void buatPesanan() {
        if (cmbConcert.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih konser terlebih dahulu!");
            return;
        }
        String s = cmbConcert.getSelectedItem().toString();
        int concertId = Integer.parseInt(s.split(" - ")[0]);
        int jumlah = (int) spnJumlah.getValue();
        String cat = ticketCategory.getSelectedItem().toString();

        try (Connection c = DBConnection.getConnection()) {
            double harga = 0;
            int kuota = 0;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT vip_price, regular_price, quota FROM concerts WHERE id=?")) {
                ps.setInt(1, concertId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    harga = cat.equals("VIP") ? rs.getDouble("vip_price") : rs.getDouble("regular_price");
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

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO orders(user_id, concert_id, order_date, quantity, total_price) " +
                            "VALUES (?,?,CURRENT_DATE,?,?)")) {
                ps.setInt(1, userId);
                ps.setInt(2, concertId);
                ps.setInt(3, jumlah);
                ps.setDouble(4, total);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE concerts SET quota = quota - ? WHERE id=?")) {
                ps.setInt(1, jumlah);
                ps.setInt(2, concertId);
                ps.executeUpdate();
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
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT o.id, c.name, o.order_date, o.quantity, o.total_price " +
                             "FROM orders o JOIN concerts c ON o.concert_id=c.id WHERE o.user_id=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelHistory.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("order_date"), rs.getInt("quantity"), rs.getDouble("total_price")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load riwayat: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserFrame(1, "tester"));
    }
}