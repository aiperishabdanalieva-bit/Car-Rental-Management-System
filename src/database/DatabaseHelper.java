package database;

import java.sql.*;

public class  DatabaseHelper {
    private static final String URL = "jdbc:sqlite:car_rental.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS cars (id INTEGER PRIMARY KEY AUTOINCREMENT, model TEXT, price_per_day REAL, available INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, car_id INTEGER, start_date TEXT, end_date TEXT, total_price REAL)");

            ResultSet rsUsers = stmt.executeQuery("SELECT count(*) FROM users WHERE role = 'ADMIN'");
            if (rsUsers.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', 'admin', 'ADMIN')");
            }

            ResultSet rsCars = stmt.executeQuery("SELECT count(*) FROM cars");
            if (rsCars.getInt(1) == 0) {
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Toyota Corolla', 45.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('BMW M4', 120.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Mercedes-Benz C-Class', 95.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Audi A4', 85.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Volkswagen Golf', 55.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Hyundai Tucson', 70.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Kia Sportage', 65.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Ford Mustang', 150.0, 1)");
                stmt.execute("INSERT INTO cars (model, price_per_day, available) VALUES ('Tesla Model 3', 130.0, 1)");
            }
        } catch (SQLException e) {
            System.out.println("Database Initialization Error: " + e.getMessage());
        }
    }
}

