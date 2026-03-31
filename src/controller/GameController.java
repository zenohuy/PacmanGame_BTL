package controller;

import model.*;
import utils.SoundManager;
import view.GamePanel;
import view.GameWindow;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameController implements  Runnable, KeyListener, MouseListener {
    private GameModel model;
    private GamePanel viewPanel;
    private GameWindow window;
    private Map crmap = new Map("level1");

    private Thread gameThread;
    private boolean isRunning = false;
    private final int FPS = 60;

    public GameController() {
        model = new GameModel();

        viewPanel = new GamePanel(model);
        viewPanel.addKeyListener(this);
        viewPanel.addMouseListener(this);
        viewPanel.setFocusable(true);
        viewPanel.requestFocusInWindow();

        window = new GameWindow(viewPanel);
        startGameLoop();
    }

    private void startGameLoop() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (isRunning) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                viewPanel.repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (model.getCurrentState() != GameState.PLAYING) {
            return;
        }

        if (model.isGameOver()) {
            return;
        }

        PacMan pacman = model.getPacman();
        if (pacman != null) {
            int nextX = pacman.getX() + pacman.getDx() * pacman.getSpeed();
            int nextY = pacman.getY() + pacman.getDy() * pacman.getSpeed();

            if (canMove(nextX, nextY)) {
                pacman.update();
                checkEatCherry();
            }
        }

        if (model.getGhosts() != null) {
            for (model.Ghost ghost : model.getGhosts()) {
                moveGhost(ghost);
            }
        }

        checkGhostCollision();
    }

    private boolean canMove(int nextX, int nextY) {
        int tileSize = GameModel.TILE_SIZE;
        int offset = 2;

        int leftCol = (nextX + offset) / tileSize;
        int rightCol = (nextX + tileSize - offset - 1) / tileSize;
        int topRow = (nextY + offset) / tileSize;
        int bottomRow = (nextY + tileSize - offset - 1) / tileSize;

        char[][] grid = model.getMap().getGrid();

        if (leftCol < 0 || rightCol >= model.getMap().getCols() ||
                topRow < 0 || bottomRow >= model.getMap().getRows()) {
            return false;
        }

        if (grid[topRow][leftCol] == 'X' || grid[topRow][rightCol] == 'X' ||
                grid[bottomRow][leftCol] == 'X' || grid[bottomRow][rightCol] == 'X') {
            return false;
        }
        return true;
    }

    private void checkEatCherry() {
        PacMan pacman = model.getPacman();
        if (pacman == null) return;

        java.awt.Rectangle pacmanBounds = new java.awt.Rectangle(
                pacman.getX() + 8, pacman.getY() + 8,
                GameModel.TILE_SIZE - 16, GameModel.TILE_SIZE - 16
        );

        java.util.Iterator<Item> iterator = model.getItems().iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (pacmanBounds.intersects(item.getBounds())) {
                model.addScore(item.getScoreValue());
                iterator.remove();
            }
        }
    }

    private void moveGhost(model.Ghost ghost) {
        int nextX = ghost.getX() + ghost.getDx() * ghost.getSpeed();
        int nextY = ghost.getY() + ghost.getDy() * ghost.getSpeed();

        if (canMove(nextX, nextY)) {
            ghost.move();
        } else {
            changeGhostDirection(ghost);
        }
    }

    private void changeGhostDirection(model.Ghost ghost) {
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        java.util.ArrayList<int[]> validMoves = new java.util.ArrayList<>();

        for (int[] dir : directions) {
            int testX = ghost.getX() + dir[0] * ghost.getSpeed();
            int testY = ghost.getY() + dir[1] * ghost.getSpeed();

            if (canMove(testX, testY)) {
                validMoves.add(dir);
            }
        }

        if (!validMoves.isEmpty()) {
            java.util.Random rand = new java.util.Random();
            int[] chosenDir = validMoves.get(rand.nextInt(validMoves.size()));
            ghost.setDirection(chosenDir[0], chosenDir[1]);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        GameState state = model.getCurrentState();

        if (state == GameState.MAIN_MENU) {
            for (MenuButton btn : model.getMainMenuButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.START) {
            for (MenuButton btn: model.getStartButtons()){
                if (btn.isClicked(mx,my)){
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.QUIT_CONFIRM) {
            for (MenuButton btn : model.getQuitConfirmButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.PAUSED) {
            for (MenuButton btn : model.getPauseButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.SETTINGS) {
            for (MenuButton btn : model.getSettingsButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        } else if (state == GameState.CONTROLS) {
            MenuButton btn= model.getControlsButtons();
            if(btn.isClicked(mx,my)){
                handleButtonClick(btn.getActionCommand());
            }
        }
    }

    private void startLevel(String levelName) {
        crmap.setCurrentLevel(levelName);
        model.setMap(new Map(levelName));
        model.setGameOver(false);
        model.setCurrentState(GameState.PLAYING);
    }

    private void handleButtonClick(String command) {
        switch (command) {
            case "PLAY":
                model.setCurrentState(GameState.START);
                break;
            case "CONTROLS":
                model.setCurrentState(GameState.CONTROLS);
                break;
            case "SETTINGS":
                model.setCurrentState(GameState.SETTINGS);
                break;
            case "QUIT":
                model.setCurrentState(GameState.QUIT_CONFIRM);
                break;
            case "YES_QUIT":
                System.exit(0);
                break;
            case "NO_QUIT":
                model.setCurrentState(GameState.MAIN_MENU);
                break;
            case "RESUME":
                model.setCurrentState(GameState.PLAYING);
                break;
            case "RESTART":
                model.resetGame(crmap.getCurrentLevel());
                model.setCurrentState(GameState.PLAYING);
                break;
            case "MENU":
                model.setCurrentState(GameState.MAIN_MENU);
                break;
            case "TOGGLE_SOUND":
                SoundManager.getInstance().toggleMute();
                break;
            case "BACK_TO_MENU":
                model.setCurrentState(GameState.MAIN_MENU);
                break;
            case "LEVEL1":
                startLevel("level1");
                break;
            case "LEVEL2":
                startLevel("level2");
                break;
            case "LEVEL3":
                startLevel("level3");
                break;
            case "LEVEL4":
                startLevel("level1");
                break;
            case "LEVEL5":
                startLevel("level2");
                break;
            case "LEVEL6":
                startLevel("level3");
                break;
            case "LEVEL7":
                startLevel("level1");
                break;
            case "LEVEL8":
                startLevel("level2");
                break;
            case "LEVEL9":
                startLevel("level3");
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        GameState state = model.getCurrentState();

        // ESC: Pause/Resume
        if (key == KeyEvent.VK_ESCAPE) {
            if (state == GameState.PLAYING) {
                model.setCurrentState(GameState.PAUSED);
            } else if (state == GameState.PAUSED) {
                model.setCurrentState(GameState.PLAYING);
            }
        }

        // Phím phụ để test các màn hình chưa có ảnh tĩnh (Level select, Controls)
        if (key == KeyEvent.VK_BACK_SPACE) {
            if (state == GameState.START || state == GameState.CONTROLS) {
                model.setCurrentState(GameState.MAIN_MENU);
            }
        }

        // move Pacman
        if (state == GameState.PLAYING) {
            PacMan pacman = model.getPacman();
            if (pacman == null) return;

            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                pacman.setDirection(0, -1);
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                pacman.setDirection(0, 1);
            } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                pacman.setDirection(-1, 0);
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                pacman.setDirection(1, 0);
            }
        }
    }

    // Các hàm trống của MouseListener
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    private void checkGhostCollision() {
        PacMan pacman = model.getPacman();
        if (pacman == null || model.getGhosts() == null) return;

        int size = GameModel.TILE_SIZE;

        java.awt.Rectangle pacmanBounds = new java.awt.Rectangle(
                pacman.getX() + 4, pacman.getY() + 4, size - 8, size - 8
        );

        for (model.Ghost ghost : model.getGhosts()) {
            java.awt.Rectangle ghostBounds = new java.awt.Rectangle(
                    ghost.getX() + 4, ghost.getY() + 4, size - 8, size - 8
            );

            if (pacmanBounds.intersects(ghostBounds)) {
                model.setGameOver(true);
                break;
            }
        }
    }
}
