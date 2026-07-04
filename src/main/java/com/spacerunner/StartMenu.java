package com.spacerunner;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * The full-screen start overlay. Extracted from GameMain's
 * createStartMenu(). The Runnable passed in is exactly the same
 * "on start" behavior GameMain's button lambda used to run inline.
 */
public class StartMenu extends VBox {

    public StartMenu(int width, int height, Runnable onStart) {
        super(30); 
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

        // Title
        Text title = new Text("SPACE RUNNER");
        title.setFont(Font.font("Consolas", 60));
        title.setFill(Color.WHITE);

        // Instructions
        Text instructions = new Text("MISSION BRIEFING:\n\nPress SPACE/UP to fire thrusters.\nCollect Oxygen to survive.\nAvoid incoming space debris.");
        instructions.setFont(Font.font("Consolas", 20));
        instructions.setFill(Color.LIGHTGRAY);
        instructions.setTextAlignment(TextAlignment.CENTER);

        // Ghost Button
        Button startBtn = new Button("START LAUNCH");
        startBtn.setFont(Font.font("Consolas", 24));
        startBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 2px; " +
            "-fx-padding: 10px 30px; " +
            "-fx-cursor: hand;"
        );

        // Button Hover Effect
        startBtn.setOnMouseEntered(e -> startBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 30px; -fx-cursor: hand;"));
        startBtn.setOnMouseExited(e -> startBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-padding: 10px 30px; -fx-cursor: hand;"));

        // Button Click Action
        startBtn.setOnAction(e -> {
            this.setVisible(false); // Hide the menu
            onStart.run();          // Let GameMain flip the game state
        });

        getChildren().addAll(title, instructions, startBtn);
    }
}