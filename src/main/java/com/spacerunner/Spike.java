package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class Spike extends Obstacle {
    
    private final double VISUAL_WIDTH = 350;
    private final double VISUAL_HEIGHT = 250;
    private final double HITBOX_SIZE = 40;
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
    @Override
    public CollisionResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return CollisionResult.DEATH;
        }
        return CollisionResult.NONE;
    }
}