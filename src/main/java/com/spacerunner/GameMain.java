package com.spacerunner;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameMain extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private final double FLOOR_Y = 500;

    private static final double NITRO_DURATION_SECONDS = 3.0;
    private static final double NITRO_MULTIPLIER = 1.6;
    private static final double MAX_DELTA_TIME = 0.05; // can't skip the player through a wall

    private Player player;
    private List<Obstacle> activeObstacles = new ArrayList<>();
    private Pane root;
    private AnimationTimer gameLoop;
    private Background background;
    private long lastNanoTime = -1;

    private GameState currentState = GameState.MENU;
    private double elapsedSeconds = 0;

    private int shields = 0;
    private final int MAX_SHIELDS = 3;

    private boolean nitroActive = false;
    private double nitroSecondsLeft = 0;

    private int currentLevelIndex = 0;
    private Level currentLevel;

    private HUD hud;
    private StartMenu startMenu;
    private GameOverMenu gameOverMenu;
    private LevelCompleteMenu levelCompleteMenu;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        background = new Background(WIDTH, HEIGHT, FLOOR_Y);

        player = new Player(100, FLOOR_Y - 40);

        hud = new HUD(WIDTH);
        startMenu = new StartMenu(WIDTH, HEIGHT, this::loadLevel);
        gameOverMenu = new GameOverMenu(WIDTH, HEIGHT, this::restartLevel);
        levelCompleteMenu = new LevelCompleteMenu(WIDTH, HEIGHT, this::goToNextLevel, this::showLevelSelect);

        root.getChildren().addAll(
            background.getStarsA(), background.getStarsB(),
            background.getFloorA(), background.getFloorB(),
            background.getStarsOverlay(), background.getFloorOverlay(), background.getFloorBorder(),
            player.getView(),
            hud.getScoreText(), hud.getLifeBox(), hud.getNitroActiveText(), hud.getNitroReadyText(),
            hud.getDoubleJumpText(), hud.getMenuButton(),
            startMenu, gameOverMenu, levelCompleteMenu
        );

        hud.getMenuButton().setOnMouseClicked(event -> {
            if (currentState == GameState.PLAYING) {
                showLevelSelect();
            }
        });

        scene.setOnKeyPressed(event -> handleInput(event.getCode()));

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanoTime < 0) {
                    lastNanoTime = now;
                    return;
                }
                double deltaTime = Math.min((now - lastNanoTime) / 1_000_000_000.0, MAX_DELTA_TIME);
                lastNanoTime = now;

                if (currentState == GameState.PLAYING) {
                    updateGame(deltaTime);
                }
            }
        };

        gameLoop.start();

        primaryStage.setTitle("Space Runner");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        System.out.println("Game Loaded. State: MENU. Pick a level to start.");
    }


    private void updateGame(double deltaTime) {
        updateNitro(deltaTime);
        double effectiveSpeed = nitroActive ? currentLevel.getSpeed() * NITRO_MULTIPLIER : currentLevel.getSpeed();

        player.update(deltaTime);
        elapsedSeconds += deltaTime;
        hud.updateDistance(elapsedSeconds);
        background.update(effectiveSpeed * deltaTime);
        handleSpawning(effectiveSpeed);
        handleObstacles(effectiveSpeed, deltaTime);

        if (currentLevel.isComplete(elapsedSeconds, activeObstacles.isEmpty())) {
            showLevelCompleteScreen();
        }
    }


    private void handleInput(KeyCode code) {
        if (currentState != GameState.PLAYING) {
            return;
        }
        if (code == KeyCode.SPACE || code == KeyCode.UP) {
            player.jump();
            hud.updateDoubleJump(player.hasDoubleJumpCharge());
        } else if (code == KeyCode.SHIFT) {
            if (!nitroActive && player.consumeNitroCharge()) {
                activateNitro();
                hud.updateNitroReady(player.getNitroCharges());
            }
        } else if (code == KeyCode.ESCAPE) {
            showLevelSelect();
        }
    }

    private void updateNitro(double deltaTime) {
        if (nitroActive) {
            nitroSecondsLeft -= deltaTime;
            if (nitroSecondsLeft <= 0) {
                nitroActive = false;
                player.getView().setEffect(null);
                hud.updateNitroActive(false);
            }
        }
    }

    private void activateNitro() {
        nitroActive = true;
        nitroSecondsLeft = NITRO_DURATION_SECONDS;
        player.getView().setEffect(new Glow(0.8));
        hud.updateNitroActive(true);
    }

    private void handleSpawning(double effectiveSpeed) {
        List<Obstacle> newSpawns = currentLevel.trySpawn(elapsedSeconds, WIDTH, FLOOR_Y);
        for (Obstacle obs : newSpawns) {
            obs.setXVelocity(effectiveSpeed);
            activeObstacles.add(obs);
            root.getChildren().add(obs.getView());
        }
    }


    private void handleObstacles(double effectiveSpeed, double deltaTime) {
        Iterator<Obstacle> iter = activeObstacles.iterator();
        while (iter.hasNext()) {
            Obstacle obs = iter.next();
            obs.setXVelocity(effectiveSpeed);
            obs.update(deltaTime);

            CollisionResult collision = obs.checkCollision(player);

            switch (collision) {
                case SHIELD:
                    if (shields < MAX_SHIELDS) {
                        shields++;
                        hud.updateShields(shields);
                        System.out.println("Shield Collected! Shields: " + shields);
                    }
                    root.getChildren().remove(obs.getView());
                    iter.remove();
                    break;

                case NITRO:
                    player.grantNitroCharge();
                    hud.updateNitroReady(player.getNitroCharges());
                    System.out.println("Nitro charge collected!");
                    root.getChildren().remove(obs.getView());
                    iter.remove();
                    break;

                case DOUBLE_JUMP:
                    player.grantDoubleJump();
                    hud.updateDoubleJump(true);
                    System.out.println("Double Jump charge collected!");
                    root.getChildren().remove(obs.getView());
                    iter.remove();
                    break;

                case DEATH:
                    if (shields > 0) {
                        shields--;
                        hud.updateShields(shields);
                        System.out.println("Shield absorbed the hit! Shields left: " + shields);

                        // visual feedback
                        player.getView().setOpacity(0.5);
                        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                        pause.setOnFinished(e -> player.getView().setOpacity(1.0));
                        pause.play();

                        root.getChildren().remove(obs.getView());
                        iter.remove();
                    } else {
                        System.out.println("GAME OVER! You survived for " + (int) elapsedSeconds + " seconds.");
                        showGameOverScreen();
                    }
                    break;

                case PLATFORM:
                    player.landOn(obs.getBounds().getMinY());
                    break;

                case NONE:
                default:
                    // No collision this frame
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
        int distance = (int) (elapsedSeconds * 6);
        int secondsSurvived = (int) elapsedSeconds;
        gameOverMenu.show(distance, secondsSurvived);
    }

    private void showLevelCompleteScreen() {
        currentState = GameState.LEVEL_COMPLETE;
        int distance = (int) (elapsedSeconds * 6);
        int secondsSurvived = (int) elapsedSeconds;
        boolean hasNextLevel = currentLevelIndex + 1 < LevelData.NAMES.length;
        levelCompleteMenu.show(currentLevel.getName(), distance, secondsSurvived, hasNextLevel);
    }

    private void loadLevel(int levelIndex) {
        currentLevelIndex = levelIndex;
        currentLevel = LevelData.get(levelIndex);
        background.setTheme(
            currentLevel.getStarsColor(), currentLevel.getStarsIntensity(),
            currentLevel.getFloorColor(), currentLevel.getFloorIntensity()
        );

        elapsedSeconds = 0;
        shields = 0;
        nitroActive = false;
        nitroSecondsLeft = 0;

        player.getView().setEffect(null);
        player.getView().setOpacity(1.0);
        player.reset(FLOOR_Y - 40);
        player.setGravityScale(currentLevel.getGravityMultiplier());

        for (Obstacle obs : activeObstacles) {
            root.getChildren().remove(obs.getView());
        }
        activeObstacles.clear();

        hud.reset();
        hud.updateShields(shields);
        hud.updateNitroActive(false);
        hud.updateNitroReady(0);
        hud.updateDoubleJump(false);

        gameOverMenu.hide();
        levelCompleteMenu.hide();
        startMenu.setVisible(false);

        currentState = GameState.PLAYING;
        System.out.println("State Changed: PLAYING (" + currentLevel.getName() + ")");
    }

    private void restartLevel() {
        loadLevel(currentLevelIndex);
    }
    private void goToNextLevel() {
        if (currentLevelIndex + 1 < LevelData.NAMES.length) {
            loadLevel(currentLevelIndex + 1);
        } else {
            showLevelSelect();
        }
    }
    private void showLevelSelect() {
        currentState = GameState.MENU;
        gameOverMenu.hide();
        levelCompleteMenu.hide();
        startMenu.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
