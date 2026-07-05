package com.spacerunner;

import javafx.animation.ScaleTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

// Represents the player's spaceship: handles jumping, gravity, landing,
// and the flight animation. Physics run on real elapsed time (delta time)
// rather than per-callback, so the feel is identical regardless of
// frame rate. Also includes a few "juice" touches beyond a bare-bones
// jump: asymmetric gravity for a snappier arc, coyote time so falling off
// a platform edge still allows a jump for a moment, jump buffering so an
// early press near landing isn't dropped, and a squash/stretch pulse.
public class Player extends GameObject {

    private static final double GRAVITY = 2200;             // px/s^2 while rising
    private static final double FALL_GRAVITY_MULTIPLIER = 1.35; // extra gravity while falling, for a snappier arc
    private static final double JUMP_VELOCITY = -780;        // px/s
    private static final double MAX_FALL_SPEED = 1400;       // px/s, terminal velocity
    private static final double LEVEL_LERP_SPEED = 12.0;     // how quickly the ship levels out after landing
    private static final double COYOTE_TIME = 0.12;          // grace period after leaving a platform edge
    private static final double JUMP_BUFFER_TIME = 0.12;     // how long an early jump press is remembered

    // Flappy-bird style tilt: rotation continuously follows vertical
    // velocity instead of spinning at a constant rate - nose pitches up
    // sharply on a flap and dives nose-down as the fall speeds up.
    private static final double RISE_TILT_DEGREES = -25;
    private static final double FALL_TILT_DEGREES = 80;

    private final double FLOOR_Y = 500;

    private double yVelocity = 0;
    private double rotation = 0;
    private boolean onGround = true;
    private boolean hasDoubleJumpCharge = false;
    private int nitroCharges = 0;
    private static final int MAX_NITRO_CHARGES = 3;

    private double coyoteTimer = 0;
    private double jumpBufferTimer = 0;
    private double gravityScale = 1.0; // lets each level tune how heavy the ship feels

    private ImageView imageView;

    // Define visual size and physical size
    private final double VISUAL_WIDTH = 350;
    private final double VISUAL_HEIGHT = 310;
    private final double HITBOX_SIZE = 40;

    // Creates the player at the given starting position.
    public Player(double x, double y) {
        super(x, y);

        Image normalImg = new Image(getClass().getResourceAsStream("/assets/spaceship_normal.png"));

        this.imageView = new ImageView(normalImg);
        this.imageView.setFitWidth(VISUAL_WIDTH);
        this.imageView.setFitHeight(VISUAL_HEIGHT);
        this.imageView.setPreserveRatio(false);

        this.view = imageView;
        this.view.setTranslateX(x);
        this.view.setTranslateY(y - (VISUAL_HEIGHT - HITBOX_SIZE) / 2);

        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;
        this.hitboxSize = HITBOX_SIZE;
    }

    // Runs every frame: applies gravity (in real px/s^2, scaled by deltaTime),
    // checks the floor, handles buffered/coyote jumps, tilts the ship
    // smoothly, and moves the picture to match the player's position.
    @Override
    public void update(double deltaTime) {
        boolean wasOnGround = onGround;

        // Falling uses stronger gravity than rising, for a snappier jump arc.
        double gravity = (yVelocity < 0 ? GRAVITY : GRAVITY * FALL_GRAVITY_MULTIPLIER) * gravityScale;
        yVelocity = Math.min(yVelocity + gravity * deltaTime, MAX_FALL_SPEED);
        y += yVelocity * deltaTime;

        // Floor collision
        if (y >= FLOOR_Y - HITBOX_SIZE) {
            y = FLOOR_Y - HITBOX_SIZE;
            yVelocity = 0;
            onGround = true;
        } else {
            onGround = false;
        }

        // Coyote time: keep a short grace window after leaving the ground
        // where a jump still counts as "from the ground".
        if (onGround) {
            coyoteTimer = COYOTE_TIME;
        } else {
            coyoteTimer = Math.max(0, coyoteTimer - deltaTime);
        }

        if (!wasOnGround && onGround) {
            playLandingSquash();
        }

        // Jump buffering: if the player pressed jump slightly too early,
        // fire it the instant they touch down instead of ignoring it.
        if (jumpBufferTimer > 0) {
            jumpBufferTimer = Math.max(0, jumpBufferTimer - deltaTime);
            if (onGround) {
                doJump();
                jumpBufferTimer = 0;
            }
        }

        // --- ANIMATION LOGIC: ROTATION (flappy-bird style tilt) ---
        if (!onGround) {
            double targetRotation = yVelocity < 0
                ? (yVelocity / JUMP_VELOCITY) * RISE_TILT_DEGREES
                : (yVelocity / MAX_FALL_SPEED) * FALL_TILT_DEGREES;
            rotation += (targetRotation - rotation) * Math.min(1, 15.0 * deltaTime);
        } else {
            rotation += (0 - rotation) * Math.min(1, LEVEL_LERP_SPEED * deltaTime);
            if (Math.abs(rotation) < 0.5) rotation = 0;
        }
        imageView.setRotate(rotation);

        // Keep the visual attached to the logical Y coordinate
        view.setTranslateY((y - (VISUAL_HEIGHT - HITBOX_SIZE) / 2) + 20);
    }

