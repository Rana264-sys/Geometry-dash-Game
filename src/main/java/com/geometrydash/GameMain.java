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

public class GameMain extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private final double FLOOR_Y = 500;

    private Player player;
    private List<Obstacle> activeObstacles = new ArrayList<>();
    private Pane root;
    private AnimationTimer gameLoop;

    // Game State & Difficulty Variables
    private GameState currentState = GameState.MENU;
    private double globalSpeed = -6.0;
    private int frameCount = 0;
    private int nextSpawnFrame = 90;

    // Hearts tracking
    private int hearts = 0; 
    private final int MAX_HEARTS = 3;
    private int heartsSpawnedPhase1 = 0; 
    private int heartsSpawnedPhase2 = 0; 

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        Rectangle floor = new Rectangle(0, FLOOR_Y, WIDTH, HEIGHT - FLOOR_Y);
        floor.setFill(Color.DARKGRAY);

        player = new Player(100, FLOOR_Y - 40);
        root.getChildren().addAll(floor, player.getView());

        scene.setOnKeyPressed(event -> handleInput(event.getCode()));

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentState == GameState.PLAYING) {
                    updateGame();
                }
            }
        };
        gameLoop.start(); 

        primaryStage.setTitle("Geometry Dash Clone");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        System.out.println("Game Loaded. State: MENU. Press ENTER to start.");
    }

    // --- MAIN GAME LOOP ---
    private void updateGame() {
        player.update();
        frameCount++;

        checkPhaseTransitions();
        handleSpawning();
        handleObstacles();
    }

    // --- HELPER METHODS ---

    private void handleInput(KeyCode code) {
        if (currentState == GameState.MENU) {
            if (code == KeyCode.ENTER) {
                currentState = GameState.PLAYING;
                System.out.println("State Changed: PLAYING");
            }
        } else if (currentState == GameState.PLAYING) {
            if (code == KeyCode.SPACE || code == KeyCode.UP) {
                player.jump();
            }
        } else if (currentState == GameState.GAME_OVER) {
            if (code == KeyCode.ENTER) {
                System.out.println("Restart logic coming soon!");
            }
        }
    }

    private void checkPhaseTransitions() {
        if (frameCount == 1800) { 
            globalSpeed = -8.0;   
            System.out.println("PHASE 2: Moderate Mode!");
        } else if (frameCount == 7200) { 
            globalSpeed = -10.0;  
            System.out.println("PHASE 3: Hard Mode!");
        }
    }

    private void handleSpawning() {
        if (frameCount >= nextSpawnFrame) {
            List<Obstacle> newSpawns = new ArrayList<>();

            if (frameCount < 1800 && heartsSpawnedPhase1 < 1 && Math.random() > 0.7) {
                newSpawns.add(new Heart(WIDTH, FLOOR_Y));
                heartsSpawnedPhase1++;
            } 
            else if (frameCount >= 1800 && frameCount < 7200 && heartsSpawnedPhase2 < 2 && Math.random() > 0.7) {
                newSpawns.add(new Heart(WIDTH, FLOOR_Y));
                heartsSpawnedPhase2++;
            } 
            else {
                if (frameCount > 7200 && Math.random() > 0.6) {
                    // The Choke Point
                    newSpawns.add(new Block(WIDTH, FLOOR_Y));
                    Obstacle ceilingSpike = new Spike(WIDTH + 250, 360); 
                    ceilingSpike.getView().setRotate(180);
                    newSpawns.add(ceilingSpike);
                } else {
                    newSpawns.add(Math.random() > 0.5 ? new Spike(WIDTH, FLOOR_Y) : new Block(WIDTH, FLOOR_Y));
                }
            }

            for (Obstacle obs : newSpawns) {
                obs.setXVelocity(globalSpeed);
                activeObstacles.add(obs);
                root.getChildren().add(obs.getView());
            }

            // Calculate random delay for the next obstacle
            int baseRate = (frameCount > 7200) ? 75 : (frameCount > 1800) ? 65 : 90;
            int randomVariance = (int)(Math.random() * 30) - 15; 
            int actualDelay = Math.max(20, baseRate + randomVariance); 
            nextSpawnFrame = frameCount + actualDelay;
        }
    }

    private void handleObstacles() {
        Iterator<Obstacle> iter = activeObstacles.iterator();
        while (iter.hasNext()) {
            Obstacle obs = iter.next();
            obs.update();

            String collision = obs.checkCollision(player);
            
            if (collision.equals("HEART")) {
                if (hearts < MAX_HEARTS) {
                    hearts++;
                    System.out.println("⭐ Heart Collected! Total Extra Lives: " + hearts);
                }
                root.getChildren().remove(obs.getView());
                iter.remove();
            } 
            else if (collision.equals("DEATH")) {
                if (hearts > 0) {
                    hearts--;
                    System.out.println("🛡️ Shield broken! Lives left: " + hearts);
                    root.getChildren().remove(obs.getView());
                    iter.remove(); 
                } else {
                    System.out.println("💀 GAME OVER! You survived for " + (frameCount / 60) + " seconds.");
                    currentState = GameState.GAME_OVER;
                }
            } 
            else if (collision.equals("PLATFORM")) {
                player.landOn(obs.getBounds().getMinY());
            }

            // Memory cleanup
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