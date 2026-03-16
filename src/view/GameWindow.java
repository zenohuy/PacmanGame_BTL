package view;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow(GamePanel gamePanel) {
        this.add(gamePanel); // Thêm GamePanel vào cửa sổ
        this.setTitle("PacMan Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack(); // Tự động ép kích thước cửa sổ vừa với GamePanel
        this.setLocationRelativeTo(null); // Hiển thị cửa sổ ở giữa màn hình
        this.setResizable(false);
        this.setVisible(true); // Lệnh này bắt buộc phải có để hiện cửa sổ
    }
}
