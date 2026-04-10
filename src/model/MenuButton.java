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

    // Hover state
    private boolean hovered = false;
    private float currentScale = 1.0f;
    private static final float HOVER_SCALE = 1.12f;   // phóng to 12%
    private static final float SCALE_SPEED = 0.08f;    // tốc độ transition

    public MenuButton(int x, int y, int width, int height, BufferedImage image, String actionCommand) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.actionCommand = actionCommand;
    }

    /**
     * Cập nhật animation scale mỗi frame.
     * Gọi trong game loop để tạo hiệu ứng mượt.
     */
    public void updateHoverAnimation() {
        float targetScale = hovered ? HOVER_SCALE : 1.0f;
        if (currentScale < targetScale) {
            currentScale = Math.min(currentScale + SCALE_SPEED, targetScale);
        } else if (currentScale > targetScale) {
            currentScale = Math.max(currentScale - SCALE_SPEED, targetScale);
        }
    }

    public void draw(Graphics g) {
        // Tính kích thước sau khi scale
        int drawWidth = Math.round(width * currentScale);
        int drawHeight = Math.round(height * currentScale);

        // Giữ nguyên tâm button khi phóng to
        int drawX = x - (drawWidth - width) / 2;
        int drawY = y - (drawHeight - height) / 2;

        if (image != null) {
            // Bật anti-aliasing để hình mượt hơn khi phóng to
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
            g.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(drawX, drawY, drawWidth, drawHeight);
            g.setColor(Color.WHITE);
            g.drawRect(drawX, drawY, drawWidth, drawHeight);
            g.drawString(actionCommand, drawX + 10, drawY + 25);
        }
    }

    /**
     * Kiểm tra xem tọa độ chuột có nằm trong vùng button không (vùng gốc, không scale).
     */
    public boolean isClicked(int mouseX, int mouseY) {
        Rectangle bounds = new Rectangle(x, y, width, height);
        return bounds.contains(mouseX, mouseY);
    }

    /**
     * Kiểm tra xem chuột có đang hover trên button không.
     */
    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isHovered() {
        return hovered;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}