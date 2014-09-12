package com.project.magneto;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    Main game; 

    private static final float MAGNETO_START_X = 240;
	private static final float MAGNETO_START_Y = 200;
	private static final float BACKGROUND_X = 0;
	private static final float BACKGROUND_Y = 0;
	private static final float GRAVITY = -50;
	
	
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	SpriteBatch batch;
	OrthographicCamera uiCamera;
	
//	Texture background;
//	Texture secondaryBackground;
	
	Texture leftWall;
	Texture secondaryLeftWall;
	Texture rightWall;
	Texture secondaryRightWall;
	
	Texture backgroundL4Right;
	Texture secondaryBackgroundL4Right;
	Texture backgroundL4Left;
	Texture secondaryBackgroundL4Left;

	Texture backgroundL3;
	Texture secondaryBackgroundL3;
	
	Texture backgroundL2;
	Texture secondaryBackgroundL2;
	
	Texture backgroundL1;
	Texture secondaryBackgroundL1;
	
	
	
	float groundOffsetX = 0;
	TextureRegion buttonUp = new TextureRegion();
	TextureRegion buttonDown = new TextureRegion();
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
	
	Deque<Obstacle> obstacles = new LinkedList<Obstacle>();
	Deque<Cart> carts = new LinkedList<Cart>();
	
	static enum Orientation {
		Left, Right
	}
	Orientation orientation = Orientation.Right;
	
	static enum GameState {
		Start, Running, GameOver
	}
	GameState gameState = GameState.Running;

	
	Vector2 backgroundL1Position = new Vector2();
	Vector2 backgroundL1Velocity = new Vector2();

	Vector2 backgroundL2Position = new Vector2();
	Vector2 backgroundL2Velocity = new Vector2();
	
	Vector2 backgroundL3Position = new Vector2();
	Vector2 backgroundL3Velocity = new Vector2();
	
	
	Vector2 magnetoPosition = new Vector2();
	Vector2 magnetoVelocity = new Vector2();
	Vector2 gravity = new Vector2();
	
	float magnetoStateTime = 0;
	float rotationStateTime = 0;
	

     public GameScreen(Main game){
             this.game = game;
     }
     
     @Override
     public void render(float delta) {
//             // update and draw stuff
//          if (Gdx.input.justTouched()) // use your own criterion here
//              game.setScreen(game.anotherScreen);

    	 updateWorld();
    	 drawWorld();
     }


    @Override
     public void resize(int width, int height) {
     }


    @Override
     public void show() {
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
		
		
		backgroundL1 = loadRandomTexture(2, "background/layer1/background");
		secondaryBackgroundL1 = loadRandomTexture(2, "background/layer1/background");
		
		backgroundL2 = loadRandomTexture(2, "background/layer2/background");
		secondaryBackgroundL2 = loadRandomTexture(2, "background/layer2/background");

		backgroundL3 = loadRandomTexture(2, "background/layer3/background");
		secondaryBackgroundL3 = loadRandomTexture(2, "background/layer3/background");

		backgroundL4Left = loadRandomTexture(3, "background/layer4/left/background");
		secondaryBackgroundL4Left = loadRandomTexture(3, "background/layer4/left/background");
		backgroundL4Right = loadRandomTexture(2, "background/layer4/right/background");
		secondaryBackgroundL4Right = loadRandomTexture(2, "background/layer4/right/background");
		
		leftWall = loadRandomTexture(8, "walls/left/left_wall");
		secondaryLeftWall = loadRandomTexture(8, "walls/left/left_wall");
		rightWall = loadRandomTexture(5, "walls/right/right_wall");
		secondaryRightWall = loadRandomTexture(5, "walls/right/right_wall");
		

		TextureRegion rotationFrame1 = new TextureRegion(new Texture("magneto/rotate/rotate1.png"));
		TextureRegion rotationFrame2 = new TextureRegion(new Texture("magneto/rotate/rotate2.png"));
		TextureRegion rotationFrame3 = new TextureRegion(new Texture("magneto/rotate/rotate3.png"));
		TextureRegion rotationFrame4 = new TextureRegion(new Texture("magneto/rotate/rotate4.png"));
		TextureRegion rotationFrame5 = new TextureRegion(new Texture("magneto/rotate/rotate5.png"));
		
		rotation = new Animation(rotationLength / 5.0f / 1000.0f, rotationFrame1, rotationFrame2, rotationFrame3, rotationFrame4, rotationFrame5);
		rotation.setPlayMode(PlayMode.NORMAL);
		
		TextureRegion blueFrame1 = new TextureRegion(new Texture("magneto/blue/magneto_blue1.png"));
		TextureRegion blueFrame2 = new TextureRegion(new Texture("magneto/blue/magneto_blue2.png"));
		TextureRegion blueFrame3 = new TextureRegion(new Texture("magneto/blue/magneto_blue3.png"));
		
		
		magnetoRight = new Animation(0.1f, blueFrame1, blueFrame2, blueFrame3, blueFrame2);
		magnetoRight.setPlayMode(PlayMode.LOOP);

		
		TextureRegion redFrame1 = new TextureRegion(new Texture("magneto/red/magneto_red1.png"));
		TextureRegion redFrame2 = new TextureRegion(new Texture("magneto/red/magneto_red2.png"));
		TextureRegion redFrame3 = new TextureRegion(new Texture("magneto/red/magneto_red3.png"));
		
		
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
		
		
		backgroundL1Position.set(BACKGROUND_X, BACKGROUND_Y);
		backgroundL1Velocity.set(0, 200);
		
		backgroundL2Position.set(BACKGROUND_X, BACKGROUND_Y);
		backgroundL2Velocity.set(0, 150);
		
		backgroundL3Position.set(BACKGROUND_X, BACKGROUND_Y);
		backgroundL3Velocity.set(0, 50);
		
		
		gravity.set(GRAVITY, 0);
		obstacles.clear();
	
		for (int i = 1; i <= 5; i++) {
			addObstacle();
		}
		for (int i = 1; i <= 40; i++) {
			addCart();
		}
		
	}
    

    @Override
     public void hide() {
          // called when current screen changes from this to a different screen
     }


    @Override
     public void pause() {
     }


    @Override
     public void resume() {
     }


    @Override
     public void dispose() {
             // never called automatically
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
		
		backgroundL1Position.mulAdd(backgroundL1Velocity, deltaTime);
		backgroundL2Position.mulAdd(backgroundL2Velocity, deltaTime);
		backgroundL3Position.mulAdd(backgroundL3Velocity, deltaTime);
		
		if (Gdx.input.justTouched()) {
			orientation = (orientation == Orientation.Left) ? Orientation.Right : Orientation.Left; 
		}
		
		magnetoPosition.mulAdd(magnetoVelocity, deltaTime);
		
		if ((magnetoPosition.x <= leftWall.getWidth()) && magnetoVelocity.x < 0) {
			magnetoVelocity.x = 0;
			magnetoPosition.x = leftWall.getWidth();
		}
	
		
		if ((magnetoPosition.x + magnetoRight.getKeyFrame(0).getRegionWidth() >= 480 - rightWall.getWidth()) &&
				magnetoVelocity.x > 0) {
			magnetoVelocity.x = 0;
			magnetoPosition.x = camera.viewportWidth - rightWall.getWidth() - magnetoRight.getKeyFrame(0).getRegionWidth();
		}
		
		
		camera.position.y = magnetoPosition.y + 200;		
		if (camera.position.y - groundOffsetX > leftWall.getHeight() + 400) {
			groundOffsetX += leftWall.getHeight();

			leftWall = secondaryLeftWall;
			secondaryLeftWall = loadRandomTexture(8, "walls/left/left_wall");
			rightWall = secondaryRightWall;
			secondaryRightWall = loadRandomTexture(5, "walls/right/right_wall");
			
			
			backgroundL4Left = secondaryBackgroundL4Left;
			secondaryBackgroundL4Left = loadRandomTexture(3, "background/layer4/left/background");
			
			backgroundL4Right = secondaryBackgroundL4Right;
			secondaryBackgroundL4Right = loadRandomTexture(2, "background/layer4/right/background");
		}
		
		
		
		if (camera.position.y - (camera.viewportHeight / 2) > backgroundL1Position.y + backgroundL1.getHeight()) {
			backgroundL1Position.y += backgroundL1.getHeight();
			
			backgroundL1 = secondaryBackgroundL1;
			secondaryBackgroundL1 = loadRandomTexture(2, "background/layer1/background");
		}

		if (camera.position.y - (camera.viewportHeight / 2) > backgroundL2Position.y + backgroundL2.getHeight()) {
			backgroundL2Position.y += backgroundL2.getHeight();
			
			backgroundL2 = secondaryBackgroundL2;
			secondaryBackgroundL2 = loadRandomTexture(2, "background/layer2/background");
		}

		if (camera.position.y - (camera.viewportHeight / 2) > backgroundL3Position.y + backgroundL3.getHeight()) {
			backgroundL3Position.y += backgroundL3.getHeight();
			
			backgroundL3 = secondaryBackgroundL3;
			secondaryBackgroundL3 = loadRandomTexture(2, "background/layer3/background");
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
		
		if (removeCarts()) {
			addCart();
		}
		
		
		hitCheck();
		countScore();
	
		MovingObstacle mo;
		
		for (Obstacle o : obstacles) {
			o.updateHitBox(camera.position.y - camera.viewportHeight / 2);
			mo = (MovingObstacle)o;
			mo.move(deltaTime);
		}
		
		for (Cart c : carts) {
			c.move(deltaTime);
		} 
		
	}

	
	private void drawWorld() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.draw(backgroundL1, backgroundL1Position.x, backgroundL1Position.y);
		batch.draw(secondaryBackgroundL1, backgroundL1Position.x, backgroundL1Position.y + backgroundL1.getHeight());
		
		batch.draw(backgroundL2, backgroundL2Position.x, backgroundL2Position.y);
		batch.draw(secondaryBackgroundL2, backgroundL2Position.x, backgroundL2Position.y + backgroundL2.getHeight());

		
		for (Cart c : carts) {
			float x = c.position.x;
			
			for (int i = 0; i < c.count; i++) {
				batch.draw(c.image, x, c.position.y);

				x += c.spacing;
			}
			batch.draw(c.rail, camera.position.x - (camera.viewportWidth / 2), c.position.y - c.rail.getHeight());
		}
		
		
		
		batch.draw(backgroundL3, backgroundL3Position.x, backgroundL3Position.y);
		batch.draw(secondaryBackgroundL3, backgroundL3Position.x, backgroundL3Position.y + backgroundL3.getHeight());
		
		
		
		batch.draw(backgroundL4Left, 0, groundOffsetX);
		batch.draw(secondaryBackgroundL4Left, 0, groundOffsetX + backgroundL4Left.getHeight());
		
		batch.draw(backgroundL4Right, camera.viewportWidth - backgroundL4Right.getWidth(), groundOffsetX);
		batch.draw(secondaryBackgroundL4Right, camera.viewportWidth - backgroundL4Right.getWidth(), groundOffsetX + backgroundL4Right.getHeight());

		
		batch.draw(leftWall, 0, groundOffsetX);
		batch.draw(secondaryLeftWall, 0, groundOffsetX + leftWall.getHeight());
		
		batch.draw(rightWall, 480 - rightWall.getWidth(), groundOffsetX);
		batch.draw(secondaryRightWall, 480 - rightWall.getWidth(), groundOffsetX + rightWall.getHeight());
		
		
		
		
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
			font3.draw(batch, "" + mo.velocity, camera.position.x + 190, mo.position.y);
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
	
//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		
		
		
//		if (gameState == GameState.Running) {
//			updateWorld();
//			drawWorld();
//		}
//
//		else if (gameState == GameState.GameOver) {
//			Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
//			shapeRenderer.begin(ShapeType.Line);
//			shapeRenderer.setColor(0, 1, 0, 1);
//			shapeRenderer.rect(table.getX(), table.getY(), table.getWidth(), table.getHeight());
//			shapeRenderer.end();
//			stage.draw();
//		}
		
//	}
	
	public void addObstacle() {
		Obstacle last = obstacles.peekLast();
		Float y = camera.viewportHeight;
		Float x = (float) leftWall.getWidth();
		Texture image = new Texture("obstacle1.png");
		
		if (last != null) {
			y = last.position.y;
		}
		
		y += getRandomValue(250, 400);
		
		if (new Random().nextBoolean()) {
			x = (float) (480 - rightWall.getWidth()) - image.getWidth();
		}
		
	
		float s = getRandomValue(0, 600);
		
		
		
		
		obstacles.add(new MovingObstacle(x, y, image, leftWall.getWidth(), camera.viewportWidth - rightWall.getWidth(), s));
	}
	
	public boolean removeObstacles() {
		Obstacle o = obstacles.peekFirst();
		boolean removedAtLeastOne = false;
		
		while (!obstacles.isEmpty() && (camera.position.y - camera.viewportHeight / 2) > (o.position.y + o.image.getHeight())) {
			obstacles.pollFirst();
			o = obstacles.peekFirst();
			removedAtLeastOne = true;
		}
		
		return removedAtLeastOne;
	}
	
	
	public void addCart() {
		Cart last = carts.peekLast();
		float y = camera.position.y - (camera.viewportHeight / 2);
		float x = backgroundL4Left.getWidth() - leftWall.getWidth();
		
		Texture image = new Texture("cart2.png");
		Texture rail = new Texture("rail2.png");
		
		
		if (last != null) {
			y = last.position.y;
		}

		y += getRandomValue(20, 80);
		
		if (new Random().nextBoolean()) {
			x = camera.viewportWidth - backgroundL4Right.getWidth() + rightWall.getWidth() - image.getWidth();
			image = new Texture("cart1.png");
			rail = new Texture("rail1.png");
		}
		
		
		float velocityX = getRandomValue(10, 40);
		
		
		
		carts.add(new Cart(x, y, image, rail, velocityX, backgroundL2Velocity.y, 
				  backgroundL4Left.getWidth() - leftWall.getWidth(), 
				  camera.viewportWidth - backgroundL4Right.getWidth() + rightWall.getWidth()));
		
	}
	
	public boolean removeCarts() {
		Cart c = carts.peekFirst();
		boolean removedAtLeastOne = false;
		
		while (!carts.isEmpty() && (camera.position.y - camera.viewportHeight / 2) > (c.position.y + c.image.getHeight())) {
			carts.pollFirst();
			c = carts.peekFirst();
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
				
				gameState = GameState.GameOver;
				resetWorld();
				
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
	

	public Texture loadRandomTexture(int max, String fileName) {
		int r = (int) getRandomValue(1, max);
		return new Texture(Gdx.files.internal(fileName + r + ".png"));
	}
	
	public float getRandomValue(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
    
    
}
