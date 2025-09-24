package io.trash_goblin.audio;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

public class SoundManager {
    private static SoundManager instance;
    private final HashMap<String, Sound> sounds = new HashMap<>();
    private float volume = 0.5f;
    private boolean muted = false;

    // Libgdx doesn't provide a callback when a sound is finished, and I don't want to estimate the sound duration, as then bugs can be introduced with that
    // So I can't pause sounds when pausing the game and resume them. When pausing the game all sounds that are playing continue playing. I can prevent new
    // sounds from being made though

    private SoundManager() {
        // Preload sounds here
//        load("zombie_groan", "sounds/zombie_groan.wav");
//        load("shot", "sounds/shot.wav");
        load("sweep", "sounds/broomSweep.wav");
        load("playerDamaged", "sounds/ow.wav");
        load("trashbagHurt", "sounds/trashbagHurt.wav");
        load("spit", "sounds/spit.wav");
        load("appleHurt", "sounds/splat.wav");

    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void load(String key, String path) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(key, sound);
    }

    public void play(String key) {
        if (!muted && sounds.containsKey(key)) {
            sounds.get(key).play(volume);
        }
    }

    public void setVolume(float vol) {
        volume = vol;
    }

    public float getVolume() {
        return volume;
    }

    public void mute(boolean mute) {
        muted = mute;
    }

    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}
