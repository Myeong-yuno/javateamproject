package javateamproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private List<Building> buildings; // 화면에 떨어질 건물 리스트
    private List<Building> predefinedBuildings; // 미리 정의된 건물 객체
    private List<BuildingFragment> fragments; // 건물 조각 리스트
    private Character character;
    private boolean gameOver = false;
    private int speedIncrementTimer = 0; // 속도 증가를 위한 타이머

    // 화면 크기 상수
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;

    // 건물 크기 상수
    private static final int BUILDING_WIDTH = 300;
    private static final int BUILDING_HEIGHT = 35;

    public GamePanel(JFrame parentFrame) {
    	 character = new Character(SCREEN_WIDTH / 2 - 20, SCREEN_HEIGHT - 160, SCREEN_WIDTH);
        buildings = new ArrayList<>();
        predefinedBuildings = new ArrayList<>();
        fragments = new ArrayList<>();

        // 미리 정의된 건물 추가
        createPredefinedBuildings();

        // 초기 건물 생성
        initializeBuildings();

        // 키 이벤트 처리
        parentFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) { // 검 휘두르기
                    if (!character.isSwinging() && !gameOver) {
                    	character.swingSword();
                    	breakBuilding(); 
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_W) { // 점프
                    character.jump();
                } else if (e.getKeyCode() == KeyEvent.VK_A) { // 왼쪽 이동
                    character.moveLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_D) { // 오른쪽 이동
                    character.moveRight();
                }
            }
        });
        // 애니메이션 타이머
        new Timer(16, e -> {
            updateBuildings();
            updateFragments();
            character.update();
            repaint();
        }).start();
    }

    private void createPredefinedBuildings() {
        // 11층, 12층, 13층, 4층 건물 생성
        predefinedBuildings.add(new Building(SCREEN_WIDTH / 2 - BUILDING_WIDTH / 2, -400, BUILDING_WIDTH, 11 * BUILDING_HEIGHT));
        predefinedBuildings.add(new Building(SCREEN_WIDTH / 2 - BUILDING_WIDTH / 2, -400, BUILDING_WIDTH, 12 * BUILDING_HEIGHT));
        predefinedBuildings.add(new Building(SCREEN_WIDTH / 2 - BUILDING_WIDTH / 2, -400, BUILDING_WIDTH, 13 * BUILDING_HEIGHT));
        predefinedBuildings.add(new Building(SCREEN_WIDTH / 2 - BUILDING_WIDTH / 2, -400, BUILDING_WIDTH, 4 * BUILDING_HEIGHT));
    }

    private void initializeBuildings() {
        Random random = new Random();
        buildings.clear();

        // 랜덤으로 10개의 건물 생성
        for (int i = 0; i < 10; i++) {
            Building template = predefinedBuildings.get(random.nextInt(predefinedBuildings.size()));

            // 초기 y 좌표를 화면 위쪽으로 설정
            buildings.add(new Building(
                SCREEN_WIDTH / 2 - BUILDING_WIDTH / 2, // x 좌표 중앙 정렬
                -100 * (i + 1) - template.getBounds().height, // y 좌표: 건물이 화면 위에서 시작
                template.getBounds().width,
                template.getBounds().height
            ));
        }
    }

    private void updateBuildings() {
        if (gameOver) return;

        // 건물 제거 및 추가를 위한 리스트
        List<Building> toRemove = new ArrayList<>();
        Random random = new Random();

        for (Building building : buildings) {
            building.fall(); // 건물 낙하

            // 화면 아래로 떨어진 건물 제거
            if (building.getY() > SCREEN_HEIGHT) {
                toRemove.add(building);
            }
        }

        // 화면 아래로 벗어난 건물 제거
        buildings.removeAll(toRemove);

        // 새 건물 추가
        while (buildings.size() < 10) { // 유지할 건물 개수
            int randomX = random.nextInt(SCREEN_WIDTH - BUILDING_WIDTH); // 랜덤 위치
            int randomHeight = (random.nextInt(4) + 9) * BUILDING_HEIGHT; // 1~4층 높이
            Building newBuilding = new Building(randomX, -randomHeight, BUILDING_WIDTH, randomHeight);
            buildings.add(newBuilding);
        }

        // 일정 시간 간격으로 낙하 속도 증가
        speedIncrementTimer++;
        if (speedIncrementTimer >= 300) { // 약 5초마다 실행
            for (Building building : buildings) {
                building.increaseSpeed(1); // 낙하 속도 증가
            }
            speedIncrementTimer = 0;
        }
    }


    private void breakBuilding() {
        if (buildings.isEmpty()) return;

        // 캐릭터의 검 범위 가져오기
        Rectangle swordBounds = character.getSwordBounds();

        // 건물 리스트 순회하여 충돌 확인
        Iterator<Building> iterator = buildings.iterator();
        while (iterator.hasNext()) {
            Building building = iterator.next();

            // 건물과 검의 충돌 확인
            if (swordBounds.intersects(building.getBounds())) {
                fragments.addAll(building.createFragments());
                iterator.remove();
                break;
            }
        }

        if (buildings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "건물을 모두 부쉈습니다! 승리!");
        }
    }

    private void updateFragments() {
        Iterator<BuildingFragment> iterator = fragments.iterator();
        while (iterator.hasNext()) {
            BuildingFragment fragment = iterator.next();
            fragment.update();

            // 화면 밖으로 나가면 제거
            if (fragment.isOutOfBounds(getWidth(), getHeight())) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 건물 그리기
        for (Building building : buildings) {
            building.draw(g);
        }

        // 조각 그리기
        for (BuildingFragment fragment : fragments) {
            fragment.draw(g);
        }

        // 캐릭터 그리기
        character.draw(g);

        // 게임 상태 표시
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("남은 층: " + buildings.size(), 140, 30);
    }
}
