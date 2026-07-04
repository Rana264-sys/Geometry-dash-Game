package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// An asteroid obstacle. It's not always deadly - if the player lands on
// top while falling, it acts like a platform instead of killing them.
public class Block extends Obstacle {
    
    private final double VISUAL_WIDTH = 450;
    private final double VISUAL_HEIGHT = 300;
    private final double HITBOX_SIZE = 40;

    // Creates a new asteroid at the given position.
    public Block(double x, double y) {
        super(x, y);
        Image img = new Image(getClass().getResourceAsStream("/assets/asteroid.png"));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(VISUAL_WIDTH);
        imageView.setFitHeight(VISUAL_HEIGHT);
        
        this.view = imageView;
        this.view.setTranslateX(x);
        
        this.view.setTranslateY(y - HITBOX_SIZE - (((VISUAL_HEIGHT - HITBOX_SIZE) / 2)-20)); 

        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;
        this.hitboxSize = HITBOX_SIZE;
    }

    // If the player is falling and lands on top, it's safe (PLATFORM).
    // Any other touch is fatal (DEATH).
    @Override
    public CollisionResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            if (player.getYVelocity() > 0 && player.getBounds().getMaxY() <= this.getBounds().getMinY() + 15) {
                return CollisionResult.PLATFORM;
            }
            return CollisionResult.DEATH;
        }
        return CollisionResult.NONE;
    }
}