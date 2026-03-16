package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AssetManager {
    private static AssetManager instance;

    private HashMap<String, BufferedImage> imageCache;
    private HashMap<String, char[][]> mapCache ;

    private AssetManager() {
        imageCache = new HashMap<>();
        mapCache = new HashMap<>();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    public BufferedImage getImage(String imageName) {
        if (!imageCache.containsKey(imageName)) {
            try {
                InputStream is = getClass().getResourceAsStream("/assets/" + imageName + ".png");
                if (is != null) {
                    imageCache.put(imageName, ImageIO.read(is));
                } else {
                    System.err.println("Không tìm thấy ảnh: " + imageName);
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi đọc ảnh " + imageName + ": " + e.getMessage());
            }
        }
        return imageCache.get(imageName);
    }

    public char[][] getMap(String mapName) {
        if (!mapCache.containsKey(mapName)) {
            mapCache.put(mapName, loadMapFromFile(mapName));
        }
        return mapCache.get(mapName);
    }

    private char[][] loadMapFromFile(String mapName) {
        try {
            InputStream is = getClass().getResourceAsStream("/maps/" + mapName + ".txt");
            if (is == null) {
                System.err.println("Không tìm thấy file map: " + mapName);
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            ArrayList<String> lines = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
            br.close();

            int rows = lines.size();
            int cols = lines.get(0).length();
            char[][] mapData = new char[rows][cols];

            for (int i = 0; i < rows; i++) {
                String currentLine = lines.get(i);
                for (int j = 0; j < cols; j++) {
                    if (j < currentLine.length()) {
                        mapData[i][j] = currentLine.charAt(j);
                    } else {
                        mapData[i][j] = ' ';
                    }
                }
            }
            return mapData;

        } catch (Exception e) {
            System.err.println("Lỗi khi đọc map " + mapName + ": " + e.getMessage());
            return null;
        }
    }
}
