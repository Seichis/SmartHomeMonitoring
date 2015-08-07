package com.example.androidprototype;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.controllers.ControllerUserSettingsCommands;

/**
 *         The TimerService is a Service that gets the actions and sensors
 *         to be enabled from the server periodically The actual call to the server
 *         is done by the {@link ControllerUserSettingsCommands}
 *         @author s141279
 */
public class TimerService extends Service {

	/**
	 * Constant determining the period of actions retrieval
	 */
	public static final long NOTIFY_INTERVAL = 10000;

	/**
	 * List of Intents to be filled by the
	 * {@link ControllerUserSettingsCommands}
	 */
	public static List<Intent> actionIntents = null;

	/**
	 * The handler allows to enqueue the threads for getting the actions this is
	 * done in order to avoid crashes
	 * 
	 */
	private Handler mHandler = new Handler();

	/**
	 * The timer is used to schedule the threads periodically
	 * 
	 */
	private Timer mTimer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Creates the timer and set the scheduler for it
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {

		// cancel if already existed
		if (mTimer != null) {
			mTimer.cancel();
		} else {
			// create new
			mTimer = new Timer();
		}
		// schedule task
		mTimer.scheduleAtFixedRate(new ActionsTimerTask(), 0, NOTIFY_INTERVAL);
	}

	@Override
	public void onDestroy() {

		mTimer.cancel();
		super.onDestroy();
	}

	/**
	 * 
	 * The {@link ActionsTimerTask} is run by the timer periodically It runs the
	 * {@link ControllerUserSettingsCommands} and start the desired services and
	 * activities via the handler
	 * 
	 * 
	 */
	class ActionsTimerTask extends TimerTask {

		/*
		 * (non-Javadoc)
		 * 
		 * Every time the ActionTimerTask is run it only post the code to run
		 * inside the handler
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// run on another thread
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					getActions();

				}

			});
		}

		/**
		 * 
		 * The getActions method call the {@link ControllerUserSettingsCommands}
		 * that retrieves the commands and add the related intents to be started
		 * or stopped inside the actionIntents list.
		 * 
		 * It start and stop the services related to the sensors settings and
		 * start the desired commands services and activities.
		 * 
		 */
		private void getActions() {

			Log.i("TimerService", "Starting actions");

			// get the actions from the server but not synchronized
			// synchronizing prove to be a bit problematic

			Thread userSetAction = new Thread(
					new ControllerUserSettingsCommands(TimerService.this));
			userSetAction.start();

			List<Intent> intents = actionIntents;
			if (intents != null) {
				for (Intent actionIntent : intents) {

					actionIntent.getExtras();

					if (actionIntent.getStringExtra("Video") != null) {
						MainActivity.streaming = false;
						startActivity(actionIntent);
					}
					if (actionIntent.getStringExtra("Stream") != null) {
						MainActivity.streaming = true;
						startActivity(actionIntent);
					}
					if ((actionIntent.getStringExtra("Email") != null)
							|| (actionIntent.getStringExtra("Sound")) != null
							|| actionIntent.getStringExtra("Accel") != null
							|| actionIntent.getStringExtra("Light") != null) {
						startService(actionIntent);
					}
					if (actionIntent.getStringExtra("NoAccel") != null
							|| actionIntent.getStringExtra("NoLight") != null) {
						stopService(actionIntent);
					}
				}

				actionIntents.clear();
			}
		}
	}
}
