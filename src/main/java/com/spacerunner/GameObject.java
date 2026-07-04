package com.spacerunner;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

// The base class for anything that appears in the game world.
// It stores the object's position and its picture (the "view"),
// and every subclass must say how it updates itself each frame.
public abstract class GameObject {
    
    protected double x;
    protected double y;
    protected Node view; // the image/shape shown on screen

    // Used to work out the hitbox size and position (see getBounds below).
    protected double visualWidth;
    protected double visualHeight;
    protected double hitboxSize;

    // Sets the object's starting position.
    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Called every frame. Each subclass decides what "update" means for it.
    public abstract void update();

    // Gives back the picture/shape so it can be added to the screen.
    public Node getView() {
        return view;
    }

    // Works out the object's hitbox (a smaller box centered inside its picture),
    // used to check if it's touching another object.
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