package com.project.magneto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class LogoScreen implements Screen{
	Main game;
	
	Texture logo = new Texture("coollogo.png");
	SpriteBatch batch;
	OrthographicCamera camera;
	OrthographicCamera uiCamera;
	boolean timerIsOn = false;
	public LogoScreen(Main game) {
		this.game = game;
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.update();
		
	}
	
	@Override
	public void render(float delta) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(logo, camera.position.x - logo.getWidth() / 2, camera.position.y - logo.getHeight() / 2);
		batch.end();
		
	    if(!timerIsOn) {
	    	timerIsOn = true;
	          
	        Timer.schedule(new Task() {
	             
	        	@Override
	        	public void run() {
	        		game.setScreen(game.mainMenuScreen);
	        	}
	        }, 3);
	    } 

	    else if(Gdx.input.isTouched()) {
	    	Timer.instance().clear(); 
	    	game.setScreen(game.mainMenuScreen);
	    }
	}
		

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
