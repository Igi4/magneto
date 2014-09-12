package com.project.magneto;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class MovingObstacle extends Obstacle {

	float leftBorder;
	float rightBorder;
	Vector2 velocity = new Vector2();
	float velocityX;
	
	
	public MovingObstacle(float x, float y, Texture image, float leftBorder, float rightBorder, float velocityX) {
		super(x, y, image);
		this.leftBorder = leftBorder;
		this.rightBorder = rightBorder;
		this.velocityX = velocityX;
	}


	public void move(float deltaTime) {
		if (position.x <= leftBorder) {
			velocity.set(velocityX, 0);
		}
		
		else if (position.x + image.getWidth() >= rightBorder) {
			velocity.set((velocityX) * -1, 0);
		} 
		
		position.mulAdd(velocity, deltaTime);
	}
}
