package com.example.androidprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.example.controllers.ControllerUserSettingsCommands;

import dto.ACTION;

/**
 *         This activity is triggered after that the user register (in the main
 *         activity) Basically it only allows the user to activate the system on
 *         his phone via the "start" button
 *         @author s141279, s141966
 */
public class ActivationActivity extends ActionBarActivity {

	private Button start;
	public static ActivationActivity fa;
	Intent accelIntent, lightIntent;

	/*
	 * (non-Javadoc)
	 * 
	 * Initialize attributes and set actions on the start button When the start
	 * button was pressed, MainActivity.isActivated is set to True When the
	 * system is activated, the sensors services are started relating to the
	 * setting entered in the settings page of the phone Three sensors:
	 * Accelerometer, Light Detection and No sensor Timer Service is also
	 * started when the system is activated The Timer Service is responsible to
	 * call periodically the ControllerUserSettingsCommands thread to retrieve
	 * actions and sensors settings When the system is deactivated, timer
	 * service is stopped
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		fa = this;
		super.onCreate(savedInstanceState);
		MainActivity.isActivated = false;
		setContentView(R.layout.activity_activation);
		start = (Button) findViewById(R.id.start);
		start.setText("Activate SMaSHS!");
		start.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!MainActivity.isActivated) {
					start.setEnabled(false);
					start.setText("Deactivate SMaSHS!");
					MainActivity.isActivated = true;

					startService(new Intent(ActivationActivity.this,
							TimerService.class));
					if (MainActivity.phoneId.getRoles() != null) {
						for (ACTION act : MainActivity.phoneId.getRoles())

							switch (act) {
							case ACCEL_SENSOR_ACTION:
								accelIntent = new Intent(
										ActivationActivity.this,
										AccelerometerService.class);
								accelIntent.putExtra("Accel", "Intent");
								ControllerUserSettingsCommands.sensorDeactivate = false;
								startService(accelIntent);
								break;
							case LIGHT_SENSOR_ACTION:
								lightIntent = new Intent(
										ActivationActivity.this,
										LightDetectionService.class);
								lightIntent.putExtra("Light", "Intent");
								ControllerUserSettingsCommands.sensorDeactivate = false;

								break;

							}
					}
					start.setEnabled(true);
				} else {
					start.setEnabled(false);
					start.setText("Activate SMaSHS!");
					MainActivity.isActivated = false;
					if (MainActivity.accelRunning == true) {
						stopService(new Intent(ActivationActivity.this,
								AccelerometerService.class));
					}
					if (MainActivity.lightRunning == true) {
						stopService(new Intent(ActivationActivity.this,
								LightDetectionService.class));
					}
					stopService(new Intent(ActivationActivity.this,
							TimerService.class));
					start.setEnabled(true);
					stopService(new Intent(ActivationActivity.this,
							TimerService.class));
				}
			}
		});

	}

	/*
	 * (non-Javadoc) Nothing special here, auto generated method handling return
	 * button
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

}
