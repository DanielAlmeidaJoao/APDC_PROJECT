package apdc.tpc.resources;
import java.util.logging.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import com.google.cloud.datastore.Datastore;
import apdc.events.utils.EventData;
import apdc.events.utils.EventsDatabaseManagement;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/events")
public class EventsResources {
	private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());
	@Context
	private HttpServletRequest httpRequest;
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
	public Response doCreateEvent(@CookieParam(Constants.COOKIE_NAME) String value, EventData data){
		Response response;
		LOG.severe("GOING TO CREATE AN EVENT!");
		System.out.println("HELLLO -> kkk");
		LOG.severe(Constants.g.toJson(data));
		Datastore ds = Constants.datastore;
		String email = HandleTokens.validateToken(data.getToken());
		String result = EventsDatabaseManagement.createEvent(ds,data,email);
		if(value==null) {
			NewCookie k = HandleTokens.makeCookie(Constants.COOKIE_NAME,"daniel");
			response = Response.ok().cookie(k).entity(Constants.g.toJson(result)).build();
		}else {
			response = Response.ok().entity(Constants.g.toJson(result)).build();
		}
		return response;
	}
	/**
	 * 
	 * @param nElements number of elements loaded so far
	 * @return a response object with the object data requested
	 */
	@GET
	@Path("/view/{token}")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetEvent(@CookieParam(Constants.GET_EVENT_CURSOR_CK) String value , @PathParam("token") String token) {
		LOG.severe("cursor cookies "+value);
		//LOG.severe("NEWcookie "+cookie);
		Response resp;
		String result [] =null;
		try {
			//HandleTokens.validateToken(token);
			result = EventsDatabaseManagement.getEvents(value);
			NewCookie nk = HandleTokens.makeCookie(Constants.GET_EVENT_CURSOR_CK,result[1]);
			resp = Response.ok().cookie(nk).entity(result[0]).build();
		}catch(Exception e) {
			resp = Response.ok().entity(Constants.g.toJson("-1")).build();
		}
		
		return resp;
	}
}
