package apdc.events.utils;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Transaction;

import apdc.tpc.resources.LoginManager;

import java.util.logging.Logger;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;

public class EventsDatabaseManagement {

	private static final String EVENTS="EVENTS";
	private static final String USERS = "Users";
	private static final String NAME="NAME";
	private static final String DESCRIPTION="DESCRIPTION";
	private static final String GOAL="GOAL";
	private static final String LOCATION="LOCATION";
	private static final String MEETING_PLACE="MEETING_PLACE";
	private static final String START_DATE="START_DATE";
	private static final String END_DATE="END_DATE";
	private static final String DURATION="DURATION";
	private static final Logger LOG = Logger.getLogger(EventsDatabaseManagement.class.getName());

	public EventsDatabaseManagement() {
	}
	public static String createEvent(Datastore datastore, EventData et,String email) {
		//Generate automatically a key
		LOG.severe("GOING TO CREATE EVENT!!! -> "+email);

		String result="";
		
		  try {
		  com.google.cloud.datastore.Key eventKey=datastore.allocateId(datastore.newKeyFactory()
					.addAncestors(PathElement.of(USERS,email))
					.setKind(EVENTS).newKey());
			Transaction txn = datastore.newTransaction();
			Entity ev=Entity.newBuilder(eventKey)
					.set(NAME,et.getName())
					.set(DESCRIPTION,et.getDescription())
					.set(GOAL,et.getGoals())
					.set(LOCATION,et.getLocation())
					.set(MEETING_PLACE,et.getMeetingPlace())
					.set(START_DATE,et.getStartDate())
					.set(END_DATE,et.getEndDate())
					.set(DURATION,et.getDuration())
					.build();
			txn.put(ev);
		    txn.commit();
		    result="1";
		  }catch(Exception e) {
			  result="-1";
			  LOG.severe("ERRRORR");
			  LOG.severe(e.getLocalizedMessage());
			  e.printStackTrace();
			  LOG.severe(result);
		  }
		  return result;
	}
	public static StringValue noIndexProperties(String par, Cursor pageCursor) {
		return StringValue.newBuilder(par)
		.setExcludeFromIndexes(true).build();
	}
	/**
	 * Caution: Be careful when passing a cursor to a client, such as in a web form.
	 * Although the client cannot change the cursor value to access results outside of the original query,
	 *  it is possible for it to decode the cursor to expose information about result entities,
	 *  such as the project ID, entity kind, key name or numeric ID, ancestor keys,
	 *  and properties used in the query's filters and sort orders.
	 *  If you don't want users to have access to that information,
	 *  you can encrypt the cursor, or store it and provide the user with an opaque key
	 * @param pageSize
	 * @param pageCursor
	 */
	public static void fetchEvent(int pageSize, String pageCursor) {
		EntityQuery.Builder queryBuilder = Query.newEntityQueryBuilder().setKind("Task")
			    .setLimit(pageSize);
			if (pageCursor != null) {
			  queryBuilder.setStartCursor(null);
			}
			QueryResults<Entity> tasks = LoginManager.datastore.run(queryBuilder.build());
			while (tasks.hasNext()) {
			  Entity task = tasks.next();
			  // do something with the task
			}
			Cursor nextPageCursor = tasks.getCursorAfter();
	}
}
