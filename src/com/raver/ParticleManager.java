package com.raver;

import android.graphics.Canvas;
import android.util.Log;

public class ParticleManager implements Constants {
	
	private final Particle[] particles;
	private int i, j;
	private int canvasWidth, canvasHeight, halfWidth, halfHeight;
	// Used to figure out elapsed time between frames
    private long mLastTime;
    // Current time
	private long now;
	// Temp variables
	Particle p1, p2;
	private byte state;
	private double t;
	
	public ParticleManager() {
		// load the particles 
        particles = new Particle[NUM_PARTICLES];
        for(i = 0; i < NUM_PARTICLES; i++) {
        	particles[i] = new Particle(30, 30);
        }
        state = STARTCOLLIDE;
        t = 0;
	}
	
	public void reset() {
		for(i = 0; i < NUM_PARTICLES; i++) {
    		particles[i].reset(canvasWidth, canvasHeight);
		}
		mLastTime = System.currentTimeMillis() + 100;
		state = STARTCOLLIDE;
	}
	
	public void setCanvasDimensions(int width, int height) {
		canvasWidth = width;
        canvasHeight = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
        reset();
	}
	
	public void draw(Canvas canvas) {
		// Draw random color
		canvas.drawARGB(255, RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255));
		// Draw Particles
        for(i = 0; i <  NUM_PARTICLES; i++) particles[i].draw(canvas);
        mLastTime = now;
	}
	
	public void updatePhysics() {
		now = System.currentTimeMillis();
        if (mLastTime > now) return;
		switch(state) {
		case STOP:
			break;
		case STARTCOLLIDE:
			startCollision();
			break;
		case COLLIDE:
			updateCollidePhysics();
			break;
		case STARTFIGURE8:
			startFigure8();
			break;
		case FIGURE8:
			updateFigure8Physics();
			break;
		}
	}
	
	public void startCollision() {
		reset();
		state = COLLIDE;
	}
	
	public void updateCollidePhysics() {
        // Determine if there are any collisions
        // http://www.kirupa.com/developer/actionscript/multiple_collision2.htm
        for(i = 0; i < NUM_PARTICLES; i++) {
        	p1 = particles[i];
        	p1.update(now);
        	if(p1.collidesWithEdge(canvasWidth, canvasHeight)) {
        		p1.velocity.mult(-1);
        	}
        	for(j = i + 1; j < NUM_PARTICLES; j++) {
        		p2 = particles[j];
        		if(p1.collidesWith(p2))
        			p1.resolveCollision(p2);
        	}
        }
    }
	
	public void startFigure8() {
		for(i = 0; i < NUM_PARTICLES; i++) {
			particles[i].moveTo(figure8X(i * DISTANCE), figure8Y(i * DISTANCE));
		}
		state = FIGURE8;
	}
	
	public double figure8X(double t) {
		return (halfWidth) * Math.sin(PI_2 * t) + halfWidth;
	}
	
	public double figure8Y(double t) {
		return (halfHeight) * Math.cos(PI_4 * t) + halfHeight;
	}
	
	public void updateFigure8Physics() {
		for(i = 0; i < NUM_PARTICLES; i++) {
			particles[i].moveTo(figure8X(DISTANCE * i + t), figure8Y(DISTANCE * i + t));
			t += INTERVAL;
			if(t > REVOLUTION) t = 0;
		}
		
	}
	
	public void nextState() {
		switch(state) {
		case COLLIDE:
			Log.i("PMgr", "Switching to Figure 8");
			state = STARTFIGURE8;
			break;
		case FIGURE8:
			Log.i("PMgr", "Switching to Collisions");
			state = STARTCOLLIDE;
			break;
		}
	}
}
