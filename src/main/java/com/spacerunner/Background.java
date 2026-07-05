package com.spacerunner;

import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Background {

    private static final double PARALLAX_FACTOR = 0.2; //(make the stars run slower

    private final ImageView starsA;
    private final ImageView starsB;
    private final ImageView floorA;
    private final ImageView floorB;
    private final Rectangle floorBorder;
    private final Rectangle starsOverlay;
    private final Rectangle floorOverlay;
    private final double width;

    private double starsX = 0;
    private double floorX = 0;

    public Background(double width, double height, double floorY) {
        this.width = width;

        Image starsImg = new Image(Background.class.getResourceAsStream("/assets/space_bg.jpg"));
        starsA = tiledView(starsImg, width, height);
        starsB = tiledView(starsImg, width, height);
        starsA.setTranslateX(0);
        starsB.setTranslateX(width);

        Image floorImg = new Image(Background.class.getResourceAsStream("/assets/moon_floor.png"));
        double floorHeight = height - floorY;
        floorA = tiledView(floorImg, width, floorHeight);
        floorB = tiledView(floorImg, width, floorHeight);
        floorA.setTranslateY(floorY);
        floorB.setTranslateY(floorY);
        floorA.setTranslateX(0);
        floorB.setTranslateX(width);

        floorBorder = new Rectangle(0, floorY, width, floorHeight);
        floorBorder.setFill(Color.TRANSPARENT);
        floorBorder.setStroke(Color.web("#505254"));
        floorBorder.setStrokeWidth(3);

        starsOverlay = new Rectangle(0, 0, width, floorY);
        starsOverlay.setBlendMode(BlendMode.SCREEN);
        starsOverlay.setOpacity(0);
        starsOverlay.setMouseTransparent(true);

        floorOverlay = new Rectangle(0, floorY, width, floorHeight);
        floorOverlay.setBlendMode(BlendMode.MULTIPLY);
        floorOverlay.setOpacity(0);
        floorOverlay.setMouseTransparent(true);
    }


    public void setTheme(Color starsColor, double starsIntensity, Color floorColor, double floorIntensity) {
        starsOverlay.setFill(starsColor);
        starsOverlay.setOpacity(starsIntensity);
        floorOverlay.setFill(floorColor);
        floorOverlay.setOpacity(floorIntensity);
    }

    private ImageView tiledView(Image image, double w, double h) {
        ImageView view = new ImageView(image);
        view.setFitWidth(w);
        view.setFitHeight(h);
        view.setPreserveRatio(false);
        return view;
    }

    // Advances both scrolling layers. groundSpeed is the same (negative)
    // per-frame delta the obstacles move by, so the floor stays in sync
    // with them; the stars move slower to fake distance/depth.
    public void update(double groundSpeed) {
        floorX += groundSpeed;
        if (floorX <= -width) floorX += width;
        floorA.setTranslateX(floorX);
        floorB.setTranslateX(floorX + width);

        starsX += groundSpeed * PARALLAX_FACTOR;
        if (starsX <= -width) starsX += width;
        starsA.setTranslateX(starsX);
        starsB.setTranslateX(starsX + width);
    }

    public ImageView getStarsA() { return starsA; }
    public ImageView getStarsB() { return starsB; }
    public ImageView getFloorA() { return floorA; }
    public ImageView getFloorB() { return floorB; }
    public Rectangle getFloorBorder() { return floorBorder; }
    public Rectangle getStarsOverlay() { return starsOverlay; }
    public Rectangle getFloorOverlay() { return floorOverlay; }
}
