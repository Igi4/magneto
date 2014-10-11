package com.project.magneto;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Obstacle {

	Vector2 position = new Vector2();
	Texture image;
	Rectangle hitBox = new Rectangle();
	boolean counted = false;

	public Obstacle(Texture image) {
		this.image = image;
	}
	
	public Obstacle(float x, float y, Texture image) {
		this(image);
		this.setPosition(x, y);
	}

	public void updateHitBox(float y) {
		this.hitBox.set(this.position.x, this.position.y - y, image.getWidth(), image.getHeight());
	}
	
	public void setPosition(float x, float y) {
		this.hitBox.set(x, y, image.getWidth(), image.getHeight());
		this.position.x = x;
		this.position.y = y;
	}

}
