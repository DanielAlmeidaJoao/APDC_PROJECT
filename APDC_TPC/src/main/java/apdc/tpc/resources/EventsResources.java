package apdc.tpc.resources;
import java.util.logging.Logger;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import com.google.cloud.datastore.Datastore;
import com.google.gson.Gson;
import apdc.events.utils.EventData;
import apdc.events.utils.EventsDatabaseManagement;
import apdc.tpc.utils.tokens.HandleTokens;

public class EventsResources implements EventsResourcesInterface {
	private final Gson g = new Gson();
	private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());

	public EventsResources() {
	}

	public Response doCreateEvent(EventData data) {
		LOG.severe("GOING TO CREATE AN EVENT!");
		LOG.severe(g.toJson(data));
		Datastore ds = LoginManager.datastore;
		String email = HandleTokens.validateToken(data.getToken());
		String result = EventsDatabaseManagement.createEvent(ds,data,email);
		return Response.ok().entity(g.toJson(result)).build();
	}

	public Response doGetEvent(@PathParam("nElements") String nElements) {
		LOG.severe("GOING TO FETCH 10 EVENTS AN EVENT!");
		return Response.ok().entity("OLA").build();
	}
}
