package com.geometrydash;

import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
 * The abstract base blueprint for all physical entities in the game.
 * Manages the X/Y coordinates and the visual Node representation.
 */
public abstract class GameObject {
    
    // Protected means subclasses (like Player) can access these directly
    protected double x;
    protected double y;
    protected Node view; // The visual shape or image

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Every game object must have an update logic specific to itself
    public abstract void update();

    // Returns the visual component to be added to the scene
    public Node getView() {
        return view;
    }

    // Used for collision detection
    public Bounds getBounds() {
        return view.getBoundsInParent();
    }
}