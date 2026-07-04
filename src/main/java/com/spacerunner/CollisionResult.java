package com.spacerunner;

/**
 * The possible outcomes of Obstacle.checkCollision(Player).
 * Replaces the old raw String codes ("DEATH", "HEART", "PLATFORM", "NONE")
 * with a type-safe enum: no typos possible, and the compiler tells you
 * every valid outcome instead of it living only in comments.
 */
public enum CollisionResult {
    NONE,
    DEATH,
    HEART,
    PLATFORM
}