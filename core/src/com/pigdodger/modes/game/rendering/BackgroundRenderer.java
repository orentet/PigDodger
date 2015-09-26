package com.pigdodger.modes.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BackgroundRenderer implements BatchRenderer {
	Texture backgroundTexture;
	float worldWidthPx;
	float worldHeightPx;

	public BackgroundRenderer(float worldWidthPx, float worldHeightPx) {
		this.backgroundTexture = new Texture(Gdx.files.internal("background.png"), true);
		this.backgroundTexture.setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		this.worldWidthPx = worldWidthPx;
		this.worldHeightPx = worldHeightPx;
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(this.backgroundTexture, 0, 0, this.worldWidthPx, this.worldHeightPx);
	}
}
