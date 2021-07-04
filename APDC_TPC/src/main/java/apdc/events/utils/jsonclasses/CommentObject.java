package apdc.events.utils.jsonclasses;

public class CommentObject {

	String comment, date;
	long eventid, commentid;
	public CommentObject() {
	}
	public CommentObject(long eventid, String comment, String date,long commentId){
		this.eventid=eventid;
		this.comment=comment;
		this.date=date;
		this.commentid=commentId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}
	public long getEventid() {
		return eventid;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public void setEventid(long eventid) {
		this.eventid = eventid;
	}

}
