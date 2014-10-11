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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

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
	
	static enum GameState {
		Start, Running, GameOver
	}
	GameState gameState = GameState.Running;
	
	float groundOffsetX = 0;
	
	Image gameOverBackground;
	Image gameOverScore;
	Image gameOverBestScore;
	ButtonStyle playAgainStyle = new ButtonStyle();
	Button playAgain;
	TextureRegionDrawable playAgainDown = new TextureRegionDrawable();
	TextureRegionDrawable playAgainUp = new TextureRegionDrawable();
	Stage stage = new Stage(new StretchViewport(480, 800));
	
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
	
	Deque<MovingObstacle> obstacles = new LinkedList<MovingObstacle>();
	Deque<MovingObstacle> cartsL2 = new LinkedList<MovingObstacle>();
	Deque<MovingObstacle> cartsL3 = new LinkedList<MovingObstacle>();
	
	static enum Orientation {
		Left, Right
	}
	Orientation orientation = Orientation.Right;
	
	
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
//    	 game.gameOverScreen.show();
     }


    @Override
     public void resize(int width, int height) {
     }


    @Override
     public void show() {
    	stage = new Stage(new StretchViewport(480, 800));
    	Gdx.input.setInputProcessor(stage);  // ???
    	
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
		
		
		
		
		
		
		playAgainUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gameOver/play_again_up.png"))));
		playAgainDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gameOver/play_again_down.png"))));
		
		playAgainStyle.up = playAgainUp;
		playAgainStyle.down = playAgainDown;
		
		playAgain = new Button(playAgainStyle);
		gameOverBackground = new Image(new Texture(Gdx.files.internal("gameOver/table.png")));
		gameOverScore = new Image(new Texture(Gdx.files.internal("gameOver/score.png")));
		gameOverBestScore = new Image(new Texture(Gdx.files.internal("gameOver/best.png")));
		
		gameOverBackground.setPosition((stage.getWidth() / 2) - (gameOverBackground.getWidth() / 2), (stage.getHeight() / 2) - (gameOverBackground.getHeight() / 2));
		playAgain.setPosition(gameOverBackground.getCenterX() - playAgain.getCenterX(), 
							  (gameOverBackground.getHeight() / 5) + ((stage.getHeight() - gameOverBackground.getHeight()) / 2));
		gameOverScore.setPosition(gameOverBackground.getCenterX() - gameOverScore.getCenterX() - 40, 
							  stage.getHeight() - (150) - ((stage.getHeight() - gameOverBackground.getHeight()) / 2));
		gameOverBestScore.setPosition(gameOverBackground.getCenterX() - gameOverBestScore.getCenterX() - 40, 
				stage.getHeight() - (200) - ((stage.getHeight() - gameOverBackground.getHeight()) / 2) - gameOverScore.getHeight());
		
		
		stage.addActor(gameOverBackground);
		stage.addActor(playAgain);
		stage.addActor(gameOverScore);
		stage.addActor(gameOverBestScore);
		
		
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
		backgroundL1Position.set(BACKGROUND_X, BACKGROUND_Y);
		backgroundL2Position.set(BACKGROUND_X, BACKGROUND_Y);
		backgroundL3Position.set(BACKGROUND_X, BACKGROUND_Y);

		setSpeed(300);
		
		
		gravity.set(GRAVITY, 0);
		obstacles.clear();
	
		for (int i = 1; i <= 5; i++) {
			addObstacle();
		}
		for (int i = 1; i <= 40; i++) {
			addCart(cartsL2, "layer2", 5, 30, 20, getRandomValue(20, 80));
		}
		for (int i = 1; i <= 16; i++) {
			addCart(cartsL3, "layer3", 5, 70, 150, getRandomValue(50, 400));
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
    	shapeRenderer.dispose();
    	batch.dispose();
    	leftWall.dispose();
    	secondaryLeftWall.dispose();
    	rightWall.dispose();
    	secondaryRightWall.dispose();
    	backgroundL4Left.dispose();
    	secondaryBackgroundL4Left.dispose();
    	backgroundL4Right.dispose();
    	secondaryBackgroundL4Right.dispose();
    	backgroundL3.dispose();
    	secondaryBackgroundL3.dispose();
    	backgroundL2.dispose();
    	secondaryBackgroundL2.dispose();
    	backgroundL1.dispose();
    	secondaryBackgroundL1.dispose();
    	stage.dispose();   
    	obstacles.clear();
    	cartsL2.clear();
    	cartsL3.clear();
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
		
		
//		if (removeObstacles() && gameState == GameState.Running) {
//			addObstacle();
//		}
//		
//		if (removeCarts()) {
//			addCart();
//		}
//		
		if (removeMovingObstacle(obstacles) && gameState == GameState.Running) {
			addObstacle();
		}
		
		if (removeMovingObstacle(cartsL2)) {
			addCart(cartsL2, "layer2", 5, 30, 20, getRandomValue(20, 80));
		}
		if (removeMovingObstacle(cartsL3)) {
			addCart(cartsL3, "layer3", 5, 70, 150, getRandomValue(50, 400));
		}
		
		
		hitCheck();
		countScore();
	
		MovingObstacle mo;
		
		for (Obstacle o : obstacles) {
			o.updateHitBox(camera.position.y - camera.viewportHeight / 2);
			mo = (MovingObstacle)o;
			mo.move(deltaTime);
		}
		
		for (MovingObstacle c : cartsL2) {
			c.move(deltaTime);
		} 
		
		for (MovingObstacle c : cartsL3) {
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

		
		for (MovingObstacle cart : cartsL2) {
			Cart c = (Cart) cart;
			float x = c.position.x;
		
			for (int i = 0; i < c.count; i++) {
				batch.draw(c.image, x, c.position.y);

				x += c.spacing;
			}
			batch.draw(c.rail, camera.position.x - (camera.viewportWidth / 2), c.position.y - c.rail.getHeight());
		}
		
		for (MovingObstacle cart : cartsL3) {
			Cart c = (Cart) cart;
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

		for(MovingObstacle mo: obstacles) {
			batch.draw(mo.image, mo.position.x, mo.position.y);
			font3.draw(batch, "" + mo.velocity, camera.position.x + 190, mo.position.y);
		}
		batch.end();

		
		if (gameState == GameState.GameOver) {
			drawGameOverScreen();
		} 
		
		
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

		 
		 
	}
	
	
	public void addObstacle() {
		Float x = (float) leftWall.getWidth();
		Texture image = new Texture("obstacle1.png");
		Vector2 velocity = new Vector2(getRandomValue(0, 600), 0);
		
		if (new Random().nextBoolean()) {
			x = (float) (480 - rightWall.getWidth()) - image.getWidth();
		}
	
		addMovingObstacle(obstacles, new MovingObstacle(image, leftWall.getWidth(), camera.viewportWidth - rightWall.getWidth(), velocity), x, 
				          getRandomValue(250, 400), true);
	}
	
	
	public void addCart(Deque<MovingObstacle> collection, String layer, int maxCarts, int maxSpacing, int maxSpeed, float offset) {
		float x = backgroundL4Left.getWidth() - 64;
		Vector2 velocity = new Vector2(getRandomValue(5, maxSpeed), backgroundL3Velocity.y);
		Texture image = new Texture(Gdx.files.internal("background/" + layer + "/carts/cart2.png"));
		Texture rail = new Texture(Gdx.files.internal("background/" + layer + "/rails/rail2.png"));
		
		if (new Random().nextBoolean()) {
			x = camera.viewportWidth - backgroundL4Right.getWidth() + rightWall.getWidth() - image.getWidth();
			image = new Texture(Gdx.files.internal("background/" + layer + "/carts/cart1.png"));
			rail = new Texture(Gdx.files.internal("background/" + layer + "/rails/rail1.png"));
		}
		
		addMovingObstacle(collection, new Cart(0, 0, image, rail, backgroundL4Left.getWidth() - leftWall.getWidth(), 
				  camera.viewportWidth - backgroundL4Right.getWidth() + rightWall.getWidth(), velocity, maxCarts, maxSpacing), x, offset, false);
	}
	
	public void addMovingObstacle(Deque<MovingObstacle> collection, MovingObstacle obstacle, float x, float offset, boolean spawnOnTop) {
		MovingObstacle last = collection.peekLast();
		float y = 0;
		
		if (spawnOnTop) {
			y = camera.viewportHeight;
		}
		
		if (last != null) {
			y = last.position.y;
		}

		y += offset;
		
		obstacle.setPosition(x, y);
		collection.add(obstacle);
	}
	
	public boolean removeMovingObstacle(Deque<MovingObstacle> collection) {
		MovingObstacle obstacle = collection.peekFirst();
		boolean removedAtLeastOne = false;
		
		while (!collection.isEmpty() && (camera.position.y - camera.viewportHeight / 2) > (obstacle.position.y + obstacle.image.getHeight())) {
			collection.pollFirst();
			obstacle =  collection.peekFirst();
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
				
//				game.setScreen(game.gameOverScreen);
				gameState = GameState.GameOver;
				obstacles.clear();
				setSpeed(100);
				
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
	
	
	public void drawGameOverScreen() {
//		stage.getViewport().update((int) camera.viewportWidth, (int) camera.viewportHeight);
		
		
		stage.draw();

		if (playAgain.isPressed()) {
			gameState = GameState.Running;
			dispose();
			show();
//			game.setScreen(game.gameScreen);  // toto asi neni dobry napad
		}
	}
    
	public void setSpeed(Integer speed) {
		magnetoVelocity.set(0, speed);
		backgroundL1Velocity.set(0, (float) (speed * 0.66));
		backgroundL2Velocity.set(0, (float) (speed * 0.5));
		backgroundL3Velocity.set(0, (float) (speed * 0.166));
		
//		for (MovingObstacle cart : cartsL2) {
//			cart.velocity.y = backgroundL2Velocity.y;  
//		}
//		for (MovingObstacle cart : cartsL3) {
//			cart.velocity.y = backgroundL3Velocity.y;  
//		}
	}
	
    
}
