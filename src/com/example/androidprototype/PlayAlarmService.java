package com.example.androidprototype;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

/**
 * 
 * Service to play an alarm sound
 * @author s141277
 */
public class PlayAlarmService extends Service implements OnCompletionListener {
	MediaPlayer alarmSound;
	private static final int NUMBER_OF_PLAYS = 9;
	int count = 0;
	  @Override
	  public IBinder onBind(Intent intent) {
	    return null;
	  }

	 /* (non-Javadoc)
	  * 
	  * Triggered when the service is created
	  * Creates the alarm sound and set the onCompletionListner in order to play the alarm more than ones
	  * 
	  * @see android.app.Service#onCreate()
	  */
	@Override
	  public void onCreate() {
	    alarmSound = MediaPlayer.create(this, R.raw.alarm);
	    alarmSound.setOnCompletionListener(this);
	  }

	 /* (non-Javadoc)
	  * 
	  * When the service is started it plays the alarm
	  * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	  */
	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    if (!alarmSound.isPlaying()) {
	      alarmSound.start();
	    }
	    return START_STICKY;
	  }

	
	 /* (non-Javadoc)
	 *
	 * Release and stop the alarm if it is playing
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	public void onDestroy() {
	    if (alarmSound.isPlaying()) {
	      alarmSound.stop();
	    }
	    alarmSound.release();
	  }

	 /* (non-Javadoc)
	  * 
	  * replays the alarm until a fixed number of time, i.e. 9 + 1 times 
	  * this number could have been set by the user but no web services for that
	  * 
	  * @see android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media.MediaPlayer)
	  */
	public void onCompletion(MediaPlayer _alarmSound) {
		  if(count<NUMBER_OF_PLAYS){
			  alarmSound.start();
			  count++;
		  }
		  else{
			  stopSelf();
		  }
	    
	  }
}

