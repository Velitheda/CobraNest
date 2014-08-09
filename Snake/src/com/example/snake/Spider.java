//Written by Jasmine Vickery
//This class spawns a small creature that chases the player
//and will kill the player if it catches it.

package com.example.snake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Spider extends Segment {

	public static final int LEFT = SnakeConstants.LEFT;
	public static final int RIGHT = SnakeConstants.RIGHT;
	public static final int UP = SnakeConstants.UP;
	public static final int DOWN = SnakeConstants.DOWN;

	public static final int SNAKE_START_X = SnakeConstants.SNAKE_START_X;
	public static final int SNAKE_START_Y = SnakeConstants.SNAKE_START_Y;

	public static final int SIZE = SnakeConstants.SIZE;

	private static final String TAG = SnakeGamePanel.class.getSimpleName();

	private boolean dieIfHitWall;
	private int spiderDirection;
	private boolean squashed;
	private Snake closestSnake;

	public Spider(Snake[] snakes, int numSnakes, Rect screenArea) {

		super(screenArea);// numbers get changed
		this.screenArea = screenArea;
		squashed = false;
		dieIfHitWall = true;
		int x = getRandomXPostion();
		int y = getRandomYPostion();
		selectTarget(snakes, numSnakes);
		spiderDirection = UP;

		setClearPostion(snakes, numSnakes);

	}

	public void setSquashed(boolean squashed) {
		this.squashed = squashed;
	}

	public boolean getSquashed() {
		return squashed;
	}

	public void setClearPostion(Snake[] snakes, int numSnakes) {
		while (checkIntersection(snakes, numSnakes)
				|| setAwayFromSnake(snakes, numSnakes)) {
			setRandomPostion();
		}
	}

	public boolean setAwayFromSnake(Snake[] snakes, int numSnakes) {
		for (int i = 0; i < numSnakes; i++) {
			if (snakes[i] != null) {
				Point p = new Point(snakes[i].getHeadPoint());
				Rect r = new Rect(p.x - SIZE * 5, p.y - SIZE * 5, p.x + SIZE
						* 5, p.y + SIZE * 5);
				if (r.intersect(area))
					return true;
			}
		}
		return false;
	}

	public void jumpScreen() {
		if (dieIfHitWall) {
			return;
		}
		if (!area.intersect(screenArea)) {
			jumpCoordinates();
		}
	}

	public int getSpiderDirection() {
		return spiderDirection;
	}

	public boolean checkBarrierIntersection(Barrier[] barriers,
			int barrierNumber) {
		boolean intersects = false;
		for (int i = 0; i < barrierNumber; i++) {
			for (int j = 0; j < barriers[i].getRectangles().length; j++)
				if (barriers[i].getRectangles()[j].intersect(area)) {
					intersects = true;
					squashed = true;
				}
		}
		return intersects;
	}

	public void selectTarget(Snake[] snakes, int numSnakes) {
		if (snakes[0] == null)
			closestSnake = snakes[1];
		else
			closestSnake = snakes[0];

		int distance = getDistance(closestSnake);

		for (int i = 0; i < numSnakes; i++) {
			if (snakes[i] != null && getDistance(snakes[i]) < distance) {
				distance = getDistance(snakes[i]);
				closestSnake = snakes[i];
			}
		}
	}

	public int getDistance(Snake snake) {
		if (snake == null)
			return screenArea.width() * 10;
		Point location = snake.getHeadPoint();
		return (int) Math.sqrt((int) Math.pow(2.0,
				(location.x - area.left + 0.0))
				+ (int) Math.pow(2.0, (location.y - area.top + 0.0)));
	}

	public void moveSpider() {
		if (spiderDirection == LEFT) {
			area.left -= SIZE;
			area.right -= SIZE;
		}
		if (spiderDirection == RIGHT) {
			area.left += SIZE;
			area.right += SIZE;
		}
		if (spiderDirection == UP) {
			area.top -= SIZE;
			area.bottom -= SIZE;
		}
		if (spiderDirection == DOWN) {
			area.top += SIZE;
			area.bottom += SIZE;
		}
	}

	public void jumpCoordinates() {
		if (spiderDirection == LEFT) {
			area.left = screenArea.right - SIZE;
			area.right = screenArea.right;
		}
		if (spiderDirection == RIGHT) {
			area.left = screenArea.left;
			area.right = SIZE + screenArea.left;
		}
		if (spiderDirection == UP) {
			area.top = screenArea.bottom - SIZE;
			area.bottom = screenArea.bottom;
		}
		if (spiderDirection == DOWN) {
			area.top = screenArea.top;
			area.bottom = SIZE + screenArea.top;
		}
	}

	public void setDirection(int count) {
		Point p = new Point();
		if (closestSnake == null)
			return;
		p = closestSnake.getHeadPoint();
		int change = (int) (Math.random() * 20);
		if (p.x == area.left)
			setYDirection(p);
		else if (p.y == area.top)
			setXDirection(p);
		else if (change < 2) {
			if (change == 0)
				setXDirection(p);
			else if (change == 1)
				setYDirection(p);
		}
	}

	public void setXDirection(Point p) {
		int difference = area.left - p.x;

		if (dieIfHitWall) {
			if (difference >= 0)
				spiderDirection = LEFT;
			else
				spiderDirection = RIGHT;
		} else {
			if (difference <= screenArea.width() / 2 && difference >= 0)
				spiderDirection = LEFT;
			if (difference > screenArea.width() / 2 && difference < 0)
				spiderDirection = LEFT;
			if (difference > screenArea.width() / 2 && difference > 0)
				spiderDirection = RIGHT;
			if (difference <= screenArea.width() / 2 && difference <= 0)
				spiderDirection = RIGHT;
		}
	}

	public void setYDirection(Point p) {
		int difference = area.top - p.y;

		if (dieIfHitWall) {
			if (difference >= 0)
				spiderDirection = UP;
			else
				spiderDirection = DOWN;
		} else {
			if (difference <= screenArea.height() / 2 && difference >= 0)
				spiderDirection = UP;
			if (difference > screenArea.height() / 2 && difference < 0)
				spiderDirection = UP;
			if (difference > screenArea.height() / 2 && difference > 0)
				spiderDirection = DOWN;
			if (difference <= screenArea.height() / 2 && difference <= 0)
				spiderDirection = DOWN;
		}
	}

	public boolean checkCrash(Snake snake) {
		if ((dieIfHitWall && checkHitWall()) || checkPlayerIntersection(snake))
			return true;
		return false;
	}

	public boolean checkHitWall() {
		if (!area.intersect(screenArea))
			return true;
		return false;
	}

	public void checkIfEaten(Snake[] snakes, int numSnakes) {
		for (int i = 0; i < numSnakes; i++) {
			if (area.intersect(snakes[i].getSnakeBody()[0].getArea())) {
				snakes[i].eatStrawberry();
				setClearPostion(snakes, numSnakes);
			}
		}
	}

	public boolean checkIntersection(Snake[] snakes, int numSnakes) {
		boolean intersects = false;
		if (squashed)
			return false;
		for (int i = 0; i < numSnakes; i++)
			if (snakes[i] != null) {
				for (int j = 0; j < snakes[i].getSnakeLength(); j++) {
					if (area.intersect(snakes[i].getSnakeBody()[j].getArea()))
						intersects = true;
				}
			}
		return intersects;
	}

	public boolean checkPlayerIntersection(Snake snake) {
		if (snake == null)
			return false;
		if (squashed)
			return false;
		boolean intersects = false;
		for (int i = 0; i < snake.getSnakeLength(); i++) {
			if (area.intersect(snake.getSnakeBody()[i].getArea()))
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

	public void drawSpider(Canvas canvas, Bitmap bitmap) {
		canvas.drawBitmap(bitmap, area.left, area.top, null);
	}

	public void possiblyAppear(Snake[] snakes, int tickSpeed, int numSnakes) {
		if (squashed) {
			if ((int) (Math.random() * tickSpeed) < 6) {
				squashed = false;
				setRandomPostion();
				setClearPostion(snakes, numSnakes);
				Log.d(TAG, "Spider Appearing");
			}
		}
	}

	public void possiblyDissapear(int tickSpeed) {
		if ((int) (Math.random() * tickSpeed) < 2)
			squashed = true;
	}

}