package com.pigdodger.modes.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.pigdodger.modes.game.model.GameModel;

public class DebugRenderer implements ModelRenderer {
	Box2DDebugRenderer debugRenderer;

	GameModel model;
	Camera camera;

	BitmapFont font;
	SpriteBatch batch;

	public DebugRenderer(GameModel model, Camera camera) {
		this.model = model;
		this.camera = camera;

		this.debugRenderer = new Box2DDebugRenderer();

		this.batch = new SpriteBatch();
		this.font = new BitmapFont();
		// font = new BitmapFont(Gdx.files.internal("data/font.fnt"),
		// Gdx.files.internal("data/font.png"), false);
	}

	@Override
	public void render() {
		this.debugRenderer.render(this.model.getWorld(), this.camera.combined);

		this.batch.begin();
		this.font.draw(this.batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 0, this.camera.project(new Vector3(this.model.getWorldHeight(), 0, 0)).x);
		this.batch.end();
	}
}
