package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.DatabaseHelper;
import model.Booking;
import model.Car;

public class ExportImportService {

    public static void exportBookings(List<Booking> bookings, boolean isCsv, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            if (isCsv) {
                writer.write("id,user_id,car_id,start_date,end_date,total_price\n");
                for (Booking b : bookings) {
                    writer.write(b.getId() + "," + b.getUserId() + "," + b.getCarId() + "," +
                            b.getStartDate() + "," + b.getEndDate() + "," + b.getTotalPrice() + "\n");
                }
            } else {
                writer.write("[\n");
                for (int i = 0; i < bookings.size(); i++) {
                    Booking b = bookings.get(i);
                    writer.write(String.format("  {\"id\":%d, \"user_id\":%d, \"car_id\":%d, \"start_date\":\"%s\", \"end_date\":\"%s\", \"total_price\":%s}",
                            b.getId(), b.getUserId(), b.getCarId(), b.getStartDate(), b.getEndDate(), b.getTotalPrice()));
                    writer.write(i < bookings.size() - 1 ? ",\n" : "\n");
                }
                writer.write("]\n");
            }
            System.out.println("✔ Bookings successfully exported to " + filename);
        } catch (IOException e) {
            System.out.println("! Export Error: " + e.getMessage());
        }
    }

    public static void exportCars(List<Car> cars, boolean isCsv, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            if (isCsv) {
                writer.write("id,model,price_per_day,available\n");
                for (Car c : cars) {
                    writer.write(c.getId() + "," + c.getModel() + "," + c.getPricePerDay() + "," + c.isAvailable() + "\n");
                }
            } else {
                writer.write("[\n");
                for (int i = 0; i < cars.size(); i++) {
                    Car c = cars.get(i);
                    writer.write(String.format("  {\"id\":%d, \"model\":\"%s\", \"price_per_day\":%s, \"available\":%b}",
                            c.getId(), c.getModel(), c.getPricePerDay(), c.isAvailable()));
                    writer.write(i < cars.size() - 1 ? ",\n" : "\n");
                }
                writer.write("]\n");
            }
            System.out.println("✔ Cars successfully exported to " + filename);
        } catch (IOException e) {
            System.out.println("! Export Error: " + e.getMessage());
        }
    }

    public static void exportUsers(boolean isCsv, String filename) {
        String sql = "SELECT * FROM users";
        try (java.sql.Connection conn = DatabaseHelper.connect();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql);
             FileWriter writer = new FileWriter(filename)) {

            if (isCsv) {
                writer.write("id,username,role\n");
                while (rs.next()) {
                    writer.write(rs.getInt("id") + "," + rs.getString("username") + "," + rs.getString("role") + "\n");
                }
            } else {
                writer.write("[\n");
                boolean first = true;
                while (rs.next()) {
                    if (!first) writer.write(",\n");
                    writer.write(String.format("  {\"id\":%d, \"username\":\"%s\", \"role\":\"%s\"}",
                            rs.getInt("id"), rs.getString("username"), rs.getString("role")));
                    first = false;
                }
                writer.write("\n]\n");
            }
            System.out.println("✔ Users successfully exported to " + filename);
        } catch (Exception e) {
            System.out.println("! Export Error: " + e.getMessage());
        }
    }


    public static void importCars(boolean isCsv, String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("! File not found: " + filename);
            return;
        }

        int addedCount = 0;
        try {
            if (isCsv) {
                List<String> lines = Files.readAllLines(Paths.get(filename));
                for (int i = 1; i < lines.size(); i++) { // Пропускаем заголовок (строка 0)
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 2) {
                        CarService.addCar(parts[1].trim(), Double.parseDouble(parts[2].trim()));
                        addedCount++;
                    }
                }
            } else {
                String content = new String(Files.readAllBytes(Paths.get(filename)));
                Pattern pattern = Pattern.compile("\"model\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"price_per_day\"\\s*:\\s*([0-9.]+)");
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()) {
                    String model = matcher.group(1);
                    double price = Double.parseDouble(matcher.group(2));
                    CarService.addCar(model, price);
                    addedCount++;
                }
            }
            System.out.println("✔ Successfully imported " + addedCount + " cars into the database!");
        } catch (Exception e) {
            System.out.println("! Import Error. Ensure format is correct. " + e.getMessage());
        }
    }
}
