package apdc.events.utils;

public class EventData {

	String name, description, goals, location,
	meetingPlace, startDate, endDate, duration, token;
	public String getToken() {
		return token; //ola
	}
	public void setToken(String token) {
		this.token = token;
	}
	public EventData() {
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
}
