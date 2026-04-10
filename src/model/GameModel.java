package model;

import utils.AssetManager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private Map map;
    public static final int TILE_SIZE = 32;
    private List<Ghost> ghosts;
    private List<Item> items;
    private PacMan pacman;
    private int score = 0;

    // Quan ly button
    private List<MenuButton> settingsButtons;
    private List<MenuButton> mainMenuButtons;
    private List<MenuButton> pauseButtons;
    private List<MenuButton> quitConfirmButtons;
    private List<MenuButton> startButtons;
    private MenuButton controlsButtons;
    private List<MenuButton> gamePlayButtons;
    private List<MenuButton> gameOverButtons;
    private List<MenuButton> gameWonButtons;
    private List<MenuButton> loginButtons;
    private List<MenuButton> registerButtons;
    private List<MenuButton> levelLockedButtons;
    private List<MenuButton> levelInfoButtons;

    // Level progress fields
    private int maxUnlockedLevel = 1;
    private int selectedLevelId = 1;
    private int selectedLevelHighScore = -1;

    // Login/Register data
    private String loginUsername = "";
    private String loginPassword = "";
    private String registerUsername = "";
    private String registerPassword = "";
    private String loginMessage = "";
    private int activeField = 0; // 0 for username, 1 for password
    private String currentUser = null;
    private boolean showPassword = false;

    // Game state
    private GameState currentState = GameState.LOGIN;
    private boolean isGameOver = false;
    private boolean isGameWon = false;


    public GameModel() {
        this.ghosts = new ArrayList<>();
        this.items = new ArrayList<>();

        loadBackgrounds();
        initMenus();
    }

    private void loadBackgrounds() {
        AssetManager am = AssetManager.getInstance();
        am.getImage("bg_menu");
        am.getImage("bg_start");
        am.getImage("bg_pause");
        am.getImage("bg_settings");
        am.getImage("bg_controls");
        am.getImage("bg_gameover");
        am.getImage("bg_gamewon");
        am.getImage("bg_quit");
        am.getImage("bg_login");
        am.getImage("bg_register");
    }

    private void initMenus() {
        mainMenuButtons = new ArrayList<>();
        pauseButtons = new ArrayList<>();
        quitConfirmButtons = new ArrayList<>();
        startButtons = new ArrayList<>();
        settingsButtons = new ArrayList<>();
        gamePlayButtons = new ArrayList<>();
        gameOverButtons = new ArrayList<>();
        gameWonButtons = new ArrayList<>();
        loginButtons = new ArrayList<>();
        registerButtons = new ArrayList<>();

        // Login Menu
        loginButtons.add(new MenuButton(185, 407, 122, 41, AssetManager.getInstance().getImage("login_confirm"), "CONFIRM"));
        loginButtons.add(new MenuButton(321, 409, 96, 38, AssetManager.getInstance().getImage("login_register"), "GO REGISTER"));
        loginButtons.add(new MenuButton(410, 366, 27, 27, AssetManager.getInstance().getImage("hidePassword"), "TOGGLE_PASSWORD"));

        // Register Menu
        registerButtons.add(new MenuButton(188, 405, 117, 41, AssetManager.getInstance().getImage("register_confirm"), "CONFIRM"));
        registerButtons.add(new MenuButton(320, 407, 96, 38, AssetManager.getInstance().getImage("register_back"), "BACK"));
        registerButtons.add(new MenuButton(407, 364, 27, 27, AssetManager.getInstance().getImage("hidePassword"), "TOGGLE_PASSWORD"));

        // Popups
        levelLockedButtons = new ArrayList<>();
        levelLockedButtons.add(new MenuButton(400, 250, 40, 40, null, "X"));

        levelInfoButtons = new ArrayList<>();
        levelInfoButtons.add(new MenuButton(180, 360, 110, 40, null, "PLAY"));
        levelInfoButtons.add(new MenuButton(310, 360, 110, 40, null, "CANCEL"));

        // Menu Chính
        mainMenuButtons.add(new MenuButton(68-21, 240-23, 230, 127, AssetManager.getInstance().getImage("Play Button"), "PLAY"));
        mainMenuButtons.add(new MenuButton(354-21, 240-23, 230, 127, AssetManager.getInstance().getImage("Controls Button"), "CONTROLS"));
        mainMenuButtons.add(new MenuButton(68-21, 415-23, 230, 127, AssetManager.getInstance().getImage("Settings Button"), "SETTINGS"));
        mainMenuButtons.add(new MenuButton(354-21, 415-23, 230, 127, AssetManager.getInstance().getImage("Quit Button"), "QUIT"));

        //Start Button
        startButtons.add(new MenuButton(63,54,110,145,AssetManager.getInstance().getImage("level1"),"LEVEL1" ));
        startButtons.add(new MenuButton(249,54,110,145,AssetManager.getInstance().getImage("level2"),"LEVEL2" ));
        startButtons.add(new MenuButton(437,54,110,145,AssetManager.getInstance().getImage("level3"),"LEVEL3" ));
        startButtons.add(new MenuButton(63,220,110,145,AssetManager.getInstance().getImage("level4"),"LEVEL4" ));
        startButtons.add(new MenuButton(249,220,110,145,AssetManager.getInstance().getImage("level5"),"LEVEL5" ));
        startButtons.add(new MenuButton(437,220,110,145,AssetManager.getInstance().getImage("level6"),"LEVEL6" ));
        startButtons.add(new MenuButton(63,392,110,145,AssetManager.getInstance().getImage("level7"),"LEVEL7" ));
        startButtons.add(new MenuButton(249,392,110,145,AssetManager.getInstance().getImage("level8"),"LEVEL8" ));
        startButtons.add(new MenuButton(437,392,110,145,AssetManager.getInstance().getImage("level9"),"LEVEL9" ));
        startButtons.add(new MenuButton(187,581,234,69,AssetManager.getInstance().getImage("back_to_menu"),"BACK_TO_MENU" ));


        // Menu Pause
        pauseButtons.add(new MenuButton(159, 212, 293, 112, AssetManager.getInstance().getImage("Resume Button"), "RESUME"));
        pauseButtons.add(new MenuButton(121, 400, 132, 132, AssetManager.getInstance().getImage("Return Square Button"), "RESTART"));
        pauseButtons.add(new MenuButton(353, 421, 150, 90, AssetManager.getInstance().getImage("Menu Button"), "MENU"));

        // Quit Confirm
        quitConfirmButtons.add(new MenuButton(130, 350, 120, 120, AssetManager.getInstance().getImage("quit_yes"), "YES_QUIT"));
        quitConfirmButtons.add(new MenuButton(375, 350, 120, 120, AssetManager.getInstance().getImage("quit_no"), "NO_QUIT"));

        //Controls
        controlsButtons = new MenuButton(232,461,157,91,AssetManager.getInstance().getImage("back"),"BACK_TO_MENU" );

        //Settings
        settingsButtons.add(new MenuButton(164, 293, 241, 152, AssetManager.getInstance().getImage("on_off"), "TOGGLE_SOUND"));
        settingsButtons.add(new MenuButton(162, 464, 243, 154, AssetManager.getInstance().getImage("back_settings"), "BACK_TO_MENU"));

        //GamePlay
        gamePlayButtons.add(new MenuButton(508, 10,30,30, AssetManager.getInstance().getImage("Pause Square Button"),"PAUSED" ));
        gamePlayButtons.add(new MenuButton(558,10,30,30,AssetManager.getInstance().getImage("Settings Square Button"),"SETTINGS" ));

        //GameOver
        gameOverButtons.add(new MenuButton(178, 372, 108, 45, AssetManager.getInstance().getImage("gameover_replay"), "RESTART"));
        gameOverButtons.add(new MenuButton(320, 372, 100, 45, AssetManager.getInstance().getImage("gameover_menu"), "MENU"));

        //GameWon
        gameWonButtons.add(new MenuButton(188, 370, 100, 42, AssetManager.getInstance().getImage("gamewin_next"), "NEXT_LEVEL"));
        gameWonButtons.add(new MenuButton(318, 370, 92, 40, AssetManager.getInstance().getImage("gamewin_menu"), "MENU"));
    }

    public void setMap(Map map) {
        this.map = map;
        spawnEntities();
    }

    private void spawnEntities() {
        ghosts.clear();
        items.clear();
        this.score =0;
        char[][] grid = map.getGrid();

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                char cell = grid[row][col];

                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;

                if (cell == 'r') {
                    ghosts.add(new Ghost(x, y,"redGhost"));
                    grid[row][col] = ' ';
                } else if (cell == 'p') {
                    ghosts.add(new Ghost(x,y,"pinkGhost"));
                    grid[row][col] = ' ';
                } else if (cell == 'o') {
                    ghosts.add(new Ghost(x,y,"orangeGhost"));
                    grid[row][col] = ' ';
                } else if (cell == 'b') {
                    ghosts.add(new Ghost(x,y,"blueGhost"));
                    grid[row][col] = ' ';
                } else if (cell == 'P') {
                    pacman = new PacMan(x, y);
                    grid[row][col] = ' ';
                } else if (cell == '.') {
                    items.add(new Item(x, y, "cherry", 10, "cherry"));
                    grid[row][col] = ' ';
                }
            }
        }
    }

    public void resetGame(String levelName) {
        this.score =0;
        this.setMap(new Map(levelName));
        this.isGameOver = false;
        this.isGameWon = false;

    }

    // Button
    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState currentState) { this.currentState = currentState; }
    public List<MenuButton> getMainMenuButtons() { return mainMenuButtons; }
    public List<MenuButton> getPauseButtons() { return pauseButtons; }
    public List<MenuButton> getQuitConfirmButtons() { return quitConfirmButtons; }
    public List<MenuButton> getStartButtons () { return  startButtons;}
    public MenuButton getControlsButtons () {return controlsButtons;}
    public List<MenuButton> getGamePlayButtons () {return gamePlayButtons;}
    public List<MenuButton> getGameOverButtons() { return gameOverButtons; }
    public List<MenuButton> getGameWonButtons() { return gameWonButtons; }
    public List<MenuButton> getLoginButtons() { return loginButtons; }
    public List<MenuButton> getRegisterButtons() { return registerButtons; }

    public String getLoginUsername() { return loginUsername; }
    public void setLoginUsername(String loginUsername) { this.loginUsername = loginUsername; }
    public String getLoginPassword() { return loginPassword; }
    public void setLoginPassword(String loginPassword) { this.loginPassword = loginPassword; }
    public String getRegisterUsername() { return registerUsername; }
    public void setRegisterUsername(String registerUsername) { this.registerUsername = registerUsername; }
    public String getRegisterPassword() { return registerPassword; }
    public void setRegisterPassword(String registerPassword) { this.registerPassword = registerPassword; }
    public String getLoginMessage() { return loginMessage; }
    public void setLoginMessage(String loginMessage) { this.loginMessage = loginMessage; }
    public int getActiveField() { return activeField; }
    public void setActiveField(int activeField) { this.activeField = activeField; }
    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String currentUser) { this.currentUser = currentUser; }
    public boolean isShowPassword() { return showPassword; }
    public void setShowPassword(boolean showPassword) { this.showPassword = showPassword; }

    public int getMaxUnlockedLevel() { return maxUnlockedLevel; }
    public void setMaxUnlockedLevel(int maxUnlockedLevel) { this.maxUnlockedLevel = maxUnlockedLevel; }
    public int getSelectedLevelId() { return selectedLevelId; }
    public void setSelectedLevelId(int selectedLevelId) { this.selectedLevelId = selectedLevelId; }
    public int getSelectedLevelHighScore() { return selectedLevelHighScore; }
    public void setSelectedLevelHighScore(int selectedLevelHighScore) { this.selectedLevelHighScore = selectedLevelHighScore; }
    public List<MenuButton> getLevelLockedButtons() { return levelLockedButtons; }
    public List<MenuButton> getLevelInfoButtons() { return levelInfoButtons; }


    // PHẦN LOGIC GAME
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean isGameOver) { this.isGameOver = isGameOver; }
    public boolean isGameWon() { return isGameWon; }
    public void setGameWon(boolean isGameWon) { this.isGameWon = isGameWon; }

    // Playing
    public Map getMap() { return map; }
    public List<Ghost> getGhosts() { return ghosts; }
    public List<Item> getItems() { return items; }
    public PacMan getPacman() { return pacman; }
    public int getScore() { return score; }
    public void addScore(int points) { this.score += points; }
    public List<MenuButton> getSettingsButtons() { return settingsButtons; }
}