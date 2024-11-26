package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private List<Building> buildings = new ArrayList<>();
    private List<BuildingFragment> fragments = new ArrayList<>();
    private Character character;
    private boolean gameOver = false;
    private Random random = new Random();
    private int score = 0; // 점수 변수 추가

    // 타이머 관련 필드
    private int buildingSpawnTimer = 0;
    private final int SPAWN_INTERVAL = 100; // 새로운 건물을 생성하는 시간 간격

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    
    public GamePanel(JFrame parentFrame) {
        character = new Character(400, 600, 1280);

        // 키 입력 처리
        parentFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE: // 검 휘두르기
                        if (!character.isSwinging() && !gameOver) {
                            character.swingSword();
                            SoundEffect.playSound("src/sound/sword-swing.wav");
                            breakBuilding();
                        }
                        break;
                    case KeyEvent.VK_A: // 왼쪽 이동
                        character.moveLeft();
                        break;
                    case KeyEvent.VK_D: // 오른쪽 이동
                        character.moveRight();
                        break;
                    case KeyEvent.VK_W: // 점프
                        character.jump();
                        break;
                }
            }
        });

        // 게임 루프
        new Timer(16, e -> {
            spawnBuildings();
            buildings.forEach(Building::fall);
            fragments.forEach(BuildingFragment::update);
            character.update(); // 캐릭터 상태 업데이트
            repaint();
        }).start();
    }

    private void spawnBuildings() {
        buildingSpawnTimer++;

        // 일정 시간 간격으로 새로운 건물 생성
        if (buildingSpawnTimer >= SPAWN_INTERVAL) {
            int x = random.nextInt(1280 - 300); // 랜덤 x 좌표
            int height = 35 * (random.nextInt(10) + 1); // 랜덤 층수
            String imagePath = Building.IMAGE_PATHS[random.nextInt(Building.IMAGE_PATHS.length)];
            buildings.add(new Building(x, -height, 300, height, imagePath));

            buildingSpawnTimer = 0; // 타이머 초기화
        }
    }

    private void breakBuilding() {
        Iterator<Building> iterator = buildings.iterator();
        while (iterator.hasNext()) {
            Building building = iterator.next();

            // 캐릭터 검 범위와 충돌 감지
            if (character.getSwordBounds().intersects(building.getBounds())) {
                if (building.getBounds().height > 35) {
                    // 층 단위로 건물을 줄이며, 해당 층의 파편 생성
                    fragments.addAll(building.createFragments(35)); // 아래쪽부터 파편 생성
                    building.reduceHeight(35); // 층 하나 제거
                    SoundEffect.playSound("src/sound/building_attack.wav");
                    score += 10; // 건물을 부쉈을 때 점수 10점 추가

                } else {
                    // 마지막 층을 부수며, 전체 파편 생성
                    fragments.addAll(building.createFragments(building.getBounds().height));
                    iterator.remove(); // 건물 제거
                    score += 30; // 건물전체 부쉈을 때 점수 30점 추가

                }
                break; // 한 번에 하나의 건물만 부수기
            }
        }

       
        
    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경 그리기
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // 건물 그리기 및 층수 표시
        for (Building building : buildings) {
            building.draw(g); // 건물 이미지 또는 사각형 그리기

            // 층수 계산 및 표시
            int floors = building.getHeight() / 35; // 1층 높이 = 35
            g.setColor(Color.BLACK); // 텍스트 색상 설정
            g.setFont(new Font("고딕", Font.BOLD, 20)); // 텍스트 폰트 설정
            g.drawString(floors + "층", building.getX() + building.getWidth() / 2 - 15, building.getY() - 10);
        }

        // 파편 그리기
        for (BuildingFragment fragment : fragments) {
            fragment.draw(g);
        }

        // 캐릭터 그리기
        character.draw(g);

        
        // 게임 상태 표시
        g.setColor(Color.BLACK);
        g.setFont(new Font("고딕", Font.BOLD, 20));
        g.drawString("점수: " + score, 140, 60); // 점수 표시 추가
    }

    private void updateBuildings() {
        Iterator<Building> iterator = buildings.iterator();
        while (iterator.hasNext()) {
            Building building = iterator.next();
            building.fall(); // 계속 낙하

            // 건물이 화면 아래로 완전히 벗어났을 때 제거
            if (building.getY() > getHeight()) { // 건물의 상단(y) 좌표가 화면 높이를 초과하면
                iterator.remove(); // 리스트에서 제거
            }
        }
    }
    
    private void updateFragments() {
        Iterator<BuildingFragment> iterator = fragments.iterator();
        while (iterator.hasNext()) {
            BuildingFragment fragment = iterator.next();
            fragment.update(); // 파편 이동

            // 화면 밖으로 나간 파편만 제거
            if (fragment.isOutOfBounds(getWidth(), getHeight())) {
                iterator.remove();
            }
        }
    }

    	
    




}
