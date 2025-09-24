package io.trash_goblin.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BroomItem extends Item {

    private AssetManager assetManager;
    private Texture texture;
    private float elapsedTime = 0f;
    private float bobAmplitude = 2f; // how far it moves up/down in pixels
    private float bobSpeed = 2f;     // how fast it bobs (oscillations per second)
    private float baseY;             // original Y position
    private boolean isPickedUp = false;

    public BroomItem(AssetManager assetManager, Vector2 pos){
        this.assetManager = assetManager;
        assetManager.load("items/broom.png", Texture.class);
        assetManager.finishLoading();
        this.texture = assetManager.get("items/broom.png", Texture.class);
        this.pos = pos;

        baseY = pos.y+5;
    }

    public void pickUp() {
        isPickedUp = true;
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
}
