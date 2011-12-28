package com.raver;

/**
 * @author Nick Peters
 */

import android.graphics.Canvas;

public class Particle {
	public final double radius;
	public final double mass;
	protected final Vector2D velocity, pos;
	protected long lastTime;
	/** Used in resolveCollision() */
	// Normal is also a temp variable
	private Vector2D delta, mtd, normal;
	double length, vn;
	float im1, im2, k;
	
	public Particle(double radius, double mass) {
		this.pos = new Vector2D();
		this.velocity = new Vector2D();
		this.radius = radius;
		this.mass = mass;
		this.delta = new Vector2D();
		this.normal = new Vector2D();
		this.mtd = new Vector2D();
	}
	
	public void moveTo(double x, double y) {
		pos.x = x;
		pos.y = y;
	}

	public void setVelocity(double velX, double velY) {
		velocity.x = velX;
		velocity.y = velY;
	}
	
	// http://cgp.wikidot.com/circle-to-circle-collision-detection
	public boolean collidesWith(Particle other) {
		delta.set(other.pos);
		delta.sub(pos);
		return delta.length() < (other.radius + radius);
	}
	
	public void resolveEdgeCollision(double wallX, double wallY) {
		delta.set(pos);
		delta.x -= wallX;
		delta.y -= wallY;
		length = delta.length();
		// MTD - Minimum Translation Distance
		delta.mult((radius - length) / length);
		mtd.set(delta);
		// Move particles apart
		normal.set(mtd);
		pos.add(normal);
	}
	
	// http://stackoverflow.com/questions/345838/ball-to-ball-collision-detection-and-handling
	public void resolveCollision(Particle other) {
		delta.set(pos);
		delta.sub(other.pos);
		length = delta.length();
		// MTD - Minimum Translation Distance
		delta.mult((radius + other.radius - length) / length);
		mtd.set(delta);
		//IM - Inverse Mass
		im1 = 1 / (float)mass;
		im2 = 1 / (float)other.mass;
		// Move particles apart
		normal.set(mtd);
		normal.mult(im1 / (im1 + im2));
		pos.add(normal);
		normal.set(mtd);
		normal.mult(im2 / (im1 + im2));
		other.pos.sub(normal);
		// Impact Speed
		delta.set(velocity);
		delta.sub(other.velocity);
		normal.set(mtd);
		normal.normalize();
		vn = delta.dotProduct(normal);
		if(vn > 0) return;
		k = (float) ((-1.0f * vn) / (im1 + im2));
		mtd.mult(k);
		normal.set(mtd);
		normal.mult(im1);
		velocity.add(normal);
		normal.set(mtd);
		normal.mult(im2);
		other.velocity.sub(normal);
	}
		
	public void update(long now) {
		if (now < lastTime) return;
		// Make sure velocities are within a certain range
		if(velocity.x > Constants.MAX_VELOCITY)
			velocity.x = Constants.MAX_VELOCITY;
		if(velocity.y > Constants.MAX_VELOCITY)
			velocity.y = Constants.MAX_VELOCITY;
		if(velocity.x < Constants.MIN_VELOCITY)
			velocity.x = Constants.MIN_VELOCITY;
		if(velocity.y < Constants.MIN_VELOCITY)
			velocity.y = Constants.MIN_VELOCITY;
		pos.add(velocity);
		lastTime = now;
	}

	public void draw(Canvas canvas) {
		Constants.CIRCLE_PAINT.setARGB(255, Constants.RANDOM.nextInt(255), Constants.RANDOM.nextInt(255), Constants.RANDOM.nextInt(255));
		canvas.drawCircle((float)pos.x, (float)pos.y, (float)radius, Constants.CIRCLE_PAINT);
	}
		
	public boolean collidesWithEdge(int canvasWidth, int canvasHeight) {
		if (pos.x + radius >= canvasWidth) {  // Hit right side
			resolveEdgeCollision(canvasWidth, pos.y);
			return true;
		} else if(pos.x - radius <= 0) { // Hit left side
			resolveEdgeCollision(0, pos.y);
			return true;
		} 
        if (pos.y + radius >= canvasHeight) { // Hit bottom side
        	resolveEdgeCollision(pos.x, canvasHeight);
        	return true; 
        } else if(pos.y - radius <= 0) { // hit top side
        	resolveEdgeCollision(pos.x, 0);
        	return true; 
        }
        return false;
	}
	
	public void reset(int canvasWidth, int canvasHeight) {
		moveTo(Math.random()*canvasWidth, Math.random()*canvasHeight);
		// Ensures the entire cricle is on the screen
		if(pos.x - radius <= 0) {
			pos.x += radius;
		} else if(pos.x + radius >= canvasWidth) {
			pos.x -= radius;
		}
		
		if(pos.y - radius <= 0) {
			pos.y += radius;
		} else if(pos.y + radius >= canvasHeight) {
			pos.y -= radius;
		}
		
		setVelocity(Constants.MAX_VELOCITY * Math.random() * Math.pow(-1, Constants.RANDOM.nextInt() % 2), 
				Constants.MAX_VELOCITY * Math.random() * Math.pow(-1, Constants.RANDOM.nextInt() % 2));
	}
}