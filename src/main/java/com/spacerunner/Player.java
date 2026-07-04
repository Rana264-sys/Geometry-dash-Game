package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Represents the player's spaceship: handles jumping, gravity,
// landing on the floor, and the little tilt animation.
public class Player extends GameObject {
    private double yVelocity = 0;
    private boolean isJumping = false;
    private final double GRAVITY = 0.6;
    private final double JUMP_FORCE = -12;
    private final double FLOOR_Y = 500;

    private ImageView imageView;

    // Define visual size and physical size
    private final double VISUAL_WIDTH = 350; 
    private final double VISUAL_HEIGHT = 310;
    private final double HITBOX_SIZE = 40; 

    // Creates the player at the given starting position.
    public Player(double x, double y) {
        super(x, y);
        
        Image normalImg = new Image(getClass().getResourceAsStream("/assets/spaceship_normal.png"));
        
        this.imageView = new ImageView(normalImg);
        this.imageView.setFitWidth(VISUAL_WIDTH); 
        this.imageView.setFitHeight(VISUAL_HEIGHT);
        this.imageView.setPreserveRatio(false); 
        
        this.view = imageView;
        this.view.setTranslateX(x);
        this.view.setTranslateY(y - (VISUAL_HEIGHT - HITBOX_SIZE) / 2);

        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;
        this.hitboxSize = HITBOX_SIZE;
    }

    // Runs every frame: applies gravity, checks the floor, tilts the ship,
    // and moves the picture to match the player's position.
    @Override
    public void update() {
        yVelocity += GRAVITY;
        y += yVelocity;

        // Floor collision
        if (y >= FLOOR_Y - HITBOX_SIZE) {
            y = FLOOR_Y - HITBOX_SIZE;
            yVelocity = 0;
            isJumping = false;
        }
        
        // --- ANIMATION LOGIC: ROTATION ---
        if (yVelocity < -2) {
            // Tilt the nose up by 25 degrees when jumping
            imageView.setRotate(-25); 
        } else if (yVelocity > 2 && y < FLOOR_Y - HITBOX_SIZE) {
            // Tilt the nose down by 25 degrees when falling
            imageView.setRotate(25); 
        } else {
            // Level out when on the ground
            imageView.setRotate(0); 
        }

        // Keep the visual attached to the logical Y coordinate
        view.setTranslateY((y - (VISUAL_HEIGHT - HITBOX_SIZE) / 2)+20);
    }

    // Makes the ship jump, but only if it's not already mid-jump.
    public void jump() {
        if (!isJumping) {
            yVelocity = JUMP_FORCE;
            isJumping = true;
        }
    }

    // Places the player exactly on top of a platform (used when landing on a Block).
    public void landOn(double platformTopY) {
        this.y = platformTopY - HITBOX_SIZE;
        this.yVelocity = 0;
        this.isJumping = false;
    }

    // Returns how fast the player is moving up/down right now.
    public double getYVelocity() { return yVelocity; }
}