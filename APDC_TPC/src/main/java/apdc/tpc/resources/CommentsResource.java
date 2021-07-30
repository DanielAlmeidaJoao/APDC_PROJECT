package apdc.tpc.resources;

import javax.ws.rs.Consumes;


import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import apdc.events.utils.CommentDatastoreManagement;
import apdc.events.utils.jsonclasses.CommentObject;
import apdc.events.utils.jsonclasses.CommentObject2;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/comments")
public class CommentsResource {

	public CommentsResource() {}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	//@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response postComment(@CookieParam(Constants.COOKIE_TOKEN) String value, CommentObject comment){
		System.out.println(" I AM COMMECNT "+comment);
		Response response=null;
		try {
			long userid = HandleTokens.validateToken(value);
			CommentObject2 commentObj = CommentDatastoreManagement.addComment(comment.getEventid(),userid,comment.getComment());
			if(commentObj!=null) {
				response = Response.ok().entity(Constants.g.toJson(commentObj)).build();
			}else {
				response = Response.status(Status.BAD_REQUEST).build();
			}
		}catch(Exception e) {
			response = Response.status(Status.UNAUTHORIZED).entity(Constants.g.toJson(e.getLocalizedMessage())).build();
		}
		return response;
	}
	@DELETE
	@Path("/remove/{commentId}")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	//@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response removeComment(@CookieParam(Constants.COOKIE_TOKEN) String value, @PathParam("commentId") long commentId){
		System.out.println(" I AM COMMECNT "+commentId);
		Response response=null;
		try {
			long userid = HandleTokens.validateToken(value);
			boolean result = CommentDatastoreManagement.removeComment(commentId,userid);
			if(result) {
				response = Response.ok().build();
			}else {
				response = Response.status(Status.BAD_REQUEST).build();
			}
		}catch(Exception e) {
			response = Response.status(Status.UNAUTHORIZED).entity(Constants.g.toJson(e.getLocalizedMessage())).build();
		}
		return response;
	}
	@GET
	@Path("/load/{eventid}")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	//@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response loadComments(@CookieParam(Constants.COOKIE_TOKEN) String value,@PathParam("eventid") long eventid, @QueryParam("c") String cursor){
		System.out.println(" I AM GOING TO LOAD COMMMENTS "+eventid+ " cursor "+cursor);
		Response response=null;
		try {
			long userid = HandleTokens.validateToken(value);
			String result = CommentDatastoreManagement.loadComments(eventid,cursor,userid);		
			if(result==null) {
				response = Response.status(Status.BAD_REQUEST).build();
			}else {
				response = Response.ok().entity(result).build();
			}
		}catch(Exception e) {
			response = Response.status(Status.UNAUTHORIZED).entity(Constants.g.toJson(e.getLocalizedMessage())).build();
		}
		return response;
	}
}
