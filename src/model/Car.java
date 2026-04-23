package model;

public class Car extends Entity {
    private String model;
    private double pricePerDay;
    private boolean available;

    public Car(int id, String model, double pricePerDay, boolean available) {
        super(id);
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }

    public String getModel() {
        return model;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return model + " ($" + pricePerDay + "/day) " + (available ? "✓" : "✗");
    }
}
