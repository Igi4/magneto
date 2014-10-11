package com.project.magneto;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class BackgroundManager {
	private HashMap<String, Background> backgrounds = new HashMap<String, Background>();
	
	public void add(String name, String filename, int textureCount, float velocityModifier) {
		backgrounds.put(name, new Background(filename, textureCount, velocityModifier));
	}
	
	public void add(String name, String filename, int textureCount) {
		add(name, filename, textureCount, 0);
	}
	
	public Background get(String name) {
		return backgrounds.get(name);
	}
	
	public void dispose() {
		for (Background background : backgrounds.values()) {
			background.dispose();
		}
	}
	
	public void update(Camera camera, float deltaTime, Vector2 velocity) {
		for (Background background : backgrounds.values()) {
			background.update(camera, deltaTime, velocity);
		}
	}
	
	public void draw(Batch batch) {
		for (Background background : backgrounds.values()) {
			background.draw(batch);
		}
	}
}