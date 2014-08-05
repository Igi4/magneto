package com.project.magneto;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MovingObstacle extends Obstacle {

	public static final float SPEED = 50;
	
	float leftBorder;
	float rightBorder;
	Vector2 speed = new Vector2();
	float s;
	
	
	public MovingObstacle(float x, float y, TextureRegion image, float leftBorder, float rightBorder, float s) {
		super(x, y, image);
		this.leftBorder = leftBorder;
		this.rightBorder = rightBorder;
		this.s = s;
	}


//	public void move(float deltaTime) {
//		
//		Random rand = new Random();
//		
//		Integer min = 0;
//		Integer max = 700;
//
//		float s = rand.nextInt((max - min) + 1) + min;
//		
//		
//		if (position.x <= leftBorder) {
//			speed.set(SPEED + s, 0);
//		}
//		else if (position.x + image.getRegionWidth() >= rightBorder) {
//			speed.set((SPEED + s) * -1, 0);
//		} 
//		
//		if (speed.x < 0) {
//			speed.set((SPEED + s) * -1, 0);
//		}
//		if (speed.x > 0) {
//			speed.set(SPEED + s, 0);
//		}
//	
//		position.mulAdd(speed, deltaTime);
//	}

	
	public void move(float deltaTime) {
		

		if (position.x <= leftBorder) {
			speed.set(s, 0);
		}
		else if (position.x + image.getRegionWidth() >= rightBorder) {
			speed.set((s) * -1, 0);
		} 
		
		position.mulAdd(speed, deltaTime);
	}
}