    // Makes the ship jump: normally while grounded, within a short grace
    // window after leaving a platform (coyote time), or mid-air if a
    // double-jump charge is available. An early press otherwise gets
    // buffered so it fires the instant the ship touches down.
    public void jump() {
        if (onGround || coyoteTimer > 0) {
            doJump();
        } else if (hasDoubleJumpCharge) {
            doJump();
            hasDoubleJumpCharge = false;
        } else {
            jumpBufferTimer = JUMP_BUFFER_TIME;
        }
    }

    // Shared logic for any kind of jump (ground, coyote, or double-jump).
    private void doJump() {
        yVelocity = JUMP_VELOCITY;
        onGround = false;
        coyoteTimer = 0;
        playTakeoffStretch();
    }

    // Little squash animation played when the ship touches down.
    private void playLandingSquash() {
        ScaleTransition squash = new ScaleTransition(Duration.millis(120), imageView);
        squash.setFromX(1.15);
        squash.setFromY(0.85);
        squash.setToX(1.0);
        squash.setToY(1.0);
        squash.play();
    }

    // Little stretch animation played the moment the ship jumps.
    private void playTakeoffStretch() {
        ScaleTransition stretch = new ScaleTransition(Duration.millis(100), imageView);
        stretch.setFromX(0.9);
        stretch.setFromY(1.15);
        stretch.setToX(1.0);
        stretch.setToY(1.0);
        stretch.play();
    }

    // Grants one double-jump charge, usable the next time the player
    // jumps while already airborne.
    public void grantDoubleJump() {
        hasDoubleJumpCharge = true;
    }

    public boolean hasDoubleJumpCharge() {
        return hasDoubleJumpCharge;
    }

    // Grants one stored nitro charge (from a nitro pickup), up to a cap.
    public void grantNitroCharge() {
        nitroCharges = Math.min(nitroCharges + 1, MAX_NITRO_CHARGES);
    }

    public int getNitroCharges() {
        return nitroCharges;
    }

    // Spends one nitro charge if available. Returns whether it succeeded.
    public boolean consumeNitroCharge() {
        if (nitroCharges > 0) {
            nitroCharges--;
            return true;
        }
        return false;
    }

    // Places the player exactly on top of a platform (used when landing on a Block).
    public void landOn(double platformTopY) {
        boolean wasOnGround = onGround;
        this.y = platformTopY - HITBOX_SIZE;
        this.yVelocity = 0;
        this.onGround = true;
        if (!wasOnGround) {
            playLandingSquash();
        }
    }

    // Returns how fast the player is moving up/down right now.
    public double getYVelocity() { return yVelocity; }

    // Lets each level tune how heavy the ship feels (e.g. a lighter,
    // floatier gravity on an ice level vs. a harsher pull on a wasteland).
    public void setGravityScale(double gravityScale) {
        this.gravityScale = gravityScale;
    }

    // Resets the ship back to its starting position/state (used when a
    // level starts or restarts).
    public void reset(double startY) {
        this.y = startY;
        this.yVelocity = 0;
        this.rotation = 0;
        this.onGround = true;
        this.hasDoubleJumpCharge = false;
        this.nitroCharges = 0;
        this.coyoteTimer = COYOTE_TIME;
        this.jumpBufferTimer = 0;
        imageView.setRotate(0);
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        view.setTranslateY((y - (VISUAL_HEIGHT - HITBOX_SIZE) / 2) + 20);
    }
}