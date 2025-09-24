package io.trash_goblin.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import io.trash_goblin.Main;
import io.trash_goblin.enemies.Apple;
import io.trash_goblin.enemies.DeathAnimation;
import io.trash_goblin.enemies.Enemy;
import io.trash_goblin.enemies.Trashbag;
import io.trash_goblin.items.BroomItem;
import io.trash_goblin.items.Portal;
import io.trash_goblin.player.Player;
import io.trash_goblin.ui.Lives;
import io.trash_goblin.ui.PauseMenuUI;

import java.util.ArrayList;


public class GameScreen implements Screen {

    private AssetManager assetManager;
    private SpriteBatch batch;
    private Viewport port;
    private Camera camera;
    private Main game;
    private Player player;
    private ArrayList<Rectangle> collisionRects = new ArrayList<>();
    private ArrayList<Rectangle> lavaCollisionRects = new ArrayList<>();
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    int TILE_SIZE = 16; // pixels per tile
    int VIEWPORT_WIDTH = 256;  // in pixels
    int VIEWPORT_HEIGHT = 144; // in pixels
    private float mapWidthInPixels;
    private float mapHeightInPixels;
    private BroomItem broomItem;
    private ArrayList<Portal> portals = new ArrayList<>();

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
    private Lives lives;
    private OrthographicCamera uiCamera;
    private Texture bgLayerFar;    // farthest, moves slowest
    private Texture bgLayerMid;    // middle
    private Texture bgLayerNear;   // nearest, moves fastest
    private Texture vignette;

    private Animation<TextureRegion> deathAnimation;
    private ArrayList<DeathAnimation> enemyDeaths;

    private int levelNumber = 0;

    private Stage stage;
    private Table root = new Table();
    private PauseMenuUI pauseMenuUI;
    private VisImageButton pauseButton;
    private Portal endPortal;

    public GameScreen(SpriteBatch batch, AssetManager assetManager, Main game){
        this.assetManager = assetManager;
        this.batch = batch;
        this.game = game;

        camera = new OrthographicCamera();
        port = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        camera.position.set(VIEWPORT_WIDTH / 2f, VIEWPORT_HEIGHT / 2f, 0);
        camera.update();
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        map = new TmxMapLoader().load("maps/levels.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, batch); // reuse your SpriteBatch

        // Grab collisions
        for (MapObject object : map.getLayers().get("Collisions").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRects.add(rect);
            }
        }

        for (MapObject object : map.getLayers().get("Lava").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                lavaCollisionRects.add(rect);
            }
        }


        for (TiledMapTileSet set : map.getTileSets()) {
            for (TiledMapTile tile : set) {
                if (tile.getTextureRegion() != null) {
                    Texture texture = tile.getTextureRegion().getTexture();
                    texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                }
            }
        }

        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int mapHeightInTiles = map.getProperties().get("height", Integer.class);
        mapWidthInPixels = mapWidthInTiles * TILE_SIZE;
        mapHeightInPixels = mapHeightInTiles * TILE_SIZE;

        restartGame();

        assetManager.load("ui/background_far.png", Texture.class);
        assetManager.load("ui/background_mid.png", Texture.class);
        assetManager.load("ui/background_near.png", Texture.class);
        assetManager.finishLoading();

        bgLayerFar = assetManager.get("ui/background_far.png", Texture.class);
        bgLayerMid = assetManager.get("ui/background_mid.png", Texture.class);
        bgLayerNear = assetManager.get("ui/background_near.png", Texture.class);

        assetManager.load("enemy/deathSheet.png", Texture.class);
        assetManager.load("ui/vignette.png", Texture.class);
        assetManager.finishLoading();

        deathAnimation = Player.loadAnimation(16, 16, assetManager.get("enemy/deathSheet.png", Texture.class));
        vignette = assetManager.get("ui/vignette.png", Texture.class);

        enemyDeaths = new ArrayList<>();

        stage = new Stage(new FitViewport(1920, 1080));

        if (!VisUI.isLoaded()) {
            VisUI.load();
        }


        root.setFillParent(true);

        root.top();
        root.center();
        stage.addActor(root);

        pauseMenuUI = new PauseMenuUI(this);

        assetManager.load("ui/pause.png", Texture.class);
        assetManager.finishLoading();
        Texture pauseTex = assetManager.get("ui/pause.png", Texture.class);
        Drawable pauseDrawable = new TextureRegionDrawable(new TextureRegion(pauseTex));

        pauseButton = new VisImageButton(pauseDrawable);

