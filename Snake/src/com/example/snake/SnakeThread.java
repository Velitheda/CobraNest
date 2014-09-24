//This class handles the thread
// Jasmine Vickery


package com.example.snake;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class SnakeThread extends Thread{
	
	private static final String TAG = SnakeThread.class.getSimpleName();
	private SurfaceHolder surfaceHolder;
	private SnakeGamePanel gamePanel;
	private boolean paused;
	private Object pauseLock = new Object();
	
	public SnakeThread(SurfaceHolder surfaceHolder, SnakeGamePanel gamePanel){
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public void onResume() {
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll();
		}
	}
	
	public void onPause() {
		synchronized (pauseLock) {
			paused = true;
		}
	}
	
	public void run(){
		Canvas canvas;
		long tickCount = 0L;
		Log.d(TAG, "Starting game loop");
		while (true){
			
			canvas = null;
			try{
				canvas= this.surfaceHolder.lockCanvas(null);
				synchronized(surfaceHolder){
					this.gamePanel.doDraw(canvas);
				}
			}finally{
				if(canvas != null){
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
			gamePanel.run();
			tickCount++;
			Log.d(TAG, "Game loop executed " + tickCount + " times");
			
			synchronized (pauseLock) {
				while (paused) {
					try {
						pauseLock.wait();
					} catch (InterruptedException e) {

					}
				}
			}
			
		}
		
	}
}
