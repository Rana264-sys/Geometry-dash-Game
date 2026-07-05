package com.spacerunner;

import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// A pickup that grants the player one shield charge (absorbs the next
// deadly hit) when touched.
public class ShieldItem extends Pickup {

    private final double VISUAL_WIDTH = 90;
    private final double VISUAL_HEIGHT = 90;
    private final double HITBOX_SIZE = 30;

    public ShieldItem(double x, double y) {
        super(x, y);

        Image img = new Image(getClass().getResourceAsStream("/assets/shield.png"));
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

    // Touching it grants a shield; actually adding the shield charge is
    // handled by GameMain when it sees this result.
    @Override
    public ContactResult checkCollision(Player player) {
        if (this.getBounds().intersects(player.getBounds())) {
            return ContactResult.SHIELD;
        }
        return ContactResult.NONE;
    }
}