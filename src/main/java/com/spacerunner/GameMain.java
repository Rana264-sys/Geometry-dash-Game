package com.spacerunner;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    // Game State
    private GameState currentState = GameState.MENU;
    private int frameCount = 0;

    // Hearts tracking
    private int hearts = 0;
    private final int MAX_HEARTS = 3;

    // Collaborators
    private ObstacleSpawner spawner;
    private HUD hud;
    private StartMenu startMenu;
    private GameOverMenu gameOverMenu;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // --- 1. SETUP DEEP SPACE BACKGROUND ---
        Image bgImage = new Image(getClass().getResourceAsStream("/assets/space_bg.jpg"));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(WIDTH);
        bgView.setFitHeight(HEIGHT);
        bgView.setPreserveRatio(false);

        // --- 2. SETUP MOON FLOOR ---
        Rectangle floor = new Rectangle(0, FLOOR_Y, WIDTH, HEIGHT - FLOOR_Y);
        Image floorTexture = new Image(getClass().getResourceAsStream("/assets/moon_floor.png"));
        floor.setFill(new ImagePattern(floorTexture, 0, 0, 1, 1, true));
        floor.setStroke(Color.web("#505254"));
        floor.setStrokeWidth(3);

        player = new Player(100, FLOOR_Y - 40);

        // --- 3. SETUP COLLABORATORS (HUD, menus, spawner) ---
        spawner = new ObstacleSpawner(WIDTH, FLOOR_Y);
        hud = new HUD(WIDTH);
        startMenu = new StartMenu(WIDTH, HEIGHT, () -> {
            currentState = GameState.PLAYING;
            System.out.println("State Changed: PLAYING");
        });
        
        gameOverMenu = new GameOverMenu(WIDTH, HEIGHT, this::restartGame);

        root.getChildren().addAll(
            bgView, floor, player.getView(),
            hud.getScoreText(), hud.getLifeBox(),
            startMenu, gameOverMenu
        );

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

        primaryStage.setTitle("Space Runner");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        System.out.println("Game Loaded. State: MENU. Press ENTER to start.");
    }

    // --- MAIN GAME LOOP ---
    private void updateGame() {
        player.update();
        frameCount++;
        hud.updateDistance(frameCount);
        spawner.updateDifficulty(frameCount);
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

    private void handleSpawning() {
        List<Obstacle> newSpawns = spawner.trySpawn(frameCount);
        for (Obstacle obs : newSpawns) {
            activeObstacles.add(obs);
            root.getChildren().add(obs.getView());
        }
    }

    private void handleObstacles() {
        Iterator<Obstacle> iter = activeObstacles.iterator();
        while (iter.hasNext()) {
            Obstacle obs = iter.next();
            obs.update();

            CollisionResult collision = obs.checkCollision(player);

            switch (collision) {
                case HEART:
                    if (hearts < MAX_HEARTS) {
                        hearts++;
                        hud.updateLives(hearts);
                        System.out.println("⭐ Oxygen Collected! Total Extra Lives: " + hearts);
                    }
                    root.getChildren().remove(obs.getView());
                    iter.remove();
                    break;

                case DEATH:
                    if (hearts > 0) {
                        hearts--;
                        hud.updateLives(hearts);
                        System.out.println("🛡️ Oxygen depleted! Lives left: " + hearts);

                        // Add a brief visual feedback (optional)
                        player.getView().setOpacity(0.5);
                        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                        pause.setOnFinished(e -> player.getView().setOpacity(1.0));
                        pause.play();

                        root.getChildren().remove(obs.getView());
                        iter.remove();
                    } else {
                        System.out.println("💀 GAME OVER! You survived for " + (frameCount / 60) + " seconds.");
                        showGameOverScreen();
                    }
                    break;

                case PLATFORM:
                    player.landOn(obs.getBounds().getMinY());
                    break;

                case NONE:
                default:
                    // No collision this frame - nothing to do.
                    break;
            }

            // Memory cleanup
            if (obs.isOffScreen()) {
                root.getChildren().remove(obs.getView());
                iter.remove();
            }
        }
    }

    private void showGameOverScreen() {
        currentState = GameState.GAME_OVER;
        int distance = frameCount / 10;
        int secondsSurvived = frameCount / 60;
        gameOverMenu.show(distance, secondsSurvived);
    }

    private void restartGame() {
        // Reset time and difficulty via the spawner
        frameCount = 0;
        spawner.reset();

        // Reset hearts
        hearts = 0;

        // Reset player position
        player.getView().setTranslateY(FLOOR_Y - 40);

        // Clear all obstacles
        for (Obstacle obs : activeObstacles) {
            root.getChildren().remove(obs.getView());
        }
        activeObstacles.clear();

        // Update UI
        hud.updateLives(hearts);
        hud.reset();
        gameOverMenu.hide();

        // Resume game
        currentState = GameState.PLAYING;
    }

    public static void main(String[] args) {
        launch(args);
    }
}