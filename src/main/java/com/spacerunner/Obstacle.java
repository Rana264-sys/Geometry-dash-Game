package com.spacerunner;
public abstract class Obstacle extends GameObject {
    protected double xVelocity = -360; // px/s
    public Obstacle(double x, double y) {
        super(x, y);
    }
    @Override
    public void update(double deltaTime) {
        x += xVelocity * deltaTime;
        view.setTranslateX(x);
    }
    public abstract CollisionResult checkCollision(Player player);
    public boolean isOffScreen() {
        return x < -100;
    }
    public void setXVelocity(double speed) {
        this.xVelocity = speed;
    }
}