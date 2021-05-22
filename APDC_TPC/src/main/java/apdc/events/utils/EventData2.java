package apdc.events.utils;

public class EventData2 extends EventData {
	boolean owner, participating;
	String participants;
	int currentParticipants;
	public boolean isOwner() {
		return owner;
	}
	public void setOwner(boolean isOwner) {
		this.owner = isOwner;
	}
	public EventData2() {
		currentParticipants=0;
	}

}
