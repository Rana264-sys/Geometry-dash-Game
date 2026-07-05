package com.spacerunner;

import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SporeSwarm extends Obstacle {

    private static final double VISUAL_WIDTH = 190;
    private static final double VISUAL_HEIGHT = 110;
    private static final double HITBOX_SIZE = 80;
    private static final double ORB_RADIUS = 14;
    private static final double BOB_AMPLITUDE = 16;
    private static final double BOB_SPEED = 1.6; // radians-ish per second

    private static final double[] BASE_X_OFFSET = {-60, -20, 25, 65};
    private static final double[] BASE_Y_OFFSET = {10, -25, 15, -10};
    private static final double[] PHASE = {0.0, 1.3, 2.6, 3.9};

    private final Circle[] orbs = new Circle[BASE_X_OFFSET.length];
    private double time = 0;

    public SporeSwarm(double x, double floorY) {
        super(x, floorY);

        double centerY = floorY - 90; // same mid-air lane the old cloud used

        Group group = new Group();
        for (int i = 0; i < orbs.length; i++) {
            Circle orb = new Circle(ORB_RADIUS, Color.web("#66ff99", 0.85));
            orb.setStroke(Color.web("#ccffdd"));
            orb.setStrokeWidth(1.5);
            orb.setEffect(new Glow(0.8));
            orb.setTranslateX(VISUAL_WIDTH / 2 + BASE_X_OFFSET[i]);
            orb.setTranslateY(VISUAL_HEIGHT / 2 + BASE_Y_OFFSET[i]);
            orbs[i] = orb;
            group.getChildren().add(orb);
        }

        this.view = group;
        this.view.setTranslateX(x);
        this.view.setTranslateY(centerY - VISUAL_HEIGHT / 2);

        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;
        this.hitboxSize = HITBOX_SIZE;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        time += deltaTime;
        for (int i = 0; i < orbs.length; i++) {
            double bob = Math.sin(time * BOB_SPEED * Math.PI + PHASE[i]) * BOB_AMPLITUDE;
            orbs[i].setTranslateY(VISUAL_HEIGHT / 2 + BASE_Y_OFFSET[i] + bob);
        }
    }

    @Override
    public CollisionResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return CollisionResult.DEATH;
        }
        return CollisionResult.NONE;
    }
}
