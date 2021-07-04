package apdc.events.utils.jsonclasses;

public class CommentObject2 extends CommentObject {

	String ownerName, urlProfilePicture;
	boolean owner;
	public boolean isOwner() {
		return owner;
	}
	public void setOwner(boolean owner) {
		this.owner = owner;
	}
	public CommentObject2() {}
	public CommentObject2(long eventid, String comment, String date, long commentId){
		super(eventid, comment, date,commentId);
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
