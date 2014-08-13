package com.project.magneto;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.text.ChangedCharSetException;

import javafx.scene.layout.Background;

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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Main extends ApplicationAdapter {

	private static final float MAGNETO_START_X = 240;
	private static final float MAGNETO_START_Y = 200;
	private static final float BACKGROUND_X = 0;
	private static final float BACKGROUND_Y = 0;
	private static final float GRAVITY = -50;
	
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	SpriteBatch batch;
	OrthographicCamera uiCamera;
	Texture background;
	Texture secondaryBackground;
	TextureRegion leftWall;
	TextureRegion secondaryLeftWall;
	TextureRegion rightWall;
	TextureRegion secondaryRightWall;
	float groundOffsetX = 0;
	TextureRegion ready;
	TextureRegion gameOver;
	Animation magnetoRight;
	Animation magnetoLeft;
	Animation rotation;
	Rectangle magnetoHitBox = new Rectangle();
	Rectangle jumpRect = new Rectangle();
	BitmapFont font;
	BitmapFont font2;
	BitmapFont font3;
	int score = 0;
	int highScore = 0;
	boolean onGround = false;
	float rotationOffset = 108;    //150
	float rotationLength = 264;    //180
	
	
//	Rectangle cameraHitBox = new Rectangle();
	
	Deque<Obstacle> obstacles = new LinkedList<Obstacle>();
	
	static enum Orientation {
		Left, Right
	}

	Orientation orientation = Orientation.Right;
	
	//GameState gameState = GameState.Start;

	
	Vector2 backgroundPosition = new Vector2();
	Vector2 backgroundVelocity = new Vector2();
	Vector2 magnetoPosition = new Vector2();
	Vector2 magnetoVelocity = new Vector2();
	Vector2 gravity = new Vector2();
	
	float magnetoStateTime = 0;
	float rotationStateTime = 0;
	
//	Rectangle rect1 = new Rectangle();
//	Rectangle rect2 = new Rectangle();

	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.update();

		font = new BitmapFont(Gdx.files.internal("arial.fnt"));
		font.setColor(0, 1, 0, 1);
		font.setScale(0.5f, 1);
		
		font2 = new BitmapFont(Gdx.files.internal("arial.fnt"));
		font2.setColor(1, 1, 0, 1);
		font2.setScale(0.5f, 1);

		font3 = new BitmapFont(Gdx.files.internal("arial.fnt"));
		font3.setColor(0, 1, 0, 1);
		font3.setScale(0.3f, 0.3f);
		
		background = new Texture("background.png");	
		secondaryBackground = new Texture("background.png");	
		leftWall = new TextureRegion(new Texture("left_wall.png"));
		secondaryLeftWall = new TextureRegion(new Texture("left_wall.png"));
		rightWall = new TextureRegion(new Texture("right_wall.png"));
		secondaryRightWall = new TextureRegion(new Texture("right_wall.png"));

		TextureRegion rotationFrame1 = new TextureRegion(new Texture("rotate1.png"));
		TextureRegion rotationFrame2 = new TextureRegion(new Texture("rotate2.png"));
		TextureRegion rotationFrame3 = new TextureRegion(new Texture("rotate3.png"));
		TextureRegion rotationFrame4 = new TextureRegion(new Texture("rotate4.png"));
		TextureRegion rotationFrame5 = new TextureRegion(new Texture("rotate5.png"));
		
		rotation = new Animation(rotationLength / 5.0f / 1000.0f, rotationFrame1, rotationFrame2, rotationFrame3, rotationFrame4, rotationFrame5);
		rotation.setPlayMode(PlayMode.NORMAL);
		
		TextureRegion blueFrame1 = new TextureRegion(new Texture("magneto_blue1.png"));
		TextureRegion blueFrame2 = new TextureRegion(new Texture("magneto_blue2.png"));
		TextureRegion blueFrame3 = new TextureRegion(new Texture("magneto_blue3.png"));
		
		
		magnetoRight = new Animation(0.1f, blueFrame1, blueFrame2, blueFrame3, blueFrame2);
		magnetoRight.setPlayMode(PlayMode.LOOP);

		
		TextureRegion redFrame1 = new TextureRegion(new Texture("magneto_red1.png"));
		TextureRegion redFrame2 = new TextureRegion(new Texture("magneto_red2.png"));
		TextureRegion redFrame3 = new TextureRegion(new Texture("magneto_red3.png"));
		
		
		magnetoLeft = new Animation(0.1f, redFrame1, redFrame2, redFrame3, redFrame2);
		magnetoLeft.setPlayMode(PlayMode.LOOP);
		
		
		magnetoVelocity.set(100, 0);
        magnetoPosition.mulAdd(magnetoVelocity, 0.016f);
		resetWorld();
	}

	private void resetWorld() {
		score = 0;
		highScore = 0;
		groundOffsetX = 0;
		camera.position.y = 400;
		magnetoPosition.set(MAGNETO_START_X, MAGNETO_START_Y);
		magnetoVelocity.set(0, 300);
		
		backgroundPosition.set(BACKGROUND_X, BACKGROUND_Y);
		backgroundVelocity.set(0, 150);
		
		gravity.set(GRAVITY, 0);
		obstacles.clear();
	
		for (int i = 1; i <= 5; i++) {
			addObstacle();
		}
		
	}
	
	private void updateWorld() {		
		float deltaTime = Gdx.graphics.getDeltaTime();
		magnetoStateTime += deltaTime;
		

		if (orientation == Orientation.Left) {
			gravity.set(GRAVITY, 0);
		}
		else {
			gravity.set(-1 * GRAVITY, 0);
		}
		
		magnetoVelocity.add(gravity);
		
		backgroundPosition.mulAdd(backgroundVelocity, deltaTime);
		
		if (Gdx.input.justTouched()) {
			orientation = (orientation == Orientation.Left) ? Orientation.Right : Orientation.Left; 
		}
		
		magnetoPosition.mulAdd(magnetoVelocity, deltaTime);
		
		if ((magnetoPosition.x <= leftWall.getRegionWidth()) && magnetoVelocity.x < 0) {
			magnetoVelocity.x = 0;
			magnetoPosition.x = leftWall.getRegionWidth();
		}
	
		
		if ((magnetoPosition.x + magnetoRight.getKeyFrame(0).getRegionWidth() >= 480 - rightWall.getRegionWidth()) &&
				magnetoVelocity.x > 0) {
			magnetoVelocity.x = 0;
			magnetoPosition.x = camera.viewportWidth - rightWall.getRegionWidth() - magnetoRight.getKeyFrame(0).getRegionWidth();
		}
		
		
		camera.position.y = magnetoPosition.y + 200;		
		if (camera.position.y - groundOffsetX > leftWall.getRegionHeight() + 400) {
			groundOffsetX += leftWall.getRegionHeight();

			rightWall = secondaryRightWall;
			leftWall = secondaryLeftWall;
			
			changeWalls();
		}
		
		
		
		if (camera.position.y - (camera.viewportHeight / 2) > backgroundPosition.y + background.getHeight()) {
			backgroundPosition.y += background.getHeight();
			
			background = secondaryBackground;
			
			changeBackground();
		}


		
		jumpRect.set(rotationOffset, 0, rotationLength, camera.viewportHeight);
		
		magnetoHitBox.set(magnetoPosition.x, 200, magnetoRight.getKeyFrame(0).getRegionWidth(), magnetoRight.getKeyFrame(0).getRegionHeight());
		
		
//		if (magnetoHitBox.overlaps(jumpRect)) {
//			onGround = false;
//			rotationStateTime += deltaTime;
//		}
		
		
		if (removeObstacles()) {
			addObstacle();
		}
		hitCheck();
		countScore();
	
		MovingObstacle mo;
		
		for (Obstacle o : obstacles) {
			o.updateHitBox(camera.position.y - camera.viewportHeight / 2);
			mo = (MovingObstacle)o;
			mo.move(deltaTime);
		}
		
	}

	
	private void drawWorld() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
