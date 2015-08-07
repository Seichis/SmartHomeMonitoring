package com.example.androidprototype;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * Class that handles the alarms and show notification to the user
 * Extends BroadcatReceiver in order to receive broadcasts across applications
 * @author s141279
 */
public class AlarmReceiver extends BroadcastReceiver {
	
	
	/* (non-Javadoc)
	 * 
	 * The AlarmReciever will only execute this method in his lifecycle 
	 * We send a notification to the user using the sendNotification method defined below
	 * Also this action is done within a thread
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.i(AlarmReceiver.class.getSimpleName(), "Got Broadcast message "
				+ (new Date()));
		// In life cycle method run everything in thread to avoid ANR
		Runnable runnable = new Runnable() {

			public void run() {
				sendNotification(context);
			}

		};

		Thread thread = new Thread(runnable);
		thread.start();

	}

	/**
	 * Create and send the notification for the user
	 * 
	 * @param context: context of the calling activity or service
	 */
	private void sendNotification(Context context) {
		final int NOTE_ID = 100;
		final String ns = Context.NOTIFICATION_SERVICE;
		final PendingIntent contentIntent = PendingIntent.getActivity(context,
				0, new Intent(context, NotificationActivity.class), 0);
		final NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		Notification note = new Notification.Builder(context)
				.setContentTitle("You've been robbed ")
				.setContentText("Please go to the webpage to check what happened")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(contentIntent).build();

		note.defaults |= Notification.DEFAULT_SOUND;
		note.flags |= Notification.FLAG_AUTO_CANCEL;
		note.defaults |= Notification.DEFAULT_VIBRATE;
		note.defaults |= Notification.DEFAULT_LIGHTS;
		mNotificationManager.notify(NOTE_ID, note);		
	}
}
