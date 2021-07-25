package apdc.events.utils;

import com.google.cloud.datastore.Entity;



import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.openlocationcode.OpenLocationCode;
import com.google.cloud.datastore.Transaction;

import apdc.events.utils.jsonclasses.Coords;
import apdc.events.utils.jsonclasses.EventData;
import apdc.events.utils.jsonclasses.EventData2;
import apdc.events.utils.jsonclasses.EventData3;
import apdc.events.utils.jsonclasses.EventLocationArgs;
import apdc.events.utils.jsonclasses.EventLocationResponse;
import apdc.events.utils.jsonclasses.ReportEventArgs;
import apdc.events.utils.jsonclasses.ReportProperty;
import apdc.events.utils.jsonclasses.UpcomingEventsArgs;
import apdc.tpc.resources.EventsResources;
import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;
import apdc.utils.conts.DatastoreConstants;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;

public class EventsDatabaseManagement {

	public static final String AREA_PLUS_CODE = "AREA_PLUS_CODE";
	private static final String LATLNG_EVENT_PROP = "LATLNG";
	private static final String FORMATTED_ADDRESS_EVENT_PROP = "FORMATTED_ADDRESS";
	public static final String EVENTS="EVENTS";
	//private static final String USERS = "Users";
	private static final String NAME="NAME";
	private static final String DESCRIPTION="DESCRIPTION";
	private static final String DIFFICULTY="DIFFICULTY";
	private static final String REPORT_TEXTS_PROPERTY="REPORT_TEXTS";
	private static final String REPORTED_PROP="REPORTED";


	//private static final String MEETING_PLACE="MEETING_PLACE";
	private static final String START_DATE="START_DATE";
	private static final String END_DATE="END_DATE";
	private static final String VOLUNTEERS="N_VOLUNTEERS";
	private static final String EVENT_OWNER="OWNER";
	
