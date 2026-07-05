package com.spacerunner;

// All the possible things that can happen when the player touches a
// scrolling object (obstacle or pickup) this frame.
public enum ContactResult {
    NONE,        // nothing happened, no contact
    DEATH,       // hit something harmful, game over unless a shield absorbs it
    SHIELD,      // picked up a shield charge
    NITRO,       // picked up a nitro charge
    PLATFORM,    // landed safely on top of a block
    DOUBLE_JUMP  // picked up a double-jump charge
}