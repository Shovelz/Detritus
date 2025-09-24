package io.trash_goblin.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.trash_goblin.Main;

public class CutSceneScreen implements Screen {

    private AssetManager assetManager;
    private SpriteBatch batch;
    private Viewport port;
    private Camera camera;
    private Main game;
    private float timer;
    private float changeScene = 8f, endScene = 16f;
    //DEBUG
//    private float changeScene = 1f, endScene = 2f;
    private Texture frame1, frame2;
    private Texture blackPixel;
    private float fadeAlpha = 1f;   // start black
    private boolean fadingIn = true;
    private float fadeDuration = 2f; // seconds



    public CutSceneScreen(SpriteBatch batch, AssetManager assetManager, Main game){
        this.assetManager = assetManager;
        this.batch = batch;
        this.game = game;

        camera = new OrthographicCamera();
        port = new FitViewport(256, 144, camera);

        assetManager.load("ui/cutScene1.png", Texture.class);
        assetManager.load("ui/cutScene2.png", Texture.class);

        assetManager.finishLoading();

        frame1 = assetManager.get("ui/cutScene1.png", Texture.class);
        frame2 = assetManager.get("ui/cutScene2.png", Texture.class);

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if(timer < changeScene) {
            batch.draw(frame1, 0, 0);
        } else {
            batch.draw(frame2, 0, 0);
        }

        batch.end();
        if(timer > endScene){
            game.startGame();
        }

    }

    public void update(float delta){
        timer += delta;
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        port.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
