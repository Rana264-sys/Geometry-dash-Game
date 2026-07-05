package com.spacerunner;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// The "MISSION FAILED" overlay shown when the player dies. Shows the run's
// status and a message that changes based on how long they survived, plus
// a restart button.
public class GameOverMenu extends VBox {

    private final Text gameOverStats;
    private final Text gameOverMessage;

    public GameOverMenu(int width, int height, Runnable onRestart) {
        super(25);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        setVisible(false); // hidden by default

        Text title = new Text("MISSION FAILED");
        title.setFont(Font.font("Consolas", 60));
        title.setFill(Color.web("#ff4444")); // A realistic error-red color

        gameOverStats = new Text("");
        gameOverStats.setFont(Font.font("Consolas", 30));
        gameOverStats.setFill(Color.WHITE);

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
        restartBtn.setOnAction(e -> onRestart.run());

        getChildren().addAll(title, gameOverStats, gameOverMessage, restartBtn);
    }

    // Fills in the stats and picks a message based on how long the run lasted, then shows the menu.
    public void show(int distance, int secondsSurvived) {
        gameOverStats.setText("TOTAL DISTANCE: " + distance + "m");

        if (secondsSurvived < 15) {
            gameOverMessage.setText("CATASTROPHIC LAUNCH FAILURE.\nCalibrate your thrusters and try harder.");
        } else if (secondsSurvived < 45) {
            gameOverMessage.setText("NAVIGATION LOST.\nYou reached the outer belt, but space is unforgiving.");
        } else {
            gameOverMessage.setText("OUTSTANDING ENDURANCE.\nThe agency is highly impressed with your survival skills.");
        }

        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }
}