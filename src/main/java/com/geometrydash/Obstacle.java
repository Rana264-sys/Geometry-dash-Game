package com.geometrydash;

public abstract class Obstacle extends GameObject {
    protected double xVelocity = -6.0;

    public Obstacle(double x, double y) {
        super(x, y);
    }

    @Override
    public void update() {
        x += xVelocity;
        view.setTranslateX(x);
    }

    public abstract String checkCollision(Player player);

    public boolean isOffScreen() {
        return x < -100;
    }

    public void setXVelocity(double speed) {
        this.xVelocity = speed;
    }
}