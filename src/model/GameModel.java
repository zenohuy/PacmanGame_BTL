package model;

import utils.AssetManager;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private Map map;
    public static final int TILE_SIZE = 32;
    private List<Ghost> ghosts;
    private PacMan pacman;
    private int score = 0;
    private boolean isGameOver = false;
    private List<MenuButton> settingsButtons;

    // Quan ly menu
    private GameState currentState = GameState.MAIN_MENU;
    private List<MenuButton> mainMenuButtons;
    private List<MenuButton> pauseButtons;
    private List<MenuButton> quitConfirmButtons;
    private List<MenuButton> startButtons;
    private MenuButton controlsButtons;

    public GameModel() {
        this.ghosts = new ArrayList<>();
        initMenus();
    }

    private void initMenus() {
        mainMenuButtons = new ArrayList<>();
        pauseButtons = new ArrayList<>();
        quitConfirmButtons = new ArrayList<>();
        startButtons = new ArrayList<>();

        int centerX = 608/2 -100;

        // Menu Chính
        mainMenuButtons.add(new MenuButton(centerX, 150, 200, 60, AssetManager.getInstance().getImage("Play Button"), "PLAY"));
        mainMenuButtons.add(new MenuButton(centerX, 230, 200, 60, AssetManager.getInstance().getImage("Controls Button"), "CONTROLS"));
        mainMenuButtons.add(new MenuButton(centerX, 310, 200, 60, AssetManager.getInstance().getImage("Settings Button"), "SETTINGS"));
        mainMenuButtons.add(new MenuButton(centerX, 390, 200, 60, AssetManager.getInstance().getImage("Quit Button"), "QUIT"));

        //Start Button
        startButtons.add(new MenuButton(77,60,100,100,AssetManager.getInstance().getImage("level1"),"LEVEL1" ));
        startButtons.add(new MenuButton(254,60,100,100,AssetManager.getInstance().getImage("level2"),"LEVEL2" ));
        startButtons.add(new MenuButton(431,60,100,100,AssetManager.getInstance().getImage("level3"),"LEVEL3" ));
        startButtons.add(new MenuButton(77,220,100,100,AssetManager.getInstance().getImage("level4"),"LEVEL4" ));
        startButtons.add(new MenuButton(254,220,100,100,AssetManager.getInstance().getImage("level5"),"LEVEL5" ));
        startButtons.add(new MenuButton(431,220,100,100,AssetManager.getInstance().getImage("level6"),"LEVEL6" ));
        startButtons.add(new MenuButton(77,380,100,100,AssetManager.getInstance().getImage("level7"),"LEVEL7" ));
        startButtons.add(new MenuButton(254,380,100,100,AssetManager.getInstance().getImage("level8"),"LEVEL8" ));
        startButtons.add(new MenuButton(431,380,100,100,AssetManager.getInstance().getImage("level9"),"LEVEL9" ));
        startButtons.add(new MenuButton(268,540,100,100,AssetManager.getInstance().getImage("Back Square Button"),"BACK_TO_MENU" ));


        // Menu Pause
        pauseButtons.add(new MenuButton(centerX, 200, 200, 60, AssetManager.getInstance().getImage("Resume Button"), "RESUME"));
        pauseButtons.add(new MenuButton(centerX, 280, 200, 60, AssetManager.getInstance().getImage("Return Square Button"), "RESTART"));
        pauseButtons.add(new MenuButton(centerX, 360, 200, 60, AssetManager.getInstance().getImage("Menu Button"), "MENU"));

        // Quit Confirm
        quitConfirmButtons.add(new MenuButton(centerX, 300, 80, 80, AssetManager.getInstance().getImage("V Square Button"), "YES_QUIT"));
        quitConfirmButtons.add(new MenuButton(centerX + 120, 300, 80, 80, AssetManager.getInstance().getImage("X Square Button"), "NO_QUIT"));

        //Controls
        controlsButtons = new MenuButton(268,540,100,100,AssetManager.getInstance().getImage("Back Square Button"),"BACK_TO_MENU" );


        settingsButtons = new ArrayList<>();
        settingsButtons.add(new MenuButton(centerX, 250, 200, 60, AssetManager.getInstance().getImage("On Off Square Button"), "TOGGLE_SOUND"));

        settingsButtons.add(new MenuButton(centerX, 350, 200, 60, AssetManager.getInstance().getImage("Back Square Button"), "BACK_TO_MENU"));
    }

    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState currentState) { this.currentState = currentState; }
    public List<MenuButton> getMainMenuButtons() { return mainMenuButtons; }
    public List<MenuButton> getPauseButtons() { return pauseButtons; }
    public List<MenuButton> getQuitConfirmButtons() { return quitConfirmButtons; }
    public List<MenuButton> getStartButtons () { return  startButtons;}
    public MenuButton getControlsButtons () {return controlsButtons;}

    // --- PHẦN LOGIC GAME ---
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean isGameOver) { this.isGameOver = isGameOver; }

    public void setMap(Map map) {
        this.map = map;
        spawnEntities();
    }

    private void spawnEntities() {
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
                }
            }
        }
    }

    public Map getMap() { return map; }
    public List<Ghost> getGhosts() { return ghosts; }
    public PacMan getPacman() { return pacman; }
    public int getScore() { return score; }
    public void addScore(int points) { this.score += points; }
    public List<MenuButton> getSettingsButtons() { return settingsButtons; }
}