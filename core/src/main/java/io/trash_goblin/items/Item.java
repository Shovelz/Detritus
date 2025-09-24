package io.trash_goblin.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public abstract class Item {
    protected Vector2 pos;

    protected abstract void render(SpriteBatch batch, float delta);

}
