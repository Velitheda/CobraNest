//Written by Jasmine Vickery
//This is the class for the snake.  It is made up of an array of segments 
//and controls how they move an follow each other and handles collisions.

package com.example.jasmine.cobranest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Snake {

	public static final int LEFT = SnakeConstants.LEFT;
	public static final int RIGHT = SnakeConstants.RIGHT;
	public static final int UP = SnakeConstants.UP;
	public static final int DOWN = SnakeConstants.DOWN;

	public static final int SNAKE_START_X = SnakeConstants.SNAKE_START_X;
	public static final int SNAKE_START_Y = SnakeConstants.SNAKE_START_Y;

	public static final int SIZE = SnakeConstants.SIZE;

	protected static final String TAG = Snake.class.getSimpleName();
	protected int[] directions;
	protected int totalDirection;
	protected Segment[] snakeBody;
	protected int snakeLength;
	protected boolean dieIfHitWall;
	protected Rect screenArea;
	protected int tickDirection;

	public Snake(Rect screenArea) {
		this.screenArea = screenArea;
		directions = new int[100];
		snakeLength = 5;
		snakeBody = new Segment[100];

		// this is not setting it clear of barriers
		initialiseSnake(getRandomXPostion(), getRandomYPostion());
		dieIfHitWall = false;
	}

	public void initialiseSnake(int x, int y) {
		for (int i = 0; i < snakeLength; i++) {
			directions[i] = LEFT;
			snakeBody[i] = new Segment((x + i * SIZE) % screenArea.width(), y,
					directions[i], screenArea);
		}
		jumpScreen();// needed cause run into problems if part of snake
						// initialized off screen.
	}

	public int getRandomXPostion() {
		return (int) (Math.random() * ((screenArea.width() - SIZE) / SIZE))
				* SIZE + screenArea.left;
	}

	public int getRandomYPostion() {
		return (int) (Math.random() * ((screenArea.height() - SIZE) / SIZE))
				* SIZE + screenArea.top;
	}

	public int getTotalDirection() {
		return totalDirection;
	}

	public void setTotalDirection(int totalDirection) {

		if (totalDirection == LEFT && tickDirection != RIGHT) {
			this.totalDirection = totalDirection;
		} else if (totalDirection == RIGHT && tickDirection != LEFT) {
			this.totalDirection = totalDirection;
		} else if (totalDirection == UP && tickDirection != DOWN) {
			this.totalDirection = totalDirection;
		} else if (totalDirection == DOWN && tickDirection != UP) {
			this.totalDirection = totalDirection;
		}
	}

	public void moveSnake(int direction) {
		tickDirection = direction;
		adjustDirections(direction);
		for (int i = 0; i < snakeLength; i++) {
			snakeBody[i].moveSegment(directions[i]);
		}
	}

	public void setDirectionWhenTouched(Point start, Point end) {
		Point direction = new Point(end.x - start.x, end.y - start.y);
		double angle = Math.toDegrees(Math.atan2(direction.y, direction.x));
		setTotalDirection(chooseDirection((int) angle));
	}

	public int chooseDirection(int angle) {
		if ((angle > 135 && angle <= 180) || (angle < -135 && angle >= -180))
			return LEFT;
		else if (angle < 45 && angle > -45)
			return RIGHT;
		else if (angle <= -45 && angle >= -135)
			return UP;
		else
			// if(angle >= 45 && angle <= 135)
			return DOWN;
	}

	public void adjustDirections(int direction) {

		for (int i = snakeLength; i > 0; i--) {
			directions[i] = directions[i - 1];
		}
		directions[0] = direction;

	}

	public void jumpScreen() {
		if (dieIfHitWall) {
			return;
		}
		Point p = new Point();
		for (int i = 0; i < snakeLength; i++)
			if (!snakeBody[i].getArea().intersect(screenArea)) {
				snakeBody[i].jumpCoordinates();
			}
	}

	public void eatStrawberry() {
		int extraX = 0;
		int extraY = 0;

		if (directions[snakeLength - 1] == LEFT)
			extraX += SIZE;
		if (directions[snakeLength - 1] == RIGHT)
			extraX -= SIZE;
		if (directions[snakeLength - 1] == UP)
			extraY += SIZE;
		if (directions[snakeLength - 1] == DOWN)
			extraY -= SIZE;

		snakeBody[snakeLength] = new Segment(
				snakeBody[snakeLength - 1].getXPos() + extraX,
				snakeBody[snakeLength - 1].getYPos() + extraY,
				directions[snakeLength - 1], screenArea);
		snakeLength++;
		Log.d(TAG, "Eating strawberry");
	}

	public void drawSnake(Canvas canvas, Bitmap headBitmap, Bitmap bodyBitmap) {
		snakeBody[0].drawSegment(canvas, headBitmap);
		for (int i = 1; i < snakeLength; i++) {
			snakeBody[i].drawSegment(canvas, bodyBitmap);
		}
	}

	public int[] getDirections() {
		return directions;
	}

	public int getSnakeLength() {
		return snakeLength;
	}

	public Segment[] getSnakeBody() {
		return snakeBody;
	}

	public Point getHeadPoint() {
		Point p = new Point(snakeBody[0].getArea().left,
				snakeBody[0].getArea().top);
		return p;
	}

	public boolean checkCrash() {
		if ((dieIfHitWall && checkHitWall()) || checkHitSelf()) {
			return true;
		}
		return false;
	}

	public boolean checkHitSelf() {
		for (int i = 1; i < snakeLength; i++)
			if (snakeBody[i].getArea().contains(snakeBody[0].getPoint().x,
					snakeBody[0].getPoint().y)) {
				return true;
			}
		return false;
	}

	public boolean checkHitWall() {
		for (int i = 0; i < snakeLength; i++)
			if (!snakeBody[i].getArea().intersect(screenArea)) {
				return true;
			}
		return false;
	}

	public boolean snakeHitSnake(Snake other) {
		for (int i = 0; i < snakeLength; i++)
			for (int j = 0; j < other.getSnakeLength(); j++)
				if (snakeBody[i].getArea().intersect(
						other.getSnakeBody()[j].getArea()))
					return true;
		return false;
	}

	public boolean headHitOther(Snake other) {
        Log.d(TAG, "Snake: " + snakeBody);
       Log.d(TAG, "Other: " + other);
       if(other != null) {
            for (int i = 0; i < other.getSnakeLength(); i++)
                if (snakeBody[0].getArea().intersect(
                        other.getSnakeBody()[i].getArea()))
                    return true;
       }
		return false;
	}

	public boolean headHitHead(Snake other) {
		if (snakeBody[0].getArea().intersect(other.getSnakeBody()[0].getArea()))
			return true;
		return false;
	}

	public void looseSegment() {
		--snakeLength;
		snakeBody[snakeLength] = null;
		Log.d(TAG, "Loosing segment");
	}
}