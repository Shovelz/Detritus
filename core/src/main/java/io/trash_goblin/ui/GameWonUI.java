package io.trash_goblin.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.trash_goblin.Main;

public class GameWonUI {
    private VisTextButton startButton;
    private Table menuTable;
    private AssetManager assetManager;
    private Texture background;

    public GameWonUI(Main game, AssetManager assetManager){
        menuTable = new Table();

        this.assetManager = assetManager;
        assetManager.load("ui/win.png", Texture.class);
        assetManager.finishLoading();
        background = assetManager.get("ui/win.png", Texture.class);



        startButton = new VisTextButton("New Game");
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startGame();
            }
        });

        menuTable.top();             // align content at top
        menuTable.center();          // center horizontally
        menuTable.padBottom(250);
        menuTable.add(startButton).padTop(1080*0.75f);
        startButton.pad(15);

    }

    public Table getTable(){
        return menuTable;
    }

    public void render(SpriteBatch batch, float delta){
        batch.draw(background, 0, 0, 1920, 1080);
    }

}
