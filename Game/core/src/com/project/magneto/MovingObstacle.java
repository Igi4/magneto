package com.project.magneto;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class MovingObstacle extends Obstacle {

	float leftBorder;
	float rightBorder;
	Vector2 velocity;
	private float velocityX;
	
	public MovingObstacle(Texture image, float leftBorder, float rightBorder, Vector2 velocity) {
		super(image);
		this.leftBorder = leftBorder;
		this.rightBorder = rightBorder;
		this.velocity = velocity;
		this.velocityX = velocity.x;
	}

	public MovingObstacle(float x, float y, Texture image, float leftBorder, float rightBorder, Vector2 velocity) {
		this(image, leftBorder, rightBorder, velocity);
		this.setPosition(x, y);
	}

	

	public void move(float deltaTime) {
		if (position.x <= leftBorder) {
			velocity.set(velocityX, velocity.y);
		}
		else if (position.x + image.getWidth() >= rightBorder) {
			velocity.set((velocityX) * -1, velocity.y);
		} 
		
		position.mulAdd(velocity, deltaTime);
	}
}