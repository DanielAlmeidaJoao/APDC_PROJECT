package apdc.events.utils;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import apdc.tpc.resources.LoginManager;
import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

public class EventParticipationMethods {

	public static final String PARTICIPANT_ID_PROP="userid";
	//public static final String PARTICIPANT_EVENT_OWNER_ID_PROP="event_owner";

	public static final String PARTICIPANTS_KIND="EVENTS_PARTICIPANTS";
	public static final String COUNT_PARTICIPANTS_KIND="EVENTS_PARTICIPANTS_COUNTER";
	public static final String COUNT_PROP="NUMBER_OF_PARTICIPANTS";
	
	private static final String INTERESTED_EVENTS_COUNTER="INTERESTED_EVENTS_COUNTER_KIND";
	private static final String INTERESTED_EVENTS_COUNTER_PROP="NUMBER_OF_INTERESTED_EVENTS";
	private static final long ONE_LONG=1L;
	private static final long ZERO_LONG=0L;


	private static final Logger LOG = Logger.getLogger(EventParticipationMethods.class.getName());
	

	private static void print(String message) {
		Constants.LOG.severe(message);
	}
	public static void print(String className, String methodName, String message) {
		Constants.LOG.severe(String.format("ERROR FROM %s CLASS inside %s METHOD: %s",className,methodName,message));
	}
	public EventParticipationMethods() {
		// TODO Auto-generated constructor stub
	}
	public static boolean hasParticipant(long userid, long eventid) {
		return getParticipation(userid, eventid)!=null;
	}
	public static Entity getParticipation(long userid, long eventid) {
		try {
			print("GOING TO CHECK IF "+userid+" IS A PARTICIPANT IN "+eventid+" EVENT");
			EntityQuery  en =  Query.newEntityQueryBuilder()
				    .setKind(PARTICIPANTS_KIND)
				    .setFilter(CompositeFilter.and(
				    		PropertyFilter.hasAncestor(Constants.datastore.newKeyFactory().setKind(EventsDatabaseManagement.EVENTS).newKey(eventid)),
				    		PropertyFilter.eq(PARTICIPANT_ID_PROP,userid)))
				    .build();
			Query<Entity> query=en;
			QueryResults<Entity> results =  Constants.datastore.run(query);			
			print("GOING TO PRINT RESULT");
			return results.next();
		}catch(Exception e) {
			print("ERROR --> "+e.getLocalizedMessage());
			return null;
		}	
	}
	/**
	 * It counts the number of participants an event has so far and 
	 * also returns a boolean saying wheather the logged user is a participant or not
	 * @param loggeduserid
	 * @param eventid
	 * @param capacity max number of participants registered for this event
	 * @param res a pair object will have the number and a 
	 * string json object containing the ids of all the participants
	 * @return boolean
	 */
	public static int countParticipants(long loggeduserid, long eventid, int capacity) {
		int result=0;
		//LatLng lg = LatLng.of(lat,lng);
		try {
			print("GOING TO COUNT NUMBER OF PARTICIPANTS IN THIS EVENT: "+eventid+" And check is this user is in "+loggeduserid);
			com.google.cloud.datastore.Key countParticipants=Constants.datastore.newKeyFactory()
					.addAncestors(PathElement.of(EventsDatabaseManagement.EVENTS,eventid))
					.setKind(COUNT_PARTICIPANTS_KIND).newKey(eventid);
			Entity count = Constants.datastore.get(countParticipants);
			result =(int) count.getLong(COUNT_PROP);			
			print("GOING TO PRINT RESULT COUNT: "+result);
		}catch(Exception e) {
			print("ERROR --> "+e.getLocalizedMessage());
		}
		return result;
	}
	/**
	 * save the information that the user userid is interested in the event eventId
	 * @param userid
	 * @param eventId
	 * @param txn
	 * @return
	 */
	public static boolean addOrRemoveParticipation(long userid, long eventId, Transaction txn){
		Datastore datastore = Constants.datastore;
		Constants.LOG.severe("Going to Add a participant to an event!");
		boolean result=false;
		com.google.cloud.datastore.Key countParticipantsKey =datastore.newKeyFactory()
			.addAncestors(PathElement.of(EventsDatabaseManagement.EVENTS,eventId))
			.setKind(COUNT_PARTICIPANTS_KIND).newKey(eventId); //number of  participants per event
		Entity count = txn.get(countParticipantsKey);
		long cc;
		System.out.println("HERE 1");
		Entity ev=getParticipation(userid,eventId);
		if(ev==null) { // is not participating in the event
			if(count==null) {
				cc=ZERO_LONG;
				count=Entity.newBuilder(countParticipantsKey).set(COUNT_PROP,cc).build();
			}
			cc=1L+count.getLong(COUNT_PROP);
			KeyFactory kf = datastore.newKeyFactory() 
					.addAncestors(PathElement.of(EventsDatabaseManagement.EVENTS,eventId)) 
					.setKind(PARTICIPANTS_KIND); //event,participant entry
			
			com.google.cloud.datastore.Key eventKey=datastore.allocateId(kf.newKey()); 
			ev=Entity.newBuilder(eventKey)
					.set(PARTICIPANT_ID_PROP,userid)
					//.set(PARTICIPANT_EVENT_OWNER_ID_PROP,eventOwner)
					.build();  
			txn.put(ev);
			
			result=true;
		}else {
			cc=count.getLong(COUNT_PROP)-ONE_LONG;
			txn.delete(ev.getKey());
			result=false;
		}
		countInterestedEvents(userid,result,txn);//updates the number of interests the user has
		count=Entity.newBuilder(countParticipantsKey).set(COUNT_PROP,cc).build();
		txn.put(count);
		print("PARTICIPANT "+userid+" ADDED OR REMOVED ! "+result);
		return result;
	}
	
