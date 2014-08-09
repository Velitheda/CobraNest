//A class for a thread that I copied mostly from an internet tutorial

package com.example.snake;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class SnakeThread extends Thread{
	
	private static final String TAG = SnakeThread.class.getSimpleName();
	private SurfaceHolder surfaceHolder;
	private SnakeGamePanel gamePanel;
	private boolean running;
	
	public SnakeThread(SurfaceHolder surfaceHolder, SnakeGamePanel gamePanel){
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public void setRunning(boolean running){
		this.running = running;
	}
	
	public boolean getRunning(){
		return running;
	}
	
	public void run(){
		Canvas canvas;
		long tickCount = 0L;
		Log.d(TAG, "Starting game loop");
		while (running){
			
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
			
		}
		Log.d(TAG, "Game loop executed " + tickCount + " times");
	}
}
