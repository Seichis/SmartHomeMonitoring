package com.example.androidprototype;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * Service for sending an email
 * Note that this action was implemented before implementing the notifications
 * @author s141277
 *
 */
public class MailService extends Service {

	private static String username = "se2groupb@gmail.com";
	private static String password = "qwertyqwerty12";
	private static String name = "SMaSHS - It's easy!";
	public static boolean mailSent = false;

	/* (non-Javadoc)
	 * 
	 * Called when created
	 * send the mail directlu via send mail
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {

		super.onCreate();

		String subject = "SMASHS ALERT!! Something's wrong at home";
		String message = "An event occured while you are away.The information about the event is uploaded so you can visit your profile page in SMASMS web site.";
		Log.i("Service", "Created");
		sendMail(MainActivity.registeredUser.getName(), subject, message);

	}	
	
	/* (non-Javadoc)
	 * 
	 * Called when startService is called
	 * stop the service if a mail was already sent 
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i("Service", "Started");

		if (mailSent == true) {
			mailSent = false;
			stopSelf();
			Log.i("Service", "Stopped");
		}

		return START_STICKY;

	}

	
	/**
	 * 
	 * Method to create and send the email
	 * Use of create message and SendMailTask
	 * Use of createSessionObject to pass the session to the createMessage method
	 * @param email contains the email address of the user 
	 * @param subject
	 * @param messageBody
	 */
	private void sendMail(String email, String subject, String messageBody) {
		Session session = createSessionObject();

		try {
			Message message = createMessage(email, subject, messageBody,
					session);
			Log.i("Async", "Calling to execute");
			new SendMailTask().execute(message);
			Log.i("Async", "Returned");
			mailSent = true;

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to create the message of the mail
	 * The message is a javax.mail.Message object
	 * 
	 * @param email
	 * @param subject
	 * @param messageBody
	 * @param session: the session passed by send mail
	 * @return a java Mail message
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private Message createMessage(String email, String subject,
			String messageBody, Session session) throws MessagingException,
			UnsupportedEncodingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username, name));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				email, email));
		message.setSubject(subject);
		message.setText(messageBody);
		return message;
	}

	/**
	 * Creates the session for the mail, using smtp server
	 * @return the desired session
	 */
	private Session createSessionObject() {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		return Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	/**
	 * Asynchronous task to send the mail from a message, used in the send mail method
	 *
	 */
	private class SendMailTask extends AsyncTask<Message, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("Async", "Preexecute");
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Toast.makeText(getApplicationContext(), "Email sent",
					Toast.LENGTH_LONG).show();
			Log.i("Service", "Postexetute");
		}

		@Override
		protected Void doInBackground(Message... messages) {
			try {
				Log.i("Async", "Started");
				Transport.send(messages[0]);

			} catch (final MessagingException e) {
				e.printStackTrace();
				Log.i("Async", "Error");
			}
			return null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
