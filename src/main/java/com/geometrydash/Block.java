package com.geometrydash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A solid rectangular obstacle.
 * Acts as a deadly wall if hit from the side, or a safe platform if landed on top.
 */
public class Block extends Obstacle {

    public Block(double x, double y) {
        super(x, y);
        
        Rectangle rect = new Rectangle(40, 40);
        rect.setFill(Color.ORANGE);
        
        rect.setTranslateX(x);
        rect.setTranslateY(y - 40);
        
        this.view = rect;
    }

    @Override
    public String checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            
            double playerBottom = player.getBounds().getMaxY();
            double blockTop = this.getBounds().getMinY();
            
            // If the player is falling downward AND their bottom edge is near the block's top edge
            if (player.getYVelocity() > 0 && playerBottom <= blockTop + 15) {
                return "PLATFORM";
            } else {
                return "DEATH"; // Hitting the side
            }
        }
        return "NONE";
    }
}