package apdc.tpc.resources;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;

import apdc.events.utils.EventData;
import apdc.events.utils.EventData2;
import apdc.events.utils.EventParticipationMethods;
import apdc.events.utils.EventsDatabaseManagement;
import apdc.events.utils.GoogleCloudUtils;
import apdc.events.utils.Pair;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/events")
public class EventsResources {
	//private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());
	@Context
	private HttpServletRequest httpRequest;
	public static final String bucketName = "daniel1624401699897";
	private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());

	public EventsResources() {
	}
	/**
	 * creates a new event
	 * @param data JSON object with the information to create a new event
	 * @return a response object with the creation status inside
	 * 200 if success
	 * 401 if the token is not valid
	 * 400 if the event data was not ok
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA +";charset=utf-8")
	//@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doCreateEvent(@CookieParam(Constants.COOKIE_TOKEN) String value){
		Response response;
		Datastore ds = Constants.datastore;
		/*
		try {
			System.out.println("HELLO BROTHERS");
			Tester.downloadObject(bucketName,"firstObject");
			System.out.println("BUCKET CREATED!");
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
		}*/
		
		try {
			long userid = HandleTokens.validateToken(value);			
			response = EventsDatabaseManagement.createEvent(ds,httpRequest,userid);	
		}catch(Exception e) {
			response = Response.status(Status.UNAUTHORIZED).entity(Constants.g.toJson(e.getLocalizedMessage())).build();
		}
		return response;
	}
	/**
	 * loads upcoming events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/view")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetUpcomingEvents(@CookieParam(Constants.GET_EVENT_CURSOR_CK) String value, @CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		Response resp;
		try {
			System.out.println("TOKEN "+token);
			long userid = HandleTokens.validateToken(token.getValue());
			//data, cursor
			Pair<String,String> pair = EventsDatabaseManagement.getEvents(value,userid,false);
			NewCookie nk = HandleTokens.makeCookie(Constants.GET_EVENT_CURSOR_CK,pair.getV2(),token.getDomain());
			resp = Response.ok().cookie(nk).entity(pair.getV1()).build();
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	/**
	 * loads upcoming events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/view/{eventId}")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetEvent(@PathParam("eventId") long eventId, @CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		LOG.info("GOING TO GET EVENT "+eventId);
		Response resp;
		try {
			long userid = HandleTokens.validateToken(token.getValue());
			EventData2 event = EventsDatabaseManagement.getEvent(eventId,Constants.datastore,userid);
			resp = Response.ok().entity(Constants.g.toJson(event)).build();
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	/**
	 * loads all registered events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/view/finished")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetFinishedEvents(@CookieParam(Constants.FINISHED_EVENTS_CURSOR_CK) String cursor, @CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		Response resp;
		try {
			System.out.println("OLAADSDSA ");
			Constants.LOG.severe(token+" token");
			Constants.LOG.severe("GOING TO LOAD FINISHED EVENTS ----------------------------------------------------------->");
			long userid = HandleTokens.validateToken(token.getValue());
			Constants.LOG.severe(userid+"");
			Pair<String,String> pair = EventsDatabaseManagement.getEvents(cursor,userid,true);
			NewCookie nk = HandleTokens.makeCookie(Constants.FINISHED_EVENTS_CURSOR_CK,pair.getV2(),token.getDomain());
			resp = Response.ok().cookie(nk).entity(pair.getV1()).build();
		}catch(Exception e) {
			Constants.LOG.severe(e.getLocalizedMessage());
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	/**
	 * loads all registered events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/view/myevents")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response loggedUserEvents(@CookieParam(Constants.USER_EVENTS_CURSOR_CK) String cursor, @CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		Response resp;
		String result [] =null;
		try {
			long userid = HandleTokens.validateToken(token.getValue());
			Constants.LOG.severe(userid+"");
			result = EventsDatabaseManagement.getLoggedUserEvents(cursor,userid);
			NewCookie nk = HandleTokens.makeCookie(Constants.USER_EVENTS_CURSOR_CK,result[1],token.getDomain());
			resp = Response.ok().cookie(nk).entity(result[0]).build();
		}catch(Exception e) {
			Constants.LOG.severe(e.getLocalizedMessage());
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	/**
	 * deletes a particular event 
	 * @param eventId the event to be deleted
	 * @param token the logged user token to verify if the user is has permission to do so
	 * @return
	 */
	@DELETE
	@Path("/delete/{eventId}")
	public Response doDeleteEvent(@PathParam(Constants.EVENT_ID) String eventId, @CookieParam(Constants.COOKIE_TOKEN) String token) {
		Response resp;
		LOG.severe("GOING TO REMOVE THIS EVENT "+eventId);
		try {
			long userid = HandleTokens.validateToken(token);
			resp = EventsDatabaseManagement.deleteEvent(eventId,userid);
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	/**
	 * deletes a particular event 
	 * @param eventId the event to be deleted
	 * @param token the logged user token to verify if the user is has permission to do so
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED +";charset=utf-8")
	@Path("/participate")
	public Response doParticipate(@CookieParam(Constants.COOKIE_TOKEN) String token, @FormParam("eid") long eventid) {
		LOG.severe("GOING TO ADD OR REMOVE PARTICIPANT "+eventid);
		System.out.println(eventid);
		Status status;
		try {
			long userid = HandleTokens.validateToken(token);
			Transaction txn = Constants.datastore.newTransaction();
			boolean res = EventParticipationMethods.addOrRemoveParticipation(userid,eventid,txn);
			txn.commit();
			return Response.ok().entity(Constants.g.toJson(res)).build();
		}catch(Exception e) {
			LOG.severe("GOING TO PRINT THE ERROR :"+e.getLocalizedMessage());
			status=Status.FORBIDDEN;
		}
		return Response.status(status).build();
	}
	/**
	 * deletes a particular event 
	 * @param eventId the event to be deleted
	 * @param token the logged user token to verify if the user is has permission to do so
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED +";charset=utf-8")
	@Path("/rparticipation/{eventid}")
	public Response doRemoveParticipation(@CookieParam(Constants.COOKIE_TOKEN) String token, @PathParam("eventid") long eventid) {
		LOG.severe("GOING TO REmove participation "+eventid);
		System.out.println(eventid);
		Status status;
		try {
			long userid = HandleTokens.validateToken(token);
			if(EventParticipationMethods.removeParticipation(userid, eventid)) {
				status=Status.OK;
			}else {
				status=Status.BAD_REQUEST;
			}
		}catch(Exception e) {
			status=Status.FORBIDDEN;
		}
		return Response.status(status).build();
	}
	/**
	 * deletes a particular event 
	 * @param eventId the event to be deleted
	 * @param token the logged user token to verify if the user is has permission to do so
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM /*+";image/*"*/)
	@Path("/m/image")
	public Response downloadImage() {
		System.out.println("WAS CALLED AND IS OKKK");
		return Response.ok(GoogleCloudUtils.downloadObject(bucketName,"5755839738544128")).build();
		//return Response.ok().build();
	}
}
