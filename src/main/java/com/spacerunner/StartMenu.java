package com.spacerunner;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// The start screen shown before the game begins. It shows the title,
// instructions, and a button to start playing.
public class StartMenu extends VBox {

    // Builds the start screen. onStart runs when the player clicks the button.
    public StartMenu(int width, int height, Runnable onStart) {
        super(30); // 30 pixels of spacing between elements
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);

        // Semi-transparent black background overlay
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

        // Title
        Text title = new Text("SPACE RUNNER");
        title.setFont(Font.font("Consolas", 60));
        title.setFill(Color.WHITE);

        // Instructions
        Text instructions = new Text("MISSION BRIEFING:\n\nPress SPACE to fire thrusters.\nCollect Oxygen to survive.\nAvoid incoming space debris.");
        instructions.setFont(Font.font("Consolas", 20));
        instructions.setFill(Color.LIGHTGRAY);
        instructions.setTextAlignment(TextAlignment.CENTER);

        // Minimalist Ghost Button
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

        // When clicked: hide this menu and tell GameMain to start playing.
        startBtn.setOnAction(e -> {
            this.setVisible(false);
            onStart.run();
        });

        getChildren().addAll(title, instructions, startBtn);
    }
}