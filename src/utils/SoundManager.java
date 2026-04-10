package utils;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.HashMap;

public class SoundManager {
    private static SoundManager instance;
    private HashMap<String, Clip> soundCache;
    private boolean isMuted = false;
    private java.util.Set<String> loopingClips = new java.util.HashSet<>();

    private SoundManager() {
        soundCache = new HashMap<>();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void loadSound(String soundName) {
        if (!soundCache.containsKey(soundName)) {
            try {
                InputStream is = getClass().getResourceAsStream("/sounds/" + soundName);
                if (is != null) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    soundCache.put(soundName, clip);
                } else {
                    System.err.println("Không tìm thấy file âm thanh: " + soundName);
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
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loopSound(String soundName) {
        loopingClips.add(soundName);
        if (isMuted) return;
        Clip clip = soundCache.get(soundName);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopSound(String soundName) {
        Clip clip = soundCache.get(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void stopAndRemoveLoop(String soundName) {
        loopingClips.remove(soundName);
        Clip clip = soundCache.get(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void stopAllLooping() {
        for (String name : loopingClips) {
            Clip clip = soundCache.get(name);
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
        loopingClips.clear();
    }

    public boolean isMuted() { return isMuted; }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            for (Clip clip : soundCache.values()) {
                if (clip.isRunning()) clip.stop();
            }
        } else {
            for (String name : loopingClips) {
                Clip clip = soundCache.get(name);
                if (clip != null && !clip.isRunning()) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        }
    }
}