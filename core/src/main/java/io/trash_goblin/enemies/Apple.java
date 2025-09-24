package io.trash_goblin.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.trash_goblin.audio.SoundManager;
import io.trash_goblin.player.Player;
import io.trash_goblin.screens.GameScreen;

import java.util.ArrayList;

public class Apple extends Enemy {

    private Animation<TextureRegion> currentAnimation, attackAnimation, idleAnimation;
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
    private float attackTimer = 0f, attackPeriod= 3f;
    private float seedTimer = 0f, seedPeriod = 3f;
    private ArrayList<AppleSeedProjectile> appleSeedProjectiles = new ArrayList<>();
    private enum State {
        IDLE, ATTACK
    }

    private State state = State.IDLE;

    private AssetManager assetManager;

    public Apple(AssetManager assetManager, ArrayList<Rectangle> collisionRects, float startX, float startY, GameScreen gameScreen) {
        this.collisionRects = collisionRects;
        this.gameScreen = gameScreen;
        this.assetManager = assetManager;

        assetManager.load("enemy/appleAttackSheet.png", Texture.class);
        assetManager.load("enemy/appleIdleSheet.png", Texture.class);
        assetManager.finishLoading();

        attackAnimation = Player.loadAnimation(16, 18, assetManager.get("enemy/appleAttackSheet.png", Texture.class));
        idleAnimation = Player.loadAnimation(16, 18, assetManager.get("enemy/appleIdleSheet.png", Texture.class));
        currentAnimation = idleAnimation;

        hitbox = new Rectangle(startX, startY, 16, 18);
    }


    public void update(float delta) {

        seedTimer += delta;

        // Update projectiles
        for (AppleSeedProjectile appleSeedProjectile : new ArrayList<>(appleSeedProjectiles)){
            appleSeedProjectile.update(delta);
            if(gameScreen.getPlayer().getHitbox().overlaps(appleSeedProjectile.getHitbox()) &&
            !gameScreen.getPlayer().isTakingDamage()){
                appleSeedProjectiles.remove(appleSeedProjectile);
                gameScreen.getPlayer().damage();
            }
        }

        // Handle damage flash
        if (damaged) {
            damageTimer += delta;
            flashElapsed += delta;
            if (flashElapsed >= totalFlashTime) {
                damaged = false;
            }
        }

        time += delta * animSpeed;

        // --- Handle idle/attack state ---
        attackTimer += delta;

        switch (state) {
            case IDLE:
                currentAnimation = idleAnimation;

                if (attackTimer >= attackPeriod) {
                    // switch to attack
                    state = State.ATTACK;
                    time = 0f; // reset animation time
                }
                break;

            case ATTACK:
                currentAnimation = attackAnimation;

                // Check if attack animation finished
                Rectangle playerHitbox = gameScreen.getPlayer().getHitbox();

// Define how close the player needs to be (e.g. 200px)
                float attackRange = 200f;

// Calculate distance between centers
                float enemyCenterX = hitbox.x + hitbox.width / 2f;
                float enemyCenterY = hitbox.y + hitbox.height / 2f;
                float playerCenterX = playerHitbox.x + playerHitbox.width / 2f;
                float playerCenterY = playerHitbox.y + playerHitbox.height / 2f;

                float dx = enemyCenterX - playerCenterX;
                float dy = enemyCenterY - playerCenterY;
                float distance = (float)Math.sqrt(dx * dx + dy * dy);

                if (currentAnimation.isAnimationFinished(time) && distance <= attackRange) {
                    // spawn seeds
                    AppleSeedProjectile leftSeed = new AppleSeedProjectile(assetManager,
                        new Vector2(hitbox.x, hitbox.y + hitbox.height / 2f), Player.Direction.LEFT, this);
                    AppleSeedProjectile rightSeed = new AppleSeedProjectile(assetManager,
                        new Vector2(hitbox.x, hitbox.y + hitbox.height / 2f), Player.Direction.RIGHT, this);
                    appleSeedProjectiles.add(leftSeed);
                    appleSeedProjectiles.add(rightSeed);

                    // go back to idle
                    state = State.IDLE;
                    attackTimer = 0f;
                    time = 0f;

                    //Play sound
                    SoundManager.getInstance().play("spit");
                }
                break;
        }

    }



    public void render(SpriteBatch batch, float delta) {
        update(delta);


        for(AppleSeedProjectile appleSeedProjectile : new ArrayList<>(appleSeedProjectiles)){
            appleSeedProjectile.render(batch, delta);
        }
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

        batch.draw(frame, hitbox.x, hitbox.y);
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
            SoundManager.getInstance().play("appleHurt");
        }
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void removeSeed(AppleSeedProjectile appleSeedProjectile) {
        appleSeedProjectiles.remove(appleSeedProjectile);
    }
}
