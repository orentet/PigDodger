package com.pigdodger.modes.game.model;

import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;

// a time based scoreKeeper
public class ScoreBoardModel {

	String highestScoreFileName = "highest_score.txt";

	int highestScore;

	int currentScore = 0;
	long gameStartTimeMillis;

	GameModel gameModel;

	Boolean shouldKeepScore;

	private Observer gameModelOnPigDeath = new Observer() {
		@Override
		public void update(Observable arg0, Object arg1) {
			shouldKeepScore = false;

			// save high score
			Gdx.files.local(highestScoreFileName).writeString(Integer.toString(highestScore), false);
		}
	};

	private Observer gameModelOnWorldCreated = new Observer() {
		@Override
		public void update(Observable arg0, Object arg1) {
			currentScore = 0;
			gameStartTimeMillis = TimeUtils.millis();
			shouldKeepScore = true;
		}
	};

	public ScoreBoardModel(GameModel gameModel) {
		this.gameModel = gameModel;
		gameModel.getPigDeathObservable().addObserver(gameModelOnPigDeath);
		gameModel.getWorldCreatedObservable().addObserver(gameModelOnWorldCreated);

		this.readHighestScore();
	}

	public int getCurrentScore() {
		return this.currentScore;
	}

	public int getHighestScore() {
		return this.highestScore;
	}

	private void readHighestScore() {
		FileHandle highScoreFile = Gdx.files.local(highestScoreFileName);
		if (highScoreFile.exists()) {
			this.highestScore = Integer.parseInt(highScoreFile.readString());
		} else {
			this.highestScore = 0;
		}
	}

	public void update() {
		if (this.shouldKeepScore) {
			this.currentScore = (int) ((TimeUtils.millis() - this.gameStartTimeMillis) / 120);
		}

		if (this.currentScore > this.highestScore) {
			this.highestScore = this.currentScore;
		}
	}
}
