package com.geometrydash;

/**
 * An abstract base class for environmental hazards.
 * Inherits from GameObject and manages horizontal scrolling movement.
 */
public abstract class Obstacle extends GameObject {

    // The speed at which the world moves left
    protected double xVelocity = -6.0; 

    public Obstacle(double x, double y) {
        super(x, y);
    }

    @Override
    public void update() {
        x += xVelocity;
        view.setTranslateX(x); // Moves the visual node across the screen
    }

    // Forces subclasses to define their own collision rules
    public abstract String checkCollision(Player player);
    
    // Used to delete the obstacle when it goes off-screen to save memory
    public boolean isOffScreen() {
        return x < -100; 
    }
}