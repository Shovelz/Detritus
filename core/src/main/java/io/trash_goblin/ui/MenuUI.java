package io.trash_goblin.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.trash_goblin.screens.MenuScreen;

public class MenuUI {
    private VisTextButton startButton;
    private Table menuTable;
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();
    private Texture background;
    private AssetManager assetManager;

    public MenuUI(MenuScreen menuScreen, AssetManager assetManager){
        this.assetManager = assetManager;
        assetManager.load("ui/startScreen.png", Texture.class);
        assetManager.finishLoading();
        background = assetManager.get("ui/startScreen.png", Texture.class);

        menuTable = new Table();


        startButton = new VisTextButton("Start");
        startButton.getLabel().setFontScale(1.5f);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("START GAME!!!");
                menuScreen.startGame();
            }
        });

        menuTable.top();             // align content at top
        menuTable.center();          // center horizontally
        menuTable.padBottom(250);
        menuTable.add(startButton).padTop(1080*0.75f);
        startButton.pad(25);

    }

    public void render(SpriteBatch batch, float delta){
        batch.draw(background, 0, 0, 1920, 1080);
    }

    public Table getTable(){
        return menuTable;
    }

}
