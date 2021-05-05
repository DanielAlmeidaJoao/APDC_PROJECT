package apdc.events.utils;

import com.google.cloud.datastore.Entity;

import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Transaction;

import apdc.utils.conts.Constants;

import java.util.LinkedList;
import java.util.List;
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
	private static final int PAGESIGE = 6;
	private static final int TWO= 2;
	private static final Logger LOG = Logger.getLogger(EventsDatabaseManagement.class.getName());

	public EventsDatabaseManagement() {
	}
	public static String createEvent(Datastore datastore, EventData et,String email) {
		//Generate automatically a key
		LOG.severe("GOING TO CREATE EVENT!!! -> "+email);

		String result="";
		//SAVE TIMESTAMPS Timestamp tp = Timestamp.of(date);

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
			QueryResults<Entity> tasks = Constants.datastore.run(queryBuilder.build());
			while (tasks.hasNext()) {
			  //Entity task = tasks.next();
			  // do something with the task
			}
			//com.google.appengine.api.datastore.Cursor nextPageCursor = tasks.getCursorAfter();
			//String encodedCursor = original.toWebSafeString();
	}
	
	public static String [] getEvents(String startCursor) {
		String [] results = new String[TWO];
		Cursor startcursor=null;
		Query<Entity> query=null;
		
		if (startCursor!=null) {
	      startcursor = Cursor.fromUrlSafe(startCursor);
	      query = Query.newEntityQueryBuilder()
				    .setKind(EVENTS)
				    .setLimit(PAGESIGE).setStartCursor(startcursor)
				    .build();
	    }else {
	    	query= Query.newEntityQueryBuilder()
				    .setKind(EVENTS)
				    .setLimit(PAGESIGE)
				    .build();
	    }

		QueryResults<Entity> tasks = Constants.datastore.run(query);
		
	    Entity e;
		List<EventData> events = new LinkedList<>();
		
		while(tasks.hasNext()) {
			e = tasks.next();
			events.add(getEvent(e));
		}
		results[0]=Constants.g.toJson(events);
		results[1]=tasks.getCursorAfter().toUrlSafe();
		return results;
	}
	public static EventData getEvent(Entity en) {
		EventData ed = new  EventData();
		ed.setEventId(en.getKey().getId());
		ed.setDescription(en.getString(DESCRIPTION));
		ed.setDuration(en.getString(DURATION));
		ed.setEndDate(en.getString(END_DATE));
		ed.setGoals(en.getString(GOAL));
		ed.setLocation(en.getString(LOCATION));
		ed.setMeetingPlace(en.getString(MEETING_PLACE));
		ed.setName(en.getString(NAME));
		ed.setStartDate(en.getString(START_DATE));
		return ed;
	}
}
