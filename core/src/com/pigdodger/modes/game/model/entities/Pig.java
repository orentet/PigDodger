package com.pigdodger.modes.game.model.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.pigdodger.modes.game.model.ModelEntity;

public class Pig extends ModelEntity {
	boolean isDead;
	float width;
	float height;

	public Pig(Body body, boolean isDead, float width, float height) {
		super(body);

		this.isDead = isDead;
		this.width = width;
		this.height = height;
	}

	public float getHeight() {
		return this.height;
	}

	public float getWidth() {
		return this.width;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
}
