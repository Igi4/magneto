package com.project.magneto;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameScreen implements Screen {
    Main game; 

    private static final float MAGNETO_START_X = 240;
	private static final float MAGNETO_START_Y = 200;
	private static final float GRAVITY = -50;
	
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	OrthographicCamera uiCamera;
	SpriteBatch batch;
	BackgroundManager backgroundManager = new BackgroundManager();
	
	static enum GameState { Start, Running, GameOver }
	
	GameState gameState = GameState.Running;
	
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
	BitmapFont scoreFont;
	BitmapFont endScoreFont;
	BitmapFont font3;
	int score = 0;
	int highScore = 0;
	boolean newHighScore = false;
	Preferences prefs = Gdx.app.getPreferences("magnetoPreferences");
	boolean onGround = false;
	float rotationOffset = 118;
	float rotationLength = 244;
	
	Deque<MovingObstacle> obstacles = new LinkedList<MovingObstacle>();
	Deque<MovingObstacle> cartsL2 = new LinkedList<MovingObstacle>();
	Deque<MovingObstacle> cartsL3 = new LinkedList<MovingObstacle>();
	
	static enum Orientation { Left, Right }
	
	Orientation orientation = Orientation.Right;
	
	Vector2 magnetoPosition = new Vector2();
	Vector2 magnetoVelocity = new Vector2();
	Vector2 gravity = new Vector2();
	
	float magnetoStateTime = 0;
	float rotationStateTime = 0;
	
	Random random = new Random();
	
	public GameScreen(Main game){
		this.game = game;
	}
     
    @Override
    public void render(float delta) {
    	updateWorld();
    	drawWorld();
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

		scoreFont = new BitmapFont(Gdx.files.internal("font/arial.fnt"));
		scoreFont.setColor(1, 1, 1, 1);
		scoreFont.setScale(1.5f, 1.5f);
		
		endScoreFont = new BitmapFont(Gdx.files.internal("font/arial.fnt"));
		endScoreFont.setColor(1, 1, 1, 1);
		endScoreFont.setScale(1, 1);

		font3 = new BitmapFont(Gdx.files.internal("font/arial.fnt"));
		font3.setColor(0, 1, 0, 1);
		font3.setScale(0.3f, 0.3f);
		
		playAgainUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gameOver/play_again_up.png"))));
		playAgainDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gameOver/play_again_down.png"))));
		
		playAgainStyle.up = playAgainUp;
		playAgainStyle.down = playAgainDown;
		
		playAgain = new Button(playAgainStyle);
		
		playAgain.addListener(new ClickListener() {             
		    @Override
		    public void clicked(InputEvent event, float x, float y) {
		    	if (gameState == GameState.GameOver) {
					gameState = GameState.Running;
					dispose();
					score = 0;
					newHighScore = false;
					show();
		    	}
		    };
		});
		
		gameOverBackground = new Image(new Texture(Gdx.files.internal("gameOver/table.png")));
		gameOverScore = new Image(new Texture(Gdx.files.internal("gameOver/score.png")));
		gameOverBestScore = new Image(new Texture(Gdx.files.internal("gameOver/best.png")));
		
		gameOverBackground.setPosition((stage.getWidth() / 2) - (gameOverBackground.getWidth() / 2), 
				                       (stage.getHeight() / 2) - (gameOverBackground.getHeight() / 2));
		
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
		
		backgroundManager.add("backL1", "background/layer1/background", 2, 0.66f);
		backgroundManager.add("backL2", "background/layer2/background", 2, 0.5f);
		backgroundManager.add("backL3", "background/layer3/background", 2, 0.166f);
		backgroundManager.add("backL4Left", "background/layer4/left/background", 3);
		backgroundManager.add("backL4Right", "background/layer4/right/background", 2);
		backgroundManager.add("wallLeft", "walls/left/left_wall", 8);
		backgroundManager.add("wallRight", "walls/right/right_wall", 5);
		
		backgroundManager.get("backL4Right").setPosition(camera.viewportWidth - backgroundManager.get("backL4Right").getPrimary().getWidth(), 0);
		backgroundManager.get("wallRight").setPosition(camera.viewportWidth - backgroundManager.get("wallRight").getPrimary().getWidth(), 0);
		
		rotation = new Animation(rotationLength / 5.0f / 1000.0f, loadTextures(5, "magneto/rotate/rotate"), PlayMode.NORMAL);
		magnetoRight = new Animation(0.1f, loadTextures(4, "magneto/blue/magneto_blue"), PlayMode.LOOP);
		magnetoLeft = new Animation(0.1f, loadTextures(4, "magneto/red/magneto_red"), PlayMode.LOOP);
		
		magnetoVelocity.set(100, 0);
        magnetoPosition.mulAdd(magnetoVelocity, 0.016f);
		resetWorld();
    }
     
    public Array<TextureRegion> loadTextures(int count, String texture) {
    	Array<TextureRegion> result = new Array<TextureRegion>();
    	
    	for (int i = 1; i <= count; ++i) {
    		result.add(new TextureRegion(new Texture(Gdx.files.internal(texture + i + ".png"))));
    	}
    	
    	return result;
    }

    private void resetWorld() {
		score = 0;

		camera.position.y = 400;
		
		magnetoPosition.set(MAGNETO_START_X, MAGNETO_START_Y);

		setSpeed(300);
		
		gravity.set(GRAVITY, 0);
		obstacles.clear();
	
		for (int i = 1; i <= 5; i++) {
			addObstacle();
		}
		
		for (int i = 1; i <= 40; i++) {
			addCart(cartsL2, "layer2", getRandomValue(20, 80), "cartsL2", backgroundManager.get("backL2").getVelocity(magnetoVelocity).y);
		}
		
		for (int i = 1; i <= 16; i++) {
			addCart(cartsL3, "layer3", getRandomValue(50, 400), "cartsL3", backgroundManager.get("backL3").getVelocity(magnetoVelocity).y);
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
    	backgroundManager.dispose();
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
		
		if (Gdx.input.justTouched()) {
			orientation = (orientation == Orientation.Left) ? Orientation.Right : Orientation.Left; 
		}
		
		magnetoPosition.mulAdd(magnetoVelocity, deltaTime);
		
		if ((magnetoPosition.x <= backgroundManager.get("wallLeft").getPrimary().getWidth()) && magnetoVelocity.x < 0) {
			magnetoVelocity.x = 0;
			magnetoPosition.x = backgroundManager.get("wallLeft").getPrimary().getWidth();
		}
		
		if ((magnetoPosition.x + magnetoRight.getKeyFrame(0).getRegionWidth() >= 480 - backgroundManager.get("wallRight").getPrimary().getWidth()) &&
				magnetoVelocity.x > 0) {
			magnetoVelocity.x = 0;
			magnetoPosition.x = camera.viewportWidth - backgroundManager.get("wallRight").getPrimary().getWidth() - magnetoRight.getKeyFrame(0).getRegionWidth();
		}
		
		camera.position.y = magnetoPosition.y + 200;
		
		backgroundManager.update(camera, deltaTime, magnetoVelocity.cpy().scl(0, 1));
		
		jumpRect.set(rotationOffset, 0, rotationLength, camera.viewportHeight);
		
		magnetoHitBox.set(magnetoPosition.x, 200, magnetoRight.getKeyFrame(0).getRegionWidth(), magnetoRight.getKeyFrame(0).getRegionHeight());
		
		if (removeMovingObstacle(obstacles) && gameState == GameState.Running) {
			addObstacle();
		}
		
		if (removeMovingObstacle(cartsL2)) {
			addCart(cartsL2, "layer2", getRandomValue(20, 80), "cartsL2", backgroundManager.get("backL2").getVelocity(magnetoVelocity).y);
		}
		if (removeMovingObstacle(cartsL3)) {
			addCart(cartsL3, "layer3", getRandomValue(50, 400), "cartsL3", backgroundManager.get("backL3").getVelocity(magnetoVelocity).y);
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
		
		backgroundManager.get("backL1").draw(batch);
		backgroundManager.get("backL2").draw(batch);
		
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
		
		backgroundManager.get("backL3").draw(batch);
		backgroundManager.get("backL4Left").draw(batch);
		backgroundManager.get("backL4Right").draw(batch);
		backgroundManager.get("wallLeft").draw(batch);
		backgroundManager.get("wallRight").draw(batch);
		
		if (!magnetoHitBox.overlaps(jumpRect) && magnetoHitBox.getX() > jumpRect.getX() + jumpRect.getWidth()) {
			batch.draw(magnetoRight.getKeyFrame(magnetoStateTime), magnetoPosition.x, magnetoPosition.y);
		}
		else if (!magnetoHitBox.overlaps(jumpRect) && magnetoHitBox.getX() + magnetoHitBox.getWidth() < jumpRect.getX()) {
			batch.draw(magnetoLeft.getKeyFrame(magnetoStateTime), magnetoPosition.x, magnetoPosition.y);
		}
		
		else {
			batch.draw(rotation.getKeyFrame((magnetoPosition.x - rotationOffset) / 1000), magnetoPosition.x, magnetoPosition.y);
		}

		for(MovingObstacle mo: obstacles) {
			batch.draw(mo.image, mo.position.x, mo.position.y);
			font3.draw(batch, "" + mo.velocity, camera.position.x + 190, mo.position.y);
		}
		
		if (gameState != GameState.GameOver) scoreFont.draw(batch, "" + score, camera.position.x - (scoreFont.getBounds("" + score).width / 2), camera.position.y + 300);
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
		Float x = (float) backgroundManager.get("wallLeft").getPrimary().getWidth();
		Texture image = new Texture("obstacle1.png");
		Vector2 velocity = new Vector2(getRandomValue(0, 600), 0);
		
		if (new Random().nextBoolean()) {
			x = (float) (480 - backgroundManager.get("wallRight").getPrimary().getWidth()) - image.getWidth();
		}
	
		addMovingObstacle(obstacles, new MovingObstacle(image, backgroundManager.get("wallLeft").getPrimary().getWidth(), camera.viewportWidth - backgroundManager.get("wallRight").getPrimary().getWidth(), velocity), x, 
				          getRandomValue(250, 400), true);
	}
	
	
	public void addCart(Deque<MovingObstacle> collection, String layer, float offset, String cartLayer, float velocityY) {
		float x = backgroundManager.get("backL4Left").getPrimary().getWidth() - 64;
		Vector2 velocity = new Vector2(0, velocityY);
		Texture image = new Texture(Gdx.files.internal("background/" + layer + "/carts/cart2.png"));
		Texture rail = new Texture(Gdx.files.internal("background/" + layer + "/rails/rail2.png"));
		
		if (new Random().nextBoolean()) {
			x = camera.viewportWidth - backgroundManager.get("backL4Right").getPrimary().getWidth() + backgroundManager.get("wallRight").getPrimary().getWidth() - image.getWidth();
			image = new Texture(Gdx.files.internal("background/" + layer + "/carts/cart1.png"));
			rail = new Texture(Gdx.files.internal("background/" + layer + "/rails/rail1.png"));
		}
		
		addMovingObstacle(collection, new Cart(0, 0, image, rail,
				backgroundManager.get("backL4Left").getPrimary().getWidth()
						- backgroundManager.get("wallLeft").getPrimary()
								.getWidth(), camera.viewportWidth
						- backgroundManager.get("backL4Right").getPrimary()
								.getWidth()
						+ backgroundManager.get("wallRight").getPrimary()
								.getWidth(), velocity, cartLayer), x, offset,
				false);
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
				
				if (score > prefs.getInteger("highScore")) {
					prefs.putInteger("highScore", score);
					prefs.flush();
					newHighScore = true;
				}
				
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
	
	public float getRandomValue(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}
	
	public void drawGameOverScreen() {
		stage.draw();
		stage.getBatch().begin();
		
		if (newHighScore) {
			stage.getBatch().draw(new Texture(Gdx.files.internal("newHighScore.png")), gameOverBestScore.getX() + gameOverBestScore.getWidth() - 40, gameOverBestScore.getY() - 20);
		}

		endScoreFont.draw(stage.getBatch(), "" + score, gameOverScore.getX() + gameOverScore.getWidth() + 10, gameOverScore.getY() + endScoreFont.getXHeight() + 2);
		endScoreFont.draw(stage.getBatch(), "" + prefs.getInteger("highScore"), gameOverBestScore.getX() + gameOverBestScore.getWidth() + 10, gameOverBestScore.getY() + endScoreFont.getXHeight() + 2);
		stage.getBatch().end();
	}
    
	public void setSpeed(Integer speed) {
		magnetoVelocity.set(0, speed);
		
		for (MovingObstacle cart : cartsL2) {
			cart.velocity.y = backgroundManager.get("backL2").getVelocity(magnetoVelocity).y;  
		}
		
		for (MovingObstacle cart : cartsL3) {
			cart.velocity.y = backgroundManager.get("backL3").getVelocity(magnetoVelocity).y;  
		}
	}
}
