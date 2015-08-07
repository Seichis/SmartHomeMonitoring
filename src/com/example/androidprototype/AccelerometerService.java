package com.example.androidprototype;

import dto.EVENT;
import dto.EventDTO;
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

/**
 * Android Service to trigger accelerometer sensor without having to use an ui.
 * This Service will detect movements and send events to the server.
 * @author s141279,s141277
 */
public class AccelerometerService extends Service implements
		SensorEventListener {
	public static final String TAG = AccelerometerService.class.getName();
	public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
	public float lastAccel = 0;
	public float accelerationSquareRoot = 0;
	private SensorManager mSensorManager = null;
	private WakeLock mWakeLock = null;
	private long actualTime;
	private long lastUpdate;
	public int sensorId = 1;
	public Context context = this;
	public static boolean movementDetected = false;
	private EventDTO eventDto = new EventDTO();
	
	

	/**
	 * Register the accelerometer sensor listener
	 */
	private void registerListener() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	/**
	 * Unregister the accelerometer sensor listener
	 */
	private void unregisterListener() {
		mSensorManager.unregisterListener(this);
	}

	/**
	 * A broadcast receiver to check when the screen goes off and on and when it
	 * does we unregister and register the sensor
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
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}

	/*
	 * Creating the service
	 * 
	 */

	/* (non-Javadoc)
	 * 
	 * Called by the system when the service is created, i.e instantiated via an Intent
	 * Use of a PowerManager wakeLock in order to detect movements even if the screen is off
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
		movementDetected = false;
		MainActivity.accelRunning=true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/* (non-Javadoc)
	 * 
	 * Called every time that the service is started by using the startService method 
	 * In this method we acquire the wake lock in order for the service to work even if the screen is off
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
		MainActivity.accelRunning=false;
		super.onDestroy();
	}


	/**
	 * Calculating the accelerometer event and set the event dto
	 * Also use of actionOnAccelEvent
	 * @param event is the sensor event which contains the info of the movement
	 */
	public void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];
		actualTime=System.currentTimeMillis();
		accelerationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

		lastAccel = accelerationSquareRoot;
//		Log.i(TAG, "   " + (actualTime-lastUpdate));
		if (accelerationSquareRoot >= 2) 
		{
			if (actualTime - lastUpdate < 3000) {
				return;
			}
			lastUpdate = System.currentTimeMillis();
			
			Log.i(TAG, Float.toString(accelerationSquareRoot));
			
			this.eventDto.setValue(lastAccel);
			this.eventDto.setEventype(EVENT.ACCELEROMETER_EVENT);
			this.eventDto.setTime(Long.toString(System.currentTimeMillis()));
			actionOnAccelEvent(eventDto);
			movementDetected=true;
		}
	}

	/**
	 * This method sends the eventDTO that was created from the triggered acceleration
	 * @param eventDto: the eventDTO that will be sent to the server via the startAddEvent method
	 */
	private void actionOnAccelEvent(EventDTO eventDto) {
		//userSetAction = new ControllerUserSettingsCommands(this);
		Log.i(TAG, MainActivity.registeredUser.getName() + 
				MainActivity.phoneId.getPhoneid());
		Log.i(TAG, Float.toString(accelerationSquareRoot));
		new ControllerEventData(MainActivity.registeredUser.getName(),
				MainActivity.phoneId.getPhoneid()).startAddEvent(eventDto);		
	}
}