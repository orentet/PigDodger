package com.pigdodger.modes.game;

import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.pigdodger.misc.SimpleObservable;
import com.pigdodger.modes.Mode;
import com.pigdodger.modes.game.audio.GameAudioManager;
import com.pigdodger.modes.game.model.GameModel;
import com.pigdodger.modes.game.model.ScoreBoardModel;
import com.pigdodger.modes.game.rendering.GameRenderer;
import com.pigdodger.modes.game.rendering.ModelRenderer;
import com.pigdodger.modes.game.rendering.ScoreBoardRenderer;
import com.pigdodger.modes.game.userinterface.GameUserInterfaceManager;

public class GameMode implements Mode {
	int width = 10;
	int height = 6;

	GameModel gameModel;
	Array<ModelRenderer> renderers = new Array<ModelRenderer>();
	OrthographicCamera camera;
	GameUserInterfaceManager gameUserInterfaceManager;
	GameAudioManager gameAudioManager;
	ScoreBoardModel scoreBoard;

	boolean isStarted = false;

	SimpleObservable gameOverObservable = new SimpleObservable();

	private Observer gameModelOnPigDeathObservable = new Observer() {
		@Override
		public void update(Observable arg0, Object arg1) {
			gameOverObservable.changed();
		}
	};

	public GameMode() {
		// show game
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, this.width, this.height);

		this.gameModel = new GameModel(this.width, this.height);
		gameModel.getPigDeathObservable().addObserver(gameModelOnPigDeathObservable);

		this.gameUserInterfaceManager = new GameUserInterfaceManager(this.gameModel, this.camera);
		this.gameAudioManager = new GameAudioManager(this.gameModel);

		this.scoreBoard = new ScoreBoardModel(this.gameModel);

		// this.renderers.add(new DebugRenderer(this.gameModel, this.camera));
		this.renderers.add(new GameRenderer(this.gameModel, this.camera));
		this.renderers.add(new ScoreBoardRenderer(this.scoreBoard));
	}

	@Override
	public void close() throws Exception {
		this.gameModel.close();
		this.gameAudioManager.close();
	}

	public Observable getGameOverObservable() {
		return gameOverObservable;
	}

	@Override
	public void render() {
		this.gameModel.update();

		// rendering
		for (ModelRenderer renderer : this.renderers) {
			renderer.render();
		}

		this.gameUserInterfaceManager.processUserInputOnRender();
		this.scoreBoard.update();
	}

	@Override
	public void start() throws Exception {
		if (this.isStarted) {
			throw new Exception("Already started");
		}

		this.gameModel.createWorld(true);
		this.gameAudioManager.startGameMusic();

		this.isStarted = true;
	}
}
