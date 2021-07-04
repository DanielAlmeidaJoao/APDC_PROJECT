package apdc.events.utils;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import apdc.events.utils.jsonclasses.EventData;
import apdc.events.utils.jsonclasses.EventData2;
import apdc.events.utils.jsonclasses.EventLocation;
import apdc.events.utils.jsonclasses.ReportEventArgs;
import apdc.events.utils.jsonclasses.ReportProperty;
import apdc.tpc.resources.EventsResources;
import apdc.tpc.resources.LoginManager;
import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.users.User;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;

public class EventsDatabaseManagement {

	public static final String EVENTS="EVENTS";
	private static final String USERS = "Users";
	private static final String NAME="NAME";
	private static final String DESCRIPTION="DESCRIPTION";
	private static final String GOAL="GOAL";
	private static final String LOCATION="LOCATION";
	private static final String REPORT_TEXTS_PROPERTY="REPORT_TEXTS";
	private static final String REPORTED_PROP="REPORTED";


	private static final String MEETING_PLACE="MEETING_PLACE";
	private static final String START_DATE="START_DATE";
	private static final String END_DATE="END_DATE";
	private static final String VOLUNTEERS="N_VOLUNTEERS";
	private static final String EVENT_OWNER="OWNER";


	private static final int PAGESIGE = 6;
	private static final int TWO= 2;
	private static final Logger LOG = Logger.getLogger(EventsDatabaseManagement.class.getName());