	public static final String EDITING_FORMDATA_KEY="editing";



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
	@SuppressWarnings("unchecked")
	private static ArrayList<String> imagesToRemoveWhenEditing(HttpServletRequest httpRequest, long eventid){
		ArrayList<String> urls;
		ArrayList<String> savedUrls=null;
		try {
			Part p = httpRequest.getPart(EDITING_FORMDATA_KEY);
			/*
			p.delete();
			byte [] b = new byte[(int) p.getSize()];
			InputStream is = p.getInputStream();
			is.read(b);*/
			String h = new String(IOUtils.toByteArray(p.getInputStream()));
			urls = Constants.g.fromJson(h,ArrayList.class);
			String imageName =null;
			String splitted [] = null;
			
			try {
				com.google.cloud.datastore.Key eventKey = Constants.datastore.newKeyFactory().setKind(EVENTS).newKey(eventid);
				Entity ev = Constants.datastore.get(eventKey);
				savedUrls = Constants.g.fromJson(ev.getString(Constants.EVENT_IMGS_PROP),ArrayList.class);
			}catch(Exception e) {
				savedUrls = new ArrayList<>();
			}
			Iterator<String> it = urls.iterator();
			while(it.hasNext()) {
				String toRemove = it.next();
				splitted = toRemove.split("/");
				if(savedUrls!=null) {
					System.out.println(savedUrls.remove(toRemove)+" <-------- REMOVED ---");
				}
				imageName = splitted[splitted.length-1];
				GoogleCloudUtils.deleteObject(EventsResources.bucketName,eventid+"/"+imageName);
				System.out.println("REMOVING IMAGE ---- > "+imageName);
			}
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		return savedUrls;
	}
	
	public static EventData2 getEvent(long eventid,Datastore datastore,long userid) {
		com.google.cloud.datastore.Key eventKey = datastore.newKeyFactory().setKind(EVENTS).newKey(eventid);
		Entity ev = datastore.get(eventKey);
		EventData2 event = getEvent(ev,userid);
		return event;
	}
	/**
	 * sets an event as reported when reported is true or as unreported when reported is false.
	 * @param reportText the report justification when an event is being reported
	 * @param reported a boolean which tells whether to report or to unreport
	 * @param userid the id of the user performing the operation
	 * @param eventId the id of the event
	 * @return
	 */
	private static Response addReportAux(String reportText, boolean reported, long userid, long eventId) {
		Response result=null;
		Datastore datastore = Constants.datastore;
		try {
			com.google.cloud.datastore.Key eventKey = datastore.newKeyFactory().setKind(EVENTS).newKey(eventId);
			Transaction txn = datastore.newTransaction();
			Entity event = txn.get(eventKey);
			if(event!=null) {
				if(reported&&event.getLong(EVENT_OWNER)==userid) {
					return Response.status(Status.UNAUTHORIZED).build();
				}
				
				/*
				Entity user = StorageMethods.getUser(datastore, userid);
				if(!user.getString(StorageMethods.ROLE_PROP).equals(StorageMethods.SU)) {
					return Response.status(Status.CONFLICT).build();
				}*/
				ReportProperty rep = Constants.g.fromJson(event.getString(REPORT_TEXTS_PROPERTY),ReportProperty.class);
				if(reported) {
					rep.addReport(reportText);
				}
				Entity.Builder  builder =Entity.newBuilder(eventKey)
						.set(NAME,event.getString(NAME))
						.set(DESCRIPTION,event.getString(DESCRIPTION))
						.set(DIFFICULTY,event.getString(DIFFICULTY))
						.set(FORMATTED_ADDRESS_EVENT_PROP,event.getString(FORMATTED_ADDRESS_EVENT_PROP))
						.set(START_DATE,event.getLong(START_DATE))
						.set(END_DATE,event.getLong(END_DATE))
						.set(VOLUNTEERS,event.getLong(VOLUNTEERS))
						.set(EVENT_OWNER,event.getLong(EVENT_OWNER))
						.set(Constants.EVENT_IMGS_PROP,event.getString(Constants.EVENT_IMGS_PROP))
						.set(REPORTED_PROP,reported)
						.set(REPORT_TEXTS_PROPERTY,noIndexProperties(Constants.g.toJson(rep)))
						.set(AREA_PLUS_CODE,event.getString(AREA_PLUS_CODE));
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
	public static Response addReport(ReportEventArgs report, long userid) {
		return addReportAux(report.getReportText(),true,userid, report.getEventId());
	}
	public static Response unReport(long eventId, long userid) {
		if(StorageMethods.isSuperUser(userid)) {
			return addReportAux("",false,userid, eventId);
		}else {
			return Response.status(Status.FORBIDDEN).build();
		}
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
		System.out.println("GOING TO ADDD AN EVENT!");
		String eventJsonData = getPartString(httpRequest);
		EventData et =null;
		boolean editing=false;
		long eventId=-1;
		try {
			et = Constants.g.fromJson(eventJsonData,EventData.class);
			eventId	= et.getEventId();
			if(eventId>0) {
				editing=true;
			}
		}catch(Exception e) {
			e.printStackTrace();
			LOG.severe(" SOMETHING WENT WRONG "+e.getLocalizedMessage());
		}

		EventLocationArgs location = Constants.g.fromJson(et.getLocation(),EventLocationArgs.class);
		Response result;
		Transaction txn=null;
		  try {
			txn = datastore.newTransaction();
			Entity ev;
			  com.google.cloud.datastore.Key eventKey;
			  KeyFactory kf = datastore.newKeyFactory()
						.setKind(EVENTS);
			  if(editing) {
				  eventKey = kf.newKey(eventId);
				  ev = txn.get(eventKey);
				  if(ev==null) {
					return Response.status(Status.NOT_FOUND).build();
				  }
			  }else {
				  eventKey=datastore.allocateId(kf.newKey());
			  }
			int max_nr_images = DatastoreConstants.getMaxNImagesUploads();
			eventId = eventKey.getId();
			LatLng coords = LatLng.of(location.getLoc().getLat(),location.getLoc().getLng());
			String code = getAreaPlusCode(location.getLoc().getLat(),location.getLoc().getLng());
			
			long now = currentDate();
			long startDate = makeTimeStamp(et.getStartDate(),et.getStartTime());
			long endDate = makeTimeStamp(et.getEndDate(),et.getEndTime());
			if(startDate<now||startDate>endDate) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			if(et.getVolunteers()>Long.MAX_VALUE) {
				et.setVolunteers(Long.MAX_VALUE);
			}
			Entity.Builder  builder =Entity.newBuilder(eventKey)
					.set(NAME,et.getName())
					.set(DESCRIPTION,et.getDescription())
					.set(DIFFICULTY,et.getGoals())
					//.set(LOCATION,et.getLocation())
					.set(LATLNG_EVENT_PROP,coords)
					.set(AREA_PLUS_CODE,code)
					.set(FORMATTED_ADDRESS_EVENT_PROP,location.getName())
					.set(START_DATE,startDate)
					.set(END_DATE,endDate)
					.set(VOLUNTEERS,et.getVolunteers())
					.set(EVENT_OWNER,userid);
			
			
						
			//handles the reports
			if(!editing) {
				builder.set(REPORTED_PROP,false);
				builder.set(REPORT_TEXTS_PROPERTY,noIndexProperties(Constants.g.toJson(new ReportProperty())));
			}else {
				Entity event = datastore.get(eventKey);
				builder.set(REPORTED_PROP,event.getBoolean(REPORTED_PROP));
				builder.set(REPORT_TEXTS_PROPERTY,noIndexProperties(event.getString(REPORT_TEXTS_PROPERTY)));
			}
					
			try {
				Iterator<Part> images = httpRequest.getParts().iterator();
				Part part;
				ArrayList<String> imgs=null;
			    if(editing) {
			    	imgs = imagesToRemoveWhenEditing(httpRequest,eventId);
			    }else {
			    	imgs=new ArrayList<>();
			    }
				
				long evk = eventKey.getId();
				while(images.hasNext()&&max_nr_images>0) {
					part = images.next();
					if(!part.getName().equals(Constants.EVENT_FORMDATA_KEY)&&!part.getName().equals(EDITING_FORMDATA_KEY)) {
						String eventUrl = GoogleCloudUtils.uploadObject(EventsResources.bucketName,evk+"/"+System.currentTimeMillis()+"",part.getInputStream());
						eventUrl=GoogleCloudUtils.publicURL(EventsResources.bucketName,eventUrl); //url
						imgs.add(eventUrl);
						max_nr_images--;
						//builder = builder.set(Constants.EVENT_PICS_FORMDATA_KEY,noIndexProperties(eventUrl));
					}
				}
				builder = builder.set(Constants.EVENT_IMGS_PROP,Constants.g.toJson(imgs));
				/*
				Part p = httpRequest.getPart("img_cover");
				if(p!=null) {
					String eventUrl = GoogleCloudUtils.uploadObject(EventsResources.bucketName,System.currentTimeMillis()+"",p.getInputStream());
					eventUrl=GoogleCloudUtils.publicURL(EventsResources.bucketName,eventUrl); //url
					builder = builder.set(Constants.EVENT_PICS_FORMDATA_KEY,noIndexProperties(eventUrl));
				}*/
			}catch(Exception e) {
				e.printStackTrace();
				LOG.severe(e.getLocalizedMessage());
				return result = Response.status(Status.BAD_REQUEST).build();
			}
			ev = builder.build();
			ev = txn.put(ev);
		    if(!editing) {
		    	//eventId=ev.getKey().getId();
			    CountEventsUtils.makeUserEventCounterKind(userid,datastore,true,txn);
		    }
		    txn.commit();
		    EventData2 obj = getEvent(ev,userid);
		    result=Response.status(Status.OK).entity(Constants.g.toJson(obj)).build();
		  }catch(Exception e) {
			  StorageMethods.rollBack(txn);
			  GoogleCloudUtils.deleteObject(EventsResources.bucketName,eventId+"");
			  result= Response.status(Status.BAD_REQUEST).build();
			  LOG.severe("ERRRORR");
			  LOG.severe(e.getLocalizedMessage());
			  e.printStackTrace();
		  }
		  return result;
	}
	private static String getAreaPlusCode(double lat, double lng) {
		OpenLocationCode olc = new OpenLocationCode(lat,lng, 10); // the last parameter specifies the number of digits
		return olc.getCode().substring(0,5);
	}
	/**
	 * creates a google timestamp object from the string of a date representation
	 * @param date the date to be converted to a timestamp
	 * @return a timestamp of the input date
	 * @throws ParseException an exception case the operation fails
	 */
	private static long makeTimeStamp(String date, String time) throws ParseException {
	    Date dat=new SimpleDateFormat(Constants.DATE_FORMAT).parse(date+" "+time);
	    long millis = dat.getTime();
		//Timestamp start = Timestamp.of(dat);
		return millis;
	}
	private static long currentDate() {
		Date date = new Date();
	    long timeMilli = date.getTime();
	    return timeMilli;
	}
	/**
	 * from a timestamp, gets the string date representation
	 * @param t - the required timestamp
	 * @return string date from the timestamp
	 */
	private static String revertTimeStamp(long t){
		Date date=new Date(t);
	    String rr=new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
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
	public static Pair<String,String> getUpcomingEvents(String startCursor, long userid,UpcomingEventsArgs args) {
		try {
			Cursor cursorObject=null;
			Query<ProjectionEntity> query=null;
			long now = currentDate();
			Filter filter=PropertyFilter.ge(END_DATE,now);
			Filter unreportedEventFilter= PropertyFilter.eq(REPORTED_PROP,false);
			String code = getAreaPlusCode(args.getLat(),args.getLng());
			Filter postalCodeFilter=PropertyFilter.eq(AREA_PLUS_CODE,code);
			
			com.google.cloud.datastore.ProjectionEntityQuery.Builder dd = Query.newProjectionEntityQueryBuilder()
				    .setKind(EVENTS).setFilter(com.google.cloud.datastore.StructuredQuery.CompositeFilter
				    		.and(filter,unreportedEventFilter,postalCodeFilter))
				    .setProjection(NAME,FORMATTED_ADDRESS_EVENT_PROP,LATLNG_EVENT_PROP)
				    .setLimit(DatastoreConstants.getUpcomingEventsPagesize());
			if(startCursor!=null && !startCursor.isEmpty()){
				try {
					CursorHelper ch =  Constants.g.fromJson(startCursor,CursorHelper.class);
					if(code.equals(ch.areaCode)) {
						//startCursor = ch.getCursor();
						cursorObject = Cursor.fromUrlSafe(ch.getCursor()); 
						dd=dd.setStartCursor(cursorObject);
					}
				}catch(Exception e) {}
			}
			query=dd.build();
			
			QueryResults<ProjectionEntity> tasks = Constants.datastore.run(query);
			ProjectionEntity e;
			List<EventLocationResponse> events = new LinkedList<>();
			while(tasks.hasNext()){
				e = tasks.next();
				LatLng coords = e.getLatLng(LATLNG_EVENT_PROP);
				events.add(new EventLocationResponse(e.getString(FORMATTED_ADDRESS_EVENT_PROP),e.getKey().getId(),e.getString(NAME),new Coords(coords.getLatitude(),coords.getLongitude())));
			}
			//data,cursor
			CursorHelper ch =  new CursorHelper();
			ch.setAreaCode(code);
			ch.setCursor(tasks.getCursorAfter().toUrlSafe());
			return new Pair<String, String>(Constants.g.toJson(events),Constants.g.toJson(ch));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Pair<String,String> getReportedEvents(String startCursor, long userid) {
			try {
				Cursor startcursor=null;
				Query<Entity> query=null;
				//Timestamp.no
				Filter filter=  PropertyFilter.eq(REPORTED_PROP,true);
				Builder b=Query.newEntityQueryBuilder()
					    .setKind(EVENTS).setFilter(filter)
					    .setLimit(DatastoreConstants.getReportedEventsPagesize());
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
					EventData2 event = getEvent(e,userid);
					event.setReports(e.getString(REPORT_TEXTS_PROPERTY));
					events.add(event);
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
				if(userid==ownerid||StorageMethods.isSuperUser(userid)) {
					txn.delete(eventKey);
				    CountEventsUtils.makeUserEventCounterKind(ownerid, datastore,false,txn);
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
			return getEvent(event,userid);
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
	private static EventData2 getEvent(Entity en,long userid) {
		EventData2 ed = new  EventData2();
		long ownerid = en.getLong(EVENT_OWNER);
		ed.setEventOwner(ownerid);
		Entity parentEntity = StorageMethods.getUser(Constants.datastore,ownerid);
		ed.setEventId(en.getKey().getId());
		ed.setDescription(en.getString(DESCRIPTION));
		ed.setEndDate(revertTimeStamp(en.getLong(END_DATE)));
		ed.setGoals(en.getString(DIFFICULTY));
		//ed.setLocation(en.getString(LOCATION));
		try {
			ed.setEventAddress(en.getString(FORMATTED_ADDRESS_EVENT_PROP));
			LatLng coords = en.getLatLng(LATLNG_EVENT_PROP);
			ed.setLoc(new Coords(coords.getLatitude(),coords.getLongitude()));
		}catch(Exception e) {
			ed.setEventAddress(e.getLocalizedMessage());
		}
		
		
		ed.setName(en.getString(NAME));
		long startDate = en.getLong(START_DATE);
		ed.setStartDate(revertTimeStamp(startDate));
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
		boolean finished;
		try {
			finished = startDate<currentDate();
		}catch(Exception e) {
			finished=true;
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
			ed.setImages(en.getString(Constants.EVENT_IMGS_PROP));
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
				    .setFilter(PropertyFilter.eq(EVENT_OWNER,userid)).
				    setLimit(DatastoreConstants.getFetchUsersPagesize()).setOrderBy(OrderBy.asc(START_DATE));
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
				EventData2 ev = getEvent(e,userid);
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
	public static Pair<String,String> getEventsFinishedEvents(String startCursor,long userid,int pageSize) {
		try {
			Datastore datastore = Constants.datastore;
			Cursor startcursor=null;
			Query<Entity> query=null;
			//Timestamp.no
			Filter filter=PropertyFilter.lt(END_DATE,currentDate());
			Builder b=Query.newEntityQueryBuilder()
				    .setKind(EVENTS).setFilter(filter)
				    .setLimit(pageSize);
			if (startCursor!=null) {
				b=Query.newEntityQueryBuilder()
					    .setKind(EVENTS).setFilter(filter)
					    .setLimit(pageSize);
				startcursor = Cursor.fromUrlSafe(startCursor); 
		      
				b=b.setStartCursor(startcursor);
		    }
			query=b.build();
			QueryResults<Entity> tasks = datastore.run(query);
		    Entity e;
			List<EventData2> events = new LinkedList<>();
			while(tasks.hasNext()){
				e = tasks.next();
				events.add(getEvent(e,userid));
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
				    .setFilter(PropertyFilter.eq(EventParticipationMethods.PARTICIPANT_ID_PROP,userid))
				    .setLimit(DatastoreConstants.getInterestedEventsPagesize());
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
	public static String [] getUserEvents(String startCursor, long userid) {
		Filter filter=PropertyFilter.eq(EVENT_OWNER,userid);
		return aux_Get_FilteredEventsUsingProjection(startCursor, userid,filter,DatastoreConstants.getUsersEventsPagesize());
	}
	/*
	private static String [] getFinishedEvents(String startCursor, long userid) {
		Filter filter=PropertyFilter.le(END_DATE,Timestamp.now());
		return aux_Get_FilteredEventsUsingProjection(startCursor, userid,filter,DatastoreConstants.getFinishedEventsPagesize());
	}*/
	private static String [] aux_Get_FilteredEventsUsingProjection(String startCursor, long userid,Filter filter, int pagesige) {
		String [] results;
		try {
			System.out.println("GOING TO GET THIS USER EVENTS "+userid);
			Cursor cursorObject=null;
			Query<ProjectionEntity> query=null;			
			com.google.cloud.datastore.ProjectionEntityQuery.Builder dd = Query.newProjectionEntityQueryBuilder()
				    .setKind(EVENTS).setFilter(com.google.cloud.datastore.StructuredQuery.CompositeFilter
				    		.and(filter))
				    .setProjection(NAME,FORMATTED_ADDRESS_EVENT_PROP,Constants.EVENT_IMGS_PROP)
				    .setLimit(pagesige);
			if(startCursor!=null && !startCursor.isEmpty()){
				cursorObject = Cursor.fromUrlSafe(startCursor); 
				dd=dd.setStartCursor(cursorObject);
			}
			query=dd.build();
			
			QueryResults<ProjectionEntity> tasks = Constants.datastore.run(query);
			ProjectionEntity e;
			List<EventData3> events = new LinkedList<>();
			while(tasks.hasNext()){
				e = tasks.next();
				EventData3 event = new EventData3();
				event.setEventName(e.getString(NAME));
				event.setImages(e.getString(Constants.EVENT_IMGS_PROP));
				event.setPlace(e.getString(FORMATTED_ADDRESS_EVENT_PROP));
				event.setEventId(e.getKey().getId());
				events.add(event);
			}
			results = new String[2];
			//data,cursor
			results[0]=Constants.g.toJson(events);
			results[1]=tasks.getCursorAfter().toUrlSafe();
			return results;
		}catch(Exception e) {
			Constants.LOG.severe("");
			Constants.LOG.severe("GETTING EVENTS "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return null;
	}
}
