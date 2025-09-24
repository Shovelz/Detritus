package io.trash_goblin.player.weapon;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.trash_goblin.audio.SoundManager;
import io.trash_goblin.player.Player;


public class Broom {
    private Player player;
    private Animation<TextureRegion> currentAnimation, attackAnimation;
    private boolean loopAnimation = false;
    private Player.Direction direction;
    private Rectangle hitbox;
    private float time = 0, animSpeed = 5f;
    private boolean isAttacking = false;
    private boolean pickedUp = false;


    public Broom(Player player, AssetManager assetManager){
        this.player = player;
        assetManager.load("goblin/weapon.png", Texture.class);
        assetManager.finishLoading();
        attackAnimation = Player.loadAnimation(31, 17, assetManager.get("goblin/weapon.png", Texture.class));
        currentAnimation = attackAnimation;

        hitbox = new Rectangle(50, 50, 24, 7); // start at (50,50)
    }

    public void update(float delta){

        direction = player.getDirection();
        hitbox.x = player.getX();
        hitbox.y = player.getY();
        if(direction == Player.Direction.LEFT){
            hitbox.x -= hitbox.width/2.0f;
        }
    }

    public void attack(){
        if(!isAttacking) {
            isAttacking = true;
            time = 0;
            SoundManager.getInstance().play("sweep");
        }
    }

    public void render(SpriteBatch batch, float delta) {

        update(delta);

        if (isAttacking) {
            time += animSpeed * delta;

            // Stop attack after animation ends
            if (currentAnimation.isAnimationFinished(time)) {
                isAttacking = false;
                time = 0; // reset time for next swing
            }
        }
        TextureRegion frame = currentAnimation.getKeyFrame(time, loopAnimation);
        if (direction == Player.Direction.LEFT && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (direction == Player.Direction.RIGHT && frame.isFlipX()) {
            frame.flip(true, false);
        }

        if(isAttacking) {
            batch.draw(frame, hitbox.x, hitbox.y);
        }
    }

    public boolean getPickedUp(){
        return pickedUp;
    }

    public void setPickedUp(boolean bool){
        pickedUp = bool;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    public boolean getIsAttack(){
        return isAttacking;
    }

}
