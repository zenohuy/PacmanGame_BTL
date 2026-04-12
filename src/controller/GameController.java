package controller;

import model.*;
import utils.SoundManager;
import view.GamePanel;
import view.GameWindow;

import java.awt.event.KeyEvent;

public class GameController implements Runnable {
    private GameModel model;
    private GamePanel viewPanel;
    private GameWindow window;
    private String crmap = "level1";
    private SoundManager sound;

    private Thread gameThread;
    private boolean isRunning = false;
    private final int FPS = 60;

    private final java.util.Random random = new java.util.Random();

    public GameController() {
        model = new GameModel();

        sound = SoundManager.getInstance();
        sound.loadSound("eat.wav");
        sound.loadSound("background.wav");

        viewPanel = new GamePanel(model);

        // Tích hợp InputHandler
        InputHandler inputHandler = new InputHandler(this);
        viewPanel.addKeyListener(inputHandler.getKeyListener());
        viewPanel.addMouseListener(inputHandler.getMouseListener());

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
                sound.playSound("eat.wav");
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
            if (dir[0] == -ghost.getDx() && dir[1] == -ghost.getDy()) continue;

            int testX = ghost.getX() + dir[0] * ghost.getSpeed();
            int testY = ghost.getY() + dir[1] * ghost.getSpeed();

            if (canMove(testX, testY)) {
                validMoves.add(dir);
            }
        }
        
        if (validMoves.isEmpty()) {
            ghost.setDirection(-ghost.getDx(), -ghost.getDy());
            return;
        }

        int[] chosenDir = validMoves.get(random.nextInt(validMoves.size()));
        ghost.setDirection(chosenDir[0], chosenDir[1]);
    }

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

    private void startLevel(String levelName) {
        this.crmap = levelName;
        model.setMap(new Map(levelName));
        model.setGameOver(false);
        model.setCurrentState(GameState.PLAYING);
        sound.loopSound("background.wav");
    }

    // Phương thức công khai được gọi từ InputHandler để xử lý click chuột
    public void handleMousePress(int mx, int my) {
        GameState state = model.getCurrentState();

        if (state == GameState.MAIN_MENU) {
            for (MenuButton btn : model.getMainMenuButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        } else if (state == GameState.PLAYING) {
            for (MenuButton btn : model.getGamePlayButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        } else if (state == GameState.START) {
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
            MenuButton btn = model.getControlsButtons();
            if(btn.isClicked(mx,my)){
                handleButtonClick(btn.getActionCommand());
            }
        }
    }

    // Phim tat
    public void handleKeyPress(int key) {
        GameState state = model.getCurrentState();

        // ESC: Pause/Resume
        if (key == java.awt.event.KeyEvent.VK_ESCAPE) {
            if (state == GameState.PLAYING) {
                model.setCurrentState(GameState.PAUSED);
            } else if (state == GameState.PAUSED) {
                model.setCurrentState(GameState.PLAYING);
            }
        }

        // Settings : Q
        if (key == KeyEvent.VK_Q){
            if (state == GameState.PLAYING){
                model.setCurrentState(GameState.SETTINGS);
            }else if (state == GameState.SETTINGS){
                model.setCurrentState(GameState.PLAYING);
            }
        }

        // move Pacman
        if (state == GameState.PLAYING) {
            PacMan pacman = model.getPacman();
            if (pacman == null) return;

            if (key == java.awt.event.KeyEvent.VK_UP || key == java.awt.event.KeyEvent.VK_W) {
                pacman.setDirection(0, -1);
            } else if (key == java.awt.event.KeyEvent.VK_DOWN || key == java.awt.event.KeyEvent.VK_S) {
                pacman.setDirection(0, 1);
            } else if (key == java.awt.event.KeyEvent.VK_LEFT || key == java.awt.event.KeyEvent.VK_A) {
                pacman.setDirection(-1, 0);
            } else if (key == java.awt.event.KeyEvent.VK_RIGHT || key == java.awt.event.KeyEvent.VK_D) {
                pacman.setDirection(1, 0);
            }
        }
    }

    private void handleButtonClick(String command) {
        switch (command) {
            case "PLAY":
                model.setCurrentState(GameState.START);
                break;
            case "CONTROLS":
                model.setCurrentState(GameState.CONTROLS);
                break;
            case "PAUSED":
                model.setCurrentState(GameState.PAUSED);
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
                startLevel(this.crmap);
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
}