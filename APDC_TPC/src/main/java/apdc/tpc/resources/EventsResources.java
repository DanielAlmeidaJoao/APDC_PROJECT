package apdc.tpc.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.gson.Gson;

import apdc.events.utils.EventData;
import apdc.events.utils.EventsDatabaseManagement;
import apdc.tpc.utils.AuthenticateUser;

@Path("/events")
public class EventsResources {
	private final Gson g = new Gson();
	private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());

	public EventsResources() {
	}

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doCreateEvent(EventData data) {
		LOG.severe("GOING TO CREATE AN EVENT!");
		LOG.severe(g.toJson(data));
		Datastore ds = LoginManager.datastore;
		String email = AuthenticateUser.validateToken(ds,data.getToken());
		String result = EventsDatabaseManagement.createEvent(ds,data,email);
		return Response.ok().entity(g.toJson(result)).build();
	}
	@GET
	@Path("/ev")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetEvent() {
		return Response.ok().entity("OLA").build();
	}
}
