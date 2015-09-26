package com.pigdodger.modes.game.model;

import java.util.ArrayList;
import java.util.Observable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.pigdodger.misc.Pair;
import com.pigdodger.misc.SimpleObservable;
import com.pigdodger.misc.Utils;
import com.pigdodger.modes.game.model.entities.Ground;
import com.pigdodger.modes.game.model.entities.Pig;
import com.pigdodger.modes.game.model.entities.Rock;

public class GameModel implements ContactFilter, ContactListener, AutoCloseable {
	Vector2 gravityVector = new Vector2(0, -9.8f);
	Ground ground;
	float moveForce = 15f;
	long rockCreationTimeDelta = 2500;
	long nextRockCreationTime = TimeUtils.millis() + this.rockCreationTimeDelta;
	Pig pig = null;
	float pigHeight = 0.7695f; // pig sprite height/width
	float pigWidth = 1f;
	ArrayList<Rock> rocks = new ArrayList<Rock>();
	World world;
	int worldHeight;
	int worldWidth;

	long lastDodgeMilli = TimeUtils.millis();
	int allowedDodgeTimeDiffMilli = 1000;

	private SimpleObservable rockHittingGroundObservable = new SimpleObservable();
	private SimpleObservable entityCreationObservable = new SimpleObservable();
	private SimpleObservable entityDestructionObservable = new SimpleObservable();
	private SimpleObservable pigDodgeObservable = new SimpleObservable();
	private SimpleObservable pigMovementObservable = new SimpleObservable();
	private SimpleObservable pigDeathObservable = new SimpleObservable();
	private SimpleObservable worldCreatedObservable = new SimpleObservable();

	// does not create bodies.
	public GameModel(int worldWidth, int worldHeight) {
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	private void applyPigConstraints() {
		// prevent pig from flying off screen
		Body pigBody = this.pig.getBody();
		if ((pigBody.getPosition().x + (this.pigWidth / 2)) >= this.worldWidth) {
			pigBody.setTransform(this.worldWidth - (this.pigWidth / 2), pigBody.getPosition().y, 0);
			pigBody.setLinearVelocity(0, 0);
		} else if ((pigBody.getPosition().x - (this.pigWidth / 2)) <= 0) {
			pigBody.setTransform(this.pigWidth / 2, pigBody.getPosition().y, 0);
			pigBody.setLinearVelocity(0, 0);
		}
	}

	@Override
	public void beginContact(Contact contact) {
		ModelEntity model1 = (ModelEntity) contact.getFixtureA().getBody().getUserData();
		ModelEntity model2 = (ModelEntity) contact.getFixtureB().getBody().getUserData();

		Pig pig = null;
		Rock rock = null;
		Ground ground = null;

		if (model1.getClass() == Pig.class) {
			pig = (Pig) model1;
		} else if (model2.getClass() == Pig.class) {
			pig = (Pig) model2;
		}

		if (model1.getClass() == Rock.class) {
			rock = (Rock) model1;
		} else if (model2.getClass() == Rock.class) {
			rock = (Rock) model2;
		}

		if (model1.getClass() == Ground.class) {
			ground = (Ground) model1;
		} else if (model2.getClass() == Ground.class) {
			ground = (Ground) model2;
		}

		// rock and pig are colliding
		if ((pig != null) && (rock != null) && (pig.isDead() == false)) {
			// kill pig and give it a little bump
			pig.getBody().applyForceToCenter(0, 100f, true);
			pig.setDead(true);
			this.pigDeathObservable.changed();
		}
		if ((rock != null) && (ground != null)) {
			this.rockHittingGroundObservable.changed(rock);
		}
	}

	@Override
	public void close() throws Exception {
		this.world.dispose();
	}

	private void createGround() {
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vector2(this.worldWidth / 2, 0));
		Body body = this.world.createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(this.worldWidth / 2, 0.04f);

		this.ground = new Ground(body);
		body.setUserData(this.ground);

		body.createFixture(groundBox, 0.0f);

		groundBox.dispose();

		this.entityCreationObservable.changed(this.ground);
	}

	private void createPig() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(this.worldWidth / 2, this.worldHeight / 4);
		Body body = this.world.createBody(bodyDef);
		PolygonShape pigBox = new PolygonShape();
		pigBox.setAsBox(this.pigWidth / 2, this.pigHeight / 2);

		this.pig = new Pig(body, false, this.pigWidth, this.pigHeight);
		body.setUserData(this.pig);

		Fixture fixture = body.createFixture(pigBox, 0.0f);
		fixture.setFriction(1f);

		// create sensor fixture to detect collisions
		Fixture sensorFixture = body.createFixture(pigBox, 0.0f);
		sensorFixture.setSensor(true);

		pigBox.dispose();

