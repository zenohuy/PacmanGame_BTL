package view;

import model.GameModel;
import model.Map;
import utils.AssetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private GameModel gameModel;

    public GamePanel(GameModel gameModel) {
        this.gameModel = gameModel;
        Map map = gameModel.getMap();
        if (map != null && map.getGrid() != null) {
            // Tính toán chiều rộng và chiều cao dựa trên map
            int width = map.getCols() * Map.TILE_SIZE;
            int height = map.getRows() * Map.TILE_SIZE;

            // Cài đặt kích thước cho Panel
            this.setPreferredSize(new Dimension(width, height));
        } else {
            // Đề phòng trường hợp map bị lỗi không load được
            this.setPreferredSize(new Dimension(800, 600));
        }

        // Đặt màu nền đen cho ra chất game PacMan cổ điển
        this.setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Xóa màn hình cũ trước khi vẽ mới

        drawMap(g);
        // Sau này sẽ thêm: drawPacman(g), drawGhosts(g)...
    }

    private void drawMap(Graphics g) {
        Map map = gameModel.getMap(); // Giả sử GameModel chứa đối tượng Map
        char[][] grid = map.getGrid();

        if (grid == null) return;

        int tileSize = Map.TILE_SIZE;

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                char tileChar = grid[row][col];

                // Tính toán tọa độ x, y trên màn hình
                int x = col * tileSize;
                int y = row * tileSize;

                BufferedImage imageToDraw = null;

                // Quy ước ký tự trong file level1.txt của bạn
                switch (tileChar) {
                    case 'X': // Tường
                        imageToDraw = AssetManager.getInstance().getImage("wall");
                        break;
                    case ' ': // Thức ăn nhỏ
                        imageToDraw = AssetManager.getInstance().getImage("cherry");
                        break;
                    case 'r':
                        imageToDraw = AssetManager.getInstance().getImage("redGhost");
                        break;
                    case 'p': // Thức ăn to (Power Pellet)
                        imageToDraw = AssetManager.getInstance().getImage("pinkGhost");
                        break;
                    case 'o': // Thức ăn to (Power Pellet)
                        imageToDraw = AssetManager.getInstance().getImage("orangeGhost");
                        break;
                    case 'b': // Thức ăn to (Power Pellet)
                        imageToDraw = AssetManager.getInstance().getImage("blueGhost");
                        break;
                    case 'P': // Thức ăn to (Power Pellet)
                        imageToDraw = AssetManager.getInstance().getImage("pacmanRight");
                        break;
                    // Bỏ qua khoảng trắng hoặc các ký tự quy định vị trí spawn của nhân vật
                    default:
                        break;
                }

                // Vẽ hình ảnh nếu có
                if (imageToDraw != null) {
                    g.drawImage(imageToDraw, x, y, tileSize, tileSize, null);
                }
            }
        }
    }
}
