package controller;

import model.GameModel;
import model.Map;
import view.GamePanel;
import view.GameWindow;

public class GameController {
    private GameModel model;
    private GamePanel viewPanel;
    private GameWindow window;

    public GameController() {
        // 1. Khởi tạo Model và nạp map
        model = new GameModel();

        // Sửa lại đường dẫn ở dòng này: thêm "res/maps/" vào trước
        Map gameMap = new Map("level1");

        model.setMap(gameMap);

        viewPanel = new GamePanel(model);
        window = new GameWindow(viewPanel);
        // 3. Khởi động Game Loop (nếu có)
    }
}
