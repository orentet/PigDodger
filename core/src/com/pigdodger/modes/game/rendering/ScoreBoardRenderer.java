package com.pigdodger.modes.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.pigdodger.modes.game.model.ScoreBoardModel;

public class ScoreBoardRenderer implements ModelRenderer {

	ScoreBoardModel scoreBoard;

	BitmapFont font = new BitmapFont();
	SpriteBatch batch = new SpriteBatch();

	public ScoreBoardRenderer(ScoreBoardModel scoreBoard) {
		this.scoreBoard = scoreBoard;
		font.getData().setScale(2f);
	}

	@Override
	public void render() {
		String text = "Current Score: " + this.scoreBoard.getCurrentScore() + ". High Score: " + this.scoreBoard.getHighestScore();

		this.batch.begin();

		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(font, text);
		this.font.draw(this.batch, text, Gdx.graphics.getWidth() - glyphLayout.width - 10, Gdx.graphics.getHeight() - 10);

		this.batch.end();
	}

}
