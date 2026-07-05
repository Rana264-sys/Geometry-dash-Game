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

    // One scripted spawn: what to create and how many seconds into the
    // level it should appear.
    private static class SpawnEvent {
        final double seconds;
        final Kind kind;

        SpawnEvent(double seconds, Kind kind) {
            this.seconds = seconds;
            this.kind = kind;
        }
    }

    // Every type of thing a level can spawn (obstacles and pickups alike).
    private enum Kind { SPIKE, CEILING_SPIKE, BLOCK, SHIELD, NITRO, DOUBLE_JUMP, MOVING_BLOCK, SPORE_SWARM, FALLING_DEBRIS }

    // How much a spawn's timing can drift from its authored value, in
    // either direction. Small enough that the overall pacing and the
    // order of events stays intact, big enough that no two playthroughs
    // of the same level feel exactly the same.
    private static final double SPAWN_JITTER_SECONDS = 0.35;

    private final String name;
    private final double speed; // px/s
    private final double gravityMultiplier;
    private final List<SpawnEvent> events; // authored timeline, never modified
    private List<SpawnEvent> activeEvents; // jittered copy actually used during play
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
        this.activeEvents = jitteredCopyOf(events);
        double lastSeconds = events.isEmpty() ? 0 : events.get(events.size() - 1).seconds;
        // extra buffer covers the case where jitter pushes the last event later than authored
        this.endTime = lastSeconds + 4.0 + SPAWN_JITTER_SECONDS;
        this.starsColor = starsColor;
        this.starsIntensity = starsIntensity;
        this.floorColor = floorColor;
        this.floorIntensity = floorIntensity;
    }

    // Makes a copy of the authored timeline with each spawn time nudged by
    // a small random amount, then re-sorted so events are still processed
    // in time order. This is what gives each playthrough slightly different pacing.
    private static List<SpawnEvent> jitteredCopyOf(List<SpawnEvent> source) {
        List<SpawnEvent> jittered = new ArrayList<>();
        for (SpawnEvent event : source) {
            double offset = (Math.random() * 2 - 1) * SPAWN_JITTER_SECONDS;
            double jitteredSeconds = Math.max(0, event.seconds + offset);
            jittered.add(new SpawnEvent(jitteredSeconds, event.kind));
        }
        jittered.sort((a, b) -> Double.compare(a.seconds, b.seconds));
        return jittered;
    }

    // Returns any obstacles/pickups that are due to spawn by this point in time.
    public List<ScrollingObject> trySpawn(double elapsedSeconds, double spawnX, double floorY) {
        List<ScrollingObject> spawns = new ArrayList<>();
        while (nextEventIndex < activeEvents.size() && activeEvents.get(nextEventIndex).seconds <= elapsedSeconds) {
            spawns.add(create(activeEvents.get(nextEventIndex).kind, spawnX, floorY));
            nextEventIndex++;
        }
        return spawns;
    }

    // Turns a spawn "kind" into an actual game object at the given position.
    private ScrollingObject create(Kind kind, double spawnX, double floorY) {
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
        return nextEventIndex >= activeEvents.size() && elapsedSeconds >= endTime && noObstaclesLeft;
    }

    // Restarts the spawn timeline from the beginning, with fresh jitter
    // so a retry doesn't play out identically either.
    public void reset() {
        nextEventIndex = 0;
        activeEvents = jitteredCopyOf(events);
    }

    public double getSpeed() { return speed; }
    public double getGravityMultiplier() { return gravityMultiplier; }
    public String getName() { return name; }
    public Color getStarsColor() { return starsColor; }
    public double getStarsIntensity() { return starsIntensity; }
    public Color getFloorColor() { return floorColor; }
    public double getFloorIntensity() { return floorIntensity; }

    // --- Builder for authoring a level's obstacle timeline in seconds ---
    // Lets LevelData write a level as a readable chain of calls, e.g.
    // .spike(2.0).block(4.0).shield(6.0), instead of building the list by hand.
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

        // Finalizes the level: sorts events by time and builds an immutable Level.
        public Level build() {
            events.sort((a, b) -> Double.compare(a.seconds, b.seconds));
            return new Level(name, speed, gravityMultiplier, events, starsColor, starsIntensity, floorColor, floorIntensity);
        }
    }
}