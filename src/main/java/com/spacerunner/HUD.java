package com.spacerunner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class HUD {

    private final Text scoreText;
    private final HBox lifeBox;
    private final Text nitroActiveText;
    private final Text nitroReadyText;
    private final Text doubleJumpText;
    private final Text menuButton;
    private final Image shieldIcon;

    public HUD(int width) {
        scoreText = new Text("DISTANCE: 0m");
        scoreText.setFont(Font.font("Consolas", 24));
        scoreText.setFill(Color.WHITE);
        scoreText.setTranslateX(20);
        scoreText.setTranslateY(40);

        lifeBox = new HBox(-5);
        lifeBox.setTranslateX(width - 200);
        lifeBox.setTranslateY(20);

        nitroActiveText = new Text("");
        nitroActiveText.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        nitroActiveText.setFill(Color.ORANGE);
        nitroActiveText.setTranslateX(20);
        nitroActiveText.setTranslateY(75);

        nitroReadyText = new Text("");
        nitroReadyText.setFont(Font.font("Consolas", 18));
        nitroReadyText.setFill(Color.web("#ffcc55"));
        nitroReadyText.setTranslateX(20);
        nitroReadyText.setTranslateY(100);

        doubleJumpText = new Text("");
        doubleJumpText.setFont(Font.font("Consolas", 18));
        doubleJumpText.setFill(Color.web("#33e0ff"));
        doubleJumpText.setTranslateX(20);
        doubleJumpText.setTranslateY(125);

        menuButton = new Text("☰ MENU (ESC)");
        menuButton.setFont(Font.font("Consolas", 16));
        menuButton.setFill(Color.LIGHTGRAY);
        menuButton.setTranslateX(width - 150);
        menuButton.setTranslateY(90);
        menuButton.setCursor(javafx.scene.Cursor.HAND);

        shieldIcon = new Image(getClass().getResourceAsStream("/assets/shield.png"));
    }

    public Text getScoreText() {
        return scoreText;
    }
    public HBox getLifeBox() {
        return lifeBox;
    }
    public Text getNitroActiveText() {
        return nitroActiveText;
    }
    public Text getNitroReadyText() {
        return nitroReadyText;
    }
    public Text getDoubleJumpText() {
        return doubleJumpText;
    }
    public Text getMenuButton() {
        return menuButton;
    }
    public void updateNitroActive(boolean active) {
        nitroActiveText.setText(active ? "🔥 NITRO BOOST!" : "");
    }

    // nitro
    public void updateNitroReady(int charges) {
        nitroReadyText.setText(charges > 0 ? "⚡ NITRO x" + charges + " (SHIFT)" : "");
    }
    //double jump
    public void updateDoubleJump(boolean available) {
        doubleJumpText.setText(available ? "⇈ DOUBLE JUMP READY" : "");
    }
    //distance
    public void updateDistance(double elapsedSeconds) {
        scoreText.setText("DISTANCE: " + (int) (elapsedSeconds * 6) + "m");
    }

    public void updateShields(int shields) {
        lifeBox.getChildren().clear();
        for (int i = 0; i < shields; i++) {
            ImageView icon = new ImageView(shieldIcon);
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
