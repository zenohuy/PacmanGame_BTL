package model;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuButton {
    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage image;
    private String actionCommand;

    public MenuButton(int x, int y, int width, int height, BufferedImage image, String actionCommand) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.actionCommand = actionCommand;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
            g.setColor(Color.WHITE);
            g.drawRect(x, y, width, height);
            g.drawString(actionCommand, x + 10, y + 25);
        }
    }

    public boolean isClicked(int mouseX, int mouseY) {
        Rectangle bounds = new Rectangle(x, y, width, height);
        return bounds.contains(mouseX, mouseY);
    }

    public String getActionCommand() {
        return actionCommand;
    }
}