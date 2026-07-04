package com.spacerunner;

// The different things that can happen when an obstacle touches the player.
public enum CollisionResult {
    NONE,     // nothing happened
    DEATH,    // the player got hit and lost a life (or the game ends)
    HEART,    // the player picked up an oxygen heart
    PLATFORM  // the player landed safely on top of a block
}