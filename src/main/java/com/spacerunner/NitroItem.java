package com.spacerunner;

import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NitroItem extends Obstacle {

    private final double VISUAL_WIDTH = 90;
    private final double VISUAL_HEIGHT = 90;
    private final double HITBOX_SIZE = 30;

    public NitroItem(double x, double y) {
        super(x, y);

        Image img = new Image(getClass().getResourceAsStream("/assets/nitro.png"));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(VISUAL_WIDTH);
        imageView.setFitHeight(VISUAL_HEIGHT);
        imageView.setEffect(new Glow(0.5));

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
            return CollisionResult.NITRO;
        }
        return CollisionResult.NONE;
    }
}
