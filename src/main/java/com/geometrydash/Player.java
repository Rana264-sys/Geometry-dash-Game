package com.geometrydash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends GameObject {
    private double yVelocity = 0;
    private boolean isJumping = false;
    private final double GRAVITY = 0.6;
    private final double JUMP_FORCE = -12;
    private final double FLOOR_Y = 500;

    public Player(double x, double y) {
        super(x, y);
        Rectangle rect = new Rectangle(40, 40, Color.CYAN);
        this.view = rect;
        this.view.setTranslateX(x);
        this.view.setTranslateY(y);
    }

    @Override
    public void update() {
        yVelocity += GRAVITY;
        y += yVelocity;

        // Floor collision
        if (y >= FLOOR_Y - 40) {
            y = FLOOR_Y - 40;
            yVelocity = 0;
            isJumping = false;
        }
        view.setTranslateY(y);
    }

    public void jump() {
        if (!isJumping) {
            yVelocity = JUMP_FORCE;
            isJumping = true;
        }
    }

    public void landOn(double platformTopY) {
        this.y = platformTopY - 40;
        this.yVelocity = 0;
        this.isJumping = false;
    }

    public double getYVelocity() { return yVelocity; }
}