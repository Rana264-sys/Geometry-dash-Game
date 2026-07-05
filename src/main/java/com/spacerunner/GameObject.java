package com.spacerunner;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

// The base class for every visible thing in the game (player, obstacles,
// pickups). It just knows where it is, what it looks like on screen, and
// how to report its hitbox for collision checks.
public abstract class GameObject {

    protected double x;
    protected double y;
    protected Node view; // the actual JavaFX shape/image shown on screen

    // The hitbox used for collisions is usually smaller than the picture
    // itself (so a big meteor image doesn't kill you just by grazing its
    // edge). These three numbers let each subclass control that.
    protected double visualWidth;
    protected double visualHeight;
    protected double hitboxSize;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Called every frame. Each subclass decides what "updating" means for it
    // (moving, animating, falling, etc).
    public abstract void update(double deltaTime);

    public Node getView() {
        return view;
    }

    // Works out the small, centered hitbox rectangle used for collision
    // checks, based on where the visual is currently drawn.
    public Bounds getBounds() {
        double xOffset = (visualWidth - hitboxSize) / 2;
        double yOffset = (visualHeight - hitboxSize) / 2;
        return new BoundingBox(
            view.getTranslateX() + xOffset,
            view.getTranslateY() + yOffset,
            hitboxSize,
            hitboxSize
        );
    }
}