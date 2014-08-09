//Written by Jasmine Vickery
//This class contains the main game loop and most of the logic for the actual running of the game

package com.example.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SnakeGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	public static final int SIZE = SnakeConstants.SIZE;

	private static final String TAG = SnakeGamePanel.class.getSimpleName();
	private SnakeThread thread;
	private Sprite sprite;

	private Snake snake;
	private Rect screenArea;
	private Strawberry[] strawberries;
	private int numStrawberries;
	private Barrier[] barriers;
	private int numBarriers;
	private Snake[] snakes;
	private int numAISnakes;
	private Spider[] spiders;
	private int numSpiders;
	private Teleport[] teleports;
	private int numTeleports;

	private int level;
	private int requiredPlayerLength;
	private int startingLength;
	private double timerDelay;
	private int spiderSpawningInterval;
	private boolean paused;

	private int score;
	private int highScore;
	private boolean lost;
	private boolean won;
	private int restartDelay;
	private boolean wantToRestart;

	private Point start;
	private Point end;
	private float previousX;
	private float previousY;
	private boolean fingerDown;
	private int count;
	private int secondCount;

	public SnakeGamePanel(Context context) {
		super(context);
		getHolder().addCallback(this);

		start = new Point();
		end = new Point();

		paused = true;

		getScreenArea(context);
		start();

		thread = new SnakeThread(getHolder(), this);

		Bitmap originalBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.blue);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 9, 9,
				false);// this scales a bitmap

		sprite = new Sprite(resizedBitmap, 10, 10);// starting coordinates

		setFocusable(true);
	}

	public void getScreenArea(Context context) {
		// gets the area of the screen, takes the remainder that cannot be fit,
		// splits it in half and buffers the edges with it
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float screenWidth = metrics.widthPixels;
		float screenHeight = metrics.heightPixels;
		float widthRemainder = screenWidth % SIZE;
		float heightRemainder = screenHeight % SIZE;
		screenWidth = screenWidth - widthRemainder / 2;
		screenHeight = screenHeight - heightRemainder / 2;
		screenArea = new Rect((int) (widthRemainder / 2),
				(int) (heightRemainder / 2), (int) screenWidth,
				(int) screenHeight);
	}

	public void start() {
		spiderSpawningInterval = 30;
		if (lost) {
			lost = false;
		} else {
			level = 1;
		}
		won = false;
		wantToRestart = false;
		restartDelay = 3;
		timerDelay = 8;// should be ten
		requiredPlayerLength = 12;
		startingLength = 5;// length actually in snake class
		numTeleports = 4;
		numSpiders = 1;
		numBarriers = 3;
		numStrawberries = 9;
		numAISnakes = 0;

		initaliseLevel();
	}

	public void initaliseLevel() {
		initaliseSnakes();
		initaliseBarriers();
		initaliseTeleports();
		initaliseStrawberries();
		initaliseSpiders();
		paused = false;
	}

	public void initaliseSnakes() {
		snakes = new Snake[10];
		snakes[0] = new Snake(screenArea);
		snake = snakes[0];
		for (int i = 1; i <= numAISnakes; i++) {
			snakes[i] = new SnakeAI(screenArea);
			Log.d(TAG, "Creating AI snake");
		}
	}

	public void initaliseBarriers() {
		barriers = new Barrier[10];
		for (int i = 0; i < numBarriers; i++) {
			barriers[i] = new Barrier(snake, screenArea);
		}
	}

	public void initaliseTeleports() {
		teleports = new Teleport[20];
		for (int i = 0; i < numTeleports; i++) {
			teleports[i] = new Teleport(screenArea);
		}
	}

	public void initaliseStrawberries() {
		strawberries = new Strawberry[1000];
		for (int i = 0; i < numStrawberries; i++) {
			strawberries[i] = new Strawberry(snakes, numAISnakes + 1, barriers,
					numBarriers, screenArea);
		}
	}

	public void initaliseSpiders() {
		spiders = new Spider[200];
		for (int i = 0; i < numSpiders; i++) {
			spiders[i] = new Spider(snakes, 1, screenArea);
		}
	}

	// Settings that change how the difficulty increases with each level
	public void goUpALevel() {
		spiderSpawningInterval -= 30;
		level++;
		requiredPlayerLength++;
		numStrawberries--;
		// numAISnakes++;
		// timerDelay--; //we want this
		numSpiders++;
		// numBarriers++;
		// numTeleports++;

		initaliseLevel();
	}

	public void run() {
		if (lost || won) {
			restartDelay--;
			if (restartDelay <= 0 && wantToRestart) {
				start();
			}
		}
		if (!paused && !lost && !won) {
			if (count % timerDelay == 0) {
				updateSnakes();
				checkIfSnakeAIsAteStrawberry();
				checkAISnakeCrash();
				updateStrawberries();
				updateSpiders();
				if (checkWin()) {
					goUpALevel();
				}
				checkLost();
				checkGameWon();
			}
			count++;
		}
	}

	public void updateSnakes() {
		updatePlayer();
		updateAISnakes();
	}

	public void updatePlayer() {
		snake.moveSnake(snake.getTotalDirection());
		for (int i = 1; i < numAISnakes; i++) {
			if (snake.headHitOther(snakes[i])) {
				snake.eatStrawberry();
				snakes[i].looseSegment();
			}
		}

		for (int i = 0; i < numTeleports; i++) {
			teleports[i].teleport(snake.getSnakeBody());
		}
		snake.jumpScreen();
	}

	public void updateAISnakes() {
		for (int i = 1; i <= numAISnakes; i++) {
			SnakeAI snakeThing = new SnakeAI(screenArea);
			snakeThing = (SnakeAI) snakes[i];
			snakeThing.setDirectionToTarget();
			checkTarget(snakeThing);
			if (snakes[i].getSnakeLength() < startingLength)
				snakes[i] = new SnakeAI(screenArea);
			snakes[i].moveSnake(snakes[i].getTotalDirection());
			for (int j = 1; j < numAISnakes; j++) {
				if (i == j)// a snake eating itself could cause some problems
							// ...
					continue;
				if (snakes[i].headHitOther(snakes[j])) {
					snakes[i].eatStrawberry();
					snakes[j].looseSegment();
				}
			}
			for (int j = 0; j < numTeleports; j++) {
				teleports[j].teleport(snakes[i].getSnakeBody());
			}
			snakes[i].jumpScreen();
		}
	}

	private void checkTarget(SnakeAI snakeAI) {
		boolean targetOk = false;
		for (int i = 0; i < numStrawberries; i++) {
			if (strawberries[i].getArea().contains(snakeAI.getTarget().x,
					snakeAI.getTarget().y))
				targetOk = true;
			if (!targetOk)
				snakeAI.setTarget(strawberries[(int) (Math.random() * numStrawberries)]
						.getLocation());
		}
	}

	private void checkIfSnakeAIsAteStrawberry() {
		for (int i = 0; i < numAISnakes; i++) {
			if (snakes[i] != null) {
				for (int j = 0; j < numStrawberries; j++) {
					if (strawberries[j].getArea().intersect(
							snakes[i].getSnakeBody()[0].getArea())) {
						snakes[i].eatStrawberry();
						strawberries[j].setClearPostion(snakes, numAISnakes,
								barriers, numBarriers);
						SnakeAI tempSnake = new SnakeAI(screenArea);
						tempSnake = (SnakeAI) snakes[i];
						tempSnake
								.setTarget(strawberries[(int) (Math.random() * numStrawberries)]
										.getLocation());
					}
				}
			}
		}
	}

	private void checkAISnakeCrash() {
		for (int i = 1; i < numAISnakes; i++) {

			for (int j = 0; j < numBarriers; j++)
				if (snakes[i] != null
						&& barriers[j].checkIntersection(snakes[i])) {
					snakes[i] = null;
				}
		}
	}

	public void updateStrawberries() {
		for (int i = 0; i < numStrawberries; i++) {
			strawberries[i].checkIfEaten(snakes, numAISnakes, barriers,
					numBarriers);
			strawberries[i].randomlyMove(snakes, numAISnakes, barriers,
					numBarriers);
		}
	}

	public void updateSpiders() {
		for (int i = 0; i < numSpiders; i++) {
			spiders[i].moveSpider();
			spiders[i].jumpScreen();
			spiders[i].setDirection((int) (count / timerDelay));
			spiders[i].selectTarget(snakes, numAISnakes + 1);
			for (int j = 0; j < numTeleports; j++) {
				teleports[j].teleport(spiders[i]);
			}
			// just player for now
			if (spiders[i].checkPlayerIntersection(snake)) {
				// adding this bit of code below will instead make the player shorter
				// instead of punishing them with instant death
				
				// snake.looseSegment();

				lost = true;
				// spiders[i].setSquashed(true);
			}
			if (spiders[i].checkHitWall()) {
				spiders[i].setSquashed(true);
			}
			spiders[i].possiblyAppear(snakes, spiderSpawningInterval,
					numAISnakes + 1);
			spiders[i].checkBarrierIntersection(barriers, numBarriers);

		}
	}

	public boolean checkWin() {
		return snake.getSnakeLength() >= requiredPlayerLength;
	}

	public boolean checkLost() {
		if (snake.checkCrash())
			lost = true;
		for (int i = 0; i < numBarriers; i++) {
			if (barriers[i].checkIntersection(snake))
				lost = true;
		}
		// probably need an independant variable for
		// checking the minimum length the snake can be
		if (snake.getSnakeLength() < startingLength - 2)
			lost = true;

		return lost;
	}

	public boolean checkGameWon() {
		if (level > 8) {
			won = true;
		}
		return won;
	}

	public Rect getScreenArea() {
		return screenArea;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "Surface Changed");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "Surface Created");
		if (!thread.getRunning()) {
			thread.setRunning(true);
			thread.start();
		}
	}

	public void pause() {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				Log.d(TAG, "trying to pause " + retry);
				thread.join();

				// thread.wait();
				Log.d(TAG, "Paused");
				retry = false;
				// keep trying again with the loop
			} catch (Exception e) {
				Log.d(TAG, "Exception" + e.toString());
			}
			Log.d(TAG, "Pausing");
		}
	}

	public void resume() {
		Log.d(TAG, "Resuming" + paused);
		setFocusable(true);
		if (thread == null || !thread.getRunning()) {
			thread = new SnakeThread(getHolder(), this);
			thread.setRunning(true);
			thread.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {

				thread.join();
				retry = false;
			} catch (Exception e) {// normally interrupted
				// keep trying again with the loop
			}
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "Touched");
		if (lost || won) {
			wantToRestart = true;
		}

		// current code handles moving in different directions without lifting
		// your finger,
		// while commented out code requires you to lift your finger each time
		// you wish to change direction
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			fingerDown = true;
			// start.x = (int)event.getX();
			// start.y = (int)event.getY();
			sprite.handleActionDown((int) event.getX(), (int) event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float x = event.getX();
			float y = event.getY();

			// float dx = x - previousX;
			// float dy = y = previousY;
			start = new Point((int) previousX, (int) previousY);
			end = new Point((int) x, (int) y);

			if (sprite.isTouched()) {
				sprite.setX((int) event.getX());
				sprite.setY((int) event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// fingerDown = false;
			// end.x = (int)event.getX();
			// end.y = (int)event.getY();
			if (sprite.isTouched()) {
				sprite.setTouched(false);
			}
		}
		snake.setDirectionWhenTouched(start, end);
		previousX = end.x;
		previousY = end.y;
		return true;
	}

	protected void doDraw(Canvas canvas) {
		if (paused) {
			return;
		}
		canvas.drawColor(Color.BLACK);
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);

		drawBarriers(canvas);
		drawPlayer(canvas);
		drawAISnakes(canvas);
		drawStrawberries(canvas);
		drawSpiders(canvas);
		drawTeleports(canvas);
		drawInfo(canvas, paint);
		drawWin(canvas, paint);
		drawLost(canvas, paint);
	}

	// Need to create a bitmap for this
	public void drawWin(Canvas canvas, Paint paint) {
		if (won) {
			paint.setColor(Color.WHITE);
			paint.setTextSize(30);

			// the paint uses the point given as the center
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("You Win!", screenArea.right / 2,
					screenArea.bottom / 2, paint);
		}
	}

	public void drawLost(Canvas canvas, Paint paint) {
		if (lost) {
			paint.setColor(Color.WHITE);
			paint.setTextSize(30);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("You Lose!", screenArea.right / 2,
					screenArea.bottom / 2, paint);
		}
	}

	// draws a grid on the screen, useful for debugging
	public void drawGrid(Canvas canvas, Paint paint) {
		paint.setColor(Color.DKGRAY);
		for (int i = 0; i <= screenArea.right; i += SIZE) {// vertical lines
			canvas.drawLine(i + screenArea.left, screenArea.top, i
					+ screenArea.left, screenArea.bottom, paint);
		}
		for (int i = 0; i <= screenArea.bottom; i += SIZE) {// horizontal lines
			canvas.drawLine(screenArea.left, i + screenArea.top,
					screenArea.right, i + screenArea.top, paint);
		}
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
	}

	public void drawInfo(Canvas canvas, Paint paint) {
		paint.setColor(Color.WHITE);
		paint.setTextSize(12);
		canvas.drawText("Level " + level, 3, 12, paint);
	}

	public void drawPlayer(Canvas canvas) {
		Bitmap originalBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.bluetile);
		Bitmap snakeHeadBitmap = Bitmap.createScaledBitmap(originalBitmap,
				SIZE, SIZE, false);

		Bitmap originalBodyBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.lightbluetile);
		Bitmap snakeBodyBitmap = Bitmap.createScaledBitmap(originalBodyBitmap,
				SIZE, SIZE, false);
		snake.drawSnake(canvas, snakeHeadBitmap, snakeBodyBitmap);
	}

	public void drawAISnakes(Canvas canvas) {
		Bitmap originalBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.greytile);
		Bitmap snakeHeadBitmap = Bitmap.createScaledBitmap(originalBitmap,
				SIZE, SIZE, false);

		Bitmap originalBodyBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.greytile2);
		Bitmap snakeBodyBitmap = Bitmap.createScaledBitmap(originalBodyBitmap,
				SIZE, SIZE, false);

		for (int i = 1; i <= numAISnakes; i++) {
			snakes[i].drawSnake(canvas, snakeHeadBitmap, snakeBodyBitmap);
		}
	}

	public void drawSpiders(Canvas canvas) {
		Bitmap originalBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.greentile);
		Bitmap spiderBitmap = Bitmap.createScaledBitmap(originalBitmap, SIZE,
				SIZE, false);
		for (int i = 0; i < numSpiders; i++) {
			if (!spiders[i].getSquashed())
				spiders[i].drawSpider(canvas, spiderBitmap);
		}
	}

	public void drawTeleports(Canvas canvas) {
		Bitmap originalBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.magentatile);
		Bitmap teleportBitmap = Bitmap.createScaledBitmap(originalBitmap, SIZE,
				SIZE, false);
		for (int i = 0; i < numTeleports; i++) {
			teleports[i].drawTeleport(canvas, teleportBitmap);
		}
	}

	public void drawBarriers(Canvas canvas) {
		Bitmap originalBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.greytile3);
		Bitmap barrierBitmap = Bitmap.createScaledBitmap(originalBitmap, SIZE,
				SIZE, false);
		for (int i = 0; i < numBarriers; i++) {
			barriers[i].drawBarrier(canvas, barrierBitmap);
		}
	}

	public void drawStrawberries(Canvas canvas) {
		Bitmap originalBitmap = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.redtile);
		Bitmap strawberryBitmap = Bitmap.createScaledBitmap(originalBitmap,
				SIZE, SIZE, false);
		for (int i = 0; i < numStrawberries; i++) {
			strawberries[i].drawStrawberry(canvas, strawberryBitmap);
		}
	}

	public boolean getPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
		Log.d(TAG, "Paused: " + this.paused);
	}
}