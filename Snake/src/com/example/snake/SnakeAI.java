//Written by Jasmine Vickery
//This class creates another snake that in controlled by the computer, instead of the player

package com.example.snake;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class SnakeAI extends Snake {

	private Point target;

	public SnakeAI(Rect screenArea) {
		super(screenArea);

		target = new Point(100, 100);
		initialiseSnake(getRandomXPostion(), getRandomYPostion());
		dieIfHitWall = false;
	}

	public Point getTarget() {
		return target;
	}

	public void setTarget(Point newTarget) {
		target = newTarget;
	}

	public void setDirectionToTarget() {

		int direction = RIGHT;

		if (target.x == snakeBody[0].getArea().left)
			direction = setYDirection(target);
		else
			direction = setXDirection(target);
		moveSnake(direction);
		jumpScreen();
	}

	public int setXDirection(Point p) {
		int difference = snakeBody[0].getArea().left - p.x;
		if (dieIfHitWall) {
			if (difference >= 0)
				return LEFT;
			else
				return RIGHT;
		} else {

			if (difference <= screenArea.width() / 2 && difference >= 0)
				return LEFT;
			if (difference > screenArea.width() / 2 && difference < 0)
				return LEFT;
			if (difference > screenArea.width() / 2 && difference > 0)
				return RIGHT;
			else
				return RIGHT;
		}
	}

	public int setYDirection(Point p) {
		int difference = snakeBody[0].getArea().top - p.y;
		if (dieIfHitWall) {
			if (difference >= 0)
				return UP;
			else
				return DOWN;
		} else {

			if (difference <= screenArea.height() / 2 && difference >= 0)
				return UP;
			if (difference > screenArea.height() / 2 && difference < 0)
				return UP;
			if (difference > screenArea.height() / 2 && difference > 0)
				return DOWN;
			else
				return DOWN;
		}
	}

}
