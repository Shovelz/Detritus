package io.trash_goblin.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.trash_goblin.audio.SoundManager;
import io.trash_goblin.enemies.Enemy;
import io.trash_goblin.items.Portal;
import io.trash_goblin.player.weapon.Broom;
import io.trash_goblin.screens.GameScreen;

import java.util.ArrayList;

public class Player {

    private Animation<TextureRegion> currentAnimation, idleAnimation, walkAnimation, attackAnimation;
    private float time = 0;
    private float animSpeed = 2f;
    private boolean loopAnimation = true;
    private Rectangle hitbox;

    public void teleportTo(Vector2 vector2) {
        hitbox.x = vector2.x + 16;
        hitbox.y = vector2.y;
    }

    //States
    public enum Direction {LEFT, RIGHT}

    private Direction direction;

    private enum State {IDLE, WALK, JUMP, ATTACK}

    private State state;

    //Movement
    private Vector2 velocity = new Vector2();
    private float speed = 80f;   // horizontal speed (pixels/sec)
    private float gravity = -1200f;
    private float jumpSpeed = 300f;
    private boolean grounded = true; // simple ground check for now
    private ArrayList<Rectangle> collisionRects, lavaCollisionRects;
    private Rectangle broomItemCollisionRect;

    private Broom weapon;
    private GameScreen gameScreen;

    private ArrayList<Enemy> enemies;
    private boolean flashing = false;
    private float flashTimer = 0f;
    private float flashDuration = 0.15f; // how long one flash lasts
    private float totalFlashTime = 1.5f;   // total time player flashes
    private float flashElapsed = 0f;


    public Player(AssetManager assetManager, ArrayList<Rectangle> collisionRects, Rectangle broomItemRect, GameScreen gameScreen, ArrayList<Rectangle> lavaCollisonRects) {
        this.collisionRects = new ArrayList<>(collisionRects);
        this.lavaCollisionRects = new ArrayList<>(lavaCollisonRects);
        this.gameScreen = gameScreen;

        enemies = new ArrayList<>(gameScreen.getEnemies());

        assetManager.load("goblin/idle.png", Texture.class);
        assetManager.load("goblin/walk.png", Texture.class);
        assetManager.finishLoading();
        idleAnimation = Player.loadAnimation(16, 17, assetManager.get("goblin/idle.png", Texture.class));
        walkAnimation = Player.loadAnimation(16, 17, assetManager.get("goblin/walk.png", Texture.class));
        attackAnimation = Player.loadAnimation(16, 17, assetManager.get("goblin/idle.png", Texture.class));
        currentAnimation = idleAnimation;
        state = State.IDLE;

        hitbox = new Rectangle(50, 50, 16, 17); // start at (50,50)

        weapon = new Broom(this, assetManager);
        broomItemCollisionRect = broomItemRect;

    }

