package com.project.magneto;

import com.badlogic.gdx.Game;


public class Main extends Game {
	LogoScreen logoScreen;
	MainMenuScreen mainMenuScreen;
	GameScreen gameScreen;
	GameOverScreen gameOverScreen;


	@Override
	public void create() {
		logoScreen = new LogoScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		gameScreen = new GameScreen(this);
		gameOverScreen = new GameOverScreen(this);
		
		setScreen(logoScreen);
	}
	
}
