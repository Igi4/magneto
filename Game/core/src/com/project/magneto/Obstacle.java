package com.project.magneto;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Obstacle {

	Vector2 position = new Vector2();
	Texture image;
	Rectangle hitBox = new Rectangle();
	boolean counted = false;

	public Obstacle(float x, float y, Texture image) {
		this.position.x = x;
		this.position.y = y;
		this.image = image;
		this.hitBox.set(x, y, image.getWidth(), image.getHeight());
	}
	
	public void updateHitBox(float y) {
		this.hitBox.set(this.position.x, this.position.y - y, image.getWidth(), image.getHeight());
	}
}
