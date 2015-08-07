package com.example.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;


/**
 * 
 * <h1>Phone ID Storage</h1>
 * This class contains methods for saving the phone ID that the user has inserted 
 * via MainActivity as a txt file and reading it.
 * @author s141279
 */

public class ControlPhoneId {
	/**
	 * @author 
	 * This method writes the txt file with the phone ID name.
	 */
	public void writeToFile(String data, Context context) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					context.openFileOutput("phoneid.txt", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
	/**
	 * @author 
	 * This method reads the phone ID name from the txt file.
	 */
	public String readFromFile(Context context) {

		String phoneIdStored = "";

		try {
			InputStream inputStream = context.openFileInput("phoneid.txt");

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				phoneIdStored = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return phoneIdStored;
	}
}