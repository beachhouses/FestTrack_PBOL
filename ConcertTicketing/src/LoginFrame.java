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
        setTitle("FestTrack | Login");
        setSize(460, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ========== BACKGROUND GRADIENT ==========
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 20, 60),
                        getWidth(), getHeight(), new Color(0, 90, 200)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new BorderLayout(0, 10));
        add(bgPanel);

        // ========== HEADER ==========
        JLabel lblTitle = new JLabel("FestTrack – Login", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(230, 235, 255));
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        bgPanel.add(lblTitle, BorderLayout.NORTH);

        // ========== FORM CARD ==========
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bgPanel.add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(Color.WHITE);
        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(Color.WHITE);
        JLabel lblRole = new JLabel("Login sebagai");
        lblRole.setForeground(Color.WHITE);

        txtUsername = createInputField();
        txtPassword = createPasswordField();
        cmbRole = new JComboBox<>(new String[]{"User", "Admin"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbRole.setPreferredSize(new Dimension(230, 32));

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblUser, gbc);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblPass, gbc);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblRole, gbc);
        gbc.gridx = 1;
        formPanel.add(cmbRole, gbc);

        // ========== BUTTON BAR ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setOpaque(false);

        btnLogin = createBlueButton("Login");
        btnRegister = createWhiteButton("Daftar");

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        bgPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ========== ACTIONS ==========
        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> doRegister());
    }

    // ====== STYLE HELPERS ======
    private JTextField createInputField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(230, 32));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(new Color(20, 30, 60));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 170, 230), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JPasswordField createPasswordField() {
        JPasswordField p = new JPasswordField();
        p.setPreferredSize(new Dimension(230, 32));
        p.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.setForeground(new Color(20, 30, 60));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 170, 230), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return p;
    }

    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(110, 34));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(40, 100, 220));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(20, 70, 160), 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createWhiteButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(110, 34));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(240, 245, 255));
        btn.setForeground(new Color(20, 60, 160));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(40, 90, 200), 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ====== LOGIN ======
    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String selectedRole = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi");
            return;
        }

        String sql = "SELECT id, username, role FROM users " +
                     "WHERE username=? AND password=? AND role=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, selectedRole);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId   = rs.getInt("id");
                String uname = rs.getString("username");
                String role  = rs.getString("role");

                JOptionPane.showMessageDialog(this,
                        "Login berhasil. Selamat datang, " + uname + " (" + role + ")");

                if ("Admin".equalsIgnoreCase(role)) {
                    new AdminFrame(userId, uname).setVisible(true);
                } else {
                    new UserFrame(userId, uname).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username / password / role tidak cocok");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error koneksi DB: " + ex.getMessage());
        }
    }

    // ====== REGISTER (PAKAI FIELD YANG SAMA) ======
    private void doRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role     = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi");
            return;
        }

        String checkSql  = "SELECT id FROM users WHERE username=?";
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
                JOptionPane.showMessageDialog(this,
                        "Registrasi berhasil sebagai " + role + ". Silakan login.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error koneksi DB: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}