package com.geometrydash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Block extends Obstacle {
    
    public Block(double x, double y) {
        super(x, y);
        Rectangle rect = new Rectangle(40, 40, Color.ORANGE);
        this.view = rect;
        this.view.setTranslateX(x);
        this.view.setTranslateY(y - 40); 
    }

    @Override
    public String checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            // Check if player is falling onto the top of the block
            if (player.getYVelocity() > 0 && player.getBounds().getMaxY() <= this.getBounds().getMinY() + 15) {
                return "PLATFORM";
            }
            return "DEATH";
        }
        return "NONE";
    }
}