package com.example.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.example.androidprototype.MainActivity;

import dto.PhoneDTO;
import dto.UserDTO;
/**
 * 
 * <h1>Authentication of User</h1>
 * This is the class that verifies user's credentials 
 * @author s141279,s141966
 */

public class ControllerAuthentication {
	protected String userName;
	protected String userPassword;
	private static boolean correctCredentials = false;
	private String phoneIdToRegister;
	private Thread authenticationThread;
	private boolean userValid = false; // check if the user is registered
	private UserDTO credResponse = new UserDTO();
	private UserDTO userToCheck = new UserDTO();
	public static boolean registeredPhone = false;
	
	/**
	 * This method takes the username, password, and phoneid
	 * and starts a new Thread to check the values
	 * @param user: UserDTO object
	 */

	public ControllerAuthentication(final UserDTO user) {
		this.userToCheck = user;
		this.userName = user.getName();
		this.userPassword = user.getPassword();
		this.phoneIdToRegister = MainActivity.phoneId.getPhoneid();
		this.authenticationThread = new Thread() {
			public void run() {
				userValid = checkCredentials(userName, userPassword,
						phoneIdToRegister);
			}
		};
	}
	/*
	 * 
	 */
	public void startAuthentitication() {
		authenticationThread.start();
		
		// It start and join the thread,
		// because android doesn't support network operations on the main thread.
		try {
			authenticationThread.join(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method calls RestTemplate to convert the credentials and checks 
	 * if they are correct. Returns true in this case.
	 * @param userName a username string from UserDTO
	 * @param password a password string from UserDTO
	 * @param phoneId a phoneid string from MainActivity
	 * @return  a boolean that indicates if the credentials are the same or not
	 *  as in JSON
	 */

	private boolean checkCredentials(String userName, String password,
			String phoneId) {
		Log.i("Login", "before try");
		try {
			Log.i("Login", "in try");
			final String url = URL.BASE_URL + URL.AUTH_URL;

			Log.i("Login", "trying to connect to authentication url" + url);
			final Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("username", userName);
			parameters.put("password", password);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJacksonHttpMessageConverter());
			credResponse = restTemplate.getForObject(url, UserDTO.class,
					parameters);
			Log.i("Login", "after taking the stream  " + userName + "    "
					+ password);

		} catch (Exception e) {
			
			e.printStackTrace();
			Log.i("Login", "not checked");
		} finally {
			if (credResponse != null) {
				Log.i("Login",
						"finally checking checkedCredentials"
								+ Boolean.toString(correctCredentials));
				correctCredentials = true;

				final String urlPhone = URL.BASE_URL + URL.PHONE_URL;
				final Map<String, String> parametersPhone = new HashMap<String, String>();
				parametersPhone.put("username", userName);
				parametersPhone.put("phoneid", phoneId);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJacksonHttpMessageConverter());
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
			
					e.printStackTrace();
				}

				PhoneDTO result = restTemplate.getForObject(urlPhone,
						PhoneDTO.class, parametersPhone);

				MainActivity.phoneId = result;
				MainActivity.phoneId.setRoles(result.getRoles());
				if (result.getRoles()!=null){
				Log.i("Role", MainActivity.phoneId.getRoles().get(0).toString());
				}else{
					Log.i("Phone","no roles");
				}
				if (result.getPhoneid() != null) {
					registeredPhone = true;
					Log.i("After phone registration", result.getPhoneid()
							+ "   " + result.getDate());
				} else {
					Log.i("PHone", "Didnt get registered");
				}

			} else {
				correctCredentials = false;
			}
		}
		return correctCredentials;
	}
	/**
	 *This method returns the UserDTO that was checked if his 
	 *credentials were correct.
	 * @return user's credentials
	 */

	public UserDTO getRegisteredUser() {
		if (userValid == true) {
			Log.i("Login", "succesful and assigned userName");
			MainActivity.isLoggedin = true;
			return userToCheck;
		} else {
			Log.i("Login", "failed");
			return null;
		}
	}
}
