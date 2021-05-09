package apdc.events.utils;

import com.google.cloud.datastore.Entity;


import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Transaction;

import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
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
	private static final int PAGESIGE = 6;
	private static final int TWO= 2;
	private static final Logger LOG = Logger.getLogger(EventsDatabaseManagement.class.getName());

	public EventsDatabaseManagement() {
	}
	private static String getPartString(HttpServletRequest httpRequest) {
		try {
			Part p = httpRequest.getPart("evd");
			byte [] b = new byte[(int) p.getSize()];
			InputStream is = p.getInputStream();
			is.read(b);
			String h = new String(b);
			return h;
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Blob getImageBlob(HttpServletRequest httpRequest, String part) {
		try {
			Part p = httpRequest.getPart(part);
			InputStream uploadedInputStream = httpRequest.getPart(part).getInputStream();
			byte [] b = new byte[(int) p.getSize()];
			uploadedInputStream.read(b);
			Blob blob = Blob.copyFrom(b);
			return blob;
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * creates and stores an event into the database
	 * @param datastore datastore object
	 * @param et event to create into the database
	 * @param email id of the user performing the operation
	 * @return
	 */
	public static String createEvent(Datastore datastore,HttpServletRequest httpRequest,String email) {
		//Generate automatically a key
		LOG.severe("GOING TO CREATE EVENT!!! -> "+email);
		String hhh = getPartString(httpRequest);
		if(hhh!=null) {
			System.out.println(hhh);
			return null;
		}else {
			System.out.println("NULLLL");
			if(hhh==null) {
				return null;
			}
		}
		EventData et = Constants.g.fromJson(hhh,EventData.class);

		Blob b = getImageBlob(httpRequest,"imgs");

	
		String result="";
		Transaction txn=null;
		  try {
		  com.google.cloud.datastore.Key eventKey=datastore.allocateId(datastore.newKeyFactory()
					.addAncestors(PathElement.of(USERS,email))
					.setKind(EVENTS).newKey());
			txn = datastore.newTransaction();
			Entity ev=Entity.newBuilder(eventKey)
					.set(NAME,et.getName())
					.set(DESCRIPTION,et.getDescription())
					.set(GOAL,et.getGoals())
					.set(LOCATION,et.getLocation())
					.set(MEETING_PLACE,et.getMeetingPlace())
					.set(START_DATE,makeTimeStamp(et.getStartDate()))
					.set(END_DATE,makeTimeStamp(et.getEndDate()))
					.set("imgs",b)
					.build();			
			txn.put(ev);
		    txn.commit();
		    result="1";
		  }catch(Exception e) {
			  StorageMethods.rollBack(txn);
			  result="-1";
			  LOG.severe("ERRRORR");
			  LOG.severe(e.getLocalizedMessage());
			  e.printStackTrace();
			  LOG.severe(result);
		  }
		  return result;
	}
	/**
	 * creates a google timestamp object from the string of a date representation
	 * @param date the date to be converted to a timestamp
	 * @return a timestamp of the input date
	 * @throws ParseException an exception case the operation fails
	 */
	private static Timestamp makeTimeStamp(String date) throws ParseException {
		LOG.severe("SAVING DATES <----------------> "+date);
	    Date dat=new SimpleDateFormat(Constants.DATE_FORMAT).parse(date);
		Timestamp start = Timestamp.of(dat);
		return start;
	}
	/**
	 * from a timestamp, gets the string date representation
	 * @param t - the required timestamp
	 * @return string date from the timestamp
	 */
	private static String revertTimeStamp(Timestamp t){
		String rr = new SimpleDateFormat(Constants.DATE_FORMAT).format(t.toDate());
		return rr;
	}
	/**This methods says that a certain property must not be indexed. TO CHECK LATER
	 * 
	 * @param par
	 * @return
	 */
	public static StringValue noIndexProperties(String par) {
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
	 * @return an array of size 2, one entry has the operation status and the other has a collection of pageSize events fetched
	 */
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
		while(tasks.hasNext()){
			e = tasks.next();
			events.add(getEvent(e));
		}
		results[0]=Constants.g.toJson(events);
		results[1]=tasks.getCursorAfter().toUrlSafe();
		return results;
	}
	/**
	 * deletes an event
	 * @param eventId the event to be deleted
	 * @param email email of the user performing the operation
	 */
	public static void deleteEvent (String eventId, String email) throws WebApplicationException{
		Datastore datastore = Constants.datastore;
		long event = Long.parseLong(eventId);
		 com.google.cloud.datastore.Key eventKey=datastore.newKeyFactory()
					.addAncestors(PathElement.of(USERS,email)).setKind(EVENTS).newKey(event);
		 
		Transaction txn=null;
		try {
			txn = datastore.newTransaction();
			txn = datastore.newTransaction();
			txn.delete(eventKey);
		    txn.commit();
		}catch(Exception e) {
			LOG.severe(e.getLocalizedMessage());
			StorageMethods.rollBack(txn);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	/***
	 * Converts an entity event to an object event that'll be sent to the frontend
	 * @param en the entity
	 * @return an event of type EventData
	 */
	private static EventData getEvent(Entity en) {
		EventData ed = new  EventData();
		Entity parentEntity = Constants.datastore.get(en.getKey().getParent());
		ed.setEventId(en.getKey().getId());
		ed.setDescription(en.getString(DESCRIPTION));
		ed.setEndDate(revertTimeStamp(en.getTimestamp(END_DATE)));
		ed.setGoals(en.getString(GOAL));
		ed.setLocation(en.getString(LOCATION));
		ed.setMeetingPlace(en.getString(MEETING_PLACE));
		ed.setName(en.getString(NAME));
		ed.setStartDate(revertTimeStamp(en.getTimestamp(START_DATE)));
		ed.setOrganizer(parentEntity.getString(Constants.NAME_PROPERTY));
		return ed;
	}
	/*
	 * An ancestor query limits its results to the specified entity and its descendants.
	 * This example returns all Task entities that have the specified TaskList entity as an ancestor
	public void mayBeImportant() {
		Query<Entity> query = Query.newEntityQueryBuilder()
			    .setKind("Task")
			    .setFilter(PropertyFilter.hasAncestor(
			        datastore.newKeyFactory().setKind("TaskList").newKey("default")))
			    .build();ConceptsTest.java
	}*/
}
