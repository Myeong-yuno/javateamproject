package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;

public class GameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Story(new Runnable() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(GameStart::new);
                }
            });
        });
    }
}

// Story 클래스
class Story {
    private static final String[] sentences = {
            "또 밤 샜다. 내가 천재가 아니란 걸 인정한 시간이었다.",
            "눈 떠보니 시험 종료 10분 전, 이미 끝난 게임이었다.",
            "내 머릿속엔 단 하나의 생각만 남았다.",
            "학교 건물이 없으면 시험도 없는 거 아닌가?",
            "말도 안 된다고? 시간이 없으면 사람은 이상한 데까지 생각이 닿는다.",
            "그래서 결심했다. 학교 건물, 한 번 부숴볼까?"
    };

    private static final String[] imagePaths = {
            "images/story1.png",
            "images/story2.png",
            "images/story3.png",
            "images/story4.png",
            "images/story5.png",
            "images/story6.png"
    };

    private int index = 0;
    private JLabel textLabel;
    private JLabel storyImageLabel;
    private JButton nextButton;

    private Runnable onStoryEnd;

    private Clip musicClip;

    public Story(Runnable onStoryEnd) {
        this.onStoryEnd = onStoryEnd;

        // 음악 재생
        musicClip = playMusic("src/music/storymusic.wav");

        JFrame frame = new JFrame("Story App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLayout(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("images/storyBG.png");
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 1280, 720);
        frame.add(backgroundPanel);

        storyImageLabel = new JLabel();
        storyImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        storyImageLabel.setBounds(50, 50, 1100, 400);
        backgroundPanel.add(storyImageLabel);

        textLabel = new JLabel("", SwingConstants.CENTER);
        textLabel.setBounds(50, 400, 1100, 100);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        backgroundPanel.add(textLabel);

        nextButton = new JButton("다음");
        nextButton.setBounds(550, 600, 100, 50);
        nextButton.addActionListener(e -> showNextContent(frame));
        backgroundPanel.add(nextButton);

        JSlider volumeSlider = createVolumeSlider(musicClip);
        volumeSlider.setBounds(50, 600, 300, 50);
        backgroundPanel.add(volumeSlider);

        showNextContent(frame);

        frame.setVisible(true);
    }

    private void showNextContent(JFrame frame) {
        if (index < sentences.length) {
            textLabel.setText(sentences[index]);
            ImageIcon storyImage = new ImageIcon(getClass().getClassLoader().getResource(imagePaths[index]));
            storyImageLabel.setIcon(storyImage);
            index++;
        } else {
            nextButton.setEnabled(false);
            musicClip.stop(); // 음악 정지
            frame.dispose(); // 스토리 창 닫기
            onStoryEnd.run(); // GameStart 실행
        }
    }

    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(getClass().getClassLoader().getResource(imagePath)).getImage();
            } catch (Exception e) {
                System.out.println("이미지를 로드할 수 없습니다: " + imagePath);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private Clip playMusic(String filePath) {
        try {
            File musicFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 반복 재생
            clip.start();
            return clip;
        } catch (Exception e) {
            System.out.println("음악 파일을 재생할 수 없습니다: " + filePath);
            return null;
        }
    }

    private JSlider createVolumeSlider(Clip clip) {
        JSlider slider = new JSlider(0, 100, 50);
        slider.addChangeListener(e -> {
            if (clip != null) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volume = (float) (Math.log10(slider.getValue() / 100.0) * 20);
                volumeControl.setValue(volume);
            }
        });
        return slider;
    }
}

// GameStart 클래스
class GameStart {
    private JFrame frame;
    private JPanel panel;
    private JLabel char1, char2;
    private JButton gameStartButton;
    private boolean char1Selected = false, char2Selected = false;

    private Clip musicClip;

    public GameStart() {
        // 음악 재생
        musicClip = playMusic("src/music/GameStartmusic.wav");
        setupIntroScreen();
    }

    private void setupIntroScreen() {
        frame = new JFrame("Game Start");
        panel = new JPanel(null);

        JLabel introLabel = createBackgroundLabel("src/images/intro.png");
        panel.add(introLabel);

        introLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    setupCharacterSelectionScreen();
                }
            }
        });

        frame.add(panel);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setupCharacterSelectionScreen() {
        panel.removeAll();
        panel.setLayout(null);

        JLabel bgLabel = createBackgroundLabel("src/images/selectBg.png");
        panel.add(bgLabel);

        char1 = createCharacterLabel("src/images/selectCh1.png", 250, 150, 1);
        panel.add(char1);

        char2 = createCharacterLabel("src/images/selectCh2.png", 750, 150, 2);
        panel.add(char2);

        gameStartButton = createStartButton();
        panel.add(gameStartButton);

        JSlider volumeSlider = createVolumeSlider(musicClip);
        volumeSlider.setBounds(50, 600, 300, 50);
        panel.add(volumeSlider);

        panel.add(bgLabel);

        frame.repaint();
        frame.revalidate();
    }

    private void selectCharacter(int character) {
        if (character == 1) {
            char1Selected = true;
            char2Selected = false;
            updateCharacterIcons("src/images/selectedCh1.png", "src/images/selectCh2.png");
        } else if (character == 2) {
            char1Selected = false;
            char2Selected = true;
            updateCharacterIcons("src/images/selectCh1.png", "src/images/selectedCh2.png");
        }
    }

    private void startGame() {
        if (!char1Selected && !char2Selected) {
            JOptionPane.showMessageDialog(frame, "캐릭터를 골라주세요", "메시지", JOptionPane.ERROR_MESSAGE);
        } else {
            musicClip.stop(); // 음악 정지
            JOptionPane.showMessageDialog(frame, "게임 시작!", "메시지", JOptionPane.INFORMATION_MESSAGE);
            // GamePanel로 전환
            JFrame gameFrame = new JFrame("건물 부수기 게임");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(1280,720); // 창 크기 설정
            gameFrame.setLocationRelativeTo(null);

            // GamePanel 생성 및 추가
            GamePanel gamePanel = new GamePanel(gameFrame);
            gameFrame.add(gamePanel);

            gameFrame.setVisible(true); // 화면 표시

        }
    }

    private JLabel createBackgroundLabel(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        JLabel label = new JLabel(icon);
        label.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        return label;
    }

    private JLabel createCharacterLabel(String imagePath, int x, int y, int characterId) {
        ImageIcon icon = new ImageIcon(imagePath);
        JLabel label = new JLabel(icon);
        label.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCharacter(characterId);
            }
        });
        return label;
    }

    private JButton createStartButton() {
        ImageIcon icon = new ImageIcon("src/images/GameStartBtn.png");
        JButton button = new JButton(icon);
        button.setBounds(450, 500, icon.getIconWidth(), icon.getIconHeight());
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(e -> startGame());
        return button;
    }

    private void updateCharacterIcons(String char1Image, String char2Image) {
        char1.setIcon(new ImageIcon(char1Image));
        char2.setIcon(new ImageIcon(char2Image));
    }

    private Clip playMusic(String filePath) {
        try {
            File musicFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 반복 재생
            clip.start();
            return clip;
        } catch (Exception e) {
            System.out.println("음악 파일을 재생할 수 없습니다: " + filePath);
            return null;
        }
    }

    private JSlider createVolumeSlider(Clip clip) {
        JSlider slider = new JSlider(0, 100, 50);
        slider.addChangeListener(e -> {
            if (clip != null) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volume = (float) (Math.log10(slider.getValue() / 100.0) * 20);
                volumeControl.setValue(volume);
            }
        });
        return slider;
    }
}
