package apdc.events.utils.jsonclasses;

import java.util.List;

public class LoadCommentsResponse {

	List<CommentObject2> comments;
	String cursor;
	public LoadCommentsResponse() {}
	public LoadCommentsResponse(List<CommentObject2> comments, String cursor) {
		this.comments=comments;
		this.cursor=cursor;
	}
}