    public void handleInput(float delta) {
        // Reset horizontal velocity each frame
        velocity.x = 0;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && weapon.getPickedUp()) {
            state = State.ATTACK;
            weapon.attack();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -speed;
            direction = Direction.LEFT;
            state = State.WALK;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = speed;
            direction = Direction.RIGHT;
            state = State.WALK;
        } else if (state != State.ATTACK) {
            state = State.IDLE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && grounded) {
            velocity.y = jumpSpeed;
            grounded = false;
            state = State.JUMP;
        }
    }


    public void render(SpriteBatch batch, float delta) {

        handleInput(delta);
        update(delta);

        TextureRegion frame = currentAnimation.getKeyFrame(time, loopAnimation);
        if (direction == Direction.LEFT && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (direction == Direction.RIGHT && frame.isFlipX()) {
            frame.flip(true, false);
        }


        // Flash effect
        if (flashing) {
            if (((int) (flashTimer * 10)) % 2 == 0) {
                batch.setColor(Color.RED); // visible flash
            } else {
                batch.setColor(1, 1, 1, 1); // could also use red
            }
        } else {
            batch.setColor(1, 1, 1, 1); // normal
        }

        batch.draw(frame, hitbox.x, hitbox.y);
        batch.setColor(Color.WHITE);

        weapon.render(batch, delta);
    }

    public void damage() {
        flashing = true;
        flashTimer = 0f;
        flashElapsed = 0f;
        SoundManager.getInstance().play("playerDamaged");
        gameScreen.loseLife();
    }

    private void itemCollision() {
        if (hitbox.overlaps(broomItemCollisionRect)) {
            weapon.setPickedUp(true);
            gameScreen.pickupItem();
        }
    }

    public boolean isTakingDamage(){
        return flashing;
    }

    public void update(float delta) {

        if (flashing) {
            flashTimer += delta;
            flashElapsed += delta;

            // End flashing after total time
            if (flashElapsed >= totalFlashTime) {
                flashing = false;
            }
        }

        if (!weapon.getPickedUp()) {
            itemCollision();
        }
        time += animSpeed * delta;

        // Apply gravity
        velocity.y += gravity * delta;


        // --- HORIZONTAL MOVEMENT & COLLISION ---
        float newX = hitbox.x + velocity.x * delta;
        Rectangle horizontalBounds = new Rectangle(newX, hitbox.y, hitbox.width, hitbox.height);

        for (Rectangle rect : collisionRects) {
            if (horizontalBounds.overlaps(rect)) {
                if (velocity.x > 0) { // moving right
                    newX = rect.x - hitbox.width;
                } else if (velocity.x < 0) { // moving left
                    newX = rect.x + rect.width;
                }
                velocity.x = 0;
            }
        }

        hitbox.x = newX;

        // --- VERTICAL MOVEMENT & COLLISION ---
        float newY = hitbox.y + velocity.y * delta;
        Rectangle verticalBounds = new Rectangle(hitbox.x, newY, hitbox.width, hitbox.height);

        grounded = false;

        for (Rectangle rect : collisionRects) {
            if (verticalBounds.overlaps(rect)) {
                if (velocity.y > 0) { // moving up, hit ceiling
                    newY = rect.y - hitbox.height;
                    velocity.y = 0;
                } else if (velocity.y < 0) { // falling, hit ground
                    newY = rect.y + rect.height;
                    velocity.y = 0;
                    grounded = true;
                }
            }
        }

        hitbox.y = newY;

        for (Enemy enemy : gameScreen.getEnemies()) {
            if (enemy.getHitbox().overlaps(hitbox) && !flashing) {
                damage();
            }
        }
        for (Rectangle lava : lavaCollisionRects) {
            if (lava.overlaps(hitbox)) {
                gameScreen.loseLife();
            }
        }
        if (weapon.getIsAttack()) {
            for (Enemy enemy : gameScreen.getEnemies()) {
                if (enemy.getHitbox().overlaps(weapon.getHitbox())) {
                    enemy.damage();
                }
            }
        }

        if(gameScreen.getEndPortal().getPos().overlaps(hitbox)){
            gameScreen.portalEntered();
        }

        for (Portal portal : gameScreen.getPortals()) {
            if (portal.getPos().overlaps(hitbox)) {
                portal.goToNextPortal();
//                gameScreen.portalEntered();
            }
        }

        // Pick animation
        switch (state) {
            case IDLE:
                currentAnimation = idleAnimation;
                break;
            case WALK:
                currentAnimation = walkAnimation;
                break;
            case JUMP:
                // could use a jump animation
                break;
            case ATTACK:
                currentAnimation = attackAnimation;
                break;
        }
    }


    public static Animation<TextureRegion> loadAnimation(int width, int height, Texture sheet) {

        TextureRegion[][] tmpFrames = TextureRegion.split(sheet, width, height);

        TextureRegion[] animationFrames = new TextureRegion[tmpFrames.length * tmpFrames[0].length];
        int index = 0;
        for (int row = 0; row < tmpFrames.length; row++) {
            for (int col = 0; col < tmpFrames[tmpFrames.length - 1].length; col++) {
                animationFrames[index++] = tmpFrames[row][col];
            }

        }
        return new Animation<TextureRegion>(1f / 4f, animationFrames);
    }


    public float getX() {
        return hitbox.x;
    }

    public float getY() {
        return hitbox.y;
    }

    public float getWidth() {
        return hitbox.width;
    }

    public float getHeight() {
        return hitbox.height;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Direction getDirection() {
        return direction;
    }
}
