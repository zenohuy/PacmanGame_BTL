package model;

import utils.AssetManager;

public class Map {
    private char[][] grid;
    private String currentLevel;

    public Map(String mapFileName) {
        char[][] cacheGrid = AssetManager.getInstance().getMap(mapFileName);
        if (cacheGrid != null){
            this.grid = new char[cacheGrid.length][];
            for (int i = 0; i < cacheGrid.length; i++) {
                this.grid[i] = cacheGrid[i].clone();
            }
        }
        this.currentLevel = mapFileName;
    }

    public char[][] getGrid() {
        return grid;
    }

    public int getRows() {
        return grid != null ? grid.length : 0;
    }

    public int getCols() {
        return grid != null && grid.length > 0 ? grid[0].length : 0;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }
}
