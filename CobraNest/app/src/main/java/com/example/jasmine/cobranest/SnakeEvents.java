package com.example.jasmine.cobranest;

import java.util.TimerTask;

import android.util.Log;

public class SnakeEvents {

	private static final String TAG = SnakeEvents.class.getSimpleName();
	public SnakeEvents() {}

	public void run() {
		Log.d(TAG, "Ticked");
	}

}
