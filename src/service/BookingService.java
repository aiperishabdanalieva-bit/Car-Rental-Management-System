package service;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import database.DatabaseHelper;
import model.Booking;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class BookingService {
    public static List<Booking> getBookingsByCarId(int carId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE car_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("car_id"),
                        LocalDate.parse(rs.getString("start_date")), LocalDate.parse(rs.getString("end_date")), rs.getDouble("total_price")));
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
        return bookings;
    }

    public static List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("car_id"),
                        LocalDate.parse(rs.getString("start_date")), LocalDate.parse(rs.getString("end_date")), rs.getDouble("total_price")));
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
        return bookings;
    }

    public static boolean cancelBooking(int bookingId, int userId) {
        String sql = "DELETE FROM bookings WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
            return false;
        }
    }

    public static List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(new Booking(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("car_id"),
                        LocalDate.parse(rs.getString("start_date")), LocalDate.parse(rs.getString("end_date")), rs.getDouble("total_price")));
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
        return bookings;
    }

    public static void addBooking(int userId, int carId, LocalDate startDate, LocalDate endDate, double totalPrice) {
        String sql = "INSERT INTO bookings (user_id, car_id, start_date, end_date, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, carId);
            pstmt.setString(3, startDate.toString());
            pstmt.setString(4, endDate.toString());
            pstmt.setDouble(5, totalPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
    }

    public static double getTotalIncome() {
        String sql = "SELECT SUM(total_price) FROM bookings";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
        return 0.0;
    }
}
