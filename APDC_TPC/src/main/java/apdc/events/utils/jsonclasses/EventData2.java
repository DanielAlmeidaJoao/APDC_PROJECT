package apdc.events.utils.jsonclasses;

public class EventData2 extends EventData {

	boolean owner, participating;
	String participants,imgUrl, reports, eventAddress;
	Coords loc;
	public Coords getLoc() {
		return loc;
	}
	public void setLoc(Coords loc) {
		this.loc = loc;
	}
	public String getEventAddress() {
		return eventAddress;
	}
	public Coords getEventCoords() {
		return loc;
	}
	public void setEventAddress(String eventAddress) {
		this.eventAddress = eventAddress;
	}
	public void setEventCoords(Coords eventCoords) {
		this.loc = eventCoords;
	}
	public String getReports() {
		return reports;
	}
	public void setReports(String reports) {
		this.reports = reports;
	}
	int currentParticipants;
	long countComments;
	
	public long getCountComments() {
		return countComments;
	}
	public void setCountComments(long countComments) {
		this.countComments = countComments;
	}
	public EventData2() {
		currentParticipants=0;
		reports="";
	}
	public boolean isParticipating() {
		return participating;
	}
	public String getParticipants() {
		return participants;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public int getCurrentParticipants() {
		return currentParticipants;
	}
	public void setParticipating(boolean participating) {
		this.participating = participating;
	}
	public void setParticipants(String participants) {
		this.participants = participants;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public void setCurrentParticipants(int currentParticipants) {
		this.currentParticipants = currentParticipants;
	}
	public boolean isOwner() {
		return owner;
	}
	public void setOwner(boolean isOwner) {
		this.owner = isOwner;
	}
}
