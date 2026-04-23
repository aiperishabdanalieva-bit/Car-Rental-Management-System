package service;

import database.DatabaseHelper;
import model.User;
import model.Car;
import model.Booking;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import java.util.Scanner;
public class MenuService {
    public static void CustomerMenu(User user, Scanner sc) {
        boolean staying = true;

        while (staying) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║              CUSTOMER MENU               ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println(" User: " + user.getUsername() + " | Role: " + user.getRole());
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println(" [1] View available cars");
            System.out.println(" [2] Book a car");
            System.out.println(" [3] View my bookings");
            System.out.println(" [4] Cancel a booking");
            System.out.println(" [5] Export my booking history");
            System.out.println(" [0] Logout");
            System.out.println("────────────────────────────────────────────");
            System.out.print(" Select an option > ");

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1:
                        System.out.println("\n--- [ Available Cars List ] ---");
                        CarService.show();
                        break;
                    case 2:
                        System.out.print("\n>>> Enter car ID for rent: ");
                        int carId = Integer.parseInt(sc.nextLine());
                        Car selectedCar = CarService.getCarById(carId);

                        if (selectedCar != null && selectedCar.isAvailable()) {
                            boolean validDates = false;
                            LocalDate startDate = null;
                            LocalDate endDate = null;
                            Booking tempBooking = null;

                            while (!validDates) {
                                try {
                                    System.out.print(">>> Enter start date (YYYY-MM-DD): ");
                                    startDate = LocalDate.parse(sc.nextLine());
                                    System.out.print(">>> Enter end date (YYYY-MM-DD): ");
                                    endDate = LocalDate.parse(sc.nextLine());
                                    tempBooking = new Booking(0, user.getId(), carId, startDate, endDate, selectedCar.getPricePerDay());
                                    validDates = tempBooking.isValidDates();
                                } catch (Exception e) {
                                    System.out.println("! Error: Wrong date format! Use YYYY-MM-DD.");
                                }
                            }

                            List<Booking> carBookings = BookingService.getBookingsByCarId(carId);
                            boolean datesTaken = false;
                            for (Booking b : carBookings) {
                                if (b.isOverlapping(startDate, endDate)) {
                                    System.out.println("! Sorry, the car with ID " + carId + " is already booked for these dates.");
                                    datesTaken = true;
                                    break;
                                }
                            }

                            if (!datesTaken) {
                                System.out.println("\n------------------------------------");
                                System.out.println(" TOTAL PRICE: $" + tempBooking.getTotalPrice());
                                System.out.println("------------------------------------");
                                System.out.print(">>> Make a reservation? (yes/no): ");
                                if (sc.nextLine().equalsIgnoreCase("YES")) {
                                    BookingService.addBooking(user.getId(), carId, startDate, endDate, tempBooking.getTotalPrice());
                                    System.out.println("✔ Success! Reservation confirmed.");
                                }
                            }
                        } else {
                            System.out.println("! This car is currently unavailable.");
                        }
                        break;
                    case 3:
                        System.out.println("\n--- [ My Booking History ] ---");
                        List<Booking> userBookings = BookingService.getBookingsByUserId(user.getId());
                        if (userBookings.isEmpty()) System.out.println("No bookings found.");
                        for (Booking b : userBookings) {
                            System.out.println(" • " + b);
                        }
                        break;
                    case 4:
                        System.out.println("\n--- [ Cancel a Booking ] ---");
                        List<Booking> toCancel = BookingService.getBookingsByUserId(user.getId());
                        if (toCancel.isEmpty()) {
                            System.out.println("You have no active bookings to cancel.");
                        } else {
                            for (Booking b : toCancel) {
                                System.out.println(" • " + b);
                            }
                            System.out.print("\n>>> Enter Booking ID to cancel (or 0 to go back): ");
                            int bId = Integer.parseInt(sc.nextLine());
                            if (bId != 0) {
                                boolean success = BookingService.cancelBooking(bId, user.getId());
                                if (success) {
                                    System.out.println("✔ Success! Booking #" + bId + " has been cancelled.");
                                } else {
                                    System.out.println("! Error: Booking ID not found or doesn't belong to you.");
                                }
                            }
                        }
                        break;
                    case 5:
                        System.out.println("\n--- [ Export Bookings ] ---");
                        List<Booking> exportBookings = BookingService.getBookingsByUserId(user.getId());
                        if (exportBookings.isEmpty()) {
                            System.out.println("! You have no bookings to export.");
                        } else {
                            System.out.print("Select format: [1] CSV  [2] JSON > ");
                            String fmt = sc.nextLine();
                            boolean isCsv = fmt.equals("1");
                            String ext = isCsv ? ".csv" : ".json";
                            ExportImportService.exportBookings(exportBookings, isCsv, "my_history_" + user.getUsername() + ext);
                        }
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        staying = false;
                        break;
                    default:
                        System.out.println("! Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("! Error: Please enter numbers only.");
            }
        }
    }

    public static void AdminMenu(User user, Scanner sc) {
        boolean staying = true;
        while (staying) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║               ADMIN PANEL                ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println(" Admin: " + user.getUsername());
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println(" [1] View All Bookings");
            System.out.println(" [2] View ALL Cars");
            System.out.println(" [3] View Customers Only");
            System.out.println(" [4] Add New Car (Manual)");
            System.out.println(" [5] Add New Admin (Protected)");
            System.out.println(" [6] View Total Income");
            System.out.println(" [7] Toggle Car Status (On/Off)");
            System.out.println(" [8] View ALL Users & Passwords (Protected)");
            System.out.println(" [9] Export System Data (Users/Cars/Bookings)");
            System.out.println(" [10] Import Cars from File");
            System.out.println(" [0] Logout");
            System.out.println("────────────────────────────────────────────");
            System.out.print(" Admin action > ");
            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        System.out.println("\n--- [ GLOBAL BOOKING LIST ] ---");
                        for (Booking b : BookingService.getAllBookings()) {
                            System.out.println("ID:" + b.getId() + " | UID:" + b.getUserId() + " | CID:" + b.getCarId() + " | Dates: " + b.getStartDate() + "/" + b.getEndDate() + " | $" + b.getTotalPrice());
                        }
                        break;
                    case 2:
                        System.out.println("\n--- [ SYSTEM CAR LIST ] ---");
                        CarService.show();
                        break;
                    case 3:
                        System.out.println("\n--- [ REGISTERED CUSTOMERS ] ---");
                        String sql3 = "SELECT * FROM users WHERE role = 'CUSTOMER'";
                        try (java.sql.Connection conn = DatabaseHelper.connect(); java.sql.Statement stmt = conn.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql3)) {
                            while (rs.next()) {
                                System.out.println(" • ID: " + rs.getInt("id") + " | Username: " + rs.getString("username") + " | Role: " + rs.getString("role"));
                            }
                        } catch (java.sql.SQLException e) {
                            System.out.println("[!] DB Error: " + e.getMessage());
                        }
                        break;
                    case 4:
                        System.out.print(">>> Enter car model: ");
                        String model = sc.nextLine();
                        System.out.print(">>> Enter price per day: ");
                        double price = Double.parseDouble(sc.nextLine());
                        CarService.addCar(model, price);
                        System.out.println("✔ Car added successfully!");
                        break;
                    case 5:
                        System.out.print(">>> Enter Master Password to authorize: ");
                        String masterPass5 = sc.nextLine();
                        if (masterPass5.equals("adminpro")) {
                            System.out.print(">>> New Admin username: ");
                            String username = sc.nextLine();
                            System.out.print(">>> New Admin password: ");
                            String password = sc.nextLine();
                            Authorization.register(username, password, "ADMIN");
                        } else {
                            System.out.println("! Access Denied.");
                        }
                        break;
                    case 6:
                        double totalIncome = BookingService.getTotalIncome();
                        System.out.println("\n┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
                        System.out.println("┃ TOTAL SYSTEM REVENUE: $" + totalIncome);
                        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                        break;
                    case 7:
                        System.out.print(">>> Enter car ID to toggle status: ");
                        int carsId = Integer.parseInt(sc.nextLine());
                        CarService.toggleStatus(carsId);
                        System.out.println("✔ Successfully changed status!");
                        break;
                    case 8:
                        System.out.print(">>> Enter Master Password to authorize: ");
                        if (sc.nextLine().equals("adminpro")) {
                            System.out.println("\n--- [ SYSTEM USERS & PASSWORDS ] ---");
                            String sql8 = "SELECT * FROM users";
                            try (java.sql.Connection conn = DatabaseHelper.connect(); java.sql.Statement stmt = conn.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql8)) {
                                while (rs.next()) {
                                    System.out.println(" • ID: " + rs.getInt("id") + " | Username: " + rs.getString("username") + " | Password: " + rs.getString("password") + " | Role: " + rs.getString("role"));
                                }
                            } catch (java.sql.SQLException e) {
                                System.out.println("[!] DB Error: " + e.getMessage());
                            }
                        } else {
                            System.out.println("! Access Denied.");
                        }
                        break;
                    case 9:
                        System.out.println("\n--- [ Data Export ] ---");
                        System.out.print("Export: [1] Users  [2] Cars  [3] Bookings > ");
                        String expType = sc.nextLine();
                        System.out.print("Format: [1] CSV    [2] JSON > ");
                        boolean isCsvExport = sc.nextLine().equals("1");
                        String extension = isCsvExport ? ".csv" : ".json";

                        if (expType.equals("1"))
                            ExportImportService.exportUsers(isCsvExport, "system_users" + extension);
                        else if (expType.equals("2"))
                            ExportImportService.exportCars(CarService.getAll(), isCsvExport, "system_cars" + extension);
                        else if (expType.equals("3"))
                            ExportImportService.exportBookings(BookingService.getAllBookings(), isCsvExport, "system_bookings" + extension);
                        break;
                    case 10:
                        System.out.println("\n--- [ Car Import ] ---");
                        System.out.println("Note: Format should be ID,Model,Price for CSV.");
                        System.out.println("For JSON: {\"model\":\"Model Name\", \"price_per_day\": 100.0}");
                        System.out.print("File Format: [1] CSV  [2] JSON > ");
                        boolean isCsvImport = sc.nextLine().equals("1");
                        System.out.print("Enter full filename (e.g. cars.csv): ");
                        String filename = sc.nextLine();
                        ExportImportService.importCars(isCsvImport, filename);
                        break;
                    case 0:
                        System.out.println("Logging out admin...");
                        staying = false;
                        break;
                    default:
                        System.out.println("! Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("! Error: Please enter valid numbers.");
            }
        }
    }
}

