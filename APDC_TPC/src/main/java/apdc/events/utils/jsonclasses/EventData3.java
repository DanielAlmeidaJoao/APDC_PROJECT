package apdc.events.utils.jsonclasses;

public class EventData3 {
	long eventId;
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	String  eventName, images, place;
	public EventData3() {}
	public String getImages() {
		return images;
	}
	public String getPlace() {
		return place;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public void setPlace(String place) {
		this.place = place;
	}

}
