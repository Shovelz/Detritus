package io.trash_goblin.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import io.trash_goblin.Main;
import io.trash_goblin.ui.GameOverUI;
import io.trash_goblin.ui.MenuUI;

public class GameOverScreen implements Screen {

    private AssetManager assetManager;
    private SpriteBatch batch;
    private Stage stage;
    private Table root = new Table();
    private Viewport port;
    private Camera camera;
    private Main game;
    private GameOverUI gameOverUI;


    public GameOverScreen(SpriteBatch batch, AssetManager assetManager, Main game){
        this.assetManager = assetManager;
        this.batch = batch;
        this.game = game;

        camera = new OrthographicCamera();
        port = new FitViewport(1920, 1080, camera);
        stage = new Stage(port);

        root.setFillParent(true);

        root.top();
        root.center();
        stage.addActor(root);


        gameOverUI = new GameOverUI(game, assetManager);
        root.add(gameOverUI.getTable()).expandX().top(); // add to root, horizontally expand, align top


    }

    public void startGame(){
        game.startGame();
    }

    @Override
    public void show() {

    }

    public void update(float delta){
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameOverUI.render(batch, delta);
        batch.end();
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        root.setSize(width, height);
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

    public void focus() {
        Gdx.input.setInputProcessor(stage);
    }
}
