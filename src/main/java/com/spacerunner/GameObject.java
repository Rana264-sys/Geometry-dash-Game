package com.spacerunner;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
 * The abstract base blueprint for all physical entities in the game.
 * Manages the X/Y coordinates and the visual Node representation.
 */
public abstract class GameObject {
    
    protected double x;
    protected double y;
    protected Node view; 

    protected double visualWidth;
    protected double visualHeight;
    protected double hitboxSize;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update();

    public Node getView() {
        return view;
    }

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