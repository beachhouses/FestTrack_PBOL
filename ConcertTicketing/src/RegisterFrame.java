import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnRegister, btnBack;

    public RegisterFrame() {
        setTitle("FestTrack | Register");
        setSize(480, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // === Background ===
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(25, 70, 130),
                        getWidth(), getHeight(), new Color(100, 160, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new BorderLayout(0, 10));
        add(bgPanel);

        // === Header ===
        JLabel lblTitle = new JLabel("Buat Akun FestTrack", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        bgPanel.add(lblTitle, BorderLayout.NORTH);

        // === Form ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        bgPanel.add(formPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username");
        JLabel lblPass = new JLabel("Password");
        JLabel lblRole = new JLabel("Daftar sebagai");
        for (JLabel l : new JLabel[]{lblUser, lblPass, lblRole}) l.setForeground(Color.WHITE);

        txtUsername = createInputField();
        txtPassword = createPasswordField();
        cmbRole = new JComboBox<>(new String[]{"User", "Admin"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbRole.setPreferredSize(new Dimension(230, 32));

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblUser, gbc);
        gbc.gridx = 1; formPanel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblPass, gbc);
        gbc.gridx = 1; formPanel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblRole, gbc);
        gbc.gridx = 1; formPanel.add(cmbRole, gbc);

        // === Buttons ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        btnRegister = createBlueButton("Daftar");
        btnBack = createWhiteButton("Kembali");
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);
        bgPanel.add(buttonPanel, BorderLayout.SOUTH);

        // === Actions ===
        btnRegister.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private JTextField createInputField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(230, 32));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 180, 250), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JPasswordField createPasswordField() {
        JPasswordField p = new JPasswordField();
        p.setPreferredSize(new Dimension(230, 32));
        p.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 180, 250), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return p;
    }

    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 36));
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
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(245, 250, 255));
        btn.setForeground(new Color(20, 60, 160));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(40, 90, 200), 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // === Register Logic ===
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
                JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login.");
                dispose();
                new LoginFrame().setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error koneksi DB: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}