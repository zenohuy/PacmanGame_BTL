package utils;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.HashMap;

public class SoundManager {
    private static SoundManager instance;
    private HashMap<String, Clip> soundCache;
    private boolean isMuted = false;

    private SoundManager() {
        soundCache = new HashMap<>();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // Tải file âm thanh (.wav)
    public void loadSound(String soundName, String filePath) {
        if (!soundCache.containsKey(soundName)) {
            try {
                InputStream is = getClass().getResourceAsStream(filePath);
                if (is != null) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    soundCache.put(soundName, clip);
                } else {
                    System.err.println("Không tìm thấy file âm thanh: " + filePath);
                }
            } catch (Exception e) {
                System.err.println("Lỗi load âm thanh " + soundName + ": " + e.getMessage());
            }
        }
    }

    public void playSound(String soundName) {
        if (isMuted) return;
        Clip clip = soundCache.get(soundName);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    // Phát nhạc nền lặp đi lặp lại
    public void loopSound(String soundName) {
        if (isMuted) return;

        Clip clip = soundCache.get(soundName);
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopSound(String soundName) {
        Clip clip = soundCache.get(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public boolean isMuted() { return isMuted; }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            for (Clip clip : soundCache.values()) {
                if (clip.isRunning()) clip.stop();
            }
        }
    }
}