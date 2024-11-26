package project;

import java.awt.*;
import java.util.Random;

public class BuildingFragment {
    private int x, y, size;
    private int speedX;
    private double velocityY;
    private final double gravity = 0.5; // 중력 효과
    private Color color;

    public BuildingFragment(int x, int y, int size, int speedX, int speedY) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speedX = speedX;
        this.velocityY = speedY;

        // 랜덤한 색상 생성
        Random random = new Random();
        this.color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public void update() {
        x += speedX;
        y += velocityY;
        if (x < 0 || x + size > GamePanel.SCREEN_WIDTH) speedX = -speedX; // 수평 경계 반사
        if (y + size > GamePanel.SCREEN_HEIGHT) velocityY = -velocityY * 0.5; // 수직 경계 반사 및 감속
        velocityY += gravity; // 중력 효과
    }

    public void draw(Graphics g) {
        g.setColor(color); // 파편 색상 설정
        g.fillRect(x, y, size, size); // 파편 그리기
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x + size > width || y + size > height; // 화면 밖 경계 체크
    }
}
