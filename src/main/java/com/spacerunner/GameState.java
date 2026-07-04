package com.spacerunner;

// The current screen/mode the game is in.
public enum GameState {
	MENU,      // showing the start screen
    PLAYING,   // the game is actively running
    GAME_OVER  // the player died, showing the game over screen
}