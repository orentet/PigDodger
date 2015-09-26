package com.pigdodger.modes.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.pigdodger.misc.Utils;
import com.pigdodger.modes.game.model.entities.Pig;

public class PigEntityRenderer implements BatchRenderer {

	enum Direction {
		RIGHT, LEFT
	}

	Pig pig;

	OrthographicCamera camera;

	private static final int WALKING_FRAME_COLS = 4;
	private static final int WALKING_FRAME_ROWS = 1;
	Animation walkAnimation;
	Texture walkSheet;
	TextureRegion[] walkFrames;
	float walkingStateTime;

	private static final int BREATHING_FRAME_COLS = 3;
	private static final int BREATHING_FRAME_ROWS = 1;
	Animation breathingAnimation;
	Texture breathingSheet;
	TextureRegion[] breathingFrames;
	float breathingStateTime;

	private static final int DEATH_FRAME_COLS = 5;
	private static final int DEATH_FRAME_ROWS = 1;
	Animation deathAnimation;
	Texture deathSheet;
	TextureRegion[] deathFrames;
	float deathStateTime;

	Direction currentDirection;

	public PigEntityRenderer(Pig pig, OrthographicCamera camera) {
		this.pig = pig;

		this.camera = camera;

		this.initWalkAnimation();
		this.initBreathingAnimation();
		this.initDeathAnimation();

		this.currentDirection = Direction.RIGHT; // all textures are pointing
													// right
	}

	private void initBreathingAnimation() {
		this.breathingSheet = new Texture(Gdx.files.internal("breathing_pig_sheet.png"), true);
		this.breathingSheet.setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		TextureRegion[][] tmp = TextureRegion.split(this.breathingSheet, this.breathingSheet.getWidth() / BREATHING_FRAME_COLS, this.breathingSheet.getHeight()
				/ BREATHING_FRAME_ROWS);
		this.breathingFrames = new TextureRegion[BREATHING_FRAME_COLS * BREATHING_FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < BREATHING_FRAME_ROWS; i++) {
			for (int j = 0; j < BREATHING_FRAME_COLS; j++) {
				this.breathingFrames[index++] = tmp[i][j];
			}
		}
		this.breathingAnimation = new Animation(0.2f, this.breathingFrames);
		this.breathingAnimation.setPlayMode(PlayMode.LOOP_PINGPONG);

		this.breathingStateTime = 0;
	}

	private void initDeathAnimation() {
		this.deathSheet = new Texture(Gdx.files.internal("death_pig_sheet.png"), true);
		this.deathSheet.setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		TextureRegion[][] tmp = TextureRegion.split(this.deathSheet, this.deathSheet.getWidth() / DEATH_FRAME_COLS, this.deathSheet.getHeight()
				/ DEATH_FRAME_ROWS);
		this.deathFrames = new TextureRegion[DEATH_FRAME_COLS * DEATH_FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < DEATH_FRAME_ROWS; i++) {
			for (int j = 0; j < DEATH_FRAME_COLS; j++) {
				this.deathFrames[index++] = tmp[i][j];
			}
		}
		this.deathAnimation = new Animation(0.1f, this.deathFrames);

		this.deathStateTime = 0;
	}

	private void initWalkAnimation() {
		this.walkSheet = new Texture(Gdx.files.internal("walking_pig_sheet.png"), true);
		this.walkSheet.setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		TextureRegion[][] tmp = TextureRegion.split(this.walkSheet, this.walkSheet.getWidth() / WALKING_FRAME_COLS, this.walkSheet.getHeight()
				/ WALKING_FRAME_ROWS);
		this.walkFrames = new TextureRegion[WALKING_FRAME_COLS * WALKING_FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < WALKING_FRAME_ROWS; i++) {
			for (int j = 0; j < WALKING_FRAME_COLS; j++) {
				this.walkFrames[index++] = tmp[i][j];
			}
		}
		this.walkAnimation = new Animation(10f, this.walkFrames);
		this.walkAnimation.setPlayMode(PlayMode.LOOP_PINGPONG);

		this.walkingStateTime = 0;
	}

	@Override
	public void render(SpriteBatch batch) {
		this.setPigCurrentDirection();

		float currentAbsSpeed = Utils.abs(this.pig.getBody().getLinearVelocity().x);

		TextureRegion currentFrame;
		// set current frame

		if (!this.pig.isDead()) {
			if (currentAbsSpeed > 0) {
				this.walkingStateTime += currentAbsSpeed;
				currentFrame = this.walkAnimation.getKeyFrame(this.walkingStateTime, true);
			} else {
				this.breathingStateTime += Gdx.graphics.getDeltaTime();
				currentFrame = this.breathingAnimation.getKeyFrame(this.breathingStateTime, true);
			}
		} else {
			// pig is now dead, show death animation
			this.deathStateTime += Gdx.graphics.getDeltaTime();
			if (this.deathStateTime <= this.deathAnimation.getAnimationDuration()) {
				currentFrame = this.deathAnimation.getKeyFrame(this.deathStateTime, true);
			} else {
				// we want to show the last image - death scene
				currentFrame = this.deathAnimation.getKeyFrame(this.deathAnimation.getAnimationDuration());
			}
		}

		// set frame direction
		if (this.currentDirection == Direction.LEFT) {
			if (!currentFrame.isFlipX()) {
				currentFrame.flip(true, false);
			}
		} else {
			if (currentFrame.isFlipX()) {
				currentFrame.flip(true, false);
			}
		}

		Vector3 screenPos = this.camera.project(new Vector3(this.pig.getBody().getPosition().x, this.pig.getBody().getPosition().y, 0));
		float screenPigHeight = this.camera.project(new Vector3(this.pig.getHeight(), 0, 0)).x;
		float screenPigWidth = this.camera.project(new Vector3(this.pig.getWidth(), 0, 0)).x;
		batch.draw(currentFrame, screenPos.x - (screenPigWidth / 2), screenPos.y - (screenPigHeight / 2), screenPigWidth, screenPigHeight);
	}

	private void setPigCurrentDirection() {
		// check if pig is moving left or right, if its moving left, we need to
		// flip (because texture is right)
		if ((this.pig.getBody().getLinearVelocity().x < 0) && (this.currentDirection == Direction.RIGHT)) {
			this.currentDirection = Direction.LEFT;
		} else if ((this.pig.getBody().getLinearVelocity().x > 0) && (this.currentDirection == Direction.LEFT)) {
			this.currentDirection = Direction.RIGHT;
		}

		// if velocity == 0 dont set the direction again, to keep texture on the
		// same direction
	}

}
