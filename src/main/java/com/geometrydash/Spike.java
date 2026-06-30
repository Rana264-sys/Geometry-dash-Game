package com.geometrydash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Spike extends Obstacle {
    
    public Spike(double x, double y) {
        super(x, y);
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double[]{
            0.0, 0.0,
            20.0, -40.0,
            40.0, 0.0
        });
        triangle.setFill(Color.RED);
        this.view = triangle;
        this.view.setTranslateX(x);
        this.view.setTranslateY(y); 
    }

    @Override
    public String checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return "DEATH";
        }
        return "NONE";
    }
}