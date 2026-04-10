package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class InputHandler {
    private GameController controller;

    public InputHandler(GameController controller) {
        this.controller = controller;
    }

    public KeyAdapter getKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                controller.handleKeyPress(e.getKeyCode());
            }
            @Override
            public void keyTyped(KeyEvent e) {
                controller.handleKeyTyped(e.getKeyChar());
            }
        };
    }

    public MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                controller.handleMousePress(e.getX(), e.getY());
            }
        };
    }

    /**
     * Listener cho sự kiện di chuột (hover).
     */
    public MouseMotionAdapter getMouseMotionListener() {
        return new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                controller.handleMouseMove(e.getX(), e.getY());
            }
        };
    }
}