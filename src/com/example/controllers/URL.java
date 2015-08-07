package com.example.controllers;

/**
 * <h1>Web Service URLs</h1>
 * This class contains all the URLs of the web app used by the other classes
 * and packages.
 *
 */

public class URL {
	public final static String BASE_URL = "http://se-se2-e14-glassfish41-b.compute.dtu.dk:8080/Prototype_FeatureComplete/rest";
//	public final static String BASE_URL = "http://10.16.174.187:8080/Prototype_FeatureComplete/rest";
	public final static String AUTH_URL = "/UserWebServices/login?username={username}&&password={password}";
	public final static String PHONE_URL = "/PhoneWebServices/registerPhone?username={username}&&phoneid={phoneid}";
	public final static String ACTIONS_URL = "/CommandServices?username={username}&&password={password}&&phoneid={phoneId}";
	public final static String EVENT_URL = "/EventWebServices/addevent?value={value}&&username={username}&&phoneid={phoneid}&&eventtype={eventtype}";
	public final static String VIDEO_UPLOAD_URL_START = "/VideoServices/";
	public final static String VIDEO_UPLOAD_URL_END = "/addVideo"; //takes VideoWrapper
	public final static String VIDEO_DONWLOAD_URL = "/VideoServices/{username}/getVideo?username={username}&&videoname={videoname}";
	public final static String VIDEO_STREAM_URL_START = "/VideoServices/";
	public final static String VIDEO_STREAM_URL_END = "/addStream"; //takes VideoWrapper
	public final static String ROLES_URL = "/PhoneWebServices/getRoles?username={username}&&phoneid={phoneid}";
	public final static String VIDEO_FINALIZESTREAM_URL = "/VideoServices/{username}/finalizeStream?username={username}";	
}

