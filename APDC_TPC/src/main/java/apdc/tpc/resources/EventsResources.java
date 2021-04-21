package apdc.tpc.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import apdc.events.utils.EventData;

@Path("/events")
public class EventsResources {
	private final Gson g = new Gson();

	public EventsResources() {
	}

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doCreateEvent(EventData data) {
		//String email = AuthenticateUser.validateToken(LoginManager.datastore,data.getToken());
		//String result = EventsDatabaseManagement.createEvent(LoginManager.datastore,data,email);
		String gg = g.toJson(data);
		System.out.println(gg);
		return Response.ok().entity(gg).build();
	}
	@GET
	@Path("/ev")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetEvent() {
		return Response.ok().entity("OLA").build();
	}
}
