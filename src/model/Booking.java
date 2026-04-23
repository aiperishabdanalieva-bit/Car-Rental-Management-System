package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking extends Entity {
    private int userId;
    private int carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;

    public Booking(int id, int userId, int carId, LocalDate startDate, LocalDate endDate, double priceOrTotal) {
        super(id);
        this.userId = userId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
        if (id == 0) {
            this.totalPrice = calculateTotalPrice(priceOrTotal);
        } else {
            this.totalPrice = priceOrTotal;
        }
    }

    private double calculateTotalPrice(double pricePerDay) {
        long days = ChronoUnit.DAYS.between(this.startDate, this.endDate);
        return days <= 0 ? 0.0 : pricePerDay * days;
    }

    public boolean isValidDates() {
        return endDate.isAfter(startDate);
    }

    public boolean isOverlapping(LocalDate newStart, LocalDate newEnd) {
        return !this.startDate.isAfter(newEnd) && !this.endDate.isBefore(newStart);
    }

    public int getUserId() {
        return userId;
    }

    public int getCarId() {
        return carId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "Booking #" + id + " | Car: " + carId + " | User: " + userId + " | " +
                startDate + " -> " + endDate + " | Price: $" + totalPrice;
    }
}
