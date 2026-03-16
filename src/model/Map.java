package model;

import utils.AssetManager;

public class Map {
    private char[][] grid;
    public static final int TILE_SIZE = 32;

    public Map(String mapFileName) {
        // Gán dữ liệu vào biến "grid"
        this.grid = AssetManager.getInstance().getMap(mapFileName);
    }

    public char[][] getGrid() {
        return grid; // Trả về "grid"
    }

    public int getRows() {
        // Dòng 15 của bạn có thể đang ở đây, hãy dùng "grid" thay vì "map"
        return grid != null ? grid.length : 0;
    }

    public int getCols() {
        // Dòng 20 của bạn có thể đang ở đây, hãy dùng "grid" thay vì "map"
        return grid != null && grid.length > 0 ? grid[0].length : 0;
    }
}
