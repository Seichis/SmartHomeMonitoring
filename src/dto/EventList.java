package dto;

import java.util.ArrayList;

public class EventList {

	private ArrayList<EventDTO> events;

	public EventList(){		
	}
	public EventList(ArrayList<EventDTO> events){	
		this.events = events;
	}
	
	
	public ArrayList<EventDTO> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<EventDTO> events) {
		this.events = events;
	}
	public void addEvent(EventDTO ev){
		this.events.add(ev);
	}
}
