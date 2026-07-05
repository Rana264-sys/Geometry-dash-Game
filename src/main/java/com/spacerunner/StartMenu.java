package com.spacerunner;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.function.IntConsumer;

// The title/level-select screen shown at launch and when returning to the
// menu. Shows one button per level; clicking a button starts that level.
public class StartMenu extends VBox {

    public StartMenu(int width, int height, IntConsumer onSelectLevel) {
        super(12);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

        Text title = new Text("SPACE RUNNER");
        title.setFont(Font.font("Consolas", 48));
        title.setFill(Color.WHITE);

        Text instructions = new Text(
            "SPACE / UP to fire thrusters. Collect a Shield to absorb the next hit.\n" +
            "Collect Nitro and press SHIFT for a speed boost. Grab the double-jump icon for an extra jump. Choose a mission:"
        );
        instructions.setFont(Font.font("Consolas", 15));
        instructions.setFill(Color.LIGHTGRAY);
        instructions.setTextAlignment(TextAlignment.CENTER);

        getChildren().addAll(title, instructions);

        // One button per level, wired to call back into GameMain with the chosen index.
        for (int i = 0; i < LevelData.NAMES.length; i++) {
            final int levelIndex = i;
            Button levelBtn = new Button(LevelData.NAMES[i]);
            styleButton(levelBtn);
            levelBtn.setOnAction(e -> {
                this.setVisible(false);
                onSelectLevel.accept(levelIndex);
            });
            getChildren().add(levelBtn);
        }
    }

    // Applies the shared look (outline style, hover highlight) to a menu button.
    private void styleButton(Button btn) {
        btn.setFont(Font.font("Consolas", 18));
        String base = "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; " +
            "-fx-border-width: 2px; -fx-padding: 8px 24px; -fx-cursor: hand;";
        String hover = "-fx-background-color: white; -fx-text-fill: black; -fx-padding: 8px 24px; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }
}