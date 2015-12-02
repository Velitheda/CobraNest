// Written by Jasmine Vickery
// This class handles most of the code relating to the android platform,
// and is the main and only activity for the program.  It then starts a 
// view with the game contained inside.
// It also creates an alert for when the game is paused offering a chance to either resume or restart.
package com.example.jasmine.cobranest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SnakeMain extends Activity {

	private static final String TAG = SnakeMain.class.getSimpleName();
	private SnakeGamePanel gamePanel;
	private boolean firstTime = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Creating activity");
		// requesting to turn the title off
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// make it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		gamePanel = new SnakeGamePanel(this);

		setContentView(gamePanel);

		Log.d(TAG, "View added");
	}

	protected void onPause() {
		super.onPause();
		gamePanel.pause();
		Log.d(TAG, "Pausing activity");

	}

	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming activity");
		if(firstTime){
			firstTime = false;
		}else{
			createAlert();
		}

	}

	protected void onRestart() {
		Log.d(TAG, "Restarting activity");
		super.onRestart();
	}

	protected void onDestroy() {
		Log.d(TAG, "Destroying ...");
		gamePanel.surfaceDestroyed(gamePanel.getHolder());
		super.onDestroy();

	}

	protected void onStop() {
		Log.d(TAG, "Stopping ...");
		super.onStop();
	}

	public void onBackPressed() {
		Log.d(TAG, "Back button pressed");
		super.onBackPressed();
	}
	
	// This code attempts to create the alert when the keys are pressed,
	// and possibly stop it from crashing.
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "Key down");
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(TAG, "Hi");
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Log.d(TAG, "Menu pressed");
			gamePanel.pause();
			createAlert();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Log.d(TAG, "Home pressed");
			return true;
		} else
			return super.onKeyUp(keyCode, event);
	}

	// This creates an alert that is all the menu functionality that the game
	// currently has.
	// It pops up when the game is paused, and offers the choice for a new game
	// or to resume.
	protected void createAlert() {
		Builder alertDialogBuilder = new Builder(this);
		// set title
		alertDialogBuilder.setTitle("Paused");

		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setPositiveButton("Resume",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.d(TAG, "Resume pressed");
                    dialog.cancel();
                    gamePanel.resume();
                }
            });
		alertDialogBuilder.setNegativeButton("New Game",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                ///resets game, need to restart thread now
                gamePanel.start();
                gamePanel.startThread();
                }
            });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		Log.d(TAG, "Made alert");
	}

}
