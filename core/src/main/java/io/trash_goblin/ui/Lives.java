package io.trash_goblin.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.trash_goblin.Main;

import java.util.ArrayList;

public class Lives {

    private ArrayList<Life> lives = new ArrayList<>();
    private int livesAmount = 5;
    private Vector2 startingPos = new Vector2(8,24);
    private Viewport port;
    private int currentLives = livesAmount;
    private Main game;

    public Lives(AssetManager assetManager, Viewport port, Main game){
        this.port = port;
        this.game = game;
        startingPos.y = port.getWorldHeight() - 16;
        for(int i = 0; i < livesAmount; i++){
            Life life = new Life(assetManager, new Vector2(startingPos.x + (i* 10), startingPos.y));
            lives.add(life);
        }
    }

    public void render(SpriteBatch batch, float delta){
        for(Life life : lives){
            life.render(batch, delta);
        }
    }

    public void resize(Viewport port){
        this.port = port;
        startingPos.y = port.getScreenHeight() - 24;
    }

    public void loseLife() {
        if(currentLives > 0) {
            lives.get(currentLives - 1).loseLife();
            currentLives--;
            if(currentLives <= 0){
                game.gameOver();
            }
        }
    }
}
