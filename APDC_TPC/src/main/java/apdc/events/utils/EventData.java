package apdc.events.utils;

public class EventData {

	String name, description, goals, location,
	meetingPlace, startDate, endDate, organizer, startTime, endTime, images;
	long eventId, volunteers;
	//volunteers is long because I am unable to fetch integer value from datastore without casting.
	/*
	description: ""
		endDate: ""
		endTime: ""
		goals: ""
		location: "null"
		meetingPlace: "null"
		name: "dasd"
		startDate: ""
		startTime: ""
		volunteers:*/
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public long getVolunteers() {
		return volunteers;
	}

	public void setVolunteers(long volunteers) {
		this.volunteers = volunteers;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}
	
	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}
	

	public EventData() {}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		//String unsafe = "<p><a href='http://example.com/' onclick='stealCookies()'>Link</a></p>";
		//String safe = Jsoup.clean(unsafe, Whitelist.basic());
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGoals() {
		return goals;
	}
	public void setGoals(String goals) {
		this.goals = goals;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getMeetingPlace() {
		return meetingPlace;
	}
	public void setMeetingPlace(String meetingPlace) {
		this.meetingPlace = meetingPlace;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
