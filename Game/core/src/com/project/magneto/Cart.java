package com.project.magneto;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Cart extends MovingObstacle{

	Texture rail;
	int count;
	int spacing;
	private static Random random = new Random();
	
	static HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String,Integer>>();

	static {
		map.put("cartsL2", new HashMap<String, Integer>());
		map.put("cartsL3", new HashMap<String, Integer>());
		
		map.get("cartsL2").put("MaxCarts", 5);
		map.get("cartsL2").put("MaxSpacing", 30);
		map.get("cartsL2").put("MaxSpeed", 20);

		map.get("cartsL3").put("MaxCarts", 5);
		map.get("cartsL3").put("MaxSpacing", 70);
		map.get("cartsL3").put("MaxSpeed", 150);
	}
	
	public Cart(float x, float y, Texture cart, Texture rail,  float leftBorder, float rightBorder, Vector2 velocity, String cartLayer) {
		super(x, y, cart, leftBorder, rightBorder, changeVelocity(velocity, cartLayer));
		this.rail = rail;
		count = getRandomValue(1, map.get(cartLayer).get("MaxCarts"));
		spacing = getRandomValue(10, map.get(cartLayer).get("MaxSpacing"));
	}
	
	private static Vector2 changeVelocity(Vector2 velocity, String cartLayer) {
		velocity.x = getRandomValue(5, map.get(cartLayer).get("MaxSpeed"));
		return velocity;
	}
	
	private static int getRandomValue(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}
}