package model;

import utils.AssetManager;
import java.awt.image.BufferedImage;

public class Ghost {
    private int startX;
    private int startY;
    private int x;
    private int y;
    private int speed;
    private int dx;
    private int dy;
    private BufferedImage image;

    public Ghost(int x, int y, String imageName) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.speed = 1;
        this.dx = 1;
        this.dy = 0;

        this.image = AssetManager.getInstance().getImage(imageName);
    }

    public void move() {
        this.x += dx * speed;
        this.y += dy * speed;
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void reset (){
        this.x = this.startX;
        this.y = this.startY;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public int getSpeed() { return speed; }
    public BufferedImage getImage() { return image; }
}