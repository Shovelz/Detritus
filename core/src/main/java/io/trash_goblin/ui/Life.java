package io.trash_goblin.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Life {

    private Texture texture;
    private TextureRegion aliveRegion, deadRegion;
    private Vector2 pos;
    private boolean alive = true;

    public Life(AssetManager assetManager, Vector2 startingPos){
        this.pos = startingPos;
        assetManager.load("ui/lives.png", Texture.class);
        assetManager.finishLoading();
        texture = assetManager.get("ui/lives.png", Texture.class);

        int halfWidth = texture.getWidth() / 2;
        int height = texture.getHeight();

        aliveRegion = new TextureRegion(texture, 0, 0, halfWidth, height);
        deadRegion = new TextureRegion(texture, halfWidth, 0, halfWidth, height);
    }

    public void loseLife(){
        alive = false;
    }

    public void render(SpriteBatch batch, float delta){
        if (alive) {
            batch.draw(aliveRegion, pos.x, pos.y, 18/3.0f, 34/3.0f);
        } else {
            batch.draw(deadRegion, pos.x, pos.y, 18/3.0f, 34/3.0f);
        }
    }
}
