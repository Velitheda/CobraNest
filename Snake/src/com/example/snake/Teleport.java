//Written by Jasmine Vickery

//Class for a two squares that teleport either a snake or a spider between them
//and sends them out the other square in the direction they entered the first.
//I am planning to remove these shortly, and possibly include them again in a level that is pre-designed and loaded,
//because they add a too distracting element of complexity to the game, and will also need a bit of checking to prevent 
//the player from being teleported straight out into a wall.

package com.example.snake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Teleport {

	public static final int LEFT = SnakeConstants.LEFT;
	public static final int RIGHT = SnakeConstants.RIGHT;
	public static final int UP = SnakeConstants.UP;
	public static final int DOWN = SnakeConstants.DOWN;

	public static final int SIZE = SnakeConstants.SIZE;

	private static final String TAG = Segment.class.getSimpleName();

	private Rect base1;
	private Rect smallerBase1;

	private Rect base2;
	private Rect smallerBase2;

	private boolean on;

	private Rect screenArea;

	public Teleport(Rect screenArea) {
		this.screenArea = screenArea;

		on = true;

		int x = getRandomXPostion();
		int y = getRandomYPostion();
		base1 = new Rect(x, y, x + SIZE, y + SIZE);

		smallerBase1 = new Rect(base1);
		smallerBase1.left++;
		smallerBase1.right--;
		smallerBase1.top++;
		smallerBase1.bottom--;

		int a = getRandomXPostion();
		int b = getRandomYPostion();
		base2 = new Rect(a, b, a + SIZE, b + SIZE);

		smallerBase2 = new Rect(base2);
		smallerBase2.left++;
		smallerBase2.right--;
		smallerBase2.top++;
		smallerBase2.bottom--;
	}

	public void teleport(Segment segment) {
		if (segment.getArea().intersect(smallerBase1)) {
			teleportSegment(base2, segment);
			Log.d(TAG, "Teleporting spider");
		} else if (segment.getArea().intersect(smallerBase2)) {
			teleportSegment(base1, segment);
			Log.d(TAG, "Teleporting spider");
		}
	}

	public void teleportSegment(Rect destination, Segment segmentToTeleport) {
		// if(!on)
		// return;
		Log.d(TAG, "Teleporting");
		int direction = segmentToTeleport.getDirection();
		segmentToTeleport.setArea(new Rect(destination));
		if (direction == LEFT) {
			segmentToTeleport.getArea().left -= SIZE;
			segmentToTeleport.getArea().right -= SIZE;
		}
		if (direction == RIGHT) {
			segmentToTeleport.getArea().left += SIZE;
			segmentToTeleport.getArea().right += SIZE;
		}
		if (direction == UP) {
			segmentToTeleport.getArea().top -= SIZE;
			segmentToTeleport.getArea().bottom -= SIZE;
		}
		if (direction == DOWN) {
			segmentToTeleport.getArea().top += SIZE;
			segmentToTeleport.getArea().bottom += SIZE;
		}
		on = false;
	}

	public void teleport(Segment[] segments) {
		for (int i = 0; i < segments.length; i++) {
			if (segments[i] != null) {
				if (segments[i].getArea().intersect(smallerBase1)) {
					teleportSegment(base2, segments[i]);
					Log.d(TAG, "Intersection " + base1.left + " " + base1.top);
				} else if (segments[i].getArea().intersect(smallerBase2))
					teleportSegment(base1, segments[i]);
			}
		}
	}

	public void drawTeleport(Canvas canvas, Bitmap bitmap) {
		canvas.drawBitmap(bitmap, base1.left, base1.top, null);
		canvas.drawBitmap(bitmap, base2.left, base2.top, null);
	}

	public void setRandomPostion(Rect area) {
		int x = getRandomXPostion();
		int y = getRandomYPostion();
		area = new Rect(x, y, x + SIZE, y + SIZE);
	}

	public int getRandomXPostion() {
		return (int) (Math.random() * ((screenArea.width() - SIZE) / SIZE))
				* SIZE + screenArea.left;
	}

	public int getRandomYPostion() {
		return (int) (Math.random() * ((screenArea.height() - SIZE) / SIZE))
				* SIZE + screenArea.top;
	}

}
