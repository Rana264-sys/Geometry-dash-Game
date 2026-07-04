package com.spacerunner;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * The full-screen "mission failed" overlay. Extracted from GameMain's
 * createGameOverMenu() (construction) and showGameOverScreen() (the
 * dynamic stats/message logic), combined into one cohesive class.
 */
public class GameOverMenu extends VBox {

    private final Text gameOverStats;
    private final Text gameOverMessage;

    public GameOverMenu(int width, int height, Runnable onRestart) {
        super(25);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);

        // Darker overlay to emphasize the end state
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        setVisible(false); // Hidden by default

        // Title
        Text title = new Text("MISSION FAILED");
        title.setFont(Font.font("Consolas", 60));
        title.setFill(Color.web("#ff4444"));

        // Dynamic Status (Distance)
        gameOverStats = new Text("");
        gameOverStats.setFont(Font.font("Consolas", 30));
        gameOverStats.setFill(Color.WHITE);

        // Dynamic Encouragement Message
        gameOverMessage = new Text("");
        gameOverMessage.setFont(Font.font("Consolas", 20));
        gameOverMessage.setFill(Color.LIGHTGRAY);
        gameOverMessage.setTextAlignment(TextAlignment.CENTER);

        // Restart Button
        Button restartBtn = new Button("RESTART MISSION");
        restartBtn.setFont(Font.font("Consolas", 24));
        restartBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 2px; " +
            "-fx-padding: 10px 30px; " +
            "-fx-cursor: hand;"
        );

        restartBtn.setOnMouseEntered(e -> restartBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 30px; -fx-cursor: hand;"));
        restartBtn.setOnMouseExited(e -> restartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-padding: 10px 30px; -fx-cursor: hand;"));

        // Restart Logic
        restartBtn.setOnAction(e -> onRestart.run());

        getChildren().addAll(title, gameOverStats, gameOverMessage, restartBtn);
    }

    /**
     * Fills in the dynamic status/message and makes the overlay visible.
     * Same distance/seconds-survived thresholds as the original
     * showGameOverScreen() logic.
     */
    public void show(int distance, int secondsSurvived) {
        gameOverStats.setText("TOTAL DISTANCE: " + distance + "m");

        if (secondsSurvived < 15) {
            // Easy Phase Failure (Under 15 seconds)
            gameOverMessage.setText("CATASTROPHIC LAUNCH FAILURE.\nSteady your thrusters and try harder.");
        } else if (secondsSurvived < 45) {
            // Middle Phase Failure (15 to 45 seconds)
            gameOverMessage.setText("NAVIGATION LOST.\nYou reached the outer belt, but space is unforgiving.");
        } else {
            // Difficult Phase + 20 seconds (Over 45 seconds total)
            gameOverMessage.setText("OUTSTANDING ENDURANCE.\nThe agency is highly impressed with your survival skills.");
        }

        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }
}