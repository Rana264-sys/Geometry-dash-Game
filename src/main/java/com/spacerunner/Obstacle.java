package com.spacerunner;

// Marker base for scrolling objects that can hurt or block the player
// (as opposed to Pickup, which only ever helps them). Subclasses just
// need to implement checkCollision() to return DEATH, PLATFORM, or NONE
// as appropriate - the scrolling behavior itself lives in ScrollingObject.
public abstract class Obstacle extends ScrollingObject {
    public Obstacle(double x, double y) {
        super(x, y);
    }
}