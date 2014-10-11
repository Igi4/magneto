package com.project.magneto;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Cart extends MovingObstacle{

	Texture rail;
	int count;
	int spacing;
	
	public Cart(float x, float y, Texture cart, Texture rail,  float leftBorder, float rightBorder, Vector2 velocity, int maxCarts, int maxSpacing) {
		super(x, y, cart, leftBorder, rightBorder, velocity);
		this.rail = rail;
		
		count = getRandomValue(1, maxCarts);
		spacing = getRandomValue(10, maxSpacing);
	}
	
	
	public int getRandomValue(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
	
	
}
