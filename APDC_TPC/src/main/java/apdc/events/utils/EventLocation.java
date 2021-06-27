package apdc.events.utils;

public class EventLocation {

	String location;
	long eventId;
	public EventLocation(){}
	public EventLocation(String location, long eventid) {
		this.location=location;
		this.eventId=eventid;
	}
	public String getLocation() {
		return location;
	}
	public long getEventId() {
		return eventId;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	

}
