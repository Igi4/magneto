package com.project.magneto;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Main extends ApplicationAdapter {

	private static final float PLANE_START_Y = 240;
	private static final float PLANE_START_X = 50;
	
	ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	OrthographicCamera camera;
	OrthographicCamera uiCamera;
	Texture background;
	TextureRegion ground;
	float groundOffsetX = 0;
	TextureRegion ceiling;
	TextureRegion ready;
	TextureRegion gameOver;

	//GameState gameState = GameState.Start;

	Vector2 planePosition = new Vector2();
	Vector2 planeVelocity = new Vector2();
	float planeStateTime = 0;
	
	int score = 0;
	Rectangle rect1 = new Rectangle();
	Rectangle rect2 = new Rectangle();

	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.update();

		background = new Texture("background.png");	
		ground = new TextureRegion(new Texture("ground.png"));
		ceiling = new TextureRegion(ground);
		ceiling.flip(true, true);

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
		camera.position.x = 400;
		planePosition.set(PLANE_START_X, PLANE_START_Y);
		planeVelocity.set(100, 0);
	}
	
	private void updateWorld() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		planeStateTime += deltaTime;

		planePosition.mulAdd(planeVelocity, deltaTime);

		camera.position.x = planePosition.x + 350;		
		if(camera.position.x - groundOffsetX > ground.getRegionWidth() + 400) {
			groundOffsetX += ground.getRegionWidth();
		}

		//rect1.set(planePosition.x + 20, planePosition.y, plane.getKeyFrames()[0].getRegionWidth() - 20, plane.getKeyFrames()[0].getRegionHeight());
	}

	
	private void drawWorld() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(background, camera.position.x - background.getWidth() / 2, 0);
		batch.draw(ground, groundOffsetX, 0);
		batch.draw(ground, groundOffsetX + ground.getRegionWidth(), 0);
		batch.draw(ceiling, groundOffsetX, 480 - ceiling.getRegionHeight());
		batch.draw(ceiling, groundOffsetX + ceiling.getRegionWidth(), 480 - ceiling.getRegionHeight());
		//batch.draw(plane.getKeyFrame(planeStateTime), planePosition.x, planePosition.y);
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
