package dto;

import java.util.List;

public class SensorDTO {
	
	private String id;
	private List<EventDTO> events;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<EventDTO> getEvents() {
		return events;
	}
	public void setEvents(List<EventDTO> events) {
		this.events = events;
	}

}
