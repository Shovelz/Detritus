package io.trash_goblin.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public abstract class Enemy {
    //Apple Core
    //Trash Cans
    //Banana peals
    //Newspaper
    //Rubbish bag
    //Crushed can

    public abstract Rectangle getHitbox();
    public abstract void render(SpriteBatch batch, float delta);

    public abstract void damage();


}
