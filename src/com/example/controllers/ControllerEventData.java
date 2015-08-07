package com.example.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.example.androidprototype.MainActivity;

import dto.EventDTO;
import dto.VideoWrapper;

/**
 * 
 * 
 * <h1>Upload Data</h1> This class includes methods for sending events
 * information to the web service. This information is the time stamp and the
 * value of the event. Also, it includes a method for uploading both the stream
 * video and the recorded video to the server.
 * @author s141966, s123230, s141279
 */

public class ControllerEventData {
	private String userName = MainActivity.registeredUser.getName();
	private String phoneId = MainActivity.phoneId.getPhoneid();

	public ControllerEventData(String userName, String phoneId) {
		this.userName = userName;
		this.phoneId = phoneId;

	}

	/**
	 * This method starts a thread for adding new events by calling the addEvent
	 * method once the user is logged in.
	 * 
	 * @param event
	 *            takes as attribute the EventDTO
	 */

	public void startAddEvent(final EventDTO event) {

		(new Thread() {
			public void run() {
				if (MainActivity.isLoggedin) {
					addEvent(event);
				} else {
					return;
				}
			}
		}).start();
	}

	/**
	 * This method sends the events data time stamp and value- for the specific
	 * user to the URL page via the RestTemplate.
	 * 
	 * @param event
	 *            takes as attribute the EventDTO
	 */
	private void addEvent(EventDTO event) {
		Log.i("Addevent", "Started adding event");
		final String url = URL.BASE_URL + URL.EVENT_URL;
		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("value", String.valueOf(event.getValue()));
		parameters.put("username", String.valueOf(userName));
		parameters.put("phoneid", String.valueOf(phoneId));
		parameters.put("eventtype", String.valueOf(event.getEventype()));
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(
				new MappingJacksonHttpMessageConverter());
		EventDTO result = restTemplate.getForObject(url, EventDTO.class,
				parameters);

		Log.i("Addevent", "Added event" + result.getTime() + result.getValue());
	}

	/**
	 * Sends the streaming video and the 10 sec video to the specified URLs.
	 * 
	 * @param video
	 *            the file of the video
	 */
	public void uploadVideo(File video) {
		Log.i("Uploading", "started");
		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(video);
			// read file to byte[]
			byte[] buffer = new byte[(int) inputStream.getChannel().size()];
			inputStream.read(buffer);

			// add content to videoWrapper
			VideoWrapper vid = new VideoWrapper();
			vid.setVideoContent(buffer);
			vid.setPhoneid(phoneId);
			vid.setUserName(userName);
			String url;
			if (MainActivity.streaming == true) {
				url = URL.BASE_URL + URL.VIDEO_STREAM_URL_START + userName
						+ URL.VIDEO_STREAM_URL_END;
			} else {
				url = URL.BASE_URL + URL.VIDEO_UPLOAD_URL_START + userName
						+ URL.VIDEO_UPLOAD_URL_END;
			}
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJacksonHttpMessageConverter());

			restTemplate.put(url, vid);
			// not tested yet... should work
			Log.i("uploadVideo", "Video uploaded succesfully");
		} catch (IOException e) {
			Log.e("uploadVideo", "Failed to upload video", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e("inputStream", "Failed to close input stream", e);
				}
			}
		}
	}
}
