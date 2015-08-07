package com.example.androidprototype;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.controllers.ControllerUserSettingsCommands;

/**
 * 
 * Simple Class for the Notification Activity
 *
 */
public class NotificationActivity extends ActionBarActivity {

	/* (non-Javadoc)
	 * 
	 * Triggered when Activity started, stop the notification and clear all the notifications
	 * Also show a basic view
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		ControllerUserSettingsCommands.alarm.stopAlarm();
		ControllerUserSettingsCommands.alarm.clearNotification();
	}

	
}
