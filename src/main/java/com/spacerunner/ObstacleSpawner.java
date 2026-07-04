package com.spacerunner;

import java.util.ArrayList;
import java.util.List;

// Decides which obstacles to create, when to create them, and how fast
// they should move. This keeps all the spawning rules in one place
// instead of inside GameMain.
public class ObstacleSpawner {

    private final double spawnX;   // where new obstacles appear (right edge of screen)
    private final double floorY;

    private double globalSpeed = -6.0;
    private int nextSpawnFrame = 90;
    private int heartsSpawnedPhase1 = 0;
    private int heartsSpawnedPhase2 = 0;

    public ObstacleSpawner(double spawnX, double floorY) {
        this.spawnX = spawnX;
        this.floorY = floorY;
    }

    // Speeds obstacles up as the game goes on (harder over time).
    public void updateDifficulty(int frameCount) {
        if (frameCount == 1800) {
            globalSpeed = -8.0;
            System.out.println("PHASE 2: Moderate Mode!");
        } else if (frameCount == 7200) {
            globalSpeed = -10.0;
            System.out.println("PHASE 3: Hard Mode!");
        }
    }

    // Decides if it's time to spawn something new this frame, and if so,
    // randomly picks what to spawn (heart, block, spike, or both).
    public List<Obstacle> trySpawn(int frameCount) {
        List<Obstacle> newSpawns = new ArrayList<>();

        if (frameCount < nextSpawnFrame) {
            return newSpawns;
        }

        if (frameCount < 1800 && heartsSpawnedPhase1 < 1 && Math.random() > 0.7) {
            newSpawns.add(new Heart(spawnX, floorY));
            heartsSpawnedPhase1++;
        }
        else if (frameCount >= 1800 && frameCount < 7200 && heartsSpawnedPhase2 < 2 && Math.random() > 0.7) {
            newSpawns.add(new Heart(spawnX, floorY));
            heartsSpawnedPhase2++;
        }
        else {
            if (frameCount > 7200 && Math.random() > 0.6) {
                newSpawns.add(new Block(spawnX, floorY));
                Obstacle ceilingSpike = new Spike(spawnX + 250, 360);
                ceilingSpike.getView().setRotate(180);
                newSpawns.add(ceilingSpike);
            } else {
                newSpawns.add(Math.random() > 0.5 ? new Spike(spawnX, floorY) : new Block(spawnX, floorY));
            }
        }

        for (Obstacle obs : newSpawns) {
            obs.setXVelocity(globalSpeed);
        }

        // Calculate random delay for the next obstacle
        int baseRate = (frameCount > 7200) ? 75 : (frameCount > 1800) ? 65 : 90;
        int randomVariance = (int) (Math.random() * 30) - 15;
        int actualDelay = Math.max(20, baseRate + randomVariance);
        nextSpawnFrame = frameCount + actualDelay;

        return newSpawns;
    }

    // Resets all spawning rules back to their starting values (for restart).
    public void reset() {
        globalSpeed = -6.0;
        nextSpawnFrame = 90;
        heartsSpawnedPhase1 = 0;
        heartsSpawnedPhase2 = 0;
    }
}