//		batch.draw(background, 0, camera.position.y - background.getHeight() / 2);
		batch.draw(background, backgroundPosition.x, backgroundPosition.y);
		batch.draw(secondaryBackground, backgroundPosition.x, backgroundPosition.y + background.getHeight());
		
		batch.draw(leftWall, 0, groundOffsetX);
		batch.draw(secondaryLeftWall, 0, groundOffsetX + leftWall.getRegionHeight());
		
		batch.draw(rightWall, 480 - rightWall.getRegionWidth(), groundOffsetX);
		batch.draw(secondaryRightWall, 480 - rightWall.getRegionWidth(), groundOffsetX + rightWall.getRegionHeight());
		
		if (!magnetoHitBox.overlaps(jumpRect) && orientation == Orientation.Right) {
			batch.draw(magnetoRight.getKeyFrame(magnetoStateTime), magnetoPosition.x, magnetoPosition.y);
		}
		else if (!magnetoHitBox.overlaps(jumpRect) && orientation == Orientation.Left) {
			batch.draw(magnetoLeft.getKeyFrame(magnetoStateTime), magnetoPosition.x, magnetoPosition.y);
		}
		
		
		else {
			batch.draw(rotation.getKeyFrame((magnetoPosition.x - rotationOffset) / 1000), magnetoPosition.x, magnetoPosition.y);
		}
		
		
		font.draw(batch, "" + score, camera.position.x, camera.position.y + 300);
		font2.draw(batch, "" + highScore, camera.position.x + 200, camera.position.y + 300);

		
		MovingObstacle mo;
		for(Obstacle o: obstacles) {
			batch.draw(o.image, o.position.x, o.position.y);
			mo = (MovingObstacle)o;
			font3.draw(batch, "" + mo.speed, camera.position.x + 190, mo.position.y);
		}
			
		batch.end();
		
		 shapeRenderer.begin(ShapeType.Line);
		 shapeRenderer.setColor(0, 1, 0, 1);
		 shapeRenderer.rect(jumpRect.x, jumpRect.y + 1, jumpRect.width, jumpRect.height - 1);
		 shapeRenderer.setColor(1, 0, 0, 1);
		 shapeRenderer.rect(magnetoHitBox.x, magnetoHitBox.y, magnetoHitBox.width, magnetoHitBox.height);
		 for (Obstacle o : obstacles) {
			 shapeRenderer.setColor(0, 1, 1, 1);
			 shapeRenderer.rect(o.hitBox.x, o.hitBox.y, o.hitBox.width, o.hitBox.height);
				
		 }
		 
		 shapeRenderer.end();

		 
		 
		 if (hitCheck()) {                 /// TESTING PURPOUSE ONLY PLEASE DELETE AFTERWARDS
			 shapeRenderer.begin(ShapeType.Filled);  /// TESTING PURPOUSE ONLY PLEASE DELETE AFTERWARDS
			 shapeRenderer.setColor(1, 0, 0, 1);    /// TESTING PURPOUSE ONLY PLEASE DELETE AFTERWARDS
			 shapeRenderer.rect(0, 0, camera.viewportWidth, camera.viewportHeight);  /// TESTING PURPOUSE ONLY PLEASE DELETE AFTERWARDS
			 shapeRenderer.end();   /// TESTING PURPOUSE ONLY PLEASE DELETE AFTERWARDS
			 
		 }
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateWorld();
		drawWorld();
	}
	
	public void addObstacle() {
		Obstacle last = obstacles.peekLast();
//		Float y = 0.0f;
		Float y = camera.viewportHeight;
		Float x = (float) leftWall.getRegionWidth();
		TextureRegion image = new TextureRegion(new Texture("obstacle1.png"));
		
		if (last != null) {
			y = last.position.y;
		}
		
		
		Random rand = new Random();
				
		Integer min = 250;
		Integer max = 400;

		y += rand.nextInt((max - min) + 1) + min;
		
		if (rand.nextBoolean()) {
			x = (float) (480 - rightWall.getRegionWidth()) - image.getRegionWidth();
		}
		
		
	
		min = 0;
		max = 600;

		float s = rand.nextInt((max - min) + 1) + min;
		
		
		
		
		obstacles.add(new MovingObstacle(x, y, image, leftWall.getRegionWidth(), camera.viewportWidth - rightWall.getRegionWidth(), s));
		
		
	}
	
	public boolean removeObstacles() {
		Obstacle o = obstacles.peekFirst();
		boolean removedAtLeastOne = false;
		
		while (!obstacles.isEmpty() && (camera.position.y - camera.viewportHeight / 2) > (o.position.y + o.image.getRegionHeight())) {
			obstacles.pollFirst();
			o = obstacles.peekFirst();
			removedAtLeastOne = true;
		}
		
		return removedAtLeastOne;
	}
	
	public boolean hitCheck() {
		for (Obstacle o : obstacles) {
			if (magnetoHitBox.overlaps(o.hitBox)) {
				System.out.println("HitCheck " + o.position.x + " " + o.position.y);
				
				if (score > highScore) {
					highScore = score;
				}
				score = 0;
				return true;
			}
			
		}
		return false;
	}
	
	public void countScore() {
		for (Obstacle o : obstacles) {
			if (!o.counted && magnetoHitBox.y > o.hitBox.y) {
				score++;
				o.counted = true;
			}
			
		} 
	}
	
	public void changeBackground() {
		Random rand = new Random();
		
		Integer min = 1;
		Integer max = 5;

		int r = rand.nextInt((max - min) + 1) + min;
		
		secondaryBackground = new Texture("background0" + r + ".png");
		
	}
	
	public void changeWalls() {
		Random rand = new Random();
		
		Integer min = 1;
		Integer max = 4;

		int r;
		int r2;
		
		r = rand.nextInt((max - min) + 1) + min;
		r2 = rand.nextInt((max - min) + 1) + min;
		
		while (r2 == r) {                    //same walls won't be facing each other (looks bad)
			r2 = rand.nextInt((max - min) + 1) + min;
		}
		
		secondaryRightWall = new TextureRegion(new Texture("right_wall" + r + ".png"));
		secondaryLeftWall = new TextureRegion(new Texture("left_wall" + r2 + ".png"));
	
	}
	
}
