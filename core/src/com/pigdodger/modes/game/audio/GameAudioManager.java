package com.pigdodger.modes.game.audio;

import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.pigdodger.misc.Pair;
import com.pigdodger.modes.game.model.GameModel;

public class GameAudioManager implements AutoCloseable {
	GameModel gameModel;
	Sound deathSound;
	private Sound rockHittingGroundSound;
	private Sound dodgeSound;
	private Sound stepSound;

	float currentFootStepDiff;
	float positionDiffForFootStep = 0.8f;
	private Music gameMusic;
	private Observer gameModelOnRockHittingGround = new Observer() {
		@Override
		public void update(Observable arg0, Object arg1) {
			rockHittingGroundSound.play();
		}
	};
	private Observer gameModelOnPigDodge = new Observer() {
		@Override
		public void update(Observable arg0, Object arg1) {
			dodgeSound.play();
		}
	};
	private Observer gameModelOnPigMovement = new Observer() {
		@Override
		public void update(Observable arg0, Object obj) {
			@SuppressWarnings("unchecked")
			Pair<Vector2, Vector2> oldAndNewPositions = (Pair<Vector2, Vector2>) obj;
			Vector2 oldPosition = oldAndNewPositions.item1;
			Vector2 newPosition = oldAndNewPositions.item2;

			currentFootStepDiff += newPosition.dst(oldPosition);

			if (currentFootStepDiff >= positionDiffForFootStep) {
				stepSound.play(1f);

				currentFootStepDiff = 0;
			}
		}
	};
	private Observer gameModelOnPigDeath = new Observer() {
		@Override
		public void update(Observable arg0, Object arg1) {
			deathSound.play();
		}
	};

	public GameAudioManager(GameModel gameModel) {
		this.gameModel = gameModel;
		gameModel.getRockHittingGroundObservable().addObserver(gameModelOnRockHittingGround);
		gameModel.getPigDodgeObservable().addObserver(gameModelOnPigDodge);
		gameModel.getPigMovementObservable().addObserver(gameModelOnPigMovement);
		gameModel.getPigDeathObservable().addObserver(gameModelOnPigDeath);

		this.gameMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		this.gameMusic.setLooping(true);
		this.gameMusic.setVolume(0.2f);
		this.rockHittingGroundSound = Gdx.audio.newSound(Gdx.files.internal("rock_hitting_ground.wav"));
		this.deathSound = Gdx.audio.newSound(Gdx.files.internal("death.wav"));
		this.dodgeSound = Gdx.audio.newSound(Gdx.files.internal("dodge.wav"));
		this.stepSound = Gdx.audio.newSound(Gdx.files.internal("step.wav"));
	}

	@Override
	public void close() throws Exception {
		this.stopGameMusic();
		this.deathSound.dispose();
		this.rockHittingGroundSound.dispose();
		this.dodgeSound.dispose();
		this.stepSound.dispose();
	}

	public void startGameMusic() {
		this.gameMusic.play();
	}

	public void stopGameMusic() {
		this.gameMusic.stop();
	}
}
