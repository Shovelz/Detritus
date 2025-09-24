package io.trash_goblin.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.trash_goblin.audio.SoundManager;
import io.trash_goblin.player.Player;
import io.trash_goblin.screens.GameScreen;

import java.util.ArrayList;

public class Trashbag extends Enemy {

    private Animation<TextureRegion> currentAnimation, walkAnimation;
    private float time = 0f;
    private float animSpeed = 2.5f;
    private boolean loopAnimation = true;

    private Rectangle hitbox;
    private float speed = 33f; // patrol speed

    private Player.Direction direction = Player.Direction.LEFT;

    private ArrayList<Rectangle> collisionRects;
    private int health = 3;
    private GameScreen gameScreen;
    private float damageTimer, flashElapsed = 0f, totalFlashTime = 0.8f;
    private boolean damaged = false;

    public Trashbag(AssetManager assetManager, ArrayList<Rectangle> collisionRects, float startX, float startY, GameScreen gameScreen) {
        this.collisionRects = collisionRects;
        this.gameScreen = gameScreen;

        assetManager.load("enemy/trashbagSheet.png", Texture.class);
        assetManager.finishLoading();

        walkAnimation = Player.loadAnimation(23, 30, assetManager.get("enemy/trashbagSheet.png", Texture.class));
        currentAnimation = walkAnimation;

        hitbox = new Rectangle(startX, startY, 23, 30);
    }

    private void ai(float delta) {
        float moveX = (direction == Player.Direction.LEFT ? -speed : speed) * delta;
        float newX = hitbox.x + moveX;

        // Check horizontal collisions
        Rectangle horizontalBounds = new Rectangle(newX, hitbox.y, hitbox.width, hitbox.height);
        boolean hitWall = false;

        for (Rectangle rect : collisionRects) {
            if (horizontalBounds.overlaps(rect)) {
                hitWall = true;
                break;
            }
        }

        if (hitWall) {
            // turn around at wall
            direction = (direction == Player.Direction.LEFT) ? Player.Direction.RIGHT : Player.Direction.LEFT;
            return;
        }

        // Check ledge (look just below front foot)
        float footY = hitbox.y - 1; // slightly below feet
        float frontX = (direction == Player.Direction.LEFT) ? newX : newX + hitbox.width;

        boolean hasGround = false;
        for (Rectangle rect : collisionRects) {
            if (rect.contains(frontX, footY)) {
                hasGround = true;
                break;
            }
        }

        if (!hasGround) {
            // turn around at edge
            direction = (direction == Player.Direction.LEFT) ? Player.Direction.RIGHT : Player.Direction.LEFT;
            return;
        }

        // Apply movement
        hitbox.x = newX;
    }

    public void update(float delta) {

        if (damaged) {
            damageTimer += delta;
            flashElapsed += delta;

            // End flashing after total time
            if (flashElapsed >= totalFlashTime) {
                damaged = false;
            }
        }

        time += delta * animSpeed;
        ai(delta);

//
//        if(){
//           gameScreen.removeEnemy(this);
//        }
    }


    public void render(SpriteBatch batch, float delta) {
        update(delta);

        TextureRegion frame = currentAnimation.getKeyFrame(time, loopAnimation);

        if (direction == Player.Direction.LEFT && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (direction == Player.Direction.RIGHT && frame.isFlipX()) {
            frame.flip(true, false);
        }

        // Flash effect
        if (damaged) {
            if (((int)(damageTimer * 10)) % 2 == 0) {
                batch.setColor(Color.RED); // visible flash
            } else {
                batch.setColor(1, 1, 1, 1); // could also use red
            }
        } else {
            batch.setColor(1, 1, 1, 1); // normal
        }

        batch.draw(frame, hitbox.x, hitbox.y-2);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void damage() {

        if(!damaged) {
            health--;
            if (health < 1) {
                gameScreen.removeEnemy(this);
                return;
            }
            damaged = true;
            damageTimer = 0f;
            flashElapsed = 0f;
            SoundManager.getInstance().play("trashbagHurt");
        }
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
}
