package com.pigdodger.modes.game.model;

import com.badlogic.gdx.physics.box2d.Body;

public class ModelEntity {
	protected Body body;

	public ModelEntity(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return this.body;
	}
}
