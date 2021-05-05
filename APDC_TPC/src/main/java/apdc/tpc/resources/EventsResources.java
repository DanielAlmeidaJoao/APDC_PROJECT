package apdc.tpc.resources;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
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

import com.google.cloud.datastore.Datastore;
import apdc.events.utils.EventData;
import apdc.events.utils.EventsDatabaseManagement;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/events")
public class EventsResources {
	//private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());
	@Context
	private HttpServletRequest httpRequest;
	private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());

	public EventsResources() {
	}
	/**
	 * creates a new event
	 * @param data JSON object with the information to create a new event
	 * @return a response object with the creation status inside
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doCreateEvent(@CookieParam(Constants.COOKIE_TOKEN) String value, EventData data){
		Response response;
		Datastore ds = Constants.datastore;
		try {
			String email = HandleTokens.validateToken(value);
			String result = EventsDatabaseManagement.createEvent(ds,data,email);
			response = Response.ok().entity(Constants.g.toJson(result)).build();
		}catch(Exception e) {
			response = Response.status(Status.FORBIDDEN).build();
		}
		return response;
	}

	/**
	 * loads all registered events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/view")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetEvent(@CookieParam(Constants.GET_EVENT_CURSOR_CK) String value ,@CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		Response resp;
		String result [] =null;
		try {
			HandleTokens.validateToken(token.getValue());
			result = EventsDatabaseManagement.getEvents(value);
			NewCookie nk = HandleTokens.makeCookie(Constants.GET_EVENT_CURSOR_CK,result[1],token.getDomain());
			resp = Response.ok().cookie(nk).entity(result[0]).build();
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
	@DELETE
	@Path("/delete/{eventId}")
	public Response doDeleteEvent(@PathParam(Constants.EVENT_ID) String eventId, @CookieParam(Constants.COOKIE_TOKEN) String token) {
		Response resp;
		LOG.severe("GOING TO REMOVE THIS EVENT "+eventId);
		resp = Response.ok().build();
		
		try {
			String email = HandleTokens.validateToken(token);
			EventsDatabaseManagement.deleteEvent(eventId,email);
			resp = Response.ok().build();
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
}
