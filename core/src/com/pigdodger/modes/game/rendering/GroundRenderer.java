package com.pigdodger.modes.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GroundRenderer implements BatchRenderer {
	int groundWidthPx;
	float worldWidthPx;
	Texture texture;

	public GroundRenderer(float worldWidthPx) {
		this.worldWidthPx = worldWidthPx;
		this.texture = new Texture(Gdx.files.internal("ground.png"), true);
		this.texture.setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		this.texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

	}

	@Override
	public void render(SpriteBatch batch) {
		for (int i = 0; i < this.worldWidthPx; i += this.texture.getWidth()) {
			batch.draw(this.texture, i, 0);
		}
	}
}
