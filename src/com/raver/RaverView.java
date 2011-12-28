package com.raver;

/**
 * This file is a heavily modified version of "AnimatedGameLoopView.java" from
 * COMP425 at CSUCI
 *
 */

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class RaverView extends SurfaceView implements SurfaceHolder.Callback {
	boolean retry;
	
	class TheGameLoopThread extends Thread {
	    // Indicate whether the surface has been created & is ready to draw
	    private boolean mRun = false;
	    // Handle to the surface manager object we interact with
	    private final SurfaceHolder mSurfaceHolder;
	    private final ParticleManager mgr;
	    private Canvas canvas;
	
	    public TheGameLoopThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
	        // get handles to some important objects
	        mSurfaceHolder = surfaceHolder;
			mgr = new ParticleManager();
			canvas = null;
	    }
	
	    public void doStart() {
	        synchronized (mSurfaceHolder) {
	        	mgr.reset();
	        }
	    }
	    
	    public void onTouchEvent(MotionEvent event) {
	    	if (event.getAction() == MotionEvent.ACTION_DOWN){
	    		mgr.nextState();
	    	}
		}
		
		@Override
	    public void run() {
			// Initialize
	    	doStart();
	    	// Start game loop
	        while (mRun) {
	            try {
	                canvas = mSurfaceHolder.lockCanvas(null);
	                synchronized (mSurfaceHolder) {
	                    mgr.updatePhysics();
	                    mgr.draw(canvas);
	                }
	            } finally {
	                // do this in a finally so that if an exception is thrown
	                // during the above, we don't leave the Surface in an
	                // inconsistent state
	                if (canvas != null) {
	                    mSurfaceHolder.unlockCanvasAndPost(canvas);
	                }
	            }
	        }
	    }
	
	    /**
	     * Used to signal the thread whether it should be running or not.
	     * Passing true allows the thread to run; passing false will shut it
	     * down if it's already running. Calling start() after this was most
	     * recently called with false will result in an immediate shutdown.
	     * 
	     * @param b true to run, false to shut down
	     */
	    public void setRunning(boolean b) {
	        mRun = b;
	    }
		
	    /* Callback invoked when the surface dimensions change. */
	    public void setSurfaceSize(int width, int height) {
	        // synchronized to make sure these all change atomically
	        synchronized (mSurfaceHolder) {
	            mgr.setCanvasDimensions(width, height);
	        }
	    }
	}

	private TheGameLoopThread thread;

	public RaverView(Context context, AttributeSet attrs) {
		super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new TheGameLoopThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            	// process any messages
            }
        });
        setFocusable(true); // make sure we get key events
	}

	/* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		// Pass the touch event on to our thread
		thread.onTouchEvent(event);
		return true;
	}
}