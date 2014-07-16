package com.project.magneto;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Main extends ApplicationAdapter {

	private static final float PLANE_START_X = 240;
	private static final float PLANE_START_Y = 200;
	private static final float GRAVITY = -50;
	
	ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	OrthographicCamera camera;
	OrthographicCamera uiCamera;
	Texture background;
	TextureRegion leftWall;
	TextureRegion rightWall;
	float groundOffsetX = 0;
	TextureRegion ready;
	TextureRegion gameOver;
	Animation plane;
	
	static enum Orientation {
		Left, Right
	}

	Orientation orientation = Orientation.Right;
	
	//GameState gameState = GameState.Start;

	Vector2 planePosition = new Vector2();
	Vector2 planeVelocity = new Vector2();
	Vector2 gravity = new Vector2();
	
	float planeStateTime = 0;
	
	int score = 0;
	Rectangle rect1 = new Rectangle();
	Rectangle rect2 = new Rectangle();

	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.update();

		background = new Texture("background.png");	
		leftWall = new TextureRegion(new Texture("Left_wall.png"));
		rightWall = new TextureRegion(new Texture("Right_wall.png"));

		Sprite frame1 = new Sprite(new Texture("plane1.png"));
		Sprite frame2 = new Sprite(new Texture("plane2.png"));
		Sprite frame3 = new Sprite(new Texture("plane3.png"));
		
		
		plane = new Animation(0.05f, frame1, frame2, frame3, frame2);
		plane.setPlayMode(PlayMode.LOOP);
		
		planeVelocity.set(100, 0);
                System.out.println(planeVelocity);
		System.out.println(planePosition);
                planePosition.mulAdd(planeVelocity, 0.016f);
                System.out.println(planePosition);
		resetWorld();
	}

	private void resetWorld() {
		score = 0;
		groundOffsetX = 0;
		camera.position.y = 400;
		planePosition.set(PLANE_START_X, PLANE_START_Y);
		planeVelocity.set(0, 100);
		gravity.set(GRAVITY, 0);
	}
	
	private void updateWorld() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		planeStateTime += deltaTime;
		
		if (orientation == Orientation.Left) {
			gravity.set(GRAVITY, 0);
		}
		else {
			gravity.set(-1 * GRAVITY, 0);
		}
		
		planeVelocity.add(gravity);
		
		
		if (Gdx.input.justTouched()) {
			orientation = (orientation == Orientation.Left) ? Orientation.Right : Orientation.Left; 
		}
		
		
		if ((planePosition.x <= leftWall.getRegionWidth()) && planeVelocity.x < 0) {
			planeVelocity.x = 0;
		}
		
		if ((planePosition.x + plane.getKeyFrame(0).getRegionWidth() >= 480 - rightWall.getRegionWidth()) &&
				planeVelocity.x > 0) {
			planeVelocity.x = 0;
		}
		
		planePosition.mulAdd(planeVelocity, deltaTime);

		camera.position.y = planePosition.y + 200;		
		if(camera.position.y - groundOffsetX > leftWall.getRegionHeight() + 400) {
			groundOffsetX += leftWall.getRegionHeight();
		}

		//rect1.set(planePosition.x + 20, planePosition.y, plane.getKeyFrames()[0].getRegionWidth() - 20, plane.getKeyFrames()[0].getRegionHeight());
	}

	
	private void drawWorld() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(background, 0, camera.position.y - background.getHeight() / 2);
		batch.draw(leftWall, 0, groundOffsetX);
		batch.draw(leftWall, 0, groundOffsetX + leftWall.getRegionHeight());
		batch.draw(rightWall, 480 - rightWall.getRegionWidth(), groundOffsetX);
		batch.draw(rightWall, 480 - rightWall.getRegionWidth(), groundOffsetX + rightWall.getRegionHeight());
		batch.draw(plane.getKeyFrame(planeStateTime), planePosition.x, planePosition.y);
		batch.end();
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateWorld();
		drawWorld();
	}
}