	/**
	 * updates the number of events user 'userid' is interested in
	 * @param userid the user calling the operation
	 * @param inc if true increases 1 else in decreases minus 1
	 * @param txn transaction object
	 */
	private static void countInterestedEvents(long userid, boolean inc,Transaction txn) {
		Datastore datastore = Constants.datastore;
		
		com.google.cloud.datastore.Key key =datastore.newKeyFactory()
				.setKind(INTERESTED_EVENTS_COUNTER).newKey(userid);
		long currentNumber=0L;
		Entity en = txn.get(key);
		if(en==null) {
			en=Entity.newBuilder(key).set(INTERESTED_EVENTS_COUNTER_PROP,currentNumber).build();
		}
		if(inc) {
			currentNumber=en.getLong(INTERESTED_EVENTS_COUNTER_PROP)+ONE_LONG;
		}else {
			currentNumber=en.getLong(INTERESTED_EVENTS_COUNTER_PROP)-ONE_LONG;
		}
		en=Entity.newBuilder(key).set(INTERESTED_EVENTS_COUNTER_PROP,currentNumber).build();
		txn.put(en);
	}
	/**
	 * removes the entry of the user with the userid from the kind INTERESTED_EVENTS_COUNTER_KIND
	 * @param userid
	 * @param txn
	 * @return true if success, else false
	 */
	public static boolean removeFromInterestedEvents(long userid,Transaction txn) {
		Datastore datastore = Constants.datastore;
		com.google.cloud.datastore.Key key =datastore.newKeyFactory()
				.setKind(INTERESTED_EVENTS_COUNTER).newKey(userid);
		try {
			txn.delete(key);
			return true;
		}catch(Exception e) {
			txn.rollback();
			print("EventParticipationMethods","removeInterestedEvents",e.getLocalizedMessage());
		}
		return false;
	}
	/**
	 * returns the nuber of interested events the user userid has
	 * @param userid
	 * @return
	 */
	public static long getNumberOfInterestedEvents(long userid) {
		Datastore datastore = Constants.datastore;
		com.google.cloud.datastore.Key key =datastore.newKeyFactory()
				.setKind(INTERESTED_EVENTS_COUNTER).newKey(userid);
		try {
			Entity en = datastore.get(key);
			if(en!=null) {
				return en.getLong(INTERESTED_EVENTS_COUNTER_PROP);
			}
		}catch(Exception e) {
			print("CLASS EventParticipationMethods, METHOD getNumberOfInterestedEvents: "+e.getLocalizedMessage());
		}
		return ZERO_LONG;
	}
	/**
	 * removes a user from participating in this event
	 * @param userid user to be removed
	 * @param eventId the event
	 * @return true if success else false
	 */
	public static boolean removeParticipation(long userid, long eventId){
		Datastore datastore = Constants.datastore;
		Transaction txn=null;
		Constants.LOG.severe("Going to Add a participant to an event!");
		boolean result=false;
		  try {
				txn = datastore.newTransaction();
				Entity participation = getParticipation(userid, eventId);
				com.google.cloud.datastore.Key countParticipants=Constants.datastore.newKeyFactory()
						.addAncestors(PathElement.of(EventsDatabaseManagement.EVENTS,eventId))
						.setKind(COUNT_PARTICIPANTS_KIND).newKey(eventId);
				Entity count = txn.get(countParticipants);
				txn.delete(participation.getKey(),count.getKey());
				txn.commit();
				print("PARTICIPATION REMOVED "+userid+" ADDED!");
				result=true;
		  }catch (Exception e) {
			  	StorageMethods.rollBack(txn);
			  	print(e.getLocalizedMessage()+" <---> ERROR TRYING TO REMOVE A PARTICIPATION!");
		  }
		  return result;
	}
	/**
	 * when an event is removed, it removes the users who participate in this event from the participation kind
	 * method called only when removing an event
	 * @param loggeduserid
	 * @param eventid
	 * @return
	 */
	public static boolean removeParticipants(long eventid,Transaction txn) {
		boolean isIn=false;
		Datastore datastore = Constants.datastore;
		print("REMOVE ALL PARTICIPATIONS IN THIS EVENT! "+eventid);
		EntityQuery  en =  Query.newEntityQueryBuilder()
			    .setKind(PARTICIPANTS_KIND)
			    .setFilter(CompositeFilter.and(
			    		PropertyFilter.hasAncestor(Constants.datastore.newKeyFactory().setKind(EventsDatabaseManagement.EVENTS).newKey(eventid))))
			    .build();
		Query<Entity> query=en;
		QueryResults<Entity> results =  datastore.run(query);
		
		Entity e;
		while(results.hasNext()) {
			e=results.next();
			txn.delete(e.getKey());
			countInterestedEvents(e.getLong(PARTICIPANT_ID_PROP),false,txn); //decreases the number of interest the user has 
			isIn=true;
		}
		return isIn;
	}
}
