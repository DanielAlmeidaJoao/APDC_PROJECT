package apdc.events.utils.jsonclasses;

public class EventLocation {

	String location,name;
	long eventId;
	public EventLocation(){}
	public EventLocation(String location, long eventid, String name) {
		this.location=location;
		this.eventId=eventid;
		this.name=name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
