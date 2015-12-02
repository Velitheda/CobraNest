//Written by Jasmine Vickery

//This class is for a strawberry that the snake eats to grow longer 
//and eventually win the level
//They will randomly respwan somewhere else if too much time has elapsed or if they get eaten


package com.example.jasmine.cobranest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Strawberry extends Segment {

	public static final int SIZE = SnakeConstants.SIZE;
	public static final int STRAWBERRY_MOVE_RATE = SnakeConstants.STRAWBERRY_MOVE_RATE;

	private static final String TAG = Strawberry.class.getSimpleName();

	public Strawberry(Snake[] snakes, int numSnakes, Barrier[] barriers,
			int numBarriers, Rect screenArea) {
		super(1, 1, 1, screenArea);
		setClearPostion(snakes, numSnakes, barriers, numBarriers);

	}

	public void setClearPostion(Snake[] snakes, int numAISnakes,
			Barrier[] barriers, int barrierNumber) {
		boolean intersection = true;
		while (intersection) {
			setRandomPostion();
			if (!checkBarrierIntersection(barriers, barrierNumber))
				intersection = false;
			for (int i = 0; i < numAISnakes + 1; i++)
				if (snakes[i] != null && checkIntersection(snakes[i]))
					intersection = true;

		}
	}

	public boolean checkBarrierIntersection(Barrier[] barriers,
			int barrierNumber) {
		boolean intersects = false;
		for (int i = 0; i < barrierNumber; i++) {
			for (int j = 0; j < barriers[i].getRectangles().length; j++)
				if (barriers[i].getRectangles()[j].intersect(area)) {
					intersects = true;
				}
		}
		return intersects;
	}

	public Point getLocation() {
		return new Point(area.left, area.top);
	}

	public void checkIfEaten(Snake[] snakes, int numSnakes, Barrier[] barriers,
			int barrierNumber) {
		if (snakes[0] == null)
			return;
		if (area.intersect(snakes[0].getSnakeBody()[0].getArea())) {
			snakes[0].eatStrawberry();
			// this replaces the strawberry
			setClearPostion(snakes, numSnakes, barriers, barrierNumber);
		}
	}

	public boolean checkIntersection(Snake snake) {
		boolean intersects = false;
		for (int i = 0; i < snake.getSnakeLength(); i++) {
			if (area.intersect(snake.getSnakeBody()[i].getArea()))
				intersects = true;
		}
		return intersects;
	}

	public boolean checkCrashIntersection(Snake snake) {
		boolean intersects = false;
		for (int i = 1; i < snake.getSnakeLength(); i++) {
			if (area.intersect(snake.getSnakeBody()[0].getArea()))
				intersects = true;
		}
		return intersects;
	}

	public void setRandomPostion() {
		int x = getRandomXPostion();
		int y = getRandomYPostion();
		area.left = x;
		area.top = y;
		area.right = x + SIZE;
		area.bottom = y + SIZE;
	}

	public int getRandomXPostion() {
		return (int) (Math.random() * ((screenArea.width() - SIZE) / SIZE))
				* SIZE + screenArea.left;
	}

	public int getRandomYPostion() {
		return (int) (Math.random() * ((screenArea.height() - SIZE) / SIZE))
				* SIZE + screenArea.top;
	}

	public void drawStrawberry(Canvas canvas, Bitmap bitmap) {
		canvas.drawBitmap(bitmap, area.left, area.top, null);
	}

	public void randomlyMove(Snake[] snakes, int numSnakes, Barrier[] barriers,
			int barrierNumber) {
		if ((int) (Math.random() * 400) < STRAWBERRY_MOVE_RATE)
			setClearPostion(snakes, numSnakes, barriers, barrierNumber);
	}

}