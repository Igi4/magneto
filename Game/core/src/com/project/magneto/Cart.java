package com.project.magneto;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;

public class Cart extends MovingObstacle{

	Texture rail;
	int count;
	int spacing;
	float velocityY;
	
	public Cart(float x, float y, Texture cart, Texture rail, float velocityX, float velocityY, float leftBorder, float rightBorder) {
		super(x, y, cart, leftBorder, rightBorder, velocityX);
		this.velocityY = velocityY;
		this.rail = rail;
		
		count = getRandomValue(1, 5);
		spacing = getRandomValue(10, 30);
	}
	
	
	public void move(float deltaTime) {
		super.move(deltaTime);
		this.velocity.y = velocityY;
	}

	public int getRandomValue(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
	
	
}
