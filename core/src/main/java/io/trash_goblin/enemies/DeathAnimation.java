package io.trash_goblin.enemies;

import com.badlogic.gdx.math.Vector2;

public class DeathAnimation {


    private Vector2 pos;

    private float timer;
    private float animSpeed = 3;

    public DeathAnimation(Vector2 pos) {
        this.pos = pos;
    }

    public Vector2 getPos() {
        return pos;
    }

    public float getTimer() {
        return timer;
    }

    public float getAnimSpeed() {
        return animSpeed;
    }

    public void addToTimer(float delta) {
        timer += animSpeed * delta;

    }
}
