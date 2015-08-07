# SmartHomeMonitoring
The MainActivity shows a login interface where the user can enter his, on the server registered, username and password. When the user enters the application for the first time
on his phone he has to enter an id for the phone also. This id is then stored into the internal storage of the phone and is used to register the phone for the given user on the server. The
ControlPhoneId provides helper methods to write and read to the internal file.

In order to login the user and register the phone properly, the MainActivity calls a controller named ControllerAuthentication, when pressing the Submit button. This controller sends the
credentials to the server, via the login RESTful service, which approves the user or not. If the credentials are correct the same controller, using the stored phone id, registers the
phone on the server. This is done by calling the register phone RESTful service. Phone registration is done at every login in order to avoid problems when the user remove
the phone on the web interface. To change the id of the phone, the user has to remove it from the web interface and reinstall the application on the phone, then he will be able to set
a new id for the phone when starting the application. Furthermore, the phone registration returns a PhoneDTO, which contains the list of sensors to be activated. This data transfer
object is assigned to a global variable named MainActivity.phoneid.

When the credentials are approved the MainActivity starts another activity called ActivationActivity via an Intent. An intent is an abstract description of an operation to be
performed. It can be used to start and stop activities and services.
It is worth mentioning that, if the user goes back from the ActivationActivity to the MainActivity, a popup appears and gives the user the possibility either to enter again the
ActivationActivity or to exit the application. This is done in order to avoid internal server errors if the user registers again.
As mentioned above the next activity is the ActivationActivity. This activity shows only a button which indicates to activate the system.When the button is pressed for the first time, several operations are executed. A global
variable, MainActivity.isActivated, is set to true and the button shows Deactivate.

Then the activity starts the sensors services related to the list of sensors to be activated in
the MainActivity.phoneid data transfer object. A service is an application component that can
perform operations in the background and does not provide a user interface. Up to two
services can be started at this point: one for the accelerometer sensor and one for the light
detection sensor. This two sensors services are implemented in AccelerometerService and
LightDetectionService. More information on these services are provided in the next section.
When the system gets activated another service is also called, it is the TimerService. This
service gets the actions and sensors to be enabled from the server periodically. In order to
schedule threads it uses a java Timer and an android Handler. The Timer allows to run the
threads for getting the commands periodically and the Handler enqueue the threads and
execute them one after the other. The threads are wrapped inside a TimerTask that can be
used by the Timer. These threads get the commands from the server and start and stop the
sensors and actions related to them.

The actual call to the commands RESTful service is done by another encapsulated thread
called ControllerUserSettingsCommand. This controller performs a call to the RESTful
service, gets the commands to be performed and, if any, wrap the services and activities to
be started in Intents. These intents are stored in a global list defined and used by the
TimerService.

This separation between calls to the Restful services and the start and stop services and
activities is done because of the fact that android doesn’t allow to perform network
operations on the main thread. Classes that don’t extend Context cannot start and stop
intents.

Furthermore, if the user press again the button on the ActivationActivity, the running sensor
services and the TimerService are stopped. The button will show Activate again.
Sensors and Actions Implementation
Sensors and actions commands are represented in the same way in the ACTION
enumeration.

A phone can react on two sensors, the accelerometer and the light detection sensor. Four
combinations of sensors are possible for one phone: both, one of them, none of them. If the
phone is set to react on none of the sensors, it only reacts on user request actions sent from
the server. Both sensors are implemented as services.
The accelerometer sensor is implemented in the AccelerometerService. The service
registers a listener on the accelerometer sensor. Whenever a new acceleration is detected,
the service calculates this acceleration and sends an event through the ControllerEventData
if the acceleration is high enough.
The ControllerEventData is a controller that sends information from the application to the
server. Two types of data are sent, events and videos.
For sending events the controller creates and sets an EventDTO with, for the acceleration
changes, an EVENT.ACCELEROMETER_EVENT and uses the addevent RESTful service
to send the event to the server. More information is available on the uploading video later.
The light detection sensor is implemented in a similar way. It registers a listener on the light
detection sensor and sends an event of type EVENT.LIGHTLEVEL_EVENT if the light level
changes significantly.
It is to notice that, a PowerManager WakeLock and a BroadcastReceiver are used in order
to get accelerations and light detection changes even if the screen is off.
An action is triggered whenever the ControllerUserSettingsCommands gets the
corresponding enumeration value (insight the CommandDTO commands list) from the
server. This value can be sent by the server when it received an event from a sensor of the
phone or, for some of the actions, when a user presses a button on the web interface, e.g.
for streaming. Five actions are implemented: alarm/notification action, video record action,
sound play action, send email action and stream action.
The notification action is implemented through two classes: the AlarmToNotify class and the
AlarmReceiver class. AlarmReceiver extends BroadcastReceiver. AlarmToNotify is called
whenever an ACTION.ALARM_ACTION is received. It creates an Intent for the
BroadcastReceiver. When the alarm starts the notification is created in the AlarmReceiver
and enables a notification.
The action to play a sound alarm is implemented in the PlayAlarmService. When the
service is started it simply creates a MediaPlayer with a given alarm sound and play it. An
onCompletionListener allows to replay the alarm a fixed number of times.
The recording action and streaming action are implemented in the VideoRecorder activity.
When the activity is created it initializes and sets the SurfaceView, Camera and
MediaRecorder needed to record. Then it starts recording. The thread that records and
uploads the video is done in a different way regarding if the action is streaming or just
recording.
When the action is a recording action, a thread called RecordingThread records a unique
long video for a fixed time and uploads it to the server through the ControllerEventData. In
order to upload the video it uses the VideoWrapper data transfer object. The reason of
recording one long video is, that, the recording action can be sent by the server when a
sensor event is triggered. The user can then watch the complete video when he logs into the
web interface.
When the action is streaming, it means that the user tries to get a real time video feed. In
order to accomplish that, a thread called UserRecordingThread records small fixed time
chunks and upload them in the same time via the ControllerEventData in a loop. The loop
condition is set by MainActivity.streaming. This variable is set to true/false while the user
pushes the streaming button on the web interface.
When recording finishes the MediaRecorder and Camera are properly released and
stopped.
We implemented a service (MailService) for sending an email notification to user’s
registered email account. We make use of the JavaMail API. JavaMail API provides a
platform­independent and protocol­independent framework to build mail and messaging
applications. In order to use this API, we need to add the relevant libraries to the build path.
The steps that we follow in order to send the mail are: getting a Session, creating a
MimeMessage and set From, To, Subject in the message, setting the text of the message,
and sending the email via the Transport object. We are using the SMTP server and calling
the javax.mail.PasswordAuthentication class to authenticate the password. The sender’s
email is the developer’s gmail. We create an AsyncTask (SendMailTask) in order to call the
Transport object to send the email in the background. When the mail is sent, a toast
message appears to ensure that the mail was sent to user’s account.
