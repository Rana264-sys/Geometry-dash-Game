package com.spacerunner;

// Same as a normal Block, but bobs up and down while scrolling, making it
// trickier to land on.
public class MovingBlock extends Block {
    private static final double AMPLITUDE = 55;   // px of vertical travel
    private static final double FREQUENCY = 0.55;  // bob cycles per second

    private double time = 0;
    private final double baseY;

    public MovingBlock(double x, double floorY) {
        super(x, floorY);
        this.baseY = view.getTranslateY();
    }

    // Scrolls left like a normal block, then adds a smooth up/down wave on top.
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime); //leftward scroll
        time += deltaTime;
        double offset = Math.sin(time * FREQUENCY * Math.PI * 2) * AMPLITUDE;
        view.setTranslateY(baseY + offset);
    }
}