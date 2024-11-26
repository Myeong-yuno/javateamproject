package project;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundEffect {
    public static void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath); // 사운드 파일 경로
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("효과음 재생 오류: " + e.getMessage());
        }
    }
}