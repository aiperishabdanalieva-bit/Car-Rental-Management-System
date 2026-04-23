package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import model.*;
import service.*;
class CustomerFrame extends JFrame {
    private User user;
    private JTable carsTable, bookingsTable;
    private DefaultTableModel carsModel, bookingsModel;

    public CustomerFrame(User user) {
        this.user = user;
        setTitle("\uD83D\uDE97 Customer - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());


        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(new Color(236, 240, 241));
        tabs.addTab("Available Cars", createCarsPanel());
        tabs.addTab("My Bookings", createBookingsPanel());

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

    private JPanel createCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        carsModel = new DefaultTableModel(new String[]{"ID", "Model", "Price/Day ($)", "Available"}, 0);
        carsTable = new JTable(carsModel);
        styleTable(carsTable);
        refreshCarsTable();

        JScrollPane scroll = new JScrollPane(carsTable);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        JButton bookBtn = createStyledButton("Book Selected Car", new Color(46, 204, 113));
        bookBtn.addActionListener(e -> bookCar());
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(236, 240, 241));
        bottom.add(bookBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        bookingsModel = new DefaultTableModel(new String[]{"Booking ID", "Car ID", "Start Date", "End Date", "Total ($)"}, 0);
        bookingsTable = new JTable(bookingsModel);
        styleTable(bookingsTable);
        refreshBookingsTable();

        JScrollPane scroll = new JScrollPane(bookingsTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(236, 240, 241));
        JButton cancelBtn = createStyledButton("Cancel Selected Booking", new Color(231, 76, 60));
        cancelBtn.addActionListener(e -> cancelBooking());
        JButton exportBtn = createStyledButton("Export History", new Color(52, 152, 219));
        exportBtn.addActionListener(e -> exportHistory());
        btnPanel.add(cancelBtn);
        btnPanel.add(exportBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(41, 128, 185));
        table.setSelectionForeground(Color.WHITE);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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

    private void refreshCarsTable() {
        carsModel.setRowCount(0);
        for (Car c : CarService.getAll()) {
            carsModel.addRow(new Object[]{c.getId(), c.getModel(), c.getPricePerDay(), c.isAvailable() ? "✓ Yes" : "✗ No"});
        }
    }

    private void refreshBookingsTable() {
        bookingsModel.setRowCount(0);
        for (Booking b : BookingService.getBookingsByUserId(user.getId())) {
            bookingsModel.addRow(new Object[]{b.getId(), b.getCarId(), b.getStartDate(), b.getEndDate(), b.getTotalPrice()});
        }
    }

    private void bookCar() {
        int row = carsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int carId = (int) carsModel.getValueAt(row, 0);
        Car car = CarService.getCarById(carId);
        if (car == null || !car.isAvailable()) {
            JOptionPane.showMessageDialog(this, "Car is currently marked as unavailable.", "Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField startField = new JTextField(10);
        JTextField endField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Rental Dates", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());
            Booking temp = new Booking(0, user.getId(), carId, start, end, car.getPricePerDay());
            if (!temp.isValidDates()) {
                JOptionPane.showMessageDialog(this, "End date must be after start date.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Booking> carBookings = BookingService.getBookingsByCarId(carId);
            List<Booking> conflictingBookings;
            conflictingBookings = new ArrayList<>();
            for (Booking b : carBookings) {
                if (b.isOverlapping(start, end)) {
                    conflictingBookings.add(b);
                }
            }

            if (!conflictingBookings.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Car is already booked for the selected dates.\n");
                sb.append("Conflicting bookings:\n");
                for (Booking b : conflictingBookings) {
                    sb.append(" • Booking #").append(b.getId())
                            .append(": ").append(b.getStartDate())
                            .append(" → ").append(b.getEndDate()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Overlap Detected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = temp.getTotalPrice();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Model: " + car.getModel() + "\nTotal Price: $" + total + "\nConfirm booking?",
                    "Confirm Booking", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                BookingService.addBooking(user.getId(), carId, start, end, total);
                JOptionPane.showMessageDialog(this, "Booking successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCarsTable();
                refreshBookingsTable();
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        int row = bookingsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookingId = (int) bookingsModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel booking #" + bookingId + "?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = BookingService.cancelBooking(bookingId, user.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Booking cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshBookingsTable();
                refreshCarsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Cancellation failed. Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportHistory() {
        List<Booking> bookings = BookingService.getBookingsByUserId(user.getId());
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] options = {"CSV", "JSON"};
        int choice = JOptionPane.showOptionDialog(this, "Choose export format", "Export",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == -1) return;
        boolean isCsv = (choice == 0);
        String filename = "my_history_" + user.getUsername() + (isCsv ? ".csv" : ".json");
        ExportImportService.exportBookings(bookings, isCsv, filename);
        JOptionPane.showMessageDialog(this, "Exported to " + filename);
    }
}
