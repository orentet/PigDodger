package com.pigdodger.modes.game.userinterface;

import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.pigdodger.modes.game.model.GameModel;
import com.pigdodger.modes.game.model.entities.Pig;

public class GameUserInterfaceManager {

	GameModel gameModel;
	private Camera camera;
	InputDetector inputDetector;

	Body pigBody;
	private Observer gameModelOnEntityCreation = new Observer() {
		@Override
		public void update(Observable arg0, Object obj) {
			if (obj.getClass() == Pig.class) {
				// save pig for later use
				pigBody = ((Pig) obj).getBody();
			}
		}
	};

	private Observer inputDetectorOnDoubleKey = new Observer() {
		@Override
		public void update(Observable arg0, Object obj) {
			Integer key = (Integer) obj;
			if (key == Keys.LEFT) {
				gameModel.dodgePigLeft();
			}
			if (key == Keys.RIGHT) {
				gameModel.dodgePigRight();
			}
		}
	};

	private Observer inputDetectorOnDoubleTap = new Observer() {
		@Override
		public void update(Observable arg0, Object obj) {
			Vector2 screenLocation = (Vector2) obj;
			Vector3 worldPos = camera.unproject(new Vector3(screenLocation, 0));

			if (pigBody.getPosition().x > worldPos.x) {
				gameModel.dodgePigLeft();
			} else if (pigBody.getPosition().x < worldPos.x) {
				gameModel.dodgePigRight();
			}
		}
	};

	public GameUserInterfaceManager(GameModel gameModel, Camera camera) {
		this.gameModel = gameModel;
		this.camera = camera;
		gameModel.getEntityCreationObservable().addObserver(gameModelOnEntityCreation);

		this.inputDetector = new InputDetector(200, 50, 200);

		Gdx.input.setInputProcessor(this.inputDetector);

		this.inputDetector.getDoubleKeyObservable().addObserver(inputDetectorOnDoubleKey);
		this.inputDetector.getDoubleTapObservable().addObserver(inputDetectorOnDoubleTap);
	}

	private void processKeyboard() {
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			this.gameModel.movePigLeft();
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			this.gameModel.movePigRight();
		}
	}

	private void processTouch() {
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			Vector3 worldPos = this.camera.unproject(touchPos);

			if (this.pigBody.getPosition().x > worldPos.x) {
				this.gameModel.movePigLeft();
			} else if (this.pigBody.getPosition().x < worldPos.x) {
				this.gameModel.movePigRight();
			}
		}
	}

	public void processUserInputOnRender() {
		this.processTouch();
		this.processKeyboard();
	}
}
