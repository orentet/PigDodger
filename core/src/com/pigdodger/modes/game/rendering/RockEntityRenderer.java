package com.pigdodger.modes.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.pigdodger.misc.Utils;
import com.pigdodger.modes.game.model.entities.Rock;

public class RockEntityRenderer implements BatchRenderer {
	Rock rock;
	OrthographicCamera camera;

	Texture texture;
	Sprite sprite;

	int rockRadiusPx;

	public RockEntityRenderer(Rock entity, OrthographicCamera camera) {
		this.rock = entity;
		this.camera = camera;
		this.texture = new Texture(Gdx.files.internal("rock.png"), true);
		this.texture.setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		this.sprite = new Sprite(this.texture);

		// set rock width and height
		Body rockBody = this.rock.getBody();
		Fixture rockFix = Utils.findNonSensorFixture(rockBody.getFixtureList());
		this.rockRadiusPx = (int) camera.project(new Vector3(rockFix.getShape().getRadius(), 0, 0)).x;
		this.sprite.setSize(this.rockRadiusPx * 2, this.rockRadiusPx * 2);
		this.sprite.setOrigin(this.rockRadiusPx, this.rockRadiusPx);
	}

	public Rock getRock() {
		return this.rock;
	}

	@Override
	public void render(SpriteBatch batch) {
		Vector3 screenPos = this.camera.project(new Vector3(this.rock.getBody().getPosition().x, this.rock.getBody().getPosition().y, 0));
		// screenpos is middle of rock
		this.sprite.setPosition(screenPos.x - this.rockRadiusPx, screenPos.y - this.rockRadiusPx);
		this.sprite.setRotation(MathUtils.radiansToDegrees * this.rock.getBody().getAngle());
		this.sprite.draw(batch);
	}
}
