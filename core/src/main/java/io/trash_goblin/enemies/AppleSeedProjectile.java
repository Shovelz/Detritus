package io.trash_goblin.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.trash_goblin.player.Player;

public class AppleSeedProjectile {

    private Rectangle hitbox;
    private Texture seedTexture;
    private float speed = 1f;
    private Player.Direction direction;
    private float lifetime = 100f, distance = 0f;
    private Apple parent;

    public AppleSeedProjectile(AssetManager assetManager, Vector2 pos, Player.Direction direction, Apple parent){
        this.direction = direction;
        assetManager.load("enemy/appleSeed.png", Texture.class);
        assetManager.finishLoading();
        seedTexture = assetManager.get("enemy/appleSeed.png", Texture.class);
        hitbox = new Rectangle(pos.x, pos.y, seedTexture.getWidth(), seedTexture.getHeight());
        this.parent = parent;

    }

    public void update(float delta){
        if(direction == Player.Direction.LEFT){
            hitbox.x += speed;
        }else {
            hitbox.x -= speed;
        }
        distance += speed;

        if(distance >= lifetime){
            parent.removeSeed(this);
        }
    }

    public void render(SpriteBatch batch, float delta){
        batch.draw(seedTexture, hitbox.x, hitbox.y);
    }

    public Rectangle getHitbox(){
        return hitbox;
    }
}
