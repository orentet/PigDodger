package com.pigdodger.modes.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pigdodger.misc.SimpleObservable;
import com.pigdodger.misc.Utils;
import com.pigdodger.modes.Mode;

public class MainMenuMode implements Mode {

    private SimpleObservable startGameClickObservable = new SimpleObservable();

    Stage stage = new Stage();

    private boolean isStarted = false;

    public MainMenuMode() {
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(Utils.getDrawableFromAsset("background.png"));
        table.add(createPlayButton());
        stage.addActor(table);
    }

    @Override
    public void close() throws Exception {
        stage.dispose();
    }

    private Actor createPlayButton() {
        ImageButtonStyle playBtnStyle = new ImageButtonStyle();
        playBtnStyle.up = Utils.getDrawableFromAsset("play_button.png");
        playBtnStyle.down = Utils.getDrawableFromAsset("play_button_clicked.png");
        playBtnStyle.over = Utils.getDrawableFromAsset("play_button_hover.png");
        ImageButton playBtn = new ImageButton(playBtnStyle);
        playBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                startGameClickObservable.changed();
            }
        });
        return playBtn;
    }

    public SimpleObservable getStartGameClickObservable() {
        return startGameClickObservable;
    }

    @Override
    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // Table.drawDebug(stage);
    }

    @Override
    public void start() throws Exception {
        if (this.isStarted) {
            throw new Exception("Already started");
        }

        Gdx.input.setInputProcessor(stage);

        isStarted = true;
    }
}