        pauseButton.setPosition(
            stage.getViewport().getWorldWidth() - pauseButton.getWidth() - 10, // 10px margin
            stage.getViewport().getWorldHeight() - pauseButton.getHeight() - 10
        );
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause();
            }
        });


        pauseButton.setSize(48,48);
        pauseButton.pad(7);
        pauseMenuUI.getTable().setSize(300, 200);
        stage.addActor(pauseButton);


        for (int i = 0; i < 4; i++) {
            Rectangle portalRect = ((RectangleMapObject) map.getLayers().get("Portal" + i).getObjects().get(0)).getRectangle();
            Portal portal = new Portal(assetManager, new Vector2(portalRect.x, portalRect.y), this);
            portals.add(portal);
        }
        portals.get(0).addPortal(portals.get(1));
        portals.get(2).addPortal(portals.get(3));


        Rectangle portalRect = ((RectangleMapObject) map.getLayers().get("EndPortal").getObjects().get(0)).getRectangle();
        endPortal = new Portal(assetManager, new Vector2(portalRect.x, portalRect.y), this);

    }

    public Portal getEndPortal(){
        return endPortal;
    }

    public Player getPlayer(){
        return player;
    }

    @Override
    public void pause() {
        root.add(pauseMenuUI.getTable()).expandX().top(); // add to root, horizontally expand, align top
    }

    public void unpause() {
        root.clear();
    }

    public void restartGame(){
        levelNumber = 0;

        enemies.clear();
        for (MapObject object : map.getLayers().get("Trashbags").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle bagRect = ((RectangleMapObject) object).getRectangle();
                Trashbag bag = new Trashbag(assetManager, collisionRects, bagRect.x, bagRect.y, this);
                enemies.add(bag);
            }
        }

        for (MapObject object : map.getLayers().get("Apples").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle appleRect = ((RectangleMapObject) object).getRectangle();
                Apple apple = new Apple(assetManager, collisionRects, appleRect.x, appleRect.y, this);
                enemies.add(apple);
            }
        }

        Rectangle rect = ((RectangleMapObject) map.getLayers().get("BroomItem").getObjects().get(0)).getRectangle();
        broomItem = new BroomItem(assetManager, new Vector2( rect.x, rect.y));


        player = new Player(assetManager, collisionRects, rect, this, lavaCollisionRects);


        lives = new Lives(assetManager, port, game);
        Gdx.input.setInputProcessor(stage);
    }

    public void pickupItem(){
        broomItem.pickUp();
    }

    public void startGame(){
        game.startGame();
    }

    @Override
    public void show() {

    }


    public ArrayList<Portal> getPortals(){
        return portals;
    }

    public void handleInput(float delta){
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            pause();
        }
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        handleInput(delta);

        port.apply();
        camera.update();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

// Parallax factors: smaller = moves slower
        float factorFar = 0.1f;
        float factorMid = 0.3f;
        float factorNear = 0.4f;

        float bgFarWidth = bgLayerFar.getWidth();
        float bgMidWidth = bgLayerMid.getWidth();
        float bgNearWidth = bgLayerNear.getWidth();

// Calculate offsets for seamless looping
        float offsetFar  = (camera.position.x * factorFar) % bgFarWidth;
        float offsetMid  = (camera.position.x * factorMid) % bgMidWidth;
        float offsetNear = (camera.position.x * factorNear) % bgNearWidth;

// Draw far layer (two copies to tile)
        batch.draw(bgLayerFar, -offsetFar, 0, bgFarWidth, VIEWPORT_HEIGHT);
        batch.draw(bgLayerFar, -offsetFar + bgFarWidth, 0, bgFarWidth, VIEWPORT_HEIGHT);

// Draw mid layer
        batch.draw(bgLayerMid, -offsetMid, 0, bgMidWidth, VIEWPORT_HEIGHT);
        batch.draw(bgLayerMid, -offsetMid + bgMidWidth, 0, bgMidWidth, VIEWPORT_HEIGHT);

// Draw near layer
        batch.draw(bgLayerNear, -offsetNear, 0, bgNearWidth, VIEWPORT_HEIGHT);
        batch.draw(bgLayerNear, -offsetNear + bgNearWidth, 0, bgNearWidth, VIEWPORT_HEIGHT);

        batch.end();
        // Render map
        mapRenderer.setView((OrthographicCamera) camera);
        mapRenderer.render();

        // Render player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        broomItem.render(batch, delta);

        for(Portal portal : portals){
            portal.render(batch, delta);
        }

        endPortal.render(batch, delta);

        for(Enemy bag : enemies){
            bag.render(batch, delta);
        }

        for(DeathAnimation death : new ArrayList<>(enemyDeaths)){
            death.addToTimer(delta);
            batch.draw(deathAnimation.getKeyFrame(death.getTimer(), false), death.getPos().x, death.getPos().y);
            if (deathAnimation.isAnimationFinished(death.getTimer())) {
                enemyDeaths.remove(death); // or queue it for removal
            }
        }
        player.render(batch, delta);
        batch.end();


// UI
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        batch.draw(vignette, 0, 0);
        lives.render(batch, delta);
        batch.end();
        cleanupEnemies();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    private void cleanupEnemies() {
        enemies.removeAll(enemiesToRemove);
        enemiesToRemove.clear();
    }

    public void removeEnemy(Enemy enemy){
        enemiesToRemove.add(enemy);
        enemyDeaths.add(new DeathAnimation(new Vector2(enemy.getHitbox().x, enemy.getHitbox().y)));
    }

    public void update(float delta) {
        float lerp = 5f * delta;
        camera.position.x += (player.getX() + player.getWidth()/2f - camera.position.x) * lerp;
        camera.position.y += (player.getY() + player.getHeight()/2f - camera.position.y) * lerp;

        // Clamp camera to map bounds
        float halfViewportWidth = port.getWorldWidth() / 2f;
        float halfViewportHeight = port.getWorldHeight() / 2f;

        camera.position.x = Math.max(halfViewportWidth, camera.position.x);
        camera.position.x = Math.min(mapWidthInPixels - halfViewportWidth, camera.position.x);

        camera.position.y = Math.max(halfViewportHeight, camera.position.y);
        camera.position.y = Math.min(mapHeightInPixels - halfViewportHeight, camera.position.y);

        camera.update();
    }

    public void loseLife(){
        lives.loseLife();
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }

    @Override
    public void resize(int width, int height) {
        port.update(width, height, true);
        lives.resize(port);
        stage.getViewport().update(width, height, true);
        root.setSize(width, height);

        pauseButton.setPosition(
            stage.getViewport().getWorldWidth() - pauseButton.getWidth() - 10,
            stage.getViewport().getWorldHeight() - pauseButton.getHeight() - 10
        );
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

    public void portalEntered() {
        game.gameWon();
    }
}
