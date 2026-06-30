package com.geometrydash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Heart extends Obstacle {

    public Heart(double x, double y) {
        super(x, y);
        Polygon star = new Polygon();
        star.getPoints().addAll(new Double[]{
            10.0, 0.0, 13.0, 7.0, 20.0, 7.0, 14.0, 11.0, 16.0, 18.0, 
            10.0, 14.0, 4.0, 18.0, 6.0, 11.0, 0.0, 7.0, 7.0, 7.0     
        });
        
        star.setFill(Color.HOTPINK);
        star.setScaleX(1.5); 
        star.setScaleY(1.5);
        this.view = star;
        this.view.setTranslateX(x);
        this.view.setTranslateY(y - 120); // Hover in the air
    }

    @Override
    public String checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return "HEART"; 
        }
        return "NONE";
    }
}