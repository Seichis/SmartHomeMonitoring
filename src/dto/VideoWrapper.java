package dto;

import java.io.Serializable;

public class VideoWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String phoneid;
	protected String userName;
	protected byte[] videoContent;

	public VideoWrapper() {
	}

	public String getPhoneid() {
		return phoneid;
	}

	public VideoWrapper(byte[] vid, String name, String phone) {
		this.videoContent = vid;
		this.userName = name;
		this.phoneid = phone;
	}

	public void setPhoneid(String value) {
		this.phoneid = value;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String value) {
		this.userName = value;
	}

	public byte[] getVideoContent() {
		return videoContent;
	}

	public void setVideoContent(byte[] value) {
		this.videoContent = ((byte[]) value);
	}

}
