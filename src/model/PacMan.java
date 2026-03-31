package model;

import utils.AssetManager;

import java.awt.image.BufferedImage;

public class PacMan {
    private int startX;
    private int startY;
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int speed;

    private BufferedImage imageUp;
    private BufferedImage imageDown;
    private BufferedImage imageLeft;
    private BufferedImage imageRight;

    private BufferedImage currentImage;

    public PacMan(int x, int y) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.speed = 2;
        this.dx = 0;
        this.dy = 0;

        this.imageUp = AssetManager.getInstance().getImage("pacmanUp");
        this.imageDown = AssetManager.getInstance().getImage("pacmanDown");
        this.imageLeft = AssetManager.getInstance().getImage("pacmanLeft");
        this.imageRight = AssetManager.getInstance().getImage("pacmanRight");

        this.currentImage = this.imageRight;
    }

    public void update() {
        this.x += dx * speed;
        this.y += dy * speed;
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;

        if (dx == 1) {
            this.currentImage = this.imageRight;
        } else if (dx == -1) {
            this.currentImage = this.imageLeft;
        } else if (dy == -1) {
            this.currentImage = this.imageUp;
        } else if (dy == 1) {
            this.currentImage = this.imageDown;
        }
    }

    public void reset(){
        this.x = this.startX;
        this.y = this.startY;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public int getSpeed() { return speed; }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }
}
