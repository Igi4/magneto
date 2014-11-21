package com.project.magneto;

import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SlowField extends PowerUp {
	
	float radius;
	Rectangle magneto;
	List<MovingObstacle> obstacles;
	ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	public SlowField(Rectangle magneto, List<MovingObstacle> obstacles, float radius) {
		this.radius = radius;
		this.magneto = magneto;
		this.obstacles = obstacles;
	}
	
	@Override
	public void render(float deltaTime) {
		shapeRenderer.begin(ShapeType.Line);
		
		for (MovingObstacle mo : obstacles) {
			computeForce(mo);
			//mo.position.x
		}
		
		shapeRenderer.circle(magneto.x + magneto.width / 2, magneto.y + magneto.height / 2, 60);
		shapeRenderer.end();
	}
	
	private Vector2 computeForce(MovingObstacle mo) {
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		float x1 = mo.hitBox.x + mo.hitBox.width / 2;
		float y1 = mo.hitBox.y + mo.hitBox.height / 2;
		float x2 = magneto.x + magneto.width / 2;
		float y2 = magneto.y + magneto.height / 2;
		float force;
		Vector2 distance = new Vector2(x1 - x2, y1 - y2);
		
		shapeRenderer.begin(ShapeType.Line);
		
		if (distance.len() > 300) {
			force = 0f;	
			shapeRenderer.setColor(0, 1, 0, 1);
		}
		else if (distance.len() < 100) {
			force = 20f;
			shapeRenderer.setColor(1, 0, 0, 1);
		}
		else {
			force = (float) ((-0.25 * distance.len()) + 75);
			shapeRenderer.setColor(0, 0, 1, 1);
		}
		
		shapeRenderer.line(x1, y1, x2, y1);
		shapeRenderer.end();
		
		return new Vector2(force, 0f);
	}

	@Override
	public boolean isActive() {
		return true;		
	}
}
