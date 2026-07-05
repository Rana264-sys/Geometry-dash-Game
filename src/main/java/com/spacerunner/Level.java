package com.spacerunner;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

// A hand-designed sequence of obstacles that plays out over a fixed
// stretch of time, plus a color theme for the background/floor. A level
// is "complete" once every scripted obstacle has spawned and scrolled
// off screen. Each level is meant to have its own identity (layout style
// and look), not just a faster version of the same obstacle mix.
public class Level {

    // One scripted obstacle: what to spawn and how many seconds into the
    // level it should appear.
    private static class SpawnEvent {
        final double seconds;
        final Kind kind;

        SpawnEvent(double seconds, Kind kind) {
            this.seconds = seconds;
            this.kind = kind;
        }
    }

    private enum Kind { SPIKE, CEILING_SPIKE, BLOCK, SHIELD, NITRO, DOUBLE_JUMP, MOVING_BLOCK, SPORE_SWARM, FALLING_DEBRIS }

    private final String name;
    private final double speed; // px/s
    private final double gravityMultiplier;
    private final List<SpawnEvent> events;
    private final double endTime;
    private int nextEventIndex = 0;

    private final Color starsColor;
    private final double starsIntensity;
    private final Color floorColor;
    private final double floorIntensity;

    private Level(String name, double speed, double gravityMultiplier, List<SpawnEvent> events,
                  Color starsColor, double starsIntensity, Color floorColor, double floorIntensity) {
        this.name = name;
        this.speed = speed;
        this.gravityMultiplier = gravityMultiplier;
        this.events = events;
        double lastSeconds = events.isEmpty() ? 0 : events.get(events.size() - 1).seconds;
        this.endTime = lastSeconds + 4.0; // buffer so the last obstacle can scroll off screen
        this.starsColor = starsColor;
        this.starsIntensity = starsIntensity;
        this.floorColor = floorColor;
        this.floorIntensity = floorIntensity;
    }

    // Returns any obstacles that are due to spawn by this point in time.
    public List<Obstacle> trySpawn(double elapsedSeconds, double spawnX, double floorY) {
        List<Obstacle> spawns = new ArrayList<>();
        while (nextEventIndex < events.size() && events.get(nextEventIndex).seconds <= elapsedSeconds) {
            spawns.add(create(events.get(nextEventIndex).kind, spawnX, floorY));
            nextEventIndex++;
        }
        return spawns;
    }

    private Obstacle create(Kind kind, double spawnX, double floorY) {
        switch (kind) {
            case SHIELD:
                return new ShieldItem(spawnX, floorY);
            case NITRO:
                return new NitroItem(spawnX, floorY);
            case DOUBLE_JUMP:
                return new DoubleJumpItem(spawnX, floorY);
            case BLOCK:
                return new Block(spawnX, floorY);
            case CEILING_SPIKE:
                Obstacle ceilingSpike = new Spike(spawnX, floorY - 140);
                ceilingSpike.getView().setRotate(180);
                return ceilingSpike;
            case MOVING_BLOCK:
                return new MovingBlock(spawnX, floorY);
            case SPORE_SWARM:
                return new SporeSwarm(spawnX, floorY);
            case FALLING_DEBRIS:
                return new FallingDebris(spawnX, floorY);
            case SPIKE:
            default:
                return new Spike(spawnX, floorY);
        }
    }

    // True once every scripted obstacle has spawned, the tail buffer has
    // elapsed, and nothing from this level is still on screen.
    public boolean isComplete(double elapsedSeconds, boolean noObstaclesLeft) {
        return nextEventIndex >= events.size() && elapsedSeconds >= endTime && noObstaclesLeft;
    }

    public void reset() {
        nextEventIndex = 0;
    }

    public double getSpeed() { return speed; }
    public double getGravityMultiplier() { return gravityMultiplier; }
    public String getName() { return name; }
    public Color getStarsColor() { return starsColor; }
    public double getStarsIntensity() { return starsIntensity; }
    public Color getFloorColor() { return floorColor; }
    public double getFloorIntensity() { return floorIntensity; }

    // --- Builder for authoring a level's obstacle timeline in seconds ---
    public static class Builder {
        private final String name;
        private final double speed;
        private double gravityMultiplier = 1.0;
        private final List<SpawnEvent> events = new ArrayList<>();
        private Color starsColor = Color.TRANSPARENT;
        private double starsIntensity = 0;
        private Color floorColor = Color.TRANSPARENT;
        private double floorIntensity = 0;

        public Builder(String name, double speed) {
            this.name = name;
            this.speed = speed;
        }

        public Builder spike(double seconds) { events.add(new SpawnEvent(seconds, Kind.SPIKE)); return this; }
        public Builder ceilingSpike(double seconds) { events.add(new SpawnEvent(seconds, Kind.CEILING_SPIKE)); return this; }
        public Builder block(double seconds) { events.add(new SpawnEvent(seconds, Kind.BLOCK)); return this; }
        public Builder shield(double seconds) { events.add(new SpawnEvent(seconds, Kind.SHIELD)); return this; }
        public Builder nitro(double seconds) { events.add(new SpawnEvent(seconds, Kind.NITRO)); return this; }
        public Builder doubleJump(double seconds) { events.add(new SpawnEvent(seconds, Kind.DOUBLE_JUMP)); return this; }
        public Builder movingBlock(double seconds) { events.add(new SpawnEvent(seconds, Kind.MOVING_BLOCK)); return this; }
        public Builder sporeSwarm(double seconds) { events.add(new SpawnEvent(seconds, Kind.SPORE_SWARM)); return this; }
        public Builder fallingDebris(double seconds) { events.add(new SpawnEvent(seconds, Kind.FALLING_DEBRIS)); return this; }

        // Tunes how heavy the ship feels on this level (1.0 = default).
        public Builder gravity(double multiplier) { this.gravityMultiplier = multiplier; return this; }

        // Sets the per-level color grading applied over the starfield and
        // the moon floor (both are otherwise identical art shared by every level).
        public Builder theme(Color starsColor, double starsIntensity, Color floorColor, double floorIntensity) {
            this.starsColor = starsColor;
            this.starsIntensity = starsIntensity;
            this.floorColor = floorColor;
            this.floorIntensity = floorIntensity;
            return this;
        }

        public Level build() {
            events.sort((a, b) -> Double.compare(a.seconds, b.seconds));
            return new Level(name, speed, gravityMultiplier, events, starsColor, starsIntensity, floorColor, floorIntensity);
        }
    }
}
