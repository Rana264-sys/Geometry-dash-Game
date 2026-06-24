package com.geometrydash;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The core game manager. 
 * Handles the window creation, scene setup, keyboard input, and the primary 60 FPS game loop.
 */
public class GameMain extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private final double FLOOR_Y = 500;

    private Player player;
    private List<Obstacle> activeObstacles = new ArrayList<>();
    private int frameCount = 0;
    private Pane root;
    private AnimationTimer gameLoop;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        Rectangle floor = new Rectangle(0, FLOOR_Y, WIDTH, HEIGHT - FLOOR_Y);
        floor.setFill(Color.DARKGRAY);

        player = new Player(100, FLOOR_Y - 40);
        root.getChildren().addAll(floor, player.getView());

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.UP) {
                player.jump();
            }
        });

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
            }
        };
        gameLoop.start();

        primaryStage.setTitle("Geometry Dash");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void updateGame() {
        player.update();

        // 1. Spawner: Add a new obstacle every 90 frames (1.5 seconds)// Maybe adjust later according to the difficulty 
        frameCount++;
        if (frameCount % 90 == 0) {
            // 50% chance for a Spike, 50% chance for a Block
            Obstacle obs = Math.random() > 0.5 ? new Spike(WIDTH, FLOOR_Y) : new Block(WIDTH, FLOOR_Y);
            activeObstacles.add(obs);
            root.getChildren().add(obs.getView());
        }

        // 2. Update all active obstacles
        Iterator<Obstacle> iter = activeObstacles.iterator();
        while (iter.hasNext()) {
            Obstacle obs = iter.next();
            obs.update();

            // 3. Collision Logic
            String collision = obs.checkCollision(player);
            if (collision.equals("DEATH")) {
                System.out.println("GAME OVER!");
                gameLoop.stop(); // Freeze the game
            } else if (collision.equals("PLATFORM")) {
                player.landOn(obs.getBounds().getMinY());
            }

            // 4. Memory Cleanup
            if (obs.isOffScreen()) {
                root.getChildren().remove(obs.getView());
                iter.remove();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}