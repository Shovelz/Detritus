package io.trash_goblin.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.trash_goblin.screens.GameScreen;

public class Portal extends Item {

    private AssetManager assetManager;
    private Texture texture;
    private float elapsedTime = 0f;
    private float bobAmplitude = 2f; // how far it moves up/down in pixels
    private float bobSpeed = 2f;     // how fast it bobs (oscillations per second)
    private float baseY;             // original Y position
    private boolean isPickedUp = false;
    private Portal nextPortal;
    private GameScreen gameScreen;
    private Rectangle hitbox;

    public Portal(AssetManager assetManager, Vector2 pos, GameScreen gameScreen){
        this.assetManager = assetManager;
        this.gameScreen = gameScreen;
        assetManager.load("items/portal.png", Texture.class);
        assetManager.finishLoading();
        this.texture = assetManager.get("items/portal.png", Texture.class);
        this.pos = pos;

        baseY = pos.y+5;
        hitbox = new Rectangle(pos.x, pos.y, 16, 16);
    }

    public void addPortal(Portal portal){
        nextPortal = portal;
    }

    public void goToNextPortal(){
        if(nextPortal != null) {
            gameScreen.getPlayer().teleportTo(new Vector2(nextPortal.getPos().x, nextPortal.getPos().y));
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if(!isPickedUp) {
            // update elapsed time
            elapsedTime += delta;

            // calculate bobbing offset using sine wave
            float offsetY = (float) Math.sin(elapsedTime * bobSpeed) * bobAmplitude;

            // draw broom with bobbing effect
            batch.draw(texture, pos.x, baseY + offsetY);
        }
    }

    public Rectangle getPos(){
        return new Rectangle(pos.x, baseY, texture.getWidth(), texture.getHeight());
    }
}
