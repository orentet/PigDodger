package com.pigdodger.misc;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class Utils {
	static Random randomGen = new Random();

	public static float abs(float val) {
		if (val >= 0) {
			return val;
		} else {
			return -val;
		}
	}

	public static Fixture findNonSensorFixture(Array<Fixture> fixtures) {
		for (Fixture fixture : fixtures) {
			if (!fixture.isSensor()) {
				return fixture;
			}
		}
		return null;
	}

	public static Drawable getDrawableFromAsset(String asset) {
		return new TextureRegionDrawable(new TextureRegion(new Texture(asset)));
	}

	public static final float random(final float pMin, final float pMax) {
		return pMin + (randomGen.nextFloat() * (pMax - pMin));
	}

	public static int random(int min, int max) {
		return randomGen.nextInt((max - min) + 1) + min;
	}
}
