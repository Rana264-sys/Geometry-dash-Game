package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// A meteor that starts above the floor and slowly falls while scrolling
// left and tumbling, then stops once it reaches the floor. Deadly on contact.
public class FallingDebris extends Obstacle {

    private static final double VISUAL_SIZE = 55;
    private static final double HITBOX_SIZE = 34;
    private static final double FALL_SPEED = 260; 
    private static final double START_HEIGHT_ABOVE_FLOOR = 260;
    private static final double ROTATION_SPEED = 90; 

    private final ImageView imageView;
    private double posY;
    private final double maxY; 
    private double rotation = 0;

    public FallingDebris(double x, double floorY) {
        super(x, floorY - START_HEIGHT_ABOVE_FLOOR);

        Image img = new Image(getClass().getResourceAsStream("/assets/meteor.png"));
        imageView = new ImageView(img);
        imageView.setFitWidth(VISUAL_SIZE);
        imageView.setFitHeight(VISUAL_SIZE);

        this.view = imageView;
        this.posY = floorY - START_HEIGHT_ABOVE_FLOOR;
        this.view.setTranslateX(x);
        this.view.setTranslateY(posY);

        this.visualWidth = VISUAL_SIZE;
        this.visualHeight = VISUAL_SIZE;
        this.hitboxSize = HITBOX_SIZE;
        this.maxY = floorY - HITBOX_SIZE;
    }

    // Scrolls left, falls down at a fixed speed (capped at the floor), and
    // spins slowly for a tumbling look.
    @Override
    public void update(double deltaTime) {
        x += xVelocity * deltaTime;
        posY = Math.min(posY + FALL_SPEED * deltaTime, maxY);
        rotation = (rotation + ROTATION_SPEED * deltaTime) % 360;
        view.setTranslateX(x);
        view.setTranslateY(posY);
        imageView.setRotate(rotation);
    }

    // Always deadly on contact.
    @Override
    public ContactResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return ContactResult.DEATH;
        }
        return ContactResult.NONE;
    }
}