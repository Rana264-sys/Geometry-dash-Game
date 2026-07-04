package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// A crystal/spike hazard. Touching it always kills the player - there's
// no safe way to land on it, unlike Block.
public class Spike extends Obstacle {
    
    private final double VISUAL_WIDTH = 350;
    private final double VISUAL_HEIGHT = 250;
    private final double HITBOX_SIZE = 40;
    
    // Creates a new spike at the given position. It can also be flipped
    // upside-down to hang from the ceiling (done in ObstacleSpawner).
    public Spike(double x, double y) {
        super(x, y);
        Image img = new Image(getClass().getResourceAsStream("/assets/crystal.png"));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(VISUAL_WIDTH);
        imageView.setFitHeight(VISUAL_HEIGHT);
        
        this.view = imageView;
        this.view.setTranslateX(x);
        this.view.setTranslateY(y - HITBOX_SIZE - ((VISUAL_HEIGHT - HITBOX_SIZE) / 2)); 

        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;
        this.hitboxSize = HITBOX_SIZE;
    }

    // Touching the spike always kills the player.
    @Override
    public CollisionResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return CollisionResult.DEATH;
        }
        return CollisionResult.NONE;
    }
}