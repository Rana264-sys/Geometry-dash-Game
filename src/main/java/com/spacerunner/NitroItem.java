package com.spacerunner;

import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// A pickup that grants the player one nitro charge (a temporary speed
// boost, activated later with SHIFT) when touched.
public class NitroItem extends Pickup {

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

    // Touching it grants a nitro charge; GameMain does the actual granting.
    @Override
    public ContactResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return ContactResult.NITRO;
        }
        return ContactResult.NONE;
    }
}