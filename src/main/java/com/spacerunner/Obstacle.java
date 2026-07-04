package com.spacerunner;

// The base class for anything that scrolls across the screen toward the
// player: Block, Spike, and Heart. It handles the shared movement logic,
// but each type decides for itself what happens when it touches the player.
public abstract class Obstacle extends GameObject {
    protected double xVelocity = -6.0; // how fast it moves left each frame

    public Obstacle(double x, double y) {
        super(x, y);
    }

    // Moves the obstacle to the left every frame.
    @Override
    public void update() {
        x += xVelocity;
        view.setTranslateX(x);
    }

    // Checks what happens if this obstacle touches the player.
    // Each subclass (Block, Spike, Heart) fills this in differently.
    public abstract CollisionResult checkCollision(Player player);

    // True once the obstacle has scrolled off the left side of the screen.
    public boolean isOffScreen() {
        return x < -100;
    }

    // Changes how fast the obstacle moves (used to speed things up over time).
    public void setXVelocity(double speed) {
        this.xVelocity = speed;
    }
}