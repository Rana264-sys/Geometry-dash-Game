package com.spacerunner;

/**
 * The main entry point of the application. 
 * Used to bypass strict JavaFX module path checks at startup.
 */
public class Launcher {
    public static void main(String[] args) {
        GameMain.main(args);
    }
}