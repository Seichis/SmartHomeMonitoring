package dto;

import java.io.Serializable;

public class EventDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float value;
	private String time;
	private EVENT eventype;

	public float getValue() {
		return this.value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time2) {
		this.time = time2;
	}
	public EVENT getEventype() {
		return eventype;
	}
	public void setEventype(EVENT eventype) {
		this.eventype = eventype;
	}

}
