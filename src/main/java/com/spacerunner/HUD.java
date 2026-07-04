package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Owns the on-screen distance counter and the row of oxygen/life icons.
 * Extracted from GameMain so GameMain doesn't have to know how the HUD
 * is built or redrawn.
 */
public class HUD {

    private final Text scoreText;
    private final HBox lifeBox;
    private final Image oxygenIcon;

    public HUD(int width) {
        scoreText = new Text("DISTANCE: 0m");
        scoreText.setFont(Font.font("Consolas", 24));
        scoreText.setFill(Color.WHITE);
        scoreText.setTranslateX(20);
        scoreText.setTranslateY(40);

        lifeBox = new HBox(-5);
        lifeBox.setTranslateX(width - 200);
        lifeBox.setTranslateY(20);

        oxygenIcon = new Image(getClass().getResourceAsStream("/assets/oxygen.png"));
    }

    public Text getScoreText() {
        return scoreText;
    }

    public HBox getLifeBox() {
        return lifeBox;
    }

    public void updateDistance(int frameCount) {
        scoreText.setText("DISTANCE: " + (frameCount / 10) + "m");
    }

    public void updateLives(int hearts) {
        lifeBox.getChildren().clear();
        for (int i = 0; i < hearts; i++) {
            ImageView icon = new ImageView(oxygenIcon);
            icon.setFitWidth(60);
            icon.setFitHeight(60);
            icon.setPreserveRatio(false);
            lifeBox.getChildren().add(icon);
        }
    }

    public void reset() {
        scoreText.setText("DISTANCE: 0m");
    }
}