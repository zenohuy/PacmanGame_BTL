package controller;

import model.*;
import utils.AssetManager;
import utils.DatabaseManager;
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
    private GameState previousState = null;
    private GameState stateBeforeSettings = null;

    //Xet direction change pacman
    private int bufferedDx = 0;
    private int bufferedDy = 0;

    private Thread gameThread;
    private boolean isRunning = false;
    private final int FPS = 60;

    private final java.util.Random random = new java.util.Random();

    public GameController() {
        model = new GameModel();

        sound = SoundManager.getInstance();
        sound.loadSound("eat.wav");
        sound.loadSound("background.wav");
        sound.loadSound("menu.wav");
        sound.loadSound("click.wav");
        sound.loadSound("game_loss.wav");
        sound.loadSound("game_win.wav");

        viewPanel = new GamePanel(model);

        viewPanel.setFocusTraversalKeysEnabled(false); // Tranh de viewPanel nuot phim tab trong Swing

        InputHandler inputHandler = new InputHandler(this);
        viewPanel.addKeyListener(inputHandler.getKeyListener());
        viewPanel.addMouseListener(inputHandler.getMouseListener());
        viewPanel.addMouseMotionListener(inputHandler.getMouseMotionListener());

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
                updateHoverAnimations();
                viewPanel.repaint();
                delta--;
            }
        }
    }

    private void update() {
        handleStateSound();
        if (model.getCurrentState() != GameState.PLAYING || model.isGameOver() || model.isGameWon()) return;

        PacMan pacman = model.getPacman();
        if (pacman == null) return;

        final int tileSize = 32;
        boolean onTileCenter = (pacman.getX() % tileSize == 0) && (pacman.getY() % tileSize == 0);

        // Xử lý hướng dự định
        if (bufferedDx != 0 || bufferedDy != 0) {
            if (bufferedDx == -pacman.getDx() && bufferedDy == -pacman.getDy()) {
                if (canMove(pacman.getX() + bufferedDx * pacman.getSpeed(), pacman.getY() + bufferedDy * pacman.getSpeed())) {
                    pacman.setDirection(bufferedDx, bufferedDy);
                    bufferedDx = 0; bufferedDy = 0;
                }
            }
            else if (onTileCenter) {
                if (canMove(pacman.getX() + bufferedDx * pacman.getSpeed(), pacman.getY() + bufferedDy * pacman.getSpeed())) {
                    pacman.setDirection(bufferedDx, bufferedDy);
                    bufferedDx = 0; bufferedDy = 0;
                }
            }
        }

        int nextX = pacman.getX() + pacman.getDx() * pacman.getSpeed();
        int nextY = pacman.getY() + pacman.getDy() * pacman.getSpeed();

        if (canMove(nextX, nextY)) {
            pacman.update();
            checkEatCherry();
        } else {
            pacman.setX(((pacman.getX() + tileSize / 2) / tileSize) * tileSize);
            pacman.setY(((pacman.getY() + tileSize / 2) / tileSize) * tileSize);
            pacman.setDirection(0, 0);
        }
        if (model.getGhosts() != null) {
            for (model.Ghost ghost : model.getGhosts()) moveGhost(ghost);
        }
        checkGhostCollision();
        checkWinCondition();
    }

    private boolean canMove(int nextX, int nextY) {
        int tileSize = 32;
        int offset = 2;

        int leftCol   = (nextX + offset) / tileSize;
        int rightCol  = (nextX + tileSize - offset - 1) / tileSize;
        int topRow    = (nextY + offset) / tileSize;
        int bottomRow = (nextY + tileSize - offset - 1) / tileSize;

        char[][] grid = model.getMap().getGrid();

        // Giới hạn cứng: 19 cột (0-18), 21 hàng (0-20)
        if (leftCol < 0 || rightCol >= 19 ||
                topRow < 0 || bottomRow >= 21) {
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

    private void moveGhost(Ghost ghost) {
        int tileSize = 32;

        int nextX = ghost.getX() + ghost.getDx() * ghost.getSpeed();
        int nextY = ghost.getY() + ghost.getDy() * ghost.getSpeed();

        if (canMove(nextX, nextY)) {
            ghost.move();

            if (ghost.getX() % tileSize == 0 && ghost.getY() % tileSize == 0) {
                changeGhostDirection(ghost);
            }

        } else {
            if (ghost.getDx() != 0) {
                ghost.setX((ghost.getX() / tileSize) * tileSize);
            }
            if (ghost.getDy() != 0) {
                ghost.setY((ghost.getY() / tileSize) * tileSize);
            }
            changeGhostDirection(ghost);
        }
    }

    private void changeGhostDirection(Ghost ghost) {
        int tileSize = 32;

        int baseX = (ghost.getX() / tileSize) * tileSize;
        int baseY = (ghost.getY() / tileSize) * tileSize;

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        java.util.ArrayList<int[]> validMoves = new java.util.ArrayList<>();

        for (int[] dir : directions) {
            if (dir[0] == -ghost.getDx() && dir[1] == -ghost.getDy()) continue;

            int testX = baseX + dir[0] * tileSize;
            int testY = baseY + dir[1] * tileSize;

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
                sound.stopAndRemoveLoop("background.wav");
                sound.playSound("game_loss.wav");
                break;
            }
        }
    }

    private void checkWinCondition() {
        if (model.getItems().isEmpty()) {
            if (!model.isGameWon()) {
                model.setGameWon(true);
                sound.stopAndRemoveLoop("background.wav");
                sound.playSound("game_win.wav");
                if (model.getCurrentUser() != null) {
                    DatabaseManager.getInstance().updateScoreAndUnlockNext(
                            model.getCurrentUser(), model.getSelectedLevelId(), model.getScore()
                    );
                    model.setMaxUnlockedLevel(Math.max(model.getMaxUnlockedLevel(), model.getSelectedLevelId() + 1));
                }
            }
        }
    }

    private void startLevel(String levelName) {
        this.crmap = levelName;
        model.setMap(new Map(levelName));
        model.setGameOver(false);
        model.setGameWon(false);
        sound.stopAllLooping();
        model.setCurrentState(GameState.PLAYING);
        sound.loopSound("background.wav");
    }

    // Xác định nhóm nhạc nền cho từng trạng thái
    private String getMusicForState(GameState state) {
        if (state == null) return null;
        switch (state) {
            case MAIN_MENU:
            case START:
            case CONTROLS:
            case QUIT_CONFIRM:
            case LEVEL_LOCKED_POPUP:
            case LEVEL_INFO_POPUP:
                return "menu.wav";
            case PLAYING:
                return "background.wav";
            default:
                return null; // PAUSED, SETTINGS, LOGIN, REGISTER: không có nhạc
        }
    }

    // Quản lý âm thanh khi chuyển trạng thái
    private void handleStateSound() {
        GameState currentState = model.getCurrentState();
        if (currentState == previousState) return;

        String prevMusic = getMusicForState(previousState);
        String currMusic = getMusicForState(currentState);

        if (prevMusic != null && prevMusic.equals(currMusic)) {
            previousState = currentState;
            return;
        }

        if (prevMusic != null) {
            sound.stopAndRemoveLoop(prevMusic);
        }

        if (currMusic != null) {
            if (currentState == GameState.PLAYING && (model.isGameOver() || model.isGameWon())) {
            } else {
                sound.loopSound(currMusic);
            }
        }

        previousState = currentState;
    }

    public void handleMousePress(int mx, int my) {
        GameState state = model.getCurrentState();

        if (state == GameState.LOGIN) {
            if (my >= 304 && my <= 331 && mx >= 225 && mx <= 407) model.setActiveField(0);
            else if (my >= 366 && my <= 393 && mx >= 225 && mx <= 407) model.setActiveField(1);

            for (MenuButton btn : model.getLoginButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.REGISTER) {
            if (my >= 302 && my <= 329 && mx >= 225 && mx <= 404) model.setActiveField(0);
            else if (my >= 364 && my <= 391 && mx >= 225 && mx <= 404) model.setActiveField(1);

            for (MenuButton btn : model.getRegisterButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.MAIN_MENU) {
            for (MenuButton btn : model.getMainMenuButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        } else if (state == GameState.PLAYING) {
            if (model.isGameOver()) {
                for (MenuButton btn : model.getGameOverButtons()) {
                    if (btn.isClicked(mx, my)) {
                        handleButtonClick(btn.getActionCommand());
                    }
                }
                return;
            }

            if (model.isGameWon()) {
                for (MenuButton btn : model.getGameWonButtons()) {
                    if (btn.isClicked(mx, my)) {
                        handleButtonClick(btn.getActionCommand());
                    }
                }
                return;
            }

            for (MenuButton btn : model.getGamePlayButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.LEVEL_LOCKED_POPUP) {
            for (MenuButton btn : model.getLevelLockedButtons()) {
                if (btn.isClicked(mx, my)) {
                    handleButtonClick(btn.getActionCommand());
                }
            }
        }
        else if (state == GameState.LEVEL_INFO_POPUP) {
            for (MenuButton btn : model.getLevelInfoButtons()) {
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
            MenuButton btn = model.getControlsButtons();
            if(btn.isClicked(mx,my)){
                handleButtonClick(btn.getActionCommand());
            }
        }
    }

    // Xử lý di chuột (hover)
    public void handleMouseMove(int mx, int my) {
        GameState state = model.getCurrentState();
        java.util.List<MenuButton> buttons = getButtonsForState(state);
        if (buttons != null) {
            for (MenuButton btn : buttons) {
                btn.setHovered(btn.contains(mx, my));
            }
        }
        if (state == GameState.CONTROLS) {
            MenuButton btn = model.getControlsButtons();
            btn.setHovered(btn.contains(mx, my));
        }
    }

    private java.util.List<MenuButton> getButtonsForState(GameState state) {
        switch (state) {
            case LEVEL_LOCKED_POPUP: return model.getLevelLockedButtons();
            case LEVEL_INFO_POPUP:   return model.getLevelInfoButtons();
            case LOGIN:        return model.getLoginButtons();
            case REGISTER:     return model.getRegisterButtons();
            case MAIN_MENU:    return model.getMainMenuButtons();
            case START:        return model.getStartButtons();
            case PAUSED:       return model.getPauseButtons();
            case QUIT_CONFIRM: return model.getQuitConfirmButtons();
            case SETTINGS:     return model.getSettingsButtons();
            case PLAYING:
                if (model.isGameOver()) return model.getGameOverButtons();
                if (model.isGameWon())  return model.getGameWonButtons();
                return model.getGamePlayButtons();
            default:           return null;
        }
    }

    // Cập nhật animation hover cho tất cả buttons
    private void updateHoverAnimations() {
        updateButtonListAnimation(model.getLevelLockedButtons());
        updateButtonListAnimation(model.getLevelInfoButtons());
        updateButtonListAnimation(model.getLoginButtons());
        updateButtonListAnimation(model.getRegisterButtons());
        updateButtonListAnimation(model.getMainMenuButtons());
        updateButtonListAnimation(model.getStartButtons());
        updateButtonListAnimation(model.getPauseButtons());
        updateButtonListAnimation(model.getQuitConfirmButtons());
        updateButtonListAnimation(model.getSettingsButtons());
        updateButtonListAnimation(model.getGamePlayButtons());
        updateButtonListAnimation(model.getGameOverButtons());
        updateButtonListAnimation(model.getGameWonButtons());
        if (model.getControlsButtons() != null) {
            model.getControlsButtons().updateHoverAnimation();
        }
    }

    private void updateButtonListAnimation(java.util.List<MenuButton> buttons) {
        if (buttons == null) return;
        for (MenuButton btn : buttons) {
            btn.updateHoverAnimation();
        }
    }

    // Phim tat
    public void handleKeyPress(int key) {
        GameState state = model.getCurrentState();

        if (state == GameState.LOGIN || state == GameState.REGISTER) {
            if (key == KeyEvent.VK_TAB) {
                model.setActiveField(1 - model.getActiveField());
            } else if (key == KeyEvent.VK_ENTER) {
                if (state == GameState.LOGIN) handleButtonClick("CONFIRM");
                else handleButtonClick("CONFIRM"); // Register confirm
            } else if (key == KeyEvent.VK_BACK_SPACE) {
                if (state == GameState.LOGIN) {
                    if (model.getActiveField() == 0 && model.getLoginUsername().length() > 0) {
                        model.setLoginUsername(model.getLoginUsername().substring(0, model.getLoginUsername().length() - 1));
                    } else if (model.getActiveField() == 1 && model.getLoginPassword().length() > 0) {
                        model.setLoginPassword(model.getLoginPassword().substring(0, model.getLoginPassword().length() - 1));
                    }
                } else {
                    if (model.getActiveField() == 0 && model.getRegisterUsername().length() > 0) {
                        model.setRegisterUsername(model.getRegisterUsername().substring(0, model.getRegisterUsername().length() - 1));
                    } else if (model.getActiveField() == 1 && model.getRegisterPassword().length() > 0) {
                        model.setRegisterPassword(model.getRegisterPassword().substring(0, model.getRegisterPassword().length() - 1));
                    }
                }
            }
        }

        // ESC: Pause/Resume
        if (key == java.awt.event.KeyEvent.VK_ESCAPE) {
            stateBeforeSettings = GameState.PLAYING;
            if (state == GameState.PLAYING) {
                model.setCurrentState(GameState.PAUSED);
            } else if (state == GameState.PAUSED) {
                if (stateBeforeSettings == GameState.PLAYING) {
                    model.setCurrentState(GameState.PLAYING);
                } else {
                    model.setCurrentState(GameState.MAIN_MENU);
                }
                stateBeforeSettings = null;
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
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                bufferedDx = 0;  bufferedDy = -1;
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                bufferedDx = 0;  bufferedDy = 1;
            } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                bufferedDx = -1; bufferedDy = 0;
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                bufferedDx = 1;  bufferedDy = 0;
            }
        }
    }

    public void handleKeyTyped(char c) {
        GameState state = model.getCurrentState();
        if (state != GameState.LOGIN && state != GameState.REGISTER) return;

        if (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '@' || c == '.') {
            if (state == GameState.LOGIN) {
                if (model.getActiveField() == 0 && model.getLoginUsername().length() < 20) {
                    model.setLoginUsername(model.getLoginUsername() + c);
                } else if (model.getActiveField() == 1 && model.getLoginPassword().length() < 20) {
                    model.setLoginPassword(model.getLoginPassword() + c);
                }
            } else {
                if (model.getActiveField() == 0 && model.getRegisterUsername().length() < 20) {
                    model.setRegisterUsername(model.getRegisterUsername() + c);
                } else if (model.getActiveField() == 1 && model.getRegisterPassword().length() < 20) {
                    model.setRegisterPassword(model.getRegisterPassword() + c);
                }
            }
        }
    }

    private void goToNextLevel() {
        String nextLevel;
        switch (crmap) {
            case "leveltest": nextLevel = "level2"; break;
            case "level1":    nextLevel = "level2"; break;
            case "level2":    nextLevel = "level3"; break;
            case "level3":    nextLevel = "level1"; break;
            default:          nextLevel = "level1"; break;
        }
        startLevel(nextLevel);
    }

    private void handleButtonClick(String command) {
        sound.playSound("click.wav");
        switch (command) {
            case "CONFIRM":
                if (model.getCurrentState() == GameState.LOGIN) {
                    if (DatabaseManager.getInstance().login(model.getLoginUsername(), model.getLoginPassword())) {
                        model.setLoginMessage("Success!");
                        model.setCurrentUser(model.getLoginUsername());
                        model.setMaxUnlockedLevel(DatabaseManager.getInstance().getUnlockedMaxLevel(model.getCurrentUser()));
                        model.setCurrentState(GameState.MAIN_MENU);
                    } else {
                        model.setLoginMessage("Invalid username or password");
                    }
                } else if (model.getCurrentState() == GameState.REGISTER) {
                    if (model.getRegisterUsername().length() < 3) {
                        model.setLoginMessage("Username too short");
                    } else if (model.getRegisterPassword().length() < 3) {
                        model.setLoginMessage("Password too short");
                    } else if (DatabaseManager.getInstance().register(model.getRegisterUsername(), model.getRegisterPassword())) {
                        model.setLoginMessage("Registered! Please login.");
                        model.setCurrentState(GameState.LOGIN);
                        model.setLoginUsername(model.getRegisterUsername());
                        model.setLoginPassword("");
                        model.setRegisterUsername("");
                        model.setRegisterPassword("");
                    } else {
                        model.setLoginMessage("Registration failed (Username exists?)");
                    }
                }
                break;
            case "GO REGISTER":
                model.setLoginMessage("");
                model.setCurrentState(GameState.REGISTER);
                break;
            case "BACK":
                model.setLoginMessage("");
                model.setCurrentState(GameState.LOGIN);
                break;
            case "PLAY":
                if (model.getCurrentState() == GameState.LEVEL_INFO_POPUP) {
                    startLevel(getMapName(model.getSelectedLevelId()));
                } else {
                    model.setCurrentState(GameState.START);
                }
                break;
            case "X":
            case "CANCEL":
                model.setCurrentState(GameState.START);
                break;
            case "CONTROLS":
                model.setCurrentState(GameState.CONTROLS);
                break;
            case "PAUSED":
                model.setCurrentState(GameState.PAUSED);
                break;
            case "SETTINGS":
                stateBeforeSettings = model.getCurrentState();
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
                sound.stopSound("game_loss.wav");
                sound.stopSound("game_win.wav");
                sound.stopAllLooping();
                startLevel(this.crmap);
                model.setCurrentState(GameState.PLAYING);
                break;
            case "MENU":
                sound.stopSound("game_loss.wav");
                sound.stopSound("game_win.wav");
                sound.stopAllLooping();
                model.setCurrentState(GameState.MAIN_MENU);
                break;
            case "TOGGLE_PASSWORD":
                boolean newShowState = !model.isShowPassword();
                model.setShowPassword(newShowState);
                java.awt.image.BufferedImage eyeIcon = utils.AssetManager.getInstance()
                        .getImage(newShowState ? "viewPassword" : "hidePassword");
                for (MenuButton btn : model.getLoginButtons()) {
                    if ("TOGGLE_PASSWORD".equals(btn.getActionCommand())) btn.setImage(eyeIcon);
                }
                for (MenuButton btn : model.getRegisterButtons()) {
                    if ("TOGGLE_PASSWORD".equals(btn.getActionCommand())) btn.setImage(eyeIcon);
                }
                break;
            case "TOGGLE_SOUND":
                SoundManager.getInstance().toggleMute();
                break;
            case "BACK_TO_MENU":
                if (stateBeforeSettings == GameState.PLAYING) {
                    model.setCurrentState(GameState.PLAYING);
                } else {
                    model.setCurrentState(GameState.MAIN_MENU);
                }
                stateBeforeSettings = null;
                break;
            case "LEVEL1": handleLevelSelection(1); break;
            case "LEVEL2": handleLevelSelection(2); break;
            case "LEVEL3": handleLevelSelection(3); break;
            case "LEVEL4": handleLevelSelection(4); break;
            case "LEVEL5": handleLevelSelection(5); break;
            case "LEVEL6": handleLevelSelection(6); break;
            case "LEVEL7": handleLevelSelection(7); break;
            case "LEVEL8": handleLevelSelection(8); break;
            case "LEVEL9": handleLevelSelection(9); break;
            case "NEXT_LEVEL":
                sound.stopSound("game_win.wav");
                goToNextLevel();
                break;
        }
    }

    private void handleLevelSelection(int requestedLvl) {
        if (requestedLvl > model.getMaxUnlockedLevel()) {
            model.setCurrentState(GameState.LEVEL_LOCKED_POPUP);
        } else {
            int highScore = DatabaseManager.getInstance().getHighScore(model.getCurrentUser(), requestedLvl);
            model.setSelectedLevelId(requestedLvl);
            if (highScore >= 0) {
                model.setSelectedLevelHighScore(highScore);
                model.setCurrentState(GameState.LEVEL_INFO_POPUP);
            } else {
                startLevel(getMapName(requestedLvl));
            }
        }
    }

    private String getMapName(int lvl) {
        int mod = lvl % 3;
        if (mod == 1) return "leveltest";
        if (mod == 2) return "level2";
        return "level3";
    }
}