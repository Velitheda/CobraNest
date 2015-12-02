//Written by Jasmine Vickery
//This class forms walls for the player to avoid,
//and they are randomly positioned anew each level, 
//and also randomly generate corners in themselves.


package com.example.jasmine.cobranest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class Barrier {

	public static final int SIZE = SnakeConstants.SIZE;

	public static final int LEFT = SnakeConstants.LEFT;
	public static final int RIGHT = SnakeConstants.RIGHT;
	public static final int UP = SnakeConstants.UP;
	public static final int DOWN = SnakeConstants.DOWN;

	public static final int BARRIER_LENGTH = SnakeConstants.BARRIER_LENGTH;
	public static final int BARRIER_CHANGE = SnakeConstants.BARRIER_CHANGE;

	private Rect[] blocks;
	private Point startingPoint;
	private Rect screenArea;

	public Barrier(Snake snake, Rect screenArea) {
		this.screenArea = screenArea;
		blocks = new Rect[BARRIER_LENGTH];
		setRandomPostion();
		initaliseBlocks();
		setStartingPoint(snake);

	}

	public void initaliseBlocks() {
		int currentDirection = startingDirection();
		for (int i = 1; i < blocks.length; i++) {

			currentDirection = addDirection(currentDirection, i);
			while (!checkDirection(blocks[i])) {
				currentDirection = addDirection(currentDirection, i);
			}
		}
	}

	public int startingDirection() {
		return (int) (Math.random() * 4);
	}

	public int changeDirection(int currentDirection) {
		if ((int) (Math.random() * 40) < BARRIER_CHANGE)
			currentDirection = createDirection(currentDirection);
		return currentDirection;
	}

	public int createDirection(int currentDirection) {
		int newDirection = (int) (Math.random() * 4);

		while (checkOverlap(currentDirection, newDirection))
			newDirection = (int) (Math.random() * 4);

		return newDirection;
	}

	public boolean checkOverlap(int currentDirection, int newDirection) {
		if (newDirection == LEFT && currentDirection == RIGHT)
			return true;
		else if (newDirection == RIGHT && currentDirection == LEFT)
			return true;
		else if (newDirection == UP && currentDirection == DOWN)
			return true;
		else if (newDirection == DOWN && currentDirection == UP)
			return true;

		return false;
	}

	public boolean checkDirection(Rect block) {
		if (!block.intersect(screenArea)) {
			return false;
		}
		return true;
	}

	public int addDirection(int currentDirection, int i) {
		int direction = changeDirection(currentDirection);
		if (direction == LEFT)
			blocks[i] = new Rect(blocks[i - 1].left - SIZE, blocks[i - 1].top,
					blocks[i - 1].left + SIZE, blocks[i - 1].top + SIZE);
		else if (direction == RIGHT)
			blocks[i] = new Rect(blocks[i - 1].left + SIZE, blocks[i - 1].top,
					blocks[i - 1].left + SIZE + SIZE, blocks[i - 1].top + SIZE);
		else if (direction == UP)
			blocks[i] = new Rect(blocks[i - 1].left, blocks[i - 1].top - SIZE,
					blocks[i - 1].left + SIZE, blocks[i - 1].top);
		else // down
			blocks[i] = new Rect(blocks[i - 1].left, blocks[i - 1].top + SIZE,
					blocks[i - 1].left + SIZE, blocks[i - 1].top + SIZE + SIZE);
		return direction;
	}

	public void setStartingPoint(Snake snake) {
		while (checkIntersection(snake)) {
			setRandomPostion();
			initaliseBlocks();
		}
	}

	public boolean checkIntersection(Snake snake) {
		if (snake == null)
			return false;
		boolean intersects = false;
		for (int i = 0; i < snake.getSnakeLength(); i++) {
			for (int j = 0; j < blocks.length; j++)
				if (blocks[j].intersect(snake.getSnakeBody()[i].getArea()))
					intersects = true;
		}
		return intersects;
	}

	public Rect[] getRectangles() {
		return blocks;
	}

	public void setRandomPostion() {
		int x = getRandomXPostion();
		int y = getRandomYPostion();
		blocks[0] = new Rect(x, y, x + SIZE, y + SIZE);
	}

	public int getRandomXPostion() {
		return (int) (Math.random() * ((screenArea.width() - SIZE) / SIZE))
				* SIZE + screenArea.left;
	}

	public int getRandomYPostion() {
		return (int) (Math.random() * ((screenArea.height() - SIZE) / SIZE))
				* SIZE + screenArea.top;
	}

	public void drawBarrier(Canvas canvas, Bitmap bitmap) {
		for (int i = 0; i < blocks.length; i++)
			canvas.drawBitmap(bitmap, blocks[i].left, blocks[i].top, null);
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < blocks.length; i++) {
			s += blocks[i];
		}
		return s;
	}

}