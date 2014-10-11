package com.project.magneto;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Background {

	private int textureCount;
	private String filename;
	private Texture primary;
	private Texture secondary;
	private Vector2 position = new Vector2(0, 0);
	private float velocityModifier;
	
	private Random random = new Random();
	
	public Background(String filename, int textureCount, float velocityModifier) {
		this.textureCount = textureCount;
		this.filename = filename;
		this.velocityModifier = velocityModifier;
		primary = load();
		secondary = load();
	}
	
	public void update(Camera camera, float deltaTime, Vector2 velocity) {
		position.mulAdd(getVelocity(velocity), deltaTime);
		
		if (camera.position.y - (camera.viewportHeight / 2) > position.y + primary.getHeight()) {
			position.y += primary.getHeight();
			primary = secondary;
			secondary = load();
		}
	}
	
	public void draw(Batch batch) {
		batch.draw(primary, position.x, position.y);
		batch.draw(secondary, position.x, position.y + primary.getHeight());
	}
	
	public Texture getPrimary() {
		return primary;
	}
	
	public Texture getSecondary() {
		return secondary;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	public void setVelocityModifier(float value) {
		velocityModifier = value;
	}
	
	public Vector2 getVelocity(Vector2 baseVelocity) {
		return baseVelocity.cpy().scl(velocityModifier);
	}
	
	public void dispose() {
		primary.dispose();
		secondary.dispose();
	}
	
	private Texture load() {
		return new Texture(Gdx.files.internal(filename + getRandomValue(1, textureCount) + ".png"));
	}
	
	private int getRandomValue(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}
}
