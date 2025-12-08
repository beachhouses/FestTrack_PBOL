import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class ConcertCatalog extends JPanel {
    private UserFrame parentFrame;
    private JPanel gridPanel;

    public ConcertCatalog(UserFrame parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Header
        JLabel lblTitle = new JLabel("Katalog Konser", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Grid Panel for Cards
        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columns
        gridPanel.setOpaque(false);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh Button
        JButton btnRefresh = new JButton("Refresh Katalog");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(255, 255, 255, 200));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadConcerts());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        loadConcerts();
    }

    public void loadConcerts() {
        gridPanel.removeAll();
        
        String sql = "SELECT * FROM concerts";
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {

            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("name");
                String date = r.getString("date");
                double price = r.getDouble("regular_price");

                String imagePath = null;
                try {
                    imagePath = r.getString("image_path");
                } catch (SQLException ex) {
                }
                
                gridPanel.add(createConcertCard(id, name, date, price, imagePath));
            }
            
            gridPanel.revalidate();
            gridPanel.repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat katalog: " + e.getMessage());
        }
    }

    private JPanel createConcertCard(int id, String name, String date, double price, String imagePath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(220, 420)); // Increased height
        card.setBackground(new Color(255, 255, 255, 240));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Poster Image
        JLabel posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(200, 250)); // Taller for portrait posters
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setBackground(new Color(230, 240, 255));
        posterLabel.setOpaque(true);

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                // Scale image to cover/fit
                Image scaled = img.getScaledInstance(200, 250, Image.SCALE_SMOOTH);
                posterLabel.setIcon(new ImageIcon(scaled));
                posterLabel.setText("");
            } catch (Exception e) {
                posterLabel.setText("No Image");
            }
        } else {
            // Draw default placeholder if no image
            posterLabel.setText("POSTER");
            posterLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            posterLabel.setForeground(Color.GRAY);
        }
        
        card.add(posterLabel, BorderLayout.NORTH);

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblName = new JLabel("<html><center>" + name + "</center></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDate = new JLabel(date);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDate.setForeground(Color.GRAY);
        lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrice = new JLabel("Mulai Rp " + formatRupiah(price));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrice.setForeground(new Color(0, 100, 0));
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblDate);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblPrice);
        
        card.add(infoPanel, BorderLayout.CENTER);

        // Button
        JButton btnOrder = new JButton("Pesan Tiket");
        btnOrder.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOrder.setBackground(new Color(30, 100, 200));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setFocusPainted(false);
        btnOrder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOrder.addActionListener(e -> parentFrame.openOrderPage(id));
        
        card.add(btnOrder, BorderLayout.SOUTH);

        return card;
    }

    private String formatRupiah(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace("Rp", "").trim();
    }
}
