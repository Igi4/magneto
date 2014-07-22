package com.project.magneto;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Obstacle {

	Vector2 position = new Vector2();
	TextureRegion image;

	public Obstacle(float x, float y, TextureRegion image) {
		this.position.x = x;
		this.position.y = y;
		this.image = image;
	}
}
