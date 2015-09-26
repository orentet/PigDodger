package com.pigdodger.modes.game.rendering;

import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pigdodger.modes.game.model.GameModel;
import com.pigdodger.modes.game.model.entities.Ground;
import com.pigdodger.modes.game.model.entities.Pig;
import com.pigdodger.modes.game.model.entities.Rock;

public class GameRenderer implements ModelRenderer {
	GameModel gameModel;
	OrthographicCamera camera;

	PigEntityRenderer pigRenderer;
	GroundRenderer groundRenderer;
	Array<RockEntityRenderer> rockRenderers;
	BackgroundRenderer backgroundRenderer;

	SpriteBatch spriteBatch;

	float worldHeightPx;
	float worldWidthPx;
	private Observer gameModelOnEntityCreation = new Observer() {
		@Override
		public void update(Observable arg0, Object entity) {
			if (entity.getClass() == Pig.class) {
				pigRenderer = new PigEntityRenderer((Pig) entity, camera);
			} else if (entity.getClass() == Ground.class) {
				groundRenderer = new GroundRenderer(worldWidthPx);
			} else if (entity.getClass() == Rock.class) {
				rockRenderers.add(new RockEntityRenderer((Rock) entity, camera));
			}
		}
	};
	private Observer gameModelOnEntityDestruction = new Observer() {
		@Override
		public void update(Observable arg0, Object entity) {
			if (entity.getClass() == Pig.class) {
				pigRenderer = null;
			} else if (entity.getClass() == Ground.class) {
				groundRenderer = null;
			} else if (entity.getClass() == Rock.class) {
				rockRenderers.removeValue(findRockRendererByRock((Rock) entity), true);
			}
		}
	};

	public GameRenderer(GameModel gameModel, OrthographicCamera camera) {
		this.gameModel = gameModel;
		this.gameModel.getEntityCreationObservable().addObserver(gameModelOnEntityCreation);
		this.gameModel.getEntityDestructionObservable().addObserver(gameModelOnEntityDestruction);
		this.camera = camera;

		this.worldHeightPx = this.camera.project(new Vector3(this.gameModel.getWorldHeight(), 0, 0)).x;
		this.worldWidthPx = this.camera.project(new Vector3(this.gameModel.getWorldWidth(), 0, 0)).x;

		this.spriteBatch = new SpriteBatch();

		this.pigRenderer = null;
		this.groundRenderer = null;
		this.rockRenderers = new Array<RockEntityRenderer>();
		this.backgroundRenderer = new BackgroundRenderer(this.worldWidthPx, this.worldHeightPx);
	}

	private RockEntityRenderer findRockRendererByRock(Rock rock) {
		for (RockEntityRenderer rockRenderer : this.rockRenderers) {
			if (rockRenderer.getRock() == rock) {
				return rockRenderer;
			}
		}
		return null;
	}

	@Override
	public void render() {
		this.spriteBatch.begin();

		this.backgroundRenderer.render(this.spriteBatch);
		this.groundRenderer.render(this.spriteBatch);
		for (RockEntityRenderer rockRenderer : this.rockRenderers) {
			rockRenderer.render(this.spriteBatch);
		}
		if (this.pigRenderer != null) {
			this.pigRenderer.render(this.spriteBatch);
		}

		this.spriteBatch.end();
	}
}
