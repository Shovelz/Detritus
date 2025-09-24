package io.trash_goblin;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.trash_goblin.audio.MusicManager;
import io.trash_goblin.audio.SoundManager;
import io.trash_goblin.screens.*;

public class Main extends Game {
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private SpriteBatch batch;
    private AssetManager assetManager;
    private GameOverScreen gameOverScreen;
    private CutSceneScreen cutSceneScreen;
    private GameWonScreen gameWonScreen;

    // Fade overlay
    private Texture fadeTex;
    private float fadeAlpha = 1f; // 1 = fully black, 0 = fully visible
    private float fadeSpeed = 1f; // seconds per fade
    private boolean fading = false;
    private boolean fadeIn = false;
    private Screen nextScreen;


    @Override
    public void create() {
        assetManager = new AssetManager();
        batch = new SpriteBatch();
        menuScreen = new MenuScreen(batch, assetManager, this);
        gameScreen = new GameScreen(batch, assetManager, this);
        gameOverScreen = new GameOverScreen(batch, assetManager, this);
        cutSceneScreen = new CutSceneScreen(batch, assetManager, this);
        gameWonScreen = new GameWonScreen(batch, assetManager, this);
        setScreen(menuScreen);
        fadeTex = new Texture(Gdx.files.internal("ui/black.png"));
        MusicManager.getInstance().play("track1", true);
        MusicManager.getInstance().setVolume(0.3f);
        SoundManager.getInstance().setVolume(1.4f);

        menuScreen.focus();
    }

    @Override
    public void render(){
        super.render();
        if (fading) {
            updateFade(Gdx.graphics.getDeltaTime());
            batch.begin();
            batch.setColor(0, 0, 0, fadeAlpha);
            batch.draw(fadeTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1f); // full alpha
            batch.end();
        }
    }

    public void gameWon(){
        setScreen(gameWonScreen);
        gameWonScreen.focus();
    }


    private void updateFade(float delta) {
        if (fadeIn) {
            fadeAlpha -= delta / fadeSpeed;
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                fading = false;
            }
        } else {
            fadeAlpha += delta / fadeSpeed;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                if (nextScreen != null) {
                    setScreen(nextScreen);
                    nextScreen = null;
                    startFadeIn();
                } else {
                    fading = false;
                }
            }
        }
    }

    // Call this to fade OUT current screen, then switch to another
    public void setScreenWithFade(Screen screen, float duration) {
        fadeSpeed = duration;
        nextScreen = screen;
        fading = true;
        fadeIn = false;
    }

    // Just fades in (useful for game.startGame())
    public void startFadeIn() {
        fading = true;
        fadeIn = true;
    }


    public void cutScene(){
        setScreen(cutSceneScreen);
    }

    public void startGame(){
        Gdx.input.setInputProcessor(null);
        setScreenWithFade(gameScreen, 2f); // 1 second fade

//        setScreen(gameScreen);
        gameScreen.restartGame();
    }

    public void gameOver(){
        gameOverScreen.focus();
        setScreen(gameOverScreen);
    }

}
