package project;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Building {
    private int x, y, width, height;
    private BufferedImage buildingImage;
    private int speed = 2;
    private int initialHeight; // 초기 높이

    
    public static final String[] IMAGE_PATHS = {
            "src/image/main_gate.png",
            "src/image/mugung_building.png",
            "src/image/techno_cube.png",
            "src/image/changhak_building.png"
        };

    public Building(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.initialHeight = height; // 초기 높이 저장

        try {
            buildingImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("이미지 로드 실패: " + imagePath + " - " + e.getMessage());
        }
    }


    public void fall() {
            y += speed;
        
    }

    public void draw(Graphics g) {
        if (buildingImage != null) {
            int totalImageHeight = buildingImage.getHeight(); // 이미지 전체 높이
            int visibleHeight = Math.min(height, initialHeight); // 현재 보이는 높이
            int croppedHeight = visibleHeight * totalImageHeight / initialHeight; // 이미지에서 그릴 높이

            if (visibleHeight > 0) {
                g.drawImage(
                    buildingImage,
                    x, y + (initialHeight - visibleHeight), x + width, y + initialHeight, // 화면에 그릴 위치
                    0, 0, buildingImage.getWidth(), croppedHeight, // 이미지에서 사용할 부분 (아래부터)
                    null
                );
            }
        } else {
            // 이미지가 없을 때 기본 사각형
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, width, height);
        }
    }
    
 // Building 클래스에 추가
    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }





    public void reduceHeight(int amount) {
        if (height > amount) {
            height -= amount; // 높이만 줄임
        } else {
            height = 0; // 최소 높이
        }
    }

    public void increaseSpeed(int increment) {
        speed += increment;
    }

    public List<BuildingFragment> createFragments(int layerHeight) {
        List<BuildingFragment> fragments = new ArrayList<>();
        Random random = new Random();

        int numFragments = 20; // 파편 개수
        for (int i = 0; i < numFragments; i++) {
            int size = random.nextInt(10) + 5; // 파편 크기
            int speedX = random.nextInt(6) - 3; // 수평 속도 (-3 ~ 3)
            int speedY = random.nextInt(6) - 10; // 수직 속도 (-10 ~ -4)
            fragments.add(new BuildingFragment(
                x + random.nextInt(width), // 파편의 x 좌표
                y + height - layerHeight + random.nextInt(layerHeight), // 파편의 y 좌표
                size, speedX, speedY // 크기 및 속도
            ));
        }
        return fragments;
    }



    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getY() {
        return y;
    }
}
