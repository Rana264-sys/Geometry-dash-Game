package com.spacerunner;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// The "MISSION COMPLETE" overlay shown when a level is finished. Shows the
// run's stats and offers a "next level" button (hidden if there isn't one)
// plus a "back to level select" button.
public class LevelCompleteMenu extends VBox {

    private final Text statsText;
    private final Button nextBtn;

    public LevelCompleteMenu(int width, int height, Runnable onNext, Runnable onMenu) {
        super(20);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: rgba(0, 0, 20, 0.85);");
        setVisible(false);

        Text title = new Text("MISSION COMPLETE");
        title.setFont(Font.font("Consolas", 50));
        title.setFill(Color.web("#44ff88"));

        statsText = new Text("");
        statsText.setFont(Font.font("Consolas", 22));
        statsText.setFill(Color.WHITE);
        statsText.setTextAlignment(TextAlignment.CENTER);

        nextBtn = new Button("NEXT LEVEL");
        styleButton(nextBtn);
        nextBtn.setOnAction(e -> onNext.run());

        Button menuBtn = new Button("LEVEL SELECT");
        styleButton(menuBtn);
        menuBtn.setOnAction(e -> onMenu.run());

        getChildren().addAll(title, statsText, nextBtn, menuBtn);
    }

    // Applies the shared look (outline style, hover highlight) to a menu button.
    private void styleButton(Button btn) {
        btn.setFont(Font.font("Consolas", 22));
        String base = "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; " +
            "-fx-border-width: 2px; -fx-padding: 10px 30px; -fx-cursor: hand;";
        String hover = "-fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 30px; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    // Fills in the status, shows/hides the next-level button depending on
    // whether there is one, then shows the menu.
    public void show(String levelName, int distance, int secondsSurvived, boolean hasNextLevel) {
        statsText.setText(levelName + " CLEARED\nDISTANCE: " + distance + "m   TIME: " + secondsSurvived + "s");
        nextBtn.setVisible(hasNextLevel);
        nextBtn.setManaged(hasNextLevel);
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }
}