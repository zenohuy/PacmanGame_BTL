package view;

import model.*;
import utils.AssetManager;
import utils.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private GameModel gameModel;
    private final Font titleFont = new Font("Arial", Font.BOLD, 40);
    public static final int PANEL_WIDTH = 608;
    public static final int PANEL_HEIGHT = 672;

    public GamePanel(GameModel gameModel) {
        this.gameModel = gameModel;
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameState state = gameModel.getCurrentState();

        switch (state) {
            case MAIN_MENU:
                drawBackground(g, "bg_menu");
                drawMainMenu(g);
                break;
            case QUIT_CONFIRM:
                drawBackground(g, "bg_menu");
                drawMainMenu(g);
                drawOverlay(g, 200);
                drawQuitConfirm(g);
                break;
            case PLAYING:
                drawBackground(g, "bg_playing");
                drawGame(g);
                break;
            case PAUSED:
                drawBackground(g, "bg_playing");
                drawGame(g);
                drawOverlay(g, 150);
                drawBackground(g, "bg_pause");
                drawPauseMenu(g);
                break;
            case CONTROLS:
                drawBackground(g, "bg_controls");
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("Di chuyển bằng các phím mũi tên", 63, 300);
                g.drawString("Nhấn BACKSPACE để quay lại", 63, 350);
                MenuButton btncl = gameModel.getControlsButtons();
                btncl.draw(g);
                break;

            case START:
                drawBackground(g, "bg_start");
                g.setColor(Color.WHITE);
                g.setFont(titleFont);
                drawStartMenu(g);
                break;

            case SETTINGS:
                drawBackground(g, "bg_settings");
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("SETTINGS", 200, 150);

                for (MenuButton btn : gameModel.getSettingsButtons()) {
                    btn.draw(g);
                }

                // Sound ON/OFF hiện tại
                g.setFont(new Font("Arial", Font.BOLD, 25));
                String soundStatus = SoundManager.getInstance().isMuted() ? "OFF" : "ON";
                g.drawString("Sound: " + soundStatus, 225, 230);
                break;
        }
    }

    private void drawBackground(Graphics g, String imageName) {
        BufferedImage bg = AssetManager.getInstance().getImage(imageName);
        if (bg != null) {
            g.drawImage(bg, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }
    }

    private void drawOverlay(Graphics g, int alpha) {
        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawMainMenu(Graphics g) {
        for (MenuButton btn : gameModel.getMainMenuButtons()) {
            btn.draw(g);
        }
    }

    private void drawStartMenu (Graphics g){
        for (MenuButton btn: gameModel.getStartButtons()){
            btn.draw(g);
        }
    }

    private void drawPauseMenu(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("PAUSED", 220, 150);
        for (MenuButton btn : gameModel.getPauseButtons()) {
            btn.draw(g);
        }
    }

    private void drawQuitConfirm(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Bạn chắc chắn muốn thoát game chứ?", 30, 200);
        for (MenuButton btn : gameModel.getQuitConfirmButtons()) {
            btn.draw(g);
        }
    }

    private void drawGamePlay (Graphics g){
        for (MenuButton btn: gameModel.getGamePlayButtons()){
            btn.draw(g);
        }
    }

    private void drawGame(Graphics g) {
        drawMap(g);
        drawEntities(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + gameModel.getScore(), 10, 25);
        drawGamePlay(g);

        if (gameModel.isGameOver()) {
            drawOverlay(g, 150);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", 170, 300);
        }
    }


    private void drawMap(Graphics g) {
        Map map = gameModel.getMap();
        char[][] grid = map.getGrid();

        if (grid == null) return;

        int tileSize = GameModel.TILE_SIZE;

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                char tileChar = grid[row][col];

                int x = col * tileSize;
                int y = row * tileSize;

                BufferedImage imageToDraw = null;

                switch (tileChar) {
                    case 'X':
                        imageToDraw = AssetManager.getInstance().getImage("wall");
                        break;
                    case '.':
                        imageToDraw = AssetManager.getInstance().getImage("cherry");
                        break;
                    case ' ':
                        break;
                    default:
                        break;
                }

                if (imageToDraw != null) {
                    g.drawImage(imageToDraw, x, y, tileSize, tileSize, null);
                }
            }
        }
    }

    private void drawEntities(Graphics g) {
        int tileSize = GameModel.TILE_SIZE;
        PacMan pacman = gameModel.getPacman();
        if (pacman != null && pacman.getCurrentImage() != null) {
            g.drawImage(pacman.getCurrentImage(), pacman.getX(), pacman.getY(),tileSize, tileSize, null);
        }

        if (gameModel.getItems() != null) {
            for (Item item : gameModel.getItems()) {
                if (item.getImage() != null) {
                    g.drawImage(item.getImage(), item.getX(), item.getY(), tileSize, tileSize, null);
                }
            }
        }

        if (gameModel.getGhosts() != null) {
            for (Ghost ghost : gameModel.getGhosts()) {
                if (ghost.getImage() != null) {
                    g.drawImage(ghost.getImage(), ghost.getX(), ghost.getY(),tileSize, tileSize, null);
                }
            }
        }
    }
}