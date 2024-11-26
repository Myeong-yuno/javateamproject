package project;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // JFrame 생성
        JFrame frame = new JFrame("건물 부수기 게임");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720); // 창 크기 설정

        // GamePanel 추가
        GamePanel gamePanel = new GamePanel(frame);
        frame.add(gamePanel);

        // 창을 화면 가운데 배치
        frame.setLocationRelativeTo(null);

        // 화면에 표시
        frame.setVisible(true);
    }
}