	public EventsDatabaseManagement() {
	}
	/**
	 * get the html form data input to create an event
	 * @param httpRequest
	 * @return
	 */
	private static String getPartString(HttpServletRequest httpRequest) {
		try {
			Part p = httpRequest.getPart(Constants.EVENT_FORMDATA_KEY);
			/*
			p.delete();
			byte [] b = new byte[(int) p.getSize()];
			InputStream is = p.getInputStream();
			is.read(b);*/
			String h = new String(IOUtils.toByteArray(p.getInputStream()));
			return h;
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static EventData2 getEvent(long eventid,Datastore datastore,long userid) {
		com.google.cloud.datastore.Key eventKey = datastore.newKeyFactory().setKind(EVENTS).newKey(eventid);
		Entity ev = datastore.get(eventKey);
		EventData2 event = getEvent(ev,userid, false);
		return event;
	}
	
	public static Response addReport(ReportEventArgs report, long userid) {
		Response result=null;
		Datastore datastore = Constants.datastore;
		try {
			com.google.cloud.datastore.Key eventKey = datastore.newKeyFactory().setKind(EVENTS).newKey(report.getEventId());
			Transaction txn = datastore.newTransaction();
			Entity event = txn.get(eventKey);
			if(event!=null) {
				if(event.getLong(EVENT_OWNER)==userid) {
					return Response.status(Status.UNAUTHORIZED).build();
				}
				/*
				Entity user = StorageMethods.getUser(datastore, userid);
				if(!user.getString(StorageMethods.ROLE_PROP).equals(StorageMethods.SU)) {
					return Response.status(Status.CONFLICT).build();
				}*/
				ReportProperty rep = Constants.g.fromJson(event.getString(REPORT_TEXTS_PROPERTY),ReportProperty.class);
				rep.addReport(report.getReportText());
				Entity.Builder  builder =Entity.newBuilder(eventKey)
						.set(NAME,event.getString(NAME))
						.set(DESCRIPTION,event.getString(DESCRIPTION))
						.set(GOAL,event.getString(GOAL))
						.set(LOCATION,event.getString(LOCATION))
						.set(START_DATE,event.getTimestamp(START_DATE))
						.set(END_DATE,event.getTimestamp(END_DATE))
						.set(VOLUNTEERS,event.getLong(VOLUNTEERS))
						.set(EVENT_OWNER,event.getLong(EVENT_OWNER))
						.set(Constants.EVENT_PICS_FORMDATA_KEY,event.getString(Constants.EVENT_PICS_FORMDATA_KEY))
						.set(REPORTED_PROP,true)
						.set(REPORT_TEXTS_PROPERTY,noIndexProperties(Constants.g.toJson(rep)));
						//.set(REPORT_PROPERTY,event.getString(REPORT_PROPERTY));
				
				event = builder.build();
				event = txn.put(event);
				txn.commit();
				result=Response.ok().build();
			}else {
				 result=Response.status(Status.NOT_FOUND).build();
			}
		}catch(Exception e) {
			e.printStackTrace();
			result=Response.status(Status.BAD_REQUEST).build();
		}
		return result;
	}
	/**
	 * creates and stores an event into the database
	 * @param datastore datastore object
	 * @param et event to create into the database
	 * @param email id of the user performing the operation
	 * @return
	 */
	public static Response createEvent(Datastore datastore,HttpServletRequest httpRequest,long userid) {
		//Generate automatically a key
		LOG.severe("GOING TO CREATE EVENT!!! -> "+userid);
		String eventJsonData = getPartString(httpRequest);
		EventData et =null;
		long eventId=-1;
		try {
			et = Constants.g.fromJson(eventJsonData,EventData.class);
			LOG.severe(et.getEventId()+" ia ma event id");
			eventId	= et.getEventId();
		}catch(Exception e) {
			LOG.severe(" SOMETHING WENT WRONG "+e.getLocalizedMessage());

		}
		Response result;
		Transaction txn=null;
		  try {
			txn = datastore.newTransaction();
			Entity ev;
			  com.google.cloud.datastore.Key eventKey;
			  KeyFactory kf = datastore.newKeyFactory()
						.setKind(EVENTS);
			  if(eventId>Constants.ZERO) {
				  eventKey = kf.newKey(eventId);
				  ev = txn.get(eventKey);
				  if(ev==null) {
					return Response.status(Status.NOT_FOUND).build();
				  }
			  }else {
				  eventKey=datastore.allocateId(kf.newKey());
			  }		  
		  //com.google.cloud.datastore.Key eventKey=datastore.newKeyFactory()
					//.addAncestors(PathElement.of(USERS,email)).setKind(EVENTS).newKey(event);
			
			Entity.Builder  builder =Entity.newBuilder(eventKey)
					.set(NAME,et.getName())
					.set(DESCRIPTION,et.getDescription())
					.set(GOAL,et.getGoals())
					.set(LOCATION,et.getLocation())
					//.set(MEETING_PLACE,noIndexProperties(et.getMeetingPlace()))
					.set(START_DATE,makeTimeStamp(et.getStartDate(),et.getStartTime()))
					.set(END_DATE,makeTimeStamp(et.getEndDate(),et.getEndTime()))
					.set(VOLUNTEERS,et.getVolunteers())
					.set(EVENT_OWNER,userid);
			
			//handles the reports
			if(eventId<=Constants.ZERO) {
				builder.set(REPORTED_PROP,false);
				builder.set(REPORT_TEXTS_PROPERTY,noIndexProperties(Constants.g.toJson(new ReportProperty())));
			}else {
				Entity event = datastore.get(eventKey);
				builder.set(REPORTED_PROP,event.getBoolean(REPORTED_PROP));
				builder.set(REPORT_TEXTS_PROPERTY,noIndexProperties(event.getString(REPORT_TEXTS_PROPERTY)));
			}
					
			try {
				Part p = httpRequest.getPart("img_cover");
				if(p!=null) {
					String eventUrl = GoogleCloudUtils.uploadObject(EventsResources.bucketName,eventKey.getId()+"",p.getInputStream());
					builder = builder.set(Constants.EVENT_PICS_FORMDATA_KEY,noIndexProperties(eventUrl));
				}
			}catch(Exception e) {
				e.printStackTrace();
				LOG.severe(e.getLocalizedMessage());
				return result = Response.status(Status.BAD_REQUEST).build();
			}
			ev = builder.build();
			ev = txn.put(ev);
		    if(eventId<=Constants.ZERO) {
		    	eventId=ev.getKey().getId();
			    CountEventsUtils.makeUserEventCounterKind(userid, datastore,true,txn);
			    if(EventParticipationMethods.addOrRemoveParticipation(userid,eventId,txn)==false) {
			    	throw new Exception("Participation does not exist, it should be false!");
			    }
		    }else {
		    	eventId=ev.getKey().getId();
		    }
		    txn.commit();
		    /*
		    com.google.cloud.datastore.Key k = datastore.newKeyFactory().addAncestors(PathElement.of(USERS,userid)).setKind(EVENTS).newKey(eventId);

			System.out.println(k.getKind()+" -------- KIND --------- "+eventId);
			System.out.println(k.getId()+"---------------- ID ------- ");
			Entity event = datastore.get(k);
		    System.out.println("HAAAAAAAAAAAAAAAAAAAAAAA "+event);*/
		    
		    EventData2 obj = getEvent(ev,userid,false);
		    result=Response.status(Status.OK).entity(Constants.g.toJson(obj)).build();
		  }catch(Exception e) {
			  StorageMethods.rollBack(txn);
			  result= Response.status(Status.BAD_REQUEST).build();
			  LOG.severe("ERRRORR");
			  LOG.severe(e.getLocalizedMessage());
			  e.printStackTrace();
		  }
		  return result;
	}
	/**
	 * creates a google timestamp object from the string of a date representation
	 * @param date the date to be converted to a timestamp
	 * @return a timestamp of the input date
	 * @throws ParseException an exception case the operation fails
	 */
	private static Timestamp makeTimeStamp(String date, String time) throws ParseException {
	    Date dat=new SimpleDateFormat(Constants.DATE_FORMAT).parse(date+" "+time);
		Timestamp start = Timestamp.of(dat);
		return start;
	}
	/**
	 * from a timestamp, gets the string date representation
	 * @param t - the required timestamp
	 * @return string date from the timestamp
	 */
	private static String revertTimeStamp(Timestamp t){
		// new SimpleDateFormat(Constants.DATE_FORMAT).format(t.toDate());
		String rr = t.toDate().toString();
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
	public static Pair<String,String> getUpcomingEvent(String startCursor, long userid) {
		try {
			System.out.println("kkkkkkkkkkkkkkkkkkkkkkk ");

			Cursor startcursor=null;
			Query<ProjectionEntity> query=null;
			Filter filter= PropertyFilter.gt(END_DATE,Timestamp.now());
					
			com.google.cloud.datastore.ProjectionEntityQuery.Builder dd = Query.newProjectionEntityQueryBuilder()
				    .setKind(EVENTS).setFilter(filter)
				    .setProjection(NAME,LOCATION)
				    .setLimit(PAGESIGE);
			if (startCursor!=null) {
			      startcursor = Cursor.fromUrlSafe(startCursor); 
				  dd=dd.setStartCursor(startcursor);
			    }
			query=dd.build();
			
			QueryResults<ProjectionEntity> tasks = Constants.datastore.run(query);
			ProjectionEntity e;
			List<EventLocation> events = new LinkedList<>();
			System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP ");
			while(tasks.hasNext()){
				System.out.println("oooooooooooooooooooooo");

				e = tasks.next();
				events.add(new EventLocation(e.getString(LOCATION),e.getKey().getId(),e.getString(NAME)));
			}
			//data,cursor
			return new Pair<String, String>(Constants.g.toJson(events),tasks.getCursorAfter().toUrlSafe());
		}catch(Exception e) {
			Constants.LOG.severe("");
			Constants.LOG.severe("GETTING EVENTS "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return null;
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
	public static Pair<String,String> getEvents(String startCursor, long userid,boolean finished) {
		try {
			Cursor startcursor=null;
			Query<Entity> query=null;
			//Timestamp.no
			Filter filter= null;
			if(finished) {
				filter=PropertyFilter.le(END_DATE,Timestamp.now());
				System.out.println("FINISHED EVENTS!!!!!!");
			}else {
				filter=PropertyFilter.gt(END_DATE,Timestamp.now());
			}
			Builder b=Query.newEntityQueryBuilder()
				    .setKind(EVENTS).setFilter(filter)
				    .setLimit(PAGESIGE);
			if (startCursor!=null) {
		      startcursor = Cursor.fromUrlSafe(startCursor); 
			  b=b.setStartCursor(startcursor);
		    }
			query=b.build();
			QueryResults<Entity> tasks = Constants.datastore.run(query);
		    Entity e;
			List<EventData2> events = new LinkedList<>();
			while(tasks.hasNext()){
				e = tasks.next();
				events.add(getEvent(e,userid,finished));
			}
			return new Pair<String, String>(Constants.g.toJson(events),tasks.getCursorAfter().toUrlSafe());

		}catch(Exception e) {
			Constants.LOG.severe("");
			Constants.LOG.severe("GETTING EVENTS "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * deletes an event and every ither information related to the event
	 * @param eventId the event to be deleted
	 * @param email email of the user performing the operation
	 */
	public static Response deleteEvent (String eventId, long userid){
		Datastore datastore = Constants.datastore;
		long event = Long.parseLong(eventId);
		 com.google.cloud.datastore.Key eventKey=datastore.newKeyFactory().setKind(EVENTS).newKey(event);
		LOG.severe("GOING TO DELETE EVENT");
		Transaction txn=null;
		Response resp=null;
		try {
			txn = datastore.newTransaction();
			Entity ev = txn.get(eventKey);
			if(ev!=null) {
				long ownerid = ev.getLong(EVENT_OWNER);
				if(userid==ownerid) {
					txn.delete(eventKey);
				    CountEventsUtils.makeUserEventCounterKind(userid, datastore,false,txn);
				    EventParticipationMethods.removeParticipants(event,txn);
				    txn.commit();
				    GoogleCloudUtils.deleteObject(EventsResources.bucketName,eventId);
				    return Response.ok().build();
				}else {
					LOG.severe("NO AUTHORIZATIONN !");
					resp=Response.status(Status.UNAUTHORIZED).build();
				}
			}
			
		}catch(Exception e) {
			LOG.severe(e.getLocalizedMessage());
			StorageMethods.rollBack(txn);
			resp=Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return resp;
	}
	private static EventData2 getEvent(com.google.cloud.datastore.Key key, long userid) {
		try {
			Datastore datastore=Constants.datastore;
			com.google.cloud.datastore.Key k = datastore.newKeyFactory().setKind(EVENTS).newKey(key.getId());
			Entity event = datastore.get(k);
			return getEvent(event,userid,false);
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
			//e.printStackTrace();
		}
		return null;
	}
	/***
	 * Converts an entity event to an object event that'll be sent to the frontend
	 * @param en the entity
	 * @return an event of type EventData
	 */
	private static EventData2 getEvent(Entity en,long userid,boolean finished) {
		EventData2 ed = new  EventData2();
		long ownerid = en.getLong(EVENT_OWNER);
		Entity parentEntity = StorageMethods.getUser(Constants.datastore,ownerid);
		ed.setEventId(en.getKey().getId());
		ed.setDescription(en.getString(DESCRIPTION));
		ed.setEndDate(revertTimeStamp(en.getTimestamp(END_DATE)));
		ed.setGoals(en.getString(GOAL));
		ed.setLocation(en.getString(LOCATION));
		ed.setName(en.getString(NAME));
		ed.setStartDate(revertTimeStamp(en.getTimestamp(START_DATE)));
		ed.setVolunteers(en.getLong(VOLUNTEERS));
		ed.setCountComments(CommentDatastoreManagement.getNumberOfCommentsOfEvent(ed.getEventId()));
		try {
			String imgurl=parentEntity.getString(StorageMethods.PROFILE_PICTURE_URL_PROP); //profile picture
			ed.setImgUrl(imgurl);
		}catch(Exception e) {
			
		}
		try {//In case the user owner was removed
			ed.setOrganizer(parentEntity.getString(StorageMethods.NAME_PROPERTY));
			ed.setOwner(userid==ownerid);
		}catch(Exception e) {
			ed.setOrganizer("PUBLIC");
			ed.setOwner(false);
		}
		if(ed.isOwner()) {
			ed.setParticipating(true);
		}else if(!finished) {
			ed.setParticipating(EventParticipationMethods.hasParticipant(userid,ed.getEventId()));
		}
		ed.setCurrentParticipants(EventParticipationMethods.countParticipants(userid, ed.getEventId(),(int)ed.getVolunteers()));
		LOG.severe("GOING TO FETCH THE IMAGES ");
		try {
			//String.format("https://storage.googleapis.com/%s/%s",EventsResources.bucketName,en.getString(Constants.EVENT_PICS_FORMDATA_KEY));
			String imgurl=GoogleCloudUtils.publicURL(EventsResources.bucketName,en.getString(Constants.EVENT_PICS_FORMDATA_KEY));
			ed.setImages(imgurl);
		}catch(Exception e){LOG.severe("ERRROR: "+e.getLocalizedMessage());}
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
	public static String [] getLoggedUserEvents(String startCursor, long userid) {
		String [] results = new String[TWO];
		try {
			Cursor startcursor=null;
			Query<Entity> query=null;
			//Timestamp.no			
			Builder b=Query.newEntityQueryBuilder()
				    .setKind(EVENTS)
				    .setFilter(PropertyFilter.eq(EVENT_OWNER,userid)).setLimit(PAGESIGE).setOrderBy(OrderBy.asc(START_DATE));
			if (startCursor!=null) {
		      startcursor = Cursor.fromUrlSafe(startCursor); 
			  b=b.setStartCursor(startcursor);
		    }
			query=b.build();
			QueryResults<Entity> tasks = Constants.datastore.run(query);
		    Entity e;
			List<EventData2> events = new LinkedList<>();
			//Timestamp currentTime = Timestamp.now();
			
			while(tasks.hasNext()){
				e = tasks.next();
				EventData2 ev = getEvent(e,userid,false);
				events.add(ev);
			}
			results[0]=Constants.g.toJson(events);
			results[1]=tasks.getCursorAfter().toUrlSafe();
		}catch(Exception e) {
			Constants.LOG.severe("");
			Constants.LOG.severe("GETTING THE EVENTS OF THE LOGGED USER "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return results;
	}
	/**
	 * 
	 * @param startCursor cursor to continue reading the data from where it was left
	 * @param userid id of the user calling the operation
	 * @return 
	 */
	public static Pair<String,String> getLoggedUserInterestedEvents(String startCursor, long userid) {
		Pair<String,String> results = null;
		try {
			Cursor startcursor=null;
			Query<Entity> query=null;
			Builder b=Query.newEntityQueryBuilder()
				    .setKind(EventParticipationMethods.PARTICIPANTS_KIND)
				    .setFilter(PropertyFilter.eq(EventParticipationMethods.PARTICIPANT_ID_PROP,userid)).setLimit(PAGESIGE);
			if (startCursor!=null) {
		      startcursor = Cursor.fromUrlSafe(startCursor); 
			  b=b.setStartCursor(startcursor);
		    }
			query=b.build();
			QueryResults<Entity> tasks = Constants.datastore.run(query);
		    Entity e;
			List<EventData2> events = new LinkedList<>();
			//Timestamp currentTime = Timestamp.now();
			
			while(tasks.hasNext()){
				e = tasks.next();
				EventData2 ev =  getEvent(e.getKey().getParent(),userid);
				events.add(ev);
			}
			results = new Pair<String, String>(Constants.g.toJson(events),tasks.getCursorAfter().toUrlSafe());
		}catch(Exception e) {
			Constants.LOG.severe("");
			Constants.LOG.severe("GETTING THE EVENTS OF THE LOGGED USER "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return results;
	}
}
