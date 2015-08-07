package com.example.androidprototype;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;

import com.example.controllers.ControllerEventData;

import dto.EVENT;
import dto.EventDTO;

/**
 * 
 *  Service that handles the Light Detection sensor Is similar to the
 *         {@link AccelerometerService} sensor
 * 	@author s141277,s141279
 */
public class LightDetectionService extends Service implements
		SensorEventListener {
	public static final String TAG = LightDetectionService.class.getName();
	public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

	private SensorManager mSensorManager = null;
	private WakeLock mWakeLock = null;
	private long lastUpdate;
	private long actualTime;
	public int sensorId = 1;
	public Context context = this;
	private EventDTO eventDto = new EventDTO();
	public static boolean lightDetected = false;

	/**
	 * Register the Light Detection sensor listener
	 */
	private void registerListener() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	/**
	 * Unregister the Light Detection sensor listener
	 */
	private void unregisterListener() {
		mSensorManager.unregisterListener(this);
	}

	/**
	 * A broadcast receiver to check when the screen goes off and on and when it
	 * does we unregister and register the sensor
	 * 
	 */

	public BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive(" + intent + ")");

			if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				return;
			}

			Runnable runnable = new Runnable() {
				public void run() {
					Log.i(TAG, "Runnable executing.");
					unregisterListener();
					registerListener();
				}
			};

			new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
		}
	};

	/**
	 * Implementation of SensorEventListener
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.i(TAG, "onAccuracyChanged().");
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			actionOnLightEvent(event);
		}
	}

	/*
	 * (non-Javadoc) Is called when the service is created Initialization of the
	 * power manager and wake lock in order to make the service work also with
	 * off screen
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		lastUpdate = System.currentTimeMillis();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

		registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		lightDetected = false;
		MainActivity.lightRunning = true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * Called every time that the service is started by using the startService
	 * method In this method we acquire the wake lock in order for the service
	 * to work even if the screen is off
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		startForeground(Process.myPid(), new Notification());
		registerListener();
		mWakeLock.acquire();
		return START_STICKY;
	}

	/**
	 * Destroy the service and unregister all listeners and receivers
	 */
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		unregisterListener();
		mWakeLock.release();
		stopForeground(true);
		MainActivity.lightRunning = false;
		super.onDestroy();
	}

	/**
	 * Calculating the Light Level event and send it to the server through the
	 * startAddEvent method
	 * 
	 * @param event
	 *            : event of the sensor detection
	 * 
	 */

	private void actionOnLightEvent(SensorEvent event) {

		actualTime = System.currentTimeMillis();

		if ((actualTime - lastUpdate < 2000) || (event.values[0] < 400)) {
			return;
		} else {
			lastUpdate = System.currentTimeMillis();
			this.eventDto.setValue(event.values[0]);
			this.eventDto.setEventype(EVENT.LIGHTLEVEL_EVENT);
			this.eventDto.setTime(Long.toString(System.currentTimeMillis()));

			Log.i(TAG, "Light detected");
			new ControllerEventData(MainActivity.registeredUser.getName(),
					MainActivity.phoneId.getPhoneid()).startAddEvent(eventDto);
			Log.i(TAG, "light value" + eventDto.getValue());
		}

	}

}
