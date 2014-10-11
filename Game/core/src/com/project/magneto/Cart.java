package com.project.magneto;

import java.util.HashMap;
import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Cart extends MovingObstacle{

	Texture rail;
	int count;
	int spacing;
	
	
	static HashMap<String, Integer> cartsL2Map = new HashMap<String, Integer>();
	static HashMap<String, Integer> cartsL3Map = new HashMap<String, Integer>();
	static HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String,Integer>>();

	static {
		cartsL2Map.put("MaxCarts", 5);
		cartsL2Map.put("MaxSpacing", 30);
		cartsL2Map.put("MaxSpeed", 20);

		cartsL3Map.put("MaxCarts", 5);
		cartsL3Map.put("MaxSpacing", 70);
		cartsL3Map.put("MaxSpeed", 150);
		
		map.put("cartsL2", cartsL2Map);
		map.put("cartsL3", cartsL3Map);
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
	
//	public Cart(float x, float y, Texture cart, Texture rail,  float leftBorder, float rightBorder, Vector2 velocity, int maxCarts, int maxSpacing) {
//		super(x, y, cart, leftBorder, rightBorder, velocity);
//		this.rail = rail;
//		
//		count = getRandomValue(1, maxCarts);
//		spacing = getRandomValue(10, maxSpacing);
//	}
	
	
	public static int getRandomValue(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
	
	
}
