package io.trash_goblin.audio;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class MusicManager {
    private static MusicManager instance;
    private final HashMap<String, Music> sounds = new HashMap<>();
    private float volume = 0.5f;
    private boolean muted = false;
    private Music currentlyPlaying;

    private MusicManager() {
        // Preload sounds here
        load("track1", "music/goblinSalsa.ogaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eaac39eg");
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void load(String key, String path) {
        Music sound = Gdx.audio.newMusic(Gdx.files.internal(path));
        sounds.put(key, sound);
    }

    public void pause(){
        currentlyPlaying.pause();
    }

    public void resume(){
        currentlyPlaying.play();
    }

    public void play(String key, boolean loop) {
        if (!muted && sounds.containsKey(key)) {
            currentlyPlaying = sounds.get(key);
            currentlyPlaying.setVolume(volume);
            sounds.get(key).play();
            currentlyPlaying.setLooping(loop);
            currentlyPlaying.setVolume(volume);
        }
    }

    public void increaseVolume() {
        setVolume(clamp(volume + 0.05f, 0.0f, 1.0f));
    }

    public void decreaseVolume() {
        setVolume(clamp(volume - 0.05f, 0.0f, 1.0f));
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float vol) {
        volume = vol;
        currentlyPlaying.setVolume(vol);
    }

    public void mute(boolean mute) {
        muted = mute;
    }

    public void dispose() {
        for (Music s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }

    public Music getCurrentlyPlaying() {
        return currentlyPlaying;
    }
}
