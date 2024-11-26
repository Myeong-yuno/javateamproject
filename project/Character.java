package project;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


public class Character {
    private int x, y; // 캐릭터의 위치
    private double swordAngle; // 검의 현재 각도
    private boolean swinging; // 검 휘두르기 여부
    private BufferedImage characterImage;
    private BufferedImage swordImage;
    private int jumpSpeed; // 점프 속도
    private boolean jumping; // 점프 상태
    private int groundY; // 지면의 y 좌표
    private int gravity = 2; // 중력 가속도
    private int moveSpeed = 25; // 횡 이동 속도
    private int screenWidth; // 화면 너비 (경계 제한용)

    public Character(int x, int y, int screenWidth) {
        this.x = x;
        this.y = y;
        this.groundY = y; // 초기 지면 설정
        this.screenWidth = screenWidth; // 화면 너비 설정
        this.swordAngle = 0;
        this.swinging = false;
        this.jumping = false;
        this.jumpSpeed = 0;

        try {
            characterImage = ImageIO.read(getClass().getResource("/image/커비.png"));
            swordImage = ImageIO.read(getClass().getResource("/image/도끼.png"));
        } catch (IOException e) {
            System.err.println("이미지 로드 실패: " + e.getMessage());
        }
    }

    public void draw(Graphics g) {
        if (characterImage != null) {
            g.drawImage(characterImage, x, y, 60, 80, null);
        }
        drawSword(g);
    }

    private void drawSword(Graphics g) {
        if (swordImage == null) return;

        Graphics2D g2d = (Graphics2D) g;
        int centerX = x + 30; // 캐릭터 중심 x
        int centerY = y + 40; // 캐릭터 중심 y

        g2d.rotate(Math.toRadians(-swordAngle), centerX, centerY);
        g2d.drawImage(swordImage, centerX - 10, centerY - 70, 20, 100, null);
        g2d.rotate(Math.toRadians(swordAngle), centerX, centerY); // 회전 복원
    }

    public void moveLeft() {
        x -= moveSpeed; // 왼쪽으로 이동
        if (x < 0) x = 0; // 화면 왼쪽 경계 제한
    }

    public void moveRight() {
        x += moveSpeed; // 오른쪽으로 이동
        if (x > screenWidth - 60) x = screenWidth - 60; // 화면 오른쪽 경계 제한
    }

    public void jump() {
        if (!jumping) {
            jumping = true;
            jumpSpeed = -50; // 점프 초기 속도
        }
    }
    
    public void swingSword() {
        if (swinging) return;

        swinging = true;
        new javax.swing.Timer(5, e -> {
            swordAngle += 5; // 검 회전 속도
            if (swordAngle >= 90) {
                swordAngle = 0; // 각도 초기화
                swinging = false;
                ((javax.swing.Timer) e.getSource()).stop(); // 타이머 정지
            }
        }).start();
    }

    
    public void update() {
        if (jumping) {
            y += jumpSpeed; // 점프 속도에 따라 y 좌표 변경
            jumpSpeed += gravity; // 중력 효과

            if (y >= groundY) { // 지면에 도달
                y = groundY;
                jumping = false;
                jumpSpeed = 0; // 속도 초기화
            }
        }
    }

    public Rectangle getSwordBounds() {
        int centerX = x + 20; // 캐릭터 중심 x
        int centerY = y + 30; // 캐릭터 중심 y
        int swordLength = 80; // 검의 길이
        int swordWidth = 10;

        return new Rectangle(centerX - 5, centerY - swordLength, swordWidth, swordLength);
    }

    public boolean isSwinging() {
        return swinging;
    }
}