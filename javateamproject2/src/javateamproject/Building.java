package javateamproject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Building {
    private int x, y, width, height;
    private int speed = 2; // 낙하 속도

    public Building(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void fall() {
        y += speed; // 낙하
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);
    }
    public void increaseSpeed(int increment) {
        speed += increment; // 속도 증가
    }

    public List<BuildingFragment> createFragments() {
        List<BuildingFragment> fragments = new ArrayList<>();
        Random random = new Random();

        int numFragments = 20; // 조각 개수
        for (int i = 0; i < numFragments; i++) {
            int size = random.nextInt(10) + 5; // 조각 크기
            int speedX = random.nextInt(6) - 3; // 수평 속도 (-3 ~ 3)
            int speedY = random.nextInt(6) - 10; // 수직 속도 (-10 ~ -4)

            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            fragments.add(new BuildingFragment(x + random.nextInt(width),
                                               y + random.nextInt(height),
                                               size, speedX, speedY, color));
        }
        return fragments;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height); // 건물의 충돌 영역 반환
    }

    public int getY() {
        return y;
    }
}
