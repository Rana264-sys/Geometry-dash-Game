package com.spacerunner;

// Marker base for scrolling objects that only ever help the player when
// touched (as opposed to Obstacle, which can hurt or block them).
// Subclasses implement checkCollision() to return the specific benefit
// (SHIELD, NITRO, DOUBLE_JUMP, ...) or NONE if not touched yet.
public abstract class Pickup extends ScrollingObject {
    public Pickup(double x, double y) {
        super(x, y);
    }
}