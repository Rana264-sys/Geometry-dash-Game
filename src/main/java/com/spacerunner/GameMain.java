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

// The main entry point and game engine. Owns the game loop, input handling,
// the list of active obstacles/pickups, collision resolution, nitro timing,
// and switching between menu/playing/game-over/level-complete screens.
public class GameMain extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private final double FLOOR_Y = 500;

    private static final double NITRO_DURATION_SECONDS = 3.0;
    private static final double NITRO_MULTIPLIER = 1.6;
    private static final double MAX_DELTA_TIME = 0.05; // can't skip the player through a wall

    // Chosen so Level 1's numbers match the old elapsedSeconds*6 formula
    // (390 px/s / 65 = 6 m/s), but now every level's distance reflects its
    // own scroll speed, and nitro boosts correctly add extra distance too.
    private static final double PIXELS_PER_METER = 65;

    private Player player;
    private List<ScrollingObject> activeObjects = new ArrayList<>(); // everything currently on screen
    private Pane root;
    private AnimationTimer gameLoop;
    private Background background;
    private long lastNanoTime = -1;

    private GameState currentState = GameState.MENU;
    private double elapsedSeconds = 0;
    private double distanceTraveled = 0; // accumulated px, at actual on-screen scroll speed

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

    // JavaFX calls this once at startup. Builds the scene, wires up input
    // and menus, and starts the game loop.
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

        // The core game loop: runs every frame, computes real elapsed time
        // (delta time), and only updates gameplay while actually playing.
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

    // Runs one frame of actual gameplay: updates nitro, moves the player and
    // background, spawns new obstacles, resolves collisions, and checks if
    // the level is finished.
    private void updateGame(double deltaTime) {
        updateNitro(deltaTime);
        double effectiveSpeed = nitroActive ? currentLevel.getSpeed() * NITRO_MULTIPLIER : currentLevel.getSpeed();

        player.update(deltaTime);
        elapsedSeconds += deltaTime;
        distanceTraveled += Math.abs(effectiveSpeed) * deltaTime;
        hud.updateDistance(distanceTraveled / PIXELS_PER_METER);
        background.update(effectiveSpeed * deltaTime);
        handleSpawning(effectiveSpeed);
        handleObstacles(effectiveSpeed, deltaTime);

        if (currentLevel.isComplete(elapsedSeconds, activeObjects.isEmpty())) {
            showLevelCompleteScreen();
        }
    }

    // Reacts to key presses: jump, activate nitro, or open the menu.
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

    // Counts down the active nitro boost and turns it off when it runs out.
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

    // Turns on the nitro speed boost for a fixed duration, with a glow effect.
    private void activateNitro() {
        nitroActive = true;
        nitroSecondsLeft = NITRO_DURATION_SECONDS;
        player.getView().setEffect(new Glow(0.8));
        hud.updateNitroActive(true);
    }

    // Asks the current level if anything is due to spawn, and adds it to
    // the scene and the active-objects list if so.
    private void handleSpawning(double effectiveSpeed) {
        List<ScrollingObject> newSpawns = currentLevel.trySpawn(elapsedSeconds, WIDTH, FLOOR_Y);
        for (ScrollingObject obs : newSpawns) {
            obs.setXVelocity(effectiveSpeed);
            activeObjects.add(obs);
            root.getChildren().add(obs.getView());
        }
    }

    // Moves every active obstacle/pickup, checks each one for contact with
    // the player, and reacts based on what kind of contact it was.
    // Also removes anything that's scrolled off screen.
    private void handleObstacles(double effectiveSpeed, double deltaTime) {
        Iterator<ScrollingObject> iter = activeObjects.iterator();
        while (iter.hasNext()) {
            ScrollingObject obs = iter.next();
            obs.setXVelocity(effectiveSpeed);
            obs.update(deltaTime);

            ContactResult collision = obs.checkCollision(player);

            switch (collision) {
                case SHIELD:
                    // Picked up a shield charge (capped at MAX_SHIELDS).
                    if (shields < MAX_SHIELDS) {
                        shields++;
                        hud.updateShields(shields);
                        System.out.println("Shield Collected! Shields: " + shields);
                    }
                    root.getChildren().remove(obs.getView());
                    iter.remove();
                    break;

                case NITRO:
                    // Picked up a nitro charge, ready to use with SHIFT.
                    player.grantNitroCharge();
                    hud.updateNitroReady(player.getNitroCharges());
                    System.out.println("Nitro charge collected!");
                    root.getChildren().remove(obs.getView());
                    iter.remove();
                    break;

                case DOUBLE_JUMP:
                    // Picked up an extra mid-air jump.
                    player.grantDoubleJump();
                    hud.updateDoubleJump(true);
                    System.out.println("Double Jump charge collected!");
                    root.getChildren().remove(obs.getView());
                    iter.remove();
                    break;

                case DEATH:
                    // Hit something harmful. A shield absorbs the hit if
                    // available; otherwise it's game over.
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
                    // Landed safely on top of a block.
                    player.landOn(obs.getBounds().getMinY());
                    break;

                case NONE:
                default:
                    // No collision this frame
                    break;
            }

            // Memory cleanup: drop anything that's scrolled off the left edge.
            if (obs.isOffScreen()) {
                root.getChildren().remove(obs.getView());
                iter.remove();
            }
        }
    }

    // Switches to the game-over screen and shows the final run stats.
    private void showGameOverScreen() {
        currentState = GameState.GAME_OVER;
        int distance = (int) (distanceTraveled / PIXELS_PER_METER);
        int secondsSurvived = (int) elapsedSeconds;
        gameOverMenu.show(distance, secondsSurvived);
    }

    // Switches to the level-complete screen and shows the final run stats.
    private void showLevelCompleteScreen() {
        currentState = GameState.LEVEL_COMPLETE;
        int distance = (int) (distanceTraveled / PIXELS_PER_METER);
        int secondsSurvived = (int) elapsedSeconds;
        boolean hasNextLevel = currentLevelIndex + 1 < LevelData.NAMES.length;
        levelCompleteMenu.show(currentLevel.getName(), distance, secondsSurvived, hasNextLevel);
    }

    // Sets up everything needed to start (or restart) a level: loads the
    // level data, resets the player/HUD/timers, clears old obstacles, and
    // switches to the PLAYING state.
    private void loadLevel(int levelIndex) {
        currentLevelIndex = levelIndex;
        currentLevel = LevelData.get(levelIndex);
        background.setTheme(
            currentLevel.getStarsColor(), currentLevel.getStarsIntensity(),
            currentLevel.getFloorColor(), currentLevel.getFloorIntensity()
        );

        elapsedSeconds = 0;
        distanceTraveled = 0;
        shields = 0;
        nitroActive = false;
        nitroSecondsLeft = 0;

        player.getView().setEffect(null);
        player.getView().setOpacity(1.0);
        player.reset(FLOOR_Y - 40);
        player.setGravityScale(currentLevel.getGravityMultiplier());

        for (ScrollingObject obs : activeObjects) {
            root.getChildren().remove(obs.getView());
        }
        activeObjects.clear();

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

    // Restarts the level the player just failed.
    private void restartLevel() {
        loadLevel(currentLevelIndex);
    }

    // Moves on to the next level, or back to level select if this was the last one.
    private void goToNextLevel() {
        if (currentLevelIndex + 1 < LevelData.NAMES.length) {
            loadLevel(currentLevelIndex + 1);
        } else {
            showLevelSelect();
        }
    }

    // Returns to the level-select menu.
    private void showLevelSelect() {
        currentState = GameState.MENU;
        gameOverMenu.hide();
        levelCompleteMenu.hide();
        startMenu.setVisible(true);
    }

    // Standard JavaFX launch point.
    public static void main(String[] args) {
        launch(args);
    }
}