package com.example.androidprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.controllers.ControlPhoneId;
import com.example.controllers.ControllerAuthentication;

import dto.PhoneDTO;
import dto.UserDTO;

/**
 * 
 *         First class that is called when the application starts Also defines
 *         some variables that will be used across the application.
 *         @author s141277, s141279
 */

public class MainActivity extends Activity {
	public static boolean streaming = false;
	Button insidePopupButton, insidePopupButton2;
	LinearLayout layoutOfPopup;
	TextView popupText;
	PopupWindow popupMessage;
	private PopupWindow pwindo;
	Button btnClosePopup, btnClosePopup2;
	Button btnCreatePopup;
	public static final String TAG = MainActivity.class.getName();
	public static boolean accelerometerOn;
	public static boolean soundLevelOn;
	public static boolean isActivated;
	private Button submit;
	public Context mainActivityContext;
	private ControllerAuthentication auth;
	protected EditText userName;
	protected EditText userPassword;
	public static PhoneDTO phoneId;
	public static UserDTO registeredUser;
	public static boolean isLoggedin = false;
	private EditText phoneIdEt;
	/**
	 * This variable will allow to write and read to a file, for storing the id
	 * of the phone when it is registered
	 */
	private ControlPhoneId mConPhone;
	private boolean textChangedUN = false;
	private Intent activation;
	private boolean textChangedUP = false;
	private String checkPhoneId = ""; // Read from file the name of the phone
	public static boolean accelRunning = false;
	public static boolean lightRunning = false;

	/**
	 * Define text watcher for user name field
	 */
	private TextWatcher watcherUN = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() > 0) {
				textChangedUN = true;
			}
		}
	};

	/**
	 * Define text watcher for user password field
	 * 
	 */
	private TextWatcher watcherUP = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() > 0) {
				textChangedUP = true;
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * Triggered when application starts
	 * 
	 * Checks that the phone id exist in the phone file system. If not register
	 * the field to enter the phone id appears. It writes the phone id on the
	 * phone's file system. It registers the phone on the server with this phone
	 * id every time, in order to avoid problems when the phone is deleted on
	 * the web site.
	 * 
	 * It authenticates the User with his password and phone id on the server.
	 * If the user exists the ActivationActivity is started.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mConPhone = new ControlPhoneId();
		registeredUser = new UserDTO();
		phoneId = new PhoneDTO();
		setContentView(R.layout.activity_main);
		final TextView phoneIdTextView = (TextView) findViewById(R.id.phoneIdTv);
		userName = (EditText) findViewById(R.id.insertEmail);
		userPassword = (EditText) findViewById(R.id.insertPassword);
		phoneIdEt = (EditText) findViewById(R.id.insertPhoneId);
		submit = (Button) findViewById(R.id.submitCredentialsButton);

		userName.addTextChangedListener(watcherUN);
		userPassword.addTextChangedListener(watcherUP);

		if (mConPhone.readFromFile(getApplicationContext()).isEmpty() == false) {
			checkPhoneId = mConPhone.readFromFile(getApplicationContext());
			ControllerAuthentication.registeredPhone = true;
			phoneIdEt.setVisibility(View.GONE);
			phoneIdTextView.setVisibility(View.GONE);
			phoneId.setPhoneid(checkPhoneId);
			Log.i(TAG, "Phone Id added" + phoneId.getPhoneid()
					+ ControllerAuthentication.registeredPhone);
		}

		submit.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				if (textChangedUN == false || textChangedUP == false) {
					return;
				} else {

					registeredUser.setPassword(userPassword.getText()
							.toString());
					registeredUser.setName(userName.getText().toString());
					Log.i("Check credentials thread", "starting");

					if (checkPhoneId.isEmpty() == true) {
						phoneId.setPhoneid(phoneIdEt.getText().toString()
								.trim());
						mConPhone.writeToFile(phoneId.getPhoneid(),
								getApplicationContext());
						Log.i(TAG, "Phone ID saved to file");
					} else {

						ControllerAuthentication.registeredPhone = true;

						Log.i(TAG, phoneId.getPhoneid()
								+ "    THe stored phone ID");
					}
					auth = new ControllerAuthentication(registeredUser);
					auth.startAuthentitication();

					registeredUser = auth.getRegisteredUser();
				}
				Log.i(TAG, phoneId.getPhoneid());
				if (isLoggedin == true) {
					Log.i(TAG, "User registered");

					activation = new Intent(MainActivity.this,
							ActivationActivity.class);
					activation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activation.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					activation.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(activation);
				} else {

					Toast.makeText(getApplicationContext(),
							"Incorrect username or password!Please try again",
							Toast.LENGTH_LONG).show();
					recreate();
				}
			}
		});

	}

	/**
	 * Method to initialize a popup This popup avoids the user to register again
	 * when pressing the return button from the {@link ActivationActivity}
	 */
	public void popupInit() {
		try {
			LayoutInflater inflater = (LayoutInflater) MainActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.popup,
					(ViewGroup) findViewById(R.id.popup_element));
			pwindo = new PopupWindow(layout, 500, 570, true);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

			btnClosePopup = (Button) layout.findViewById(R.id.cancel);
			btnClosePopup.setOnClickListener(cancel_button_click_listener);
			btnClosePopup2 = (Button) layout.findViewById(R.id.close);
			btnClosePopup2.setOnClickListener(ok_button_click);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * button to go back to {@link ActivationActivity}
	 * 
	 */
	private OnClickListener cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pwindo.dismiss();
			startActivity(activation);

		}
	};

	/**
	 * button to exit the app
	 */
	private OnClickListener ok_button_click = new OnClickListener() {
		public void onClick(View v) {
			pwindo.dismiss();
			MainActivity.this.finish();

		}
	};

	/**
	 * Start the accelerometer service, or whichever sensor the user applied
	 * for, to monitor if is stopped(mostly after we turn back to main activity)
	 */
	@Override
	protected void onResume() {
		if (registeredUser != null) {
			checkStatus(registeredUser); // ? replace with popupinit??
			// please check that
		}
		super.onResume();

	}

	private void checkStatus(UserDTO registeredUser) {
		if (isLoggedin==true) {
			popupInit();
		} else {
			return;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService(new Intent(this, AccelerometerService.class));
		stopService(new Intent(this, TimerService.class));
	}

}
