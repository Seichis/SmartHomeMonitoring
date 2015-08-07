package com.example.androidprototype;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.controllers.ControllerEventData;

/**
 * 
 * 
 * Activity for recording Two different kinds of recording are implemented,
 * recording in chunks for streaming and recording a long unique video for
 * recording on sensor event.
 * @author s141966, s141277
 */

public class VideoRecorder extends Activity implements SurfaceHolder.Callback {

	private SurfaceView surfaceView;
	private MediaRecorder recorder = new MediaRecorder();
	private int count;

	/**
	 * 
	 * The {@link ControllerEventData} is used to upload the video
	 * 
	 */

	private ControllerEventData cEvent;

	private Context context;
	private Camera camera;
	public static boolean stopped;
	private Thread recordingThread;
	private File video;
	private File file;

	/**
	 * Constant for the recording time on sensor event
	 */
	public static final int UNIQUE_TIME = 60000;

	// record one video for a long time when video recorded automatically
	// without that the user see
	public static long milliSecondsToRecord = 60000;

	/**
	 * Constant for the recording time for streaming
	 */
	public static final int CHUNK_TIME = 4000;

	/*
	 * (non-Javadoc)
	 * 
	 * Triggered when activity called. Initialize the surface and the
	 * ControllerEventData
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		cEvent = new ControllerEventData(MainActivity.registeredUser.getName(),
				MainActivity.phoneId.getPhoneid());
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_video_recorder);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		context = getApplicationContext();

		count = 1;

	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * Is triggered after onCreate. Initialize and set the camera Start
	 * recording thread for streaming or on sensor event according to the
	 * MainActivity.stream boolean
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
	 * )
	 */

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		if (MainActivity.accelerometerOn == true) {
			stopService(new Intent(this, AccelerometerService.class));
			stopService(new Intent(this, LightDetectionService.class));
			Log.i("Accelerometer", "Accelerometer is going to stop.");
			MainActivity.accelerometerOn = false;
		}

		camera = Camera.open();
		camera.lock();
		camera.setDisplayOrientation(90);
		Parameters p = camera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);

		camera.setParameters(p);
		try {
			camera.setPreviewDisplay(surfaceView.getHolder());
			camera.getParameters().setRecordingHint(true);
			camera.startPreview();
		} catch (IOException e) {
			Log.e("Camera", "cannot start preview", e);
		}
		// size = camera.getParameters().getPreferredPreviewSizeForVideo();
		camera.getParameters().setFlashMode("on");

		if (MainActivity.streaming == true) {
			recordingThread = new UserRecordingThread();
			recordingThread.start();

		} else {
			recordingThread = new RecordingThread();
			recordingThread.start();
		}

	}

	/**
	 * Helper function that properly release the MediaRecorder and lock the
	 * camera
	 * 
	 */

	private void releaseMediaRecorder() {
		if (recorder != null) {
			recorder.reset();
			recorder.release();
			recorder = null;
			camera.lock();
		}
	}

	/**
	 * 
	 * Helper function that properly release the camera, is called after
	 * releaseMediaRecorder
	 * 
	 */

	private void releaseCamera() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Is triggered when the recording finish or when the user press returns to
	 * the last activity. It release the MediaRecorder and Camera and yield the
	 * recording thread.
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
	 * SurfaceHolder)
	 */

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

		releaseMediaRecorder();
		releaseCamera();
		yieldThread();
	}

	/**
	 * Is called before every type of recording. It unlocks the camera and
	 * creates, sets, starts the MediaRecorder. Also creates the file for to be
	 * recorded on and that will be uploaded
	 */

	private synchronized void startRecording() {
		if (recorder == null) {
			recorder = new MediaRecorder();
		}
		camera.unlock();

		// recorder = new MediaRecorder();
		recorder.reset();
		recorder.setCamera(camera);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

		recorder.setVideoSize(1280, 720);

		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

		recorder.setOrientationHint(0);

		file = new File(context.getFilesDir(), "video" + count + ".mp4"); // mpg4

		if (count > 3) {
			count = 1;
		}
		count++;
		String filepath = file.getAbsolutePath();
		recorder.setOutputFile(filepath);
		try {
			recorder.prepare();
		} catch (IllegalStateException | IOException e1) {
			Log.e("Recording", "cannot prepare recording", e1);
		}
		recorder.start();

	}

	/**
	 * 
	 * Helper function to properly yield a recording thread when it is not
	 * needed anymore.
	 * 
	 */

	private void yieldThread() {
		if (recordingThread != null) {

			if (recordingThread.isAlive()) {
				Log.i("RecordingThread", "RecordingThread is going to yield.");
				stopped = true;
				Thread.yield();
				Log.i("RecordingThread", "RecordingThread succesfully yielded.");
			}
		}
	}

	/**
	 * Recording thread that records on sensor event. Only one long video is
	 * recorded and uploaded.
	 * 
	 */

	private class RecordingThread extends Thread {

		@Override
		public void run() {

			startRecording();

			try {
				sleep(milliSecondsToRecord);
			} catch (InterruptedException e) {
			}

			releaseMediaRecorder();
			video = file;
			Log.i("Uploading", "is about to start");
			cEvent.uploadVideo(video);
			Log.i("Uploading", "finished");
			VideoRecorder.this.finish();
		}
	}

	/**
	 * 
	 * Recording thread that records in chunks for streaming purposes. Small
	 * chunks are recorded and uploaded until MainActivity.streaming is false.
	 * MainAcitivity.streaming changes value whenever a user press the
	 * "start/stop stream" button on the web interface.
	 * 
	 */

	private class UserRecordingThread extends Thread {

		@Override
		public void run() {

			while (MainActivity.streaming == true) {
				startRecording();
				long recordingStart = System.currentTimeMillis();

				if (video != null)
					cEvent.uploadVideo(video);

				long delta = System.currentTimeMillis() - recordingStart;
				try {
					sleep(Math.max(4000 - delta, 0));
				} catch (InterruptedException e) {
				}

				releaseMediaRecorder();

				// set the video to the last recorded video in order to upload
				// at the next iteration

				video = file;
			}

			// not useful but defensive

			MainActivity.streaming = false;
			VideoRecorder.this.finish();
		}
	}
}
