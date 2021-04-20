package apdc.events.creation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import apdc.events.utils.EventData;
import apdc.tpc.resources.LoginManager;
import apdc.tpc.utils.AuthenticateUser;

@Path("/events")
public class EventsResources {

	public EventsResources() {
	}

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doCreateEvent(EventData data) {
		String email = AuthenticateUser.validateToken(LoginManager.datastore,data.getToken());
		return null;
	}
}
