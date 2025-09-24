package io.trash_goblin.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.trash_goblin.audio.MusicManager;
import io.trash_goblin.audio.SoundManager;
import io.trash_goblin.screens.GameScreen;

public class PauseMenuUI {
    private VisSlider soundSlider, musicSlider;
    private Table menuTable;
    private BitmapFont font, fontSmall;
    private GlyphLayout layout = new GlyphLayout();
    private Label titleLabel;
    private VisTextButton closeButton;
    private Texture bgTexture;

    public PauseMenuUI(GameScreen gameScreen) {
        menuTable = new Table();

        // 🔹 Add a VisUI background drawable
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.valueOf("1b2026")); // change alpha to taste
        pix.fill();
        bgTexture = new Texture(pix);
        pix.dispose();
        menuTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));

        this.font = new BitmapFont(Gdx.files.internal("fonts/bitmgothicMedium.fnt"));
        this.fontSmall = new BitmapFont(Gdx.files.internal("fonts/bitmgothicSmall.fnt"));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;           // your custom BitmapFont
        labelStyle.fontColor = Color.valueOf("aac39e");

        Label.LabelStyle labelStyleSmall = new Label.LabelStyle();
        labelStyleSmall.font = fontSmall;           // your custom BitmapFont
        labelStyleSmall.fontColor = Color.valueOf("aac39e");


        titleLabel = new Label("Settings", labelStyle);
        soundSlider = new VisSlider(0.0f, 1.0f, 0.05f, false);
        musicSlider = new VisSlider(0.0f, 1.0f, 0.05f, false);
        soundSlider.setValue(SoundManager.getInstance().getVolume());
        musicSlider.setValue(MusicManager.getInstance().getVolume());

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MusicManager.getInstance().setVolume(musicSlider.getValue());
            }
        });

        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().setVolume(soundSlider.getValue());
            }
        });

        closeButton = new VisTextButton("Ok");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScreen.unpause();
            }
        });

        closeButton.pad(10);


        // Create labels for the sliders
        Label soundLabel = new Label("Sound", labelStyleSmall);
        Label musicLabel = new Label("Music", labelStyleSmall);

        // Layout
        menuTable.center();            // 🔹 center contents
        menuTable.pad(20);
        menuTable.defaults().pad(10);

// Title
        menuTable.add(titleLabel).colspan(2).center().row();

// Sound row
        Table soundRow = new Table();
        soundRow.add(new Label("Sound", labelStyleSmall)).right().padRight(10);
        soundRow.add(soundSlider).width(300).left();
        menuTable.add(soundRow).center().row();

// Music row
        Table musicRow = new Table();
        musicRow.add(new Label("Music", labelStyleSmall)).right().padRight(10);
        musicRow.add(musicSlider).width(300).left();
        menuTable.add(musicRow).center().row();

// Close button
        menuTable.add(closeButton).colspan(2).padTop(20).center();


    }

    public Table getTable() {
        return menuTable;
    }
}
