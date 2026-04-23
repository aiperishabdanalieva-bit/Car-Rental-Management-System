package model;

abstract class Entity {
    protected int id;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public abstract String toString();
}

