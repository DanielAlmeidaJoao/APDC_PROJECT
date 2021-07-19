package apdc.events.utils.jsonclasses;

public class CommentObject2 extends CommentObject {

	String ownerName, urlProfilePicture;
	long ownerId;
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	boolean owner;
	public boolean isOwner() {
		return owner;
	}
	public void setOwner(boolean owner) {
		this.owner = owner;
	}
	public CommentObject2() {}
	public CommentObject2(long eventid, String comment, String date, long commentId, long ownerId){
		super(eventid, comment, date,commentId);
		this.ownerId=ownerId;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public String getUrlProfilePicture() {
		return urlProfilePicture;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public void setUrlProfilePicture(String urlProfilePicture) {
		this.urlProfilePicture = urlProfilePicture;
	}
}
