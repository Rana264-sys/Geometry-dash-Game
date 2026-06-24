package com.geometrydash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * A deadly triangular hazard.
 * Triggers an immediate game over upon any collision with the player.
 */
public class Spike extends Obstacle {

    public Spike(double x, double y) {
        super(x, y);
        
        // Draw a triangle
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double[]{
            0.0, 40.0,    // Bottom left
            20.0, 0.0,    // Top center
            40.0, 40.0    // Bottom right
        });
        triangle.setFill(Color.RED);
        
        // Position it so the base sits perfectly on the floor coordinate
        triangle.setTranslateX(x);
        triangle.setTranslateY(y - 40); 
        
        this.view = triangle;
    }

    @Override
    public String checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return "DEATH"; // Spikes are always lethal
        }
        return "NONE";
    }
}