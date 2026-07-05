package com.spacerunner;

// The overall "screen" the game is currently showing.
public enum GameState {
    MENU,           // showing the level-select start menu
    PLAYING,        // the actual gameplay is running
    GAME_OVER,      // player died, showing the game-over menu
    LEVEL_COMPLETE  // player finished the level, showing the results menu
}