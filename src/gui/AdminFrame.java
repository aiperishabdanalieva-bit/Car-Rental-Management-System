package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import database.DatabaseHelper;
import model.*;
import gui.*;
import service.*;
class AdminFrame extends JFrame {
    private User user;
    private JTable allBookingsTable, carsTable, usersTable;
    private DefaultTableModel allBookingsModel, carsModel, usersModel;
    public AdminFrame(User user) {
        this.user = user;
        setTitle("\uD83D\uDD12 Admin - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel(new BorderLayout());


        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(new Color(236, 240, 241));

        tabs.addTab("All Bookings", createAllBookingsPanel());
        tabs.addTab("Cars", createCarsPanel());
        tabs.addTab("Customers", createCustomersPanel());
        tabs.addTab("Admin Actions", createAdminActionsPanel());
        tabs.addTab("Income", createIncomePanel());
        tabs.addTab("Export Data", createExportPanel());
        tabs.addTab("Import Cars", createImportPanel());


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(236, 240, 241));
        JButton logoutBtn = createLogoutButton();
        bottomPanel.add(logoutBtn);

        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }


    private JButton createLogoutButton() {
        JButton btn = new JButton("Logout");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(192, 57, 43));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        return btn;
    }


    private JPanel createAllBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        allBookingsModel = new DefaultTableModel(new String[]{"ID", "User ID", "Car ID", "Start", "End", "Total ($)"}, 0);
        allBookingsTable = new JTable(allBookingsModel);
        styleTable(allBookingsTable);
        refreshAllBookings();
        panel.add(new JScrollPane(allBookingsTable), BorderLayout.CENTER);
        return panel;
    }


    private JPanel createCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        carsModel = new DefaultTableModel(new String[]{"ID", "Model", "Price/Day ($)", "Available"}, 0);
        carsTable = new JTable(carsModel);
        styleTable(carsTable);
        refreshCars();

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(236, 240, 241));

        JButton toggleBtn = createStyledButton("Toggle Availability", new Color(243, 156, 18));
        toggleBtn.addActionListener(e -> toggleCarStatus());

        JButton addCarBtn = createStyledButton("Add New Car", new Color(46, 204, 113));
        addCarBtn.addActionListener(e -> addNewCar());

        JButton changePriceBtn = createStyledButton("Change Price", new Color(52, 152, 219));
        changePriceBtn.addActionListener(e -> changeCarPrice());

        btnPanel.add(toggleBtn);
        btnPanel.add(addCarBtn);
        btnPanel.add(changePriceBtn);

        panel.add(new JScrollPane(carsTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }


    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        usersModel = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0);
        usersTable = new JTable(usersModel);
        styleTable(usersTable);
        refreshCustomers();
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        return panel;
    }


    private JPanel createAdminActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton addAdminBtn = createStyledButton("Add New Admin (master password)", new Color(142, 68, 173));
        addAdminBtn.addActionListener(e -> addAdmin());
        JButton showUsersBtn = createStyledButton("Show All Users & Passwords (master password)", new Color(231, 76, 60));
        showUsersBtn.addActionListener(e -> showAllUsersWithPasswords());

        panel.add(addAdminBtn);
        panel.add(showUsersBtn);
        return panel;
    }


    private JPanel createIncomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        double income = BookingService.getTotalIncome();
        JLabel label = new JLabel("Total System Revenue: $" + String.format("%.2f", income), SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(39, 174, 96));
        label.setBorder(BorderFactory.createEmptyBorder(50, 10, 50, 10));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }


    private JPanel createExportPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton exportUsersBtn = createStyledButton("Export Users", new Color(52, 152, 219));
        exportUsersBtn.addActionListener(e -> exportData("users"));
        JButton exportCarsBtn = createStyledButton("Export Cars", new Color(52, 152, 219));
        exportCarsBtn.addActionListener(e -> exportData("cars"));
        JButton exportBookingsBtn = createStyledButton("Export Bookings", new Color(52, 152, 219));
        exportBookingsBtn.addActionListener(e -> exportData("bookings"));

        panel.add(exportUsersBtn);
        panel.add(exportCarsBtn);
        panel.add(exportBookingsBtn);
        return panel;
    }


    private JPanel createImportPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton importCsvBtn = createStyledButton("Import Cars from CSV", new Color(230, 126, 34));
        importCsvBtn.addActionListener(e -> importCars(true));
        JButton importJsonBtn = createStyledButton("Import Cars from JSON", new Color(230, 126, 34));
        importJsonBtn.addActionListener(e -> importCars(false));

        panel.add(importCsvBtn);
        panel.add(importJsonBtn);
        return panel;
    }


    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(41, 128, 185));
        table.setSelectionForeground(Color.WHITE);
    }


    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
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


    private void refreshAllBookings() {
        allBookingsModel.setRowCount(0);
        for (Booking b : BookingService.getAllBookings()) {
            allBookingsModel.addRow(new Object[]{b.getId(), b.getUserId(), b.getCarId(), b.getStartDate(), b.getEndDate(), b.getTotalPrice()});
        }
    }

    private void refreshCars() {
        carsModel.setRowCount(0);
        for (Car c : CarService.getAll()) {
            carsModel.addRow(new Object[]{c.getId(), c.getModel(), c.getPricePerDay(), c.isAvailable() ? "✓ Yes" : "✗ No"});
        }
    }

    private void refreshCustomers() {
        usersModel.setRowCount(0);
        String sql = "SELECT id, username, role FROM users WHERE role = 'CUSTOMER'";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usersModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void toggleCarStatus() {
        int row = carsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int carId = (int) carsModel.getValueAt(row, 0);
        CarService.toggleStatus(carId);
        refreshCars();
    }

    private void addNewCar() {
        JTextField modelF = new JTextField();
        JTextField priceF = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Model:"));
        panel.add(modelF);
        panel.add(new JLabel("Price per day ($):"));
        panel.add(priceF);

        int res = JOptionPane.showConfirmDialog(this, panel, "Add New Car", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String model = modelF.getText().trim();
                double price = Double.parseDouble(priceF.getText().trim());
                if (model.isEmpty()) throw new IllegalArgumentException();
                CarService.addCar(model, price);
                refreshCars();
                JOptionPane.showMessageDialog(this, "Car added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check model and price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeCarPrice() {
        int row = carsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int carId = (int) carsModel.getValueAt(row, 0);
        String currentModel = (String) carsModel.getValueAt(row, 1);
        double currentPrice = (double) carsModel.getValueAt(row, 2);

        String newPriceStr = JOptionPane.showInputDialog(this,
                "Current price of " + currentModel + ": $" + currentPrice +
                        "\nEnter new price per day:",
                "Change Price",
                JOptionPane.QUESTION_MESSAGE);

        if (newPriceStr == null || newPriceStr.trim().isEmpty()) {
            return;
        }

        try {
            double newPrice = Double.parseDouble(newPriceStr.trim());
            if (newPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be a positive number.", "Invalid Price", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "UPDATE cars SET price_per_day = ? WHERE id = ?";
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, newPrice);
                pstmt.setInt(2, carId);
                pstmt.executeUpdate();
                refreshCars();
                JOptionPane.showMessageDialog(this, "Price updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void addAdmin() {
        String master = JOptionPane.showInputDialog(this, "Enter master password:");
        if (master == null || !master.equals("adminpro")) {
            JOptionPane.showMessageDialog(this, "Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTextField userF = new JTextField();
        JPasswordField passF = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("New admin username:"));
        panel.add(userF);
        panel.add(new JLabel("Password:"));
        panel.add(passF);

        int res = JOptionPane.showConfirmDialog(this, panel, "Add Admin", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String username = userF.getText().trim();
            String password = new String(passF.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = Authorization.register(username, password, "ADMIN");
            if (ok) JOptionPane.showMessageDialog(this, "Admin registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Registration failed. Username might be taken.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAllUsersWithPasswords() {
        String master = JOptionPane.showInputDialog(this, "Enter master password:");
        if (master == null || !master.equals("adminpro")) {
            JOptionPane.showMessageDialog(this, "Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "SELECT id, username, password, role FROM users";
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sb.append(String.format("ID: %-3d | User: %-10s | Pass: %-10s | Role: %s\n",
                        rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role")));
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            textArea.setEditable(false);
            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(this, scroll, "All Users & Passwords", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportData(String type) {
        String[] formats = {"CSV", "JSON"};
        int formatChoice = JOptionPane.showOptionDialog(this, "Choose format", "Export",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, formats, formats[0]);
        if (formatChoice == -1) return;
        boolean isCsv = (formatChoice == 0);

        String filename = JOptionPane.showInputDialog(this, "Enter filename (e.g. data.csv):");
        if (filename == null || filename.trim().isEmpty()) return;
        filename = filename.trim();

        try {
            switch (type) {
                case "users":
                    ExportImportService.exportUsers(isCsv, filename);
                    break;
                case "cars":
                    ExportImportService.exportCars(CarService.getAll(), isCsv, filename);
                    break;
                case "bookings":
                    ExportImportService.exportBookings(BookingService.getAllBookings(), isCsv, filename);
                    break;
            }
            JOptionPane.showMessageDialog(this, "Exported to " + filename, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void importCars(boolean isCsv) {
        String filename = JOptionPane.showInputDialog(this, "Enter filename (e.g. cars.csv):");
        if (filename == null || filename.trim().isEmpty()) return;
        ExportImportService.importCars(isCsv, filename.trim());
        refreshCars();
    }
}
