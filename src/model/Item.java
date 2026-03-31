package model;

import utils.AssetManager;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public class Item {
    private int x;
    private int y;
    private int scoreValue;
    private String type;
    private BufferedImage image;

    public Item(int x, int y, String type, int scoreValue, String imageName) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.scoreValue = scoreValue;
        this.image = AssetManager.getInstance().getImage(imageName);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getType() { return type; }
    public int getScoreValue() { return scoreValue; }
    public BufferedImage getImage() { return image; }
}