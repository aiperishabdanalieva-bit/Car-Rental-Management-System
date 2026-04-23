package gui;

import javax.swing.*;
import java.awt.*;
import model.*;
import service.Authorization;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("\uD83D\uDE97 Car Rental System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 52, 54));

        JLabel titleLabel = new JLabel("CAR RENTAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(241, 196, 15));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        JLabel userIcon = new JLabel("\uD83D\uDC64");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userIcon, gbc);
        gbc.gridx = 1;
        usernameField = createStyledTextField();
        formPanel.add(usernameField, gbc);

        JLabel passIcon = new JLabel("\uD83D\uDD12");
        passIcon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passIcon, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton loginBtn = createStyledButton("Login", new Color(46, 204, 113));
        JButton registerBtn = createStyledButton("Register", new Color(52, 152, 219));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());

        add(mainPanel);
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });
        return button;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = Authorization.login(username, password);
        if (user != null) {
            dispose();
            if (user.getRole().equalsIgnoreCase("CUSTOMER")) {
                new CustomerFrame(user).setVisible(true);
            } else {
                new AdminFrame(user).setVisible(true);
            }
        } else {
            showMessage("Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean success = Authorization.register(username, password, "CUSTOMER");
        if (success) {
            showMessage("Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage("Registration failed. Username may be taken.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }
}
