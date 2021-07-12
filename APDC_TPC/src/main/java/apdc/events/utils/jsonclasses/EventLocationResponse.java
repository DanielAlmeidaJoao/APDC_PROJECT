package apdc.events.utils.jsonclasses;

public class EventLocationResponse {

	String location,name;
	long eventId;
	Coords loc;
	public EventLocationResponse(){}
	public EventLocationResponse(String location, long eventid, String name, Coords loc) {
		this.location=location;
		this.eventId=eventid;
		this.name=name;
		this.loc=loc;
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
