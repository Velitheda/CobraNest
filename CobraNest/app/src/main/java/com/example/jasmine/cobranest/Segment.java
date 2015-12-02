//Written by Jasmine Vickery
//This is the parent class for all the visual elements of the game, 
//as each item is composed of a different coloured one of these or array of them

package com.example.jasmine.cobranest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class Segment {

	public static final int LEFT = SnakeConstants.LEFT;
	public static final int RIGHT = SnakeConstants.RIGHT;
	public static final int UP = SnakeConstants.UP;
	public static final int DOWN = SnakeConstants.DOWN;

	public static final int SIZE = SnakeConstants.SIZE;

	private static final String TAG = Segment.class.getSimpleName();
	protected Rect area;
	protected int direction;
	protected Rect screenArea;

	public Segment(int xPos, int yPos, int direction, Rect screenArea) {

		this.screenArea = screenArea;
		this.direction = direction;
		area = new Rect(xPos, yPos, SIZE + xPos, SIZE + yPos);

	}

	public Segment(Rect screenArea) {
		this.screenArea = screenArea;
		direction = RIGHT;
		int x = getRandomXPostion();
		int y = getRandomYPostion();
		area = new Rect(x, y, SIZE + x, SIZE + y);
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

	public int getDirection() {
		return direction;
	}

	public int getXPos() {
		return area.left;
	}

	public Point getPoint() {
		Point p = new Point(getXPos(), getYPos());
		return p;
	}

	public int getYPos() {
		return area.top;
	}

	public void setPostion(int xPos, int yPos) {
		area.left = xPos;
		area.right = xPos + SIZE;
		area.top = yPos;
		area.bottom = yPos + SIZE;
	}

	public Rect getArea() {
		return area;
	}

	public void setArea(Rect area) {
		this.area = area;
	}

	public void drawSegment(Canvas canvas, Bitmap bitmap) {
		canvas.drawBitmap(bitmap, area.left, area.top, null);
	}

	public boolean segmentInersection(Segment other) {
		return this.area.intersect(other.getArea());
	}

	public boolean segmentArrayIntersection(Segment[] otherArray) {
		boolean intersects = false;
		for (int i = 0; i < otherArray.length; i++) {
			if (this.area.intersect(otherArray[i].getArea())) {
				intersects = true;
			}
		}
		return intersects;
	}

	public void moveSegment(int direction) {
		this.direction = direction;
		if (direction == LEFT) {
			area.left -= SIZE;
			area.right -= SIZE;
		}
		if (direction == RIGHT) {
			area.left += SIZE;
			area.right += SIZE;
		}
		if (direction == UP) {
			area.top -= SIZE;
			area.bottom -= SIZE;
		}
		if (direction == DOWN) {
			area.top += SIZE;
			area.bottom += SIZE;
		}
	}

	public void jumpCoordinates() {
		if (direction == LEFT) {
			area.left = screenArea.right - SIZE;
			area.right = screenArea.right;
		}
		if (direction == RIGHT) {
			area.left = screenArea.left;
			area.right = SIZE + screenArea.left;
		}
		if (direction == UP) {
			area.top = screenArea.bottom - SIZE;
			area.bottom = screenArea.bottom;
		}
		if (direction == DOWN) {
			area.top = screenArea.top;
			area.bottom = SIZE + screenArea.top;
		}
	}

}