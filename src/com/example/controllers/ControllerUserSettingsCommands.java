package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.androidprototype.AccelerometerService;
import com.example.androidprototype.AlarmToNotify;
import com.example.androidprototype.LightDetectionService;
import com.example.androidprototype.MailService;
import com.example.androidprototype.MainActivity;
import com.example.androidprototype.PlayAlarmService;
import com.example.androidprototype.TimerService;
import com.example.androidprototype.VideoRecorder;

import dto.ACTION;
import dto.CommandDTO;

/**
 * 
 * <h1>Get Action Commands from Web Service</h1> A thread that requests a
 * CommandDTO from the web service to initiate actions from the user settings
 * @author s141279,s141966
 */
public class ControllerUserSettingsCommands extends Thread {
	// TODO creating intents according to string values from the respond after
	// the Controller Event Data has triggered the getCommand(to be implemented
	// here)
	public static boolean sensorDeactivate = false;
	private Context context;
	private static String userName = MainActivity.registeredUser.getName();
	private static String phoneId = MainActivity.phoneId.getPhoneid();
	private CommandDTO userSettingsCommands = null;
	private Intent videoRecordingIntent;
	private Intent playSoundIntent;
	private Intent emailIntent;
	private Intent accelIntent, lightIntent;
	private List<Intent> actionIntents;
	private ArrayList<ACTION> commands = new ArrayList<ACTION>();
	public static AlarmToNotify alarm;

	public ControllerUserSettingsCommands(Context context) {
		this.userSettingsCommands = new CommandDTO();
		this.context = context;
	}

	/*
	 * (non-Javadoc) We request the CommandDTO from the web service. If an
	 * ACTION exists in the list we add it in the global list of intents in
	 * TimerService to initialize the actions
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		this.actionIntents = new ArrayList<Intent>();
		if (MainActivity.isActivated) {
			int c = 0;
			try {
				Log.i("thread", "next iteration await Thread" + c++);
				Log.i("Command Settings Service", "in try");
				final String url = URL.BASE_URL + URL.ACTIONS_URL;
				Log.i("Command Settings Service", "trying to connect to url");
				final Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("username", userName);
				parameters.put("phoneId", phoneId);
				parameters.put("password",
						MainActivity.registeredUser.getPassword());
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJacksonHttpMessageConverter());
				Log.i("Command Settings Service", "before taking the list");

				userSettingsCommands = restTemplate.getForObject(url,
						CommandDTO.class, parameters);
				commands = userSettingsCommands.getCommands();
				if (commands.isEmpty()) {
					Log.i("Command Settings Service", ""
							+ "Null list of commands");
					return;
				}
				Log.i("Command Settings Service", ""
						+ userSettingsCommands.getCommands().size());

				Log.i("Command Settings Service", "after taking the stream");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("Command Settings Service", "not taken exception");
			}
		}
		for (ACTION action : commands) {
			switch (action) {
			case ALARM_ACTION:
				alarm = new AlarmToNotify(context);
				alarm.startAlarm();
				break;
			case STREAM_ACTION:
				videoRecordingIntent = new Intent(context, VideoRecorder.class);
				videoRecordingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				videoRecordingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				videoRecordingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				if (MainActivity.streaming == false) {
					videoRecordingIntent.putExtra("Stream", "Intent");
					this.actionIntents.add(videoRecordingIntent);
					MainActivity.streaming = true;
				} else {
					MainActivity.streaming = false;
				}
				break;

			case VIDEORECORD_ACTION:
				videoRecordingIntent = new Intent(context, VideoRecorder.class);
				videoRecordingIntent.putExtra("Video", "Intent");
				videoRecordingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				videoRecordingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				videoRecordingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				this.actionIntents.add(videoRecordingIntent);
				break;
			case SOUNDPLAY_ACTION:
				playSoundIntent = new Intent(context, PlayAlarmService.class);
				playSoundIntent.putExtra("Sound", "Intent");
				this.actionIntents.add(playSoundIntent);
				break;
			case EMAILSEND_ACTION:
				emailIntent = new Intent(context, MailService.class);
				emailIntent.putExtra("Email", "Intent");
				this.actionIntents.add(emailIntent);
				break;
			case NON_SENSOR_ACTION:
				accelIntent = new Intent(context, AccelerometerService.class);
				lightIntent = new Intent(context, LightDetectionService.class);
				if (MainActivity.accelRunning == true) {
					accelIntent.putExtra("NoAccel", "Intent");
					actionIntents.add(accelIntent);

				}
				if (MainActivity.lightRunning == true) {
					lightIntent.putExtra("NoLight", "Intent");
					actionIntents.add(lightIntent);

				}

				break;
			case ACCEL_SENSOR_ACTION:
				accelIntent = new Intent(context, AccelerometerService.class);
				if (MainActivity.accelRunning == true) {
					accelIntent.putExtra("NoAccel", "Intent");
					actionIntents.add(accelIntent);

				} else {
					accelIntent.putExtra("Accel", "Intent");
					actionIntents.add(accelIntent);
				}
				actionIntents.add(accelIntent);
				// empty for now.. need vibrate implemented
				break;
			case LIGHT_SENSOR_ACTION:
				lightIntent = new Intent(context, LightDetectionService.class);
				if (MainActivity.lightRunning == true) {
					lightIntent.putExtra("NoLight", "Intent");
					actionIntents.add(lightIntent);

				} else {
					lightIntent.putExtra("Light", "Intent");
					actionIntents.add(lightIntent);
				}

				// empty for now.. need vibrate implemented
				break;
			}
		}
		TimerService.actionIntents = actionIntents;

	}

}
