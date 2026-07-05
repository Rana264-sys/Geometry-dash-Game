package com.spacerunner;

// A separate main class that just forwards to GameMain. Some JavaFX
// packaging setups fail to launch an Application subclass directly from
// its own main() - having a plain class call it avoids that issue.
public class Launcher {
    public static void main(String[] args) {
        GameMain.main(args);
    }
}