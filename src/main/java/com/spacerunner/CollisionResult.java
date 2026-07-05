package com.spacerunner;

public enum CollisionResult {
    NONE,        // nothing happened
    DEATH,
    SHIELD,
    NITRO,
    PLATFORM,    // safely lands
    DOUBLE_JUMP
}