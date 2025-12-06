import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("FestTrack | Login");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        // === Left Panel (Gradient & Logo) ===
        JPanel leftPanel = new JPanel() {
            private float animTime = 0;
            {
                new Timer(30, e -> {
                    animTime += 1;
                    repaint();
                }).start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getHeight() == 0) return;

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Base Gradient
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 35, 95),
                        getWidth(), getHeight(), new Color(0, 110, 240)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                int w = getWidth();
                int h = getHeight();
                
                // Decorative Shapes (Motifs)
                g2.setColor(new Color(255, 255, 255, 50));
                drawBubble(g2, -80, -80, 250, 0.5f, w, h);
                drawBubble(g2, w - 150, h - 150, 300, 0.7f, w, h);
                
                g2.setColor(new Color(255, 255, 255, 40));
                drawBubble(g2, w / 2 + 50, 50, 150, 1.0f, w, h);
                drawBubble(g2, 50, h / 2, 100, 0.8f, w, h);
                
                // Subtle diagonal lines
                g2.setColor(new Color(255, 255, 255, 30));
                for (int i = 0; i < h + w; i += 40) {
                    g2.drawLine(0, i, i, 0);
                }
            }

            private void drawBubble(Graphics2D g2, int x, int startY, int size, float speed, int w, int h) {
                double range = h + size;
                double effectiveY = (startY - animTime * speed);
                double wrappedY = ((effectiveY + size) % range + range) % range - size;
                g2.fillOval(x, (int)wrappedY, size, size);
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0; gbcLeft.gridy = 0;
        
        JLabel lblLogo = new JLabel("FestTrack");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lblLogo.setForeground(Color.WHITE);
        leftPanel.add(lblLogo, gbcLeft);
        
        gbcLeft.gridy = 1;
        gbcLeft.insets = new Insets(10, 0, 0, 0);
        JLabel lblTagline = new JLabel("Your Fast Track to Every Fest");
        lblTagline.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTagline.setForeground(new Color(200, 220, 255));
        leftPanel.add(lblTagline, gbcLeft);

        // === Right Panel (Form) ===
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(235, 245, 255), // Light Blue
                        getWidth(), getHeight(), new Color(245, 245, 245) // Light Gray
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        // rightPanel.setBackground(Color.WHITE); // Removed as we paint it now

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Header
        JLabel lblWelcome = new JLabel("Hai, Selamat Datang Kembali");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(30, 30, 30));

        // Register Link
        JLabel lblRegisterLink = new JLabel("<html>Belum punya akun? <font color='#0066CC'>Daftar</font></html>");
        lblRegisterLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRegisterLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegisterLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });

        // Inputs
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUser.setForeground(new Color(60, 60, 60));
        txtUsername = createInputField();

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPass.setForeground(new Color(60, 60, 60));
        txtPassword = createPasswordField();

        // Button
        btnLogin = createBlueButton("Masuk");
        btnLogin.addActionListener(e -> doLogin());

        // Layout Assembly
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 5, 20);
        rightPanel.add(lblWelcome, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 30, 20);
        rightPanel.add(lblRegisterLink, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 5, 20);
        rightPanel.add(lblUser, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 20, 15, 20);
        rightPanel.add(txtUsername, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 5, 20);
        rightPanel.add(lblPass, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 20, 25, 20);
        rightPanel.add(txtPassword, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 20, 0, 20);
        rightPanel.add(btnLogin, gbc);

        add(leftPanel);
        add(rightPanel);
    }

    // === UI Helpers ===
    private JTextField createInputField() {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setPreferredSize(new Dimension(260, 38));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(225, 240, 255)); // More colorful background
        f.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.AbstractBorder() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(180, 200, 230));
                        g2.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
                        g2.dispose();
                    }
                    @Override
                    public Insets getBorderInsets(Component c) { return new Insets(0, 0, 0, 0); }
                },
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return f;
    }

    private JPasswordField createPasswordField() {
        JPasswordField p = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(260, 38));
        p.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.setBackground(new Color(225, 240, 255)); // More colorful background
        p.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.AbstractBorder() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(180, 200, 230));
                        g2.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
                        g2.dispose();
                    }
                    @Override
                    public Insets getBorderInsets(Component c) { return new Insets(0, 0, 0, 0); }
                },
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return p;
    }

    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 80, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 130, 255));
                } else {
                    g2.setColor(new Color(0, 100, 230));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(260, 38));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // === Login Logic ===
    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi");
            return;
        }

        String sql = "SELECT id, username, role FROM users WHERE username=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String uname = rs.getString("username");
                String role = rs.getString("role");

                if ("Admin".equalsIgnoreCase(role)) new AdminFrame(userId, uname).setVisible(true);
                else new UserFrame(userId, uname).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error koneksi DB: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}