package com.pigdodger.modes.game.userinterface;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.pigdodger.misc.SimpleObservable;

public class InputDetector implements InputProcessor {

	private SimpleObservable doubleTapObservable;
	private SimpleObservable doubleKeyObservable;

	long doubleTapDetectionDeltaMillis;
	float doubleTapRadiusTolerance;
	long doubleKeyDetectionDeltaMillis;

	int lastKeyDown;
	long lastKeyDownMillis;

	Vector2 lastTapPosition;
	long lastTapDownMillis;

	InputDetector(long doubleTapDetectionDeltaMillis, float doubleTapRadiusTolerance, long doubleKeyDetectionDeltaMillis) {
		this.doubleTapObservable = new SimpleObservable();
		this.doubleKeyObservable = new SimpleObservable();

		this.doubleTapDetectionDeltaMillis = doubleTapDetectionDeltaMillis;
		this.doubleTapRadiusTolerance = doubleTapRadiusTolerance;
		this.doubleKeyDetectionDeltaMillis = doubleKeyDetectionDeltaMillis;

		this.lastKeyDown = 0;
		this.lastTapPosition = new Vector2();
	}

	public SimpleObservable getDoubleKeyObservable() {
		return this.doubleKeyObservable;
	}

	public SimpleObservable getDoubleTapObservable() {
		return this.doubleTapObservable;
	}

	@Override
	public boolean keyDown(int keycode) {
		long nowMillis = TimeUtils.millis();

		if ((this.lastKeyDown == keycode) && ((nowMillis - this.lastKeyDownMillis) <= this.doubleKeyDetectionDeltaMillis)) {
			this.doubleKeyObservable.changed(keycode);
		}

		this.lastKeyDown = keycode;
		this.lastKeyDownMillis = nowMillis;

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		long nowMillis = TimeUtils.millis();
		Vector2 tapPosition = new Vector2(screenX, screenY);

		float lengthBetweenTapLocation = Vector2.dst(tapPosition.x, tapPosition.y, this.lastTapPosition.x, this.lastTapPosition.y);

		if (((nowMillis - this.lastTapDownMillis) <= this.doubleKeyDetectionDeltaMillis) && (lengthBetweenTapLocation <= this.doubleTapRadiusTolerance)) {
			this.doubleTapObservable.changed(tapPosition);
		}

		this.lastTapPosition = tapPosition;
		this.lastTapDownMillis = nowMillis;

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

}
