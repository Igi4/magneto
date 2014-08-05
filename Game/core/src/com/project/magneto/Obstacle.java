package com.project.magneto;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Obstacle {

	Vector2 position = new Vector2();
	TextureRegion image;
	Rectangle hitBox = new Rectangle();
	boolean counted = false;

	public Obstacle(float x, float y, TextureRegion image) {
		this.position.x = x;
		this.position.y = y;
		this.image = image;
		this.hitBox.set(x, y, image.getRegionWidth(), image.getRegionHeight());
	}
	
	public void updateHitBox(float y) {
		this.hitBox.set(this.position.x, this.position.y - y, image.getRegionWidth(), image.getRegionHeight());
	}
}
