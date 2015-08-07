package dto;

import java.io.Serializable;
import java.util.List;

public class PhoneDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String phoneid;
	private List<SensorDTO> sensors;
	private List<ACTION> roles;
	private String Date;
	
	public String getPhoneid() {
		return phoneid;
	}
	public void setPhoneid(String phoneid) {
		this.phoneid = phoneid;
	}
	public List<SensorDTO> getSensors() {
		return sensors;
	}
	public void setSensors(List<SensorDTO> sensors) {
		this.sensors = sensors;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public List<ACTION> getRoles() {
		return roles;
	}
	public void setRoles(List<ACTION> roles) {
		this.roles = roles;
	}
	
}
