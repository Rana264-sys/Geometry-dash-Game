package com.spacerunner;

import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// A pickup that grants the player one extra mid-air jump when touched.
public class DoubleJumpItem extends Pickup {

    private final double VISUAL_SIZE = 60;
    private final double HITBOX_SIZE = 36;

    public DoubleJumpItem(double x, double y) {
        super(x, y);

        Image img = new Image(getClass().getResourceAsStream("/assets/double_jump.png"));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(VISUAL_SIZE);
        imageView.setFitHeight(VISUAL_SIZE);
        imageView.setEffect(new Glow(0.5));

        this.view = imageView;
        this.view.setTranslateX(x);
        this.view.setTranslateY((y - 130) - ((VISUAL_SIZE - HITBOX_SIZE) / 2));

        this.visualWidth = VISUAL_SIZE;
        this.visualHeight = VISUAL_SIZE;
        this.hitboxSize = HITBOX_SIZE;
    }

    // Touching it grants a double-jump charge; GameMain does the actual granting.
    @Override
    public ContactResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return ContactResult.DOUBLE_JUMP;
        }
        return ContactResult.NONE;
    }
}