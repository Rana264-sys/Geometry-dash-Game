package com.spacerunner;

import javafx.scene.paint.Color;


public class LevelData {

    public static final String[] NAMES = {
        "LEVEL 1 - BLUE",
        "LEVEL 2 - GREEN",
        "LEVEL 3 - VIOLET"
    };

    public static Level get(int index) {
        switch (index) {
            case 0: return blue();
            case 1: return green();
            case 2: return violet();
            default: throw new IllegalArgumentException("No such level: " + index);
        }
    }


    private static Level blue() {
        return new Level.Builder(NAMES[0], -390)
            .gravity(0.8)
            .theme(Color.web("#33aaff"), 0.16, Color.web("#bfe9ff"), 0.30)
            .block(1.5).movingBlock(3.5).spike(5.5).block(7.5).shield(9.0)
            .movingBlock(11.0).block(13.0).spike(15.0).doubleJump(16.5)
            .movingBlock(18.5).block(20.5).spike(22.5).nitro(24.5)
            .movingBlock(26.5).block(28.5).spike(30.5).movingBlock(32.0)
            .build();
    }


    private static Level green() {
        return new Level.Builder(NAMES[1], -450)
            .gravity(1.15)
            .theme(Color.web("#33ff88"), 0.16, Color.web("#2f6b3a"), 0.35)
            .spike(1.0).spike(2.2).shield(3.4).sporeSwarm(4.6).ceilingSpike(5.8)
            .spike(7.0).nitro(8.2).sporeSwarm(9.4).doubleJump(10.6).spike(11.8)
            .ceilingSpike(13.0).sporeSwarm(14.2).shield(15.4).spike(16.6)
            .doubleJump(17.8).sporeSwarm(19.0).ceilingSpike(20.2).spike(21.4)
            .nitro(22.6).sporeSwarm(23.8).spike(25.0).shield(26.2).spike(27.4)
            .ceilingSpike(28.6).sporeSwarm(29.8)
            .build();
    }


    private static Level violet() {
        return new Level.Builder(NAMES[2], -540)
            .gravity(1.35)
            .theme(Color.web("#aa44ff"), 0.18, Color.web("#5b3b8c"), 0.35)
            .doubleJump(1.0).spike(2.0).ceilingSpike(2.6).block(4.0)
            .fallingDebris(5.2).ceilingSpike(5.8).shield(7.0).doubleJump(8.2)
            .spike(9.2).fallingDebris(9.8).spike(11.0).block(12.2)
            .fallingDebris(13.4).ceilingSpike(14.0).doubleJump(15.2).spike(16.2)
            .fallingDebris(16.8).nitro(18.0).spike(19.2).ceilingSpike(19.8)
            .fallingDebris(21.0).spike(22.2).ceilingSpike(22.8).doubleJump(24.0)
            .fallingDebris(25.0).ceilingSpike(25.6).shield(26.8).spike(28.0)
            .fallingDebris(28.6).block(29.8)
            .build();
    }
}
