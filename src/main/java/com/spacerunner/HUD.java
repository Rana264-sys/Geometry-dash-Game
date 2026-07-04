package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

// Shows the score (distance) and life icons on screen while playing.
public class HUD {

    private final Text scoreText;
    private final HBox lifeBox;
    private final Image oxygenIcon;

    // Builds the distance text and the row where life icons will go.
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

    // Gives GameMain the text node so it can be added to the screen once.
    public Text getScoreText() {
        return scoreText;
    }

    // Gives GameMain the life-icon row so it can be added to the screen once.
    public HBox getLifeBox() {
        return lifeBox;
    }

    // Updates the distance shown on screen.
    public void updateDistance(int frameCount) {
        scoreText.setText("DISTANCE: " + (frameCount / 10) + "m");
    }

    // Redraws the life icons to match how many lives the player has.
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

    // Resets the distance text back to zero (used when restarting).
    public void reset() {
        scoreText.setText("DISTANCE: 0m");
    }
}