		this.entityCreationObservable.changed(this.pig);
	}

	private void createRock() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(-1, Utils.random(1.5f, this.worldHeight));
		Body body = this.world.createBody(bodyDef);
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(Utils.random(0.4f, 0.5f));

		Rock rock = new Rock(body);
		body.setUserData(rock);

		Fixture fixture = body.createFixture(circleShape, 1f);
		fixture.setRestitution(1f); // bouncy

		this.rocks.add(rock);

		// give initial push
		body.applyForceToCenter(160f, 100f, true);
		body.applyTorque(2f, true);

		circleShape.dispose();

		this.entityCreationObservable.changed(rock);
	}

	private void createRocks() {
		if (this.nextRockCreationTime > TimeUtils.millis()) {
			return;
		}

		this.createRock();

		this.rockCreationTimeDelta -= Utils.random(0, 100);
		this.nextRockCreationTime = TimeUtils.millis() + this.rockCreationTimeDelta;
	}

	// this starts the game
	public void createWorld(boolean withPig) {
		this.world = new World(this.gravityVector, true);
		this.world.setContactFilter(this);
		this.world.setContactListener(this);

		this.createGround();
		if (withPig) {
			this.createPig();
		}

		this.worldCreatedObservable.changed();
	}

	private void deleteUnseenRocks() {
		ArrayList<Rock> deleteList = new ArrayList<Rock>();

		for (Rock rock : this.rocks) {
			Body rockBody = rock.getBody();
			// delete the rock if it goes below 0
			if (rockBody.getPosition().y < 0) {
				deleteList.add(rock);
			}
		}

		// delete from rocks list and world
		for (Rock rock : deleteList) {
			this.rocks.remove(rock);
			this.world.destroyBody(rock.getBody());

			this.entityDestructionObservable.changed(rock);
		}
	}

	public void dispose() {
		this.world.dispose();
	}

	public void dodgePigLeft() {
		if (!this.isDodgeAllowed()) {
			return;
		}

		this.movePig(-this.moveForce * 12, this.moveForce * 5);

		this.lastDodgeMilli = TimeUtils.millis();

		this.pigDodgeObservable.changed();
	}

	public void dodgePigRight() {
		if (!this.isDodgeAllowed()) {
			return;
		}

		this.movePig(this.moveForce * 12, this.moveForce * 5);

		this.lastDodgeMilli = TimeUtils.millis();

		this.pigDodgeObservable.changed();
	}

	@Override
	public void endContact(Contact contact) {
	}

	public Observable getEntityCreationObservable() {
		return this.entityCreationObservable;
	}

	public Observable getEntityDestructionObservable() {
		return this.entityDestructionObservable;
	}

	public Observable getPigDeathObservable() {
		return this.pigDeathObservable;
	}

	public Observable getPigDodgeObservable() {
		return this.pigDodgeObservable;
	}

	public Observable getPigMovementObservable() {
		return this.pigMovementObservable;
	}

	public Observable getRockHittingGroundObservable() {
		return this.rockHittingGroundObservable;
	}

	public World getWorld() {
		return this.world;
	}

	public Observable getWorldCreatedObservable() {
		return this.worldCreatedObservable;
	}

	public int getWorldHeight() {
		return this.worldHeight;
	}

	public int getWorldWidth() {
		return this.worldWidth;
	}

	private boolean isDodgeAllowed() {
		if (this.pig.isDead()) {
			return false;
		}

		if ((TimeUtils.millis() - this.lastDodgeMilli) <= this.allowedDodgeTimeDiffMilli) {
			return false;
		}

		return true;
	}

	private void movePig(float xForce, float yForce) {
		Body pigBody = this.pig.getBody();
		pigBody.applyForceToCenter(xForce, yForce, true);
	}

	public void movePigLeft() {
		if (this.pig.isDead()) {
			return;
		}

		this.movePig(-this.moveForce, 0);
	}

	public void movePigRight() {
		if (this.pig.isDead()) {
			return;
		}

		this.movePig(this.moveForce, 0);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		if (fixtureA.isSensor() || fixtureB.isSensor()) {
			return true;
		}

		// everything collide with each-other except a few
		if (this.shouldCollide((ModelEntity) fixtureA.getBody().getUserData(), (ModelEntity) fixtureB.getBody().getUserData())
				&& this.shouldCollide((ModelEntity) fixtureB.getBody().getUserData(), (ModelEntity) fixtureA.getBody().getUserData())) {
			return true;
		} else {
			return false;
		}
	}

	// will only specify non-collisions
	private boolean shouldCollide(ModelEntity model1, ModelEntity model2) {
		if (model1.getClass() == Rock.class) {
			if (model2.getClass() == Pig.class) {
				return false;
			} else if (model2.getClass() == Rock.class) {
				return false;
			}
		}

		return true;
	}

	public void update() {
		Vector2 lastPigPosition = null;
		if (this.pig != null) {
			lastPigPosition = this.pig.getBody().getPosition().cpy();
		}

		this.worldStep();

		if (lastPigPosition != null) {
			Vector2 newPigPosition = this.pig.getBody().getPosition();
			if (!lastPigPosition.equals(newPigPosition)) {
				this.pigMovementObservable.changed(new Pair<Vector2, Vector2>(lastPigPosition, newPigPosition));
			}
		}
	}

	private void worldStep() {
		this.world.step(1 / 60f, 6, 2);

		if (this.pig != null) {
			this.applyPigConstraints();
		}
		this.createRocks();
		this.deleteUnseenRocks();
	}
}
