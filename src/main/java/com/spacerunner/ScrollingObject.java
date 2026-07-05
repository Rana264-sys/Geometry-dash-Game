package com.spacerunner;

// Base class for anything that scrolls leftward across the screen at the
// level's current speed and can be checked for collision with the player.
// Both harmful things (Obstacle) and beneficial things (Pickup) share this
// movement/lifecycle behavior; what differs is what checkCollision() means
// for each of them.
public abstract class ScrollingObject extends GameObject {

    protected double xVelocity = -360; 

    public ScrollingObject(double x, double y) {
        super(x, y);
    }

    // Moves the object left every frame based on its speed.
    @Override
    public void update(double deltaTime) {
        x += xVelocity * deltaTime;
        view.setTranslateX(x);
    }

    // Returns what should happen if this object's bounds intersect the
    // player's bounds this frame (NONE if they don't intersect at all).
    public abstract ContactResult checkCollision(Player player);

    // True once the object has scrolled far enough off the left edge that
    // it can be safely deleted.
    public boolean isOffScreen() {
        return x < -100;
    }

    // Lets the level speed (and nitro boosts) control how fast this object scrolls.
    public void setXVelocity(double speed) {
        this.xVelocity = speed;
    }
}