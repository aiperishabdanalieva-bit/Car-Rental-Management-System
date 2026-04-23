package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper;
import model.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CarService {
    public static List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cars.add(new Car(rs.getInt("id"), rs.getString("model"), rs.getDouble("price_per_day"), rs.getBoolean("available")));
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
        return cars;
    }

    public static void show() {
        for (Car c : getAll()) {
            System.out.println(c.getId() + " | " + c.getModel() + " | $" + c.getPricePerDay() + " | " + (c.isAvailable() ? "Available" : "Unavailable"));
        }
    }

    public static Car getCarById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Car(rs.getInt("id"), rs.getString("model"), rs.getDouble("price_per_day"), rs.getBoolean("available"));
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
        return null;
    }

    public static void addCar(String model, double price) {
        String sql = "INSERT INTO cars (model, price_per_day, available) VALUES (?, ?, 1)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, model);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
    }

    public static void toggleStatus(int id) {
        String sql = "UPDATE cars SET available = CASE WHEN available = 1 THEN 0 ELSE 1 END WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
    }
}
