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
            case LOGIN:
                drawBackground(g, "bg_login");
                drawLoginScreen(g);
                break;
            case REGISTER:
                drawBackground(g, "bg_register");
                drawRegisterScreen(g);
                break;
            case MAIN_MENU:
                drawBackground(g, "bg_menu");
                drawMainMenu(g);
                break;
            case QUIT_CONFIRM:
                drawBackground(g, "bg_quit");
                drawQuitConfirm(g);
                break;
            case PLAYING:
                drawGame(g);
                break;
            case PAUSED:
                // drawOverlay(g, 150);
                drawBackground(g, "bg_pause");
                drawPauseMenu(g);
                break;
            case CONTROLS:
                drawBackground(g, "bg_controls");
                g.setColor(Color.WHITE);
                MenuButton btncl = gameModel.getControlsButtons();
                btncl.draw(g);
                break;

            case START:
                drawBackground(g, "bg_start");
                g.setColor(Color.WHITE);
                g.setFont(titleFont);
                drawStartMenu(g);
                break;
            case LEVEL_LOCKED_POPUP:
            case LEVEL_INFO_POPUP:
                drawBackground(g, "bg_start");
                drawStartMenu(g);
                drawOverlay(g, 150);
                if (state == GameState.LEVEL_LOCKED_POPUP) {
                    drawLevelLockedPopup(g);
                } else {
                    drawLevelInfoPopup(g);
                }
                break;
            case SETTINGS:
                drawBackground(g, "bg_settings");
                for (MenuButton btn : gameModel.getSettingsButtons()) {
                    btn.draw(g);
                }
                // Sound ON/OFF hiện tại
                String soundStatus = SoundManager.getInstance().isMuted() ? "sound_off" : "sound_on";
                drawSoundStatus(g, soundStatus);
                break;
        }
    }

    private void drawSoundStatus(Graphics g, String soundStatus) {
        BufferedImage bg = AssetManager.getInstance().getImage(soundStatus);
        if (bg != null) {
            g.drawImage(bg, 472, 285, 128, 152, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
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

    private void drawLoginScreen(Graphics g) {
        // Username field
        g.setColor(gameModel.getActiveField() == 0 ? Color.YELLOW : Color.WHITE);
        g.drawRect(225, 304, 182, 27);
        g.setColor(Color.WHITE);
        g.drawString(gameModel.getLoginUsername(), 230, 321);

        // Password field
        g.setColor(gameModel.getActiveField() == 1 ? Color.YELLOW : Color.WHITE);
        g.drawRect(225, 366, 182, 27);
        g.setColor(Color.WHITE);
        String loginPass = gameModel.isShowPassword()
                ? gameModel.getLoginPassword()
                : new String(new char[gameModel.getLoginPassword().length()]).replace("\0", "*");
        g.drawString(loginPass, 230, 384);

        // Message
        g.setColor(Color.RED);
        g.drawString(gameModel.getLoginMessage(), 225, 410);

        for (MenuButton btn : gameModel.getLoginButtons()) {
            btn.draw(g);
        }
    }

    private void drawRegisterScreen(Graphics g) {
        // Username field
        g.setColor(gameModel.getActiveField() == 0 ? Color.YELLOW : Color.WHITE);
        g.drawRect(225, 302, 179, 27);
        g.setColor(Color.WHITE);
        g.drawString(gameModel.getRegisterUsername(), 230, 320);

        // Password field
        g.setColor(gameModel.getActiveField() == 1 ? Color.YELLOW : Color.WHITE);
        g.drawRect(225, 364, 179, 27);
        g.setColor(Color.WHITE);
        String regPass = gameModel.isShowPassword()
                ? gameModel.getRegisterPassword()
                : new String(new char[gameModel.getRegisterPassword().length()]).replace("\0", "*");
        g.drawString(regPass, 230, 380);

        // Message
        g.setColor(Color.RED);
        g.drawString(gameModel.getLoginMessage(), 150, 400);

        for (MenuButton btn : gameModel.getRegisterButtons()) {
            btn.draw(g);
        }
    }

    private void drawStartMenu(Graphics g) {
        for (MenuButton btn : gameModel.getStartButtons()) {
            btn.draw(g);
        }
    }

    private void drawLevelLockedPopup(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRoundRect(150, 200, 300, 150, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(150, 200, 300, 150, 20, 20);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Level is Locked!", 220, 280);
        for (MenuButton btn : gameModel.getLevelLockedButtons()) {
            btn.draw(g);
        }
    }

    private void drawLevelInfoPopup(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRoundRect(150, 200, 300, 220, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(150, 200, 300, 220, 20, 20);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Level " + gameModel.getSelectedLevelId(), 250, 260);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.YELLOW);
        g.drawString("Latest Score: " + gameModel.getSelectedLevelHighScore(), 220, 310);

        for (MenuButton btn : gameModel.getLevelInfoButtons()) {
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

        for (MenuButton btn : gameModel.getQuitConfirmButtons()) {
            btn.draw(g);
        }
    }

    private void drawGamePlay(Graphics g) {
        for (MenuButton btn : gameModel.getGamePlayButtons()) {
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
            BufferedImage bgGameOver = AssetManager.getInstance().getImage("bg_gameover");
            if (bgGameOver != null) {
                g.drawImage(bgGameOver, 22, 126, 564, 420, null);
            }
            drawGameOverScreen(g);
        }

        if (gameModel.isGameWon()) {
            drawOverlay(g, 150);
            BufferedImage bgGameWon = AssetManager.getInstance().getImage("bg_gamewon");
            if (bgGameWon != null) {
                g.drawImage(bgGameWon, 40, 150, 538, 372, null);
            }
            drawGameWonScreen(g);
        }
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        String score = "" + gameModel.getScore();

        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(score)) / 2;

        g.drawString(score, x, 326);

        for (MenuButton btn : gameModel.getGameOverButtons()) {
            btn.draw(g);
        }
    }

    private void drawGameWonScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        String score = "" + gameModel.getScore();

        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(score)) / 2;

        g.drawString(score, x, 328);

        for (MenuButton btn : gameModel.getGameWonButtons()) {
            btn.draw(g);
        }
    }

    private void drawMap(Graphics g) {
        Map map = gameModel.getMap();
        char[][] grid = map.getGrid();

        if (grid == null)
            return;

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
            g.drawImage(pacman.getCurrentImage(), pacman.getX(), pacman.getY(), tileSize, tileSize, null);
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
                    g.drawImage(ghost.getImage(), ghost.getX(), ghost.getY(), tileSize, tileSize, null);
                }
            }
        }
    }
}