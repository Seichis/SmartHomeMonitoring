package com.example.androidprototype;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * Class that calls the {@link AlarmReceiver} Broadcast in order to send a notification to the user
 * @author s141279
 */
public class AlarmToNotify {
	Context context;
	private AlarmManager alarmManager;
	private Intent intent;
	private PendingIntent pendingIntent;

	/**
	 * Set alarm manager
	 * @param context: context of calling activity or service
	 */
	public AlarmToNotify(Context context) {
		this.context = context;
		this.intent = new Intent(context, AlarmReceiver.class);
		this.pendingIntent = PendingIntent.getBroadcast(context, 100, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		this.alarmManager= (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * startAlarm() to initiate the alarm 
	 * 
	 */
	public void startAlarm() {
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 0, pendingIntent);

	}

	/**
	 * Stop a current notification
	 */
	public void stopAlarm() {
		alarmManager.cancel(pendingIntent);
	}

	/**
	 * 
	 * To clear the notification that went off with the alarm 
	 */
	public void clearNotification() {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}
}
