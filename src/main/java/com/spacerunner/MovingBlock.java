package com.spacerunner;


public class MovingBlock extends Block {
    private static final double AMPLITUDE = 55;   // px of vertical travel
    private static final double FREQUENCY = 0.55;  // bob cycles per second

    private double time = 0;
    private final double baseY;

    public MovingBlock(double x, double floorY) {
        super(x, floorY);
        this.baseY = view.getTranslateY();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime); //leftward scroll
        time += deltaTime;
        double offset = Math.sin(time * FREQUENCY * Math.PI * 2) * AMPLITUDE;
        view.setTranslateY(baseY + offset);
    }
}
