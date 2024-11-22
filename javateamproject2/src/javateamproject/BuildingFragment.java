package javateamproject;
import java.awt.*;

public class BuildingFragment {
    private int x, y, size;
    private int speedX, speedY;
    private Color color;

    public BuildingFragment(int x, int y, int size, int speedX, int speedY, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speedX = speedX;
        this.speedY = speedY;
        this.color = color;
    }

    public void update() {
        x += speedX;
        y += speedY;
        speedY += 1; // 중력 효과
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y > height;
    }
}
