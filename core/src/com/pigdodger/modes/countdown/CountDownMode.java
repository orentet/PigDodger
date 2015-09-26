package com.pigdodger.modes.countdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pigdodger.misc.SimpleObservable;
import com.pigdodger.modes.Mode;

import java.util.Observable;

public class CountDownMode implements Mode {

    boolean isStarted = false;

    String startingOverText = "Starting Over...";
    BitmapFont startingOverFont = new BitmapFont();

    int currentNumber = 3;

    BitmapFont countDownFont = new BitmapFont();
    SpriteBatch batch = new SpriteBatch();

    private SimpleObservable countDownFinishedObservable = new SimpleObservable();

    // counts 3 to 1
    public CountDownMode() {
        resetFontScale();
        startingOverFont.getData().setScale(2f);
    }

    @Override
    public void close() throws Exception {
    }

    public Observable getCountDownFinishedObservable() {
        return countDownFinishedObservable;
    }

    @Override
    public void render() {
        float currentScale = countDownFont.getScaleX(); // x == y
        currentScale /= 1.05f;
        countDownFont.getData().setScale(currentScale);

        if (currentScale <= 1) {
            currentNumber -= 1;
            resetFontScale();
        }
        if (currentNumber == 0) {
            countDownFinishedObservable.changed();
            return;
        }

        String countDownText = Integer.toString(currentNumber);

        this.batch.begin();

        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(startingOverFont, startingOverText);
        this.startingOverFont.draw(this.batch, startingOverText, (Gdx.graphics.getWidth() - glyphLayout.width) / 2, ((Gdx.graphics.getHeight() - glyphLayout.height) / 2) + 30);

        glyphLayout.setText(countDownFont, countDownText);
        this.countDownFont.draw(this.batch, countDownText, (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
                (Gdx.graphics.getHeight() - glyphLayout.height) / 2);

        this.batch.end();
    }

    private void resetFontScale() {
        countDownFont.getData().setScale(100);
    }

    @Override
    public void start() throws Exception {
        if (this.isStarted) {
            throw new Exception("Already started");
        }
        this.isStarted = true;
    }
}
