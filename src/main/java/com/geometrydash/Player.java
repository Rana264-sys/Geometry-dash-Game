package com.geometrydash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends GameObject {

    private double yVelocity = 0;
    private boolean isJumping = false;
    
    // Physics constants moved here
    private final double GRAVITY = 0.6; // go down
    private final double JUMP_FORCE = -25;// go up
    private final double FLOOR_Y = 500;

    /**
     * The user-controlled entity. 
     * Encapsulates all physics logic including gravity, velocity, floor collision, and jumping.
     */
    public Player(double x, double y) {
        super(x, y);
        
        // Define the visual representation of the player
        Rectangle rect = new Rectangle(40, 40);
        rect.setFill(Color.CYAN);
        rect.setX(x);
        rect.setY(y);
        
        // Assign it to the parent's 'view' variable
        this.view = rect; 
    }

    @Override
    public void update() {
        // 1. Apply gravity
        yVelocity += GRAVITY;
        
        // 2. Update logical position
        y += yVelocity;

        // 3. Collision detection with floor
        Rectangle rect = (Rectangle) view;
        if (y >= FLOOR_Y - rect.getHeight()) {
            y = FLOOR_Y - rect.getHeight();
            yVelocity = 0;
            isJumping = false;
        }
        
        // 4. Sync the visual shape to the new logical coordinates
        rect.setY(y);
    }

    public void jump() {
        if (!isJumping) {
            yVelocity = JUMP_FORCE;
            isJumping = true;
        }
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public void landOn(double platformTopY) {
        Rectangle rect = (Rectangle) view;
        this.y = platformTopY - rect.getHeight(); // Snap to top of block
        rect.setY(this.y);
        this.yVelocity = 0;
        this.isJumping = false; // Allow jumping again
    }
}