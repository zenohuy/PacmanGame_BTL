package model;

public class GameModel {
    private Map map;

    public GameModel() {
        // Khởi tạo các thành phần khác nếu cần
    }

    // Hàm này để GamePanel gọi, lấy dữ liệu map ra để vẽ
    public Map getMap() {
        return map;
    }

    // Hàm này để GameController gọi, truyền dữ liệu map vào model
    public void setMap(Map map) {
        this.map = map;
    }
}
