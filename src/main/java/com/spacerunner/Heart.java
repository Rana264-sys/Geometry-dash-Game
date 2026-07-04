package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Heart extends Obstacle {

    private final double VISUAL_WIDTH = 100;
    private final double VISUAL_HEIGHT = 100;
    private final double HITBOX_SIZE = 30;

    public Heart(double x, double y) {
        super(x, y);
        Image img = new Image(getClass().getResourceAsStream("/assets/oxygen.png"));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(VISUAL_WIDTH); 
        imageView.setFitHeight(VISUAL_HEIGHT);
        
        this.view = imageView;
        this.view.setTranslateX(x);
        
        this.view.setTranslateY((y - 120) - ((VISUAL_HEIGHT - HITBOX_SIZE) / 2)); 

        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;
        this.hitboxSize = HITBOX_SIZE;
    }

    @Override
    public CollisionResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return CollisionResult.HEART; 
        }
        return CollisionResult.NONE;
    }
}