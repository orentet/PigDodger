package com.pigdodger.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;

public class ModesManager {
	Array<Mode> currentModes = new Array<Mode>();
	Array<Mode> nextModes = new Array<Mode>();

	public ModesManager(Array<Mode> initialModeList) {
		this.replaceModes(initialModeList);
	}

	public void addMode(Mode mode) {
		try {
			mode.start();
		} catch (Exception e) {
			Gdx.app.log(getClass().toString(), e.toString());
		}
		this.currentModes.add(mode);
	}

	public void render() {
		if (this.nextModes.size > 0) {
			this.replaceCurrentModes();
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		for (Mode mode : this.currentModes) {
			mode.render();
		}
	}

	private void replaceCurrentModes() {
		// close all current modes
		for (Mode mode : this.currentModes) {
			try {
				mode.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.currentModes.clear();

		// move nextModes to currentModes
		this.currentModes.addAll(this.nextModes);
		this.nextModes.clear();

		// start the new modes
		for (Mode mode : this.currentModes) {
			try {
				mode.start();
			} catch (Exception e) {
				Gdx.app.log(getClass().toString(), e.toString());
			}
		}
	}

	// this will happen in the next render
	public void replaceModes(Array<Mode> newModes) {
		this.nextModes = newModes;
	}
}
