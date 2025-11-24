import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginFrame() {

        setTitle("Login - Aplikasi Tiket Konser");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // PANEL BACKGROUND
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 20, 60),
                        getWidth(), getHeight(), new Color(0, 60, 160)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel);

        // TITLE
        JLabel lblTitle = new JLabel("Login Akun", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(210, 220, 255));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        bgPanel.add(lblTitle, BorderLayout.NORTH);

        // PANEL FORM
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bgPanel.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // LABEL
        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(new Color(220, 230, 255));
        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(new Color(220, 230, 255));
        JLabel lblRole = new JLabel("Login sebagai");
        lblRole.setForeground(new Color(220, 230, 255));

        // USERNAME
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblUser, gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        styleInput(txtUsername);
        panel.add(txtUsername, gbc);

        // PASSWORD
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblPass, gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        styleInput(txtPassword);
        panel.add(txtPassword, gbc);

        // ROLE COMBOBOX
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblRole, gbc);
        gbc.gridx = 1;
        cmbRole = new JComboBox<>(new String[]{"User", "Admin"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbRole, gbc);

        // BUTTON PANEL
        JPanel panelButton = new JPanel();
        panelButton.setOpaque(false);
        btnLogin = createBlueButton("Login");
        btnRegister = createWhiteButton("Daftar");
        panelButton.add(btnLogin);
        panelButton.add(btnRegister);
        bgPanel.add(panelButton, BorderLayout.SOUTH);

        // ACTIONS
        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> doRegister());
    }

    // STYLE HELPER METHODS
    private void styleInput(JTextField field) {
        field.setBackground(new Color(255, 255, 255, 230));
        field.setForeground(new Color(20, 30, 60));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 140, 220), 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(100, 32));
        btn.setBackground(new Color(30, 90, 200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(20, 70, 160), 2));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(50, 120, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(30, 90, 200));
            }
        });
        return btn;
    }

    private JButton createWhiteButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(100, 32));
        btn.setBackground(new Color(240, 245, 255));
        btn.setForeground(new Color(20, 60, 160));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(40, 90, 200), 2));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(220, 230, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(240, 245, 255));
            }
        });
        return btn;
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String selectedRole = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi");
            return;
        }

        String sql = "SELECT id, username, role FROM users WHERE username=? AND password=? AND role=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, selectedRole);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String uname = rs.getString("username");
                String role = rs.getString("role");

                JOptionPane.showMessageDialog(this,
                        "Login berhasil. Selamat datang, " + uname + " (" + role + ")");

                if ("Admin".equalsIgnoreCase(role)) {
                    AdminFrame admin = new AdminFrame(userId, uname);
                    admin.setVisible(true);
                } else {
                    UserFrame main = new UserFrame(userId, uname);
                    main.setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username / password / role tidak cocok");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error koneksi DB: " + ex.getMessage());
        }
    }

    private void doRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi");
            return;
        }

        String checkSql = "SELECT id FROM users WHERE username=?";
        String insertSql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setString(1, username);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username sudah terdaftar");
                return;
            }

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setString(1, username);
                insertPs.setString(2, password);
                insertPs.setString(3, role);
                insertPs.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registrasi berhasil sebagai " + role + ", silakan login");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error koneksi DB: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}