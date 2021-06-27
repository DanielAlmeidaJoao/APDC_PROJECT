package apdc.events.utils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

public class EventParticipationMethods {

	private static final String PARTICIPANT_ID_PROP="userid";
	private static final String PARTICIPANTS_KIND="event_participants";
	private static final String COUNT_PARTICIPANTS_KIND="count_participants";
	private static final String COUNT_PROP="count";



	private static void print(String message) {
		Constants.LOG.severe(message);
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
	public static boolean addOrRemoveParticipation(long userid, long eventId,Transaction txn){
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
				cc=0L;
				count=Entity.newBuilder(countParticipantsKey).set(COUNT_PROP,cc).build();
			}
			cc=1L+count.getLong(COUNT_PROP);
			KeyFactory kf = datastore.newKeyFactory() 
					.addAncestors(PathElement.of(EventsDatabaseManagement.EVENTS,eventId)) 
					.setKind(PARTICIPANTS_KIND); 
			com.google.cloud.datastore.Key eventKey=datastore.allocateId(kf.newKey()); 
			ev=Entity.newBuilder(eventKey).set(PARTICIPANT_ID_PROP,userid).build();  
			txn.put(ev);
			result=true;
		}else {
			cc=count.getLong(COUNT_PROP)-1L;
			txn.delete(ev.getKey());
			result=false;
		}
		count=Entity.newBuilder(countParticipantsKey).set(COUNT_PROP,cc).build();
		txn.put(count);
		print("PARTICIPANT "+userid+" ADDED OR REMOVED ! "+result);
		return result;
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
	 * method called only when removing an event
	 * @param loggeduserid
	 * @param eventid
	 * @return
	 */
	public static boolean removeParticipants(long eventid,Transaction txn) {
		boolean isIn=false;
		print("REMOVE ALL PARTICIPATIONS IN THIS EVENT! "+eventid);
		EntityQuery  en =  Query.newEntityQueryBuilder()
			    .setKind(PARTICIPANTS_KIND)
			    .setFilter(CompositeFilter.and(
			    		PropertyFilter.hasAncestor(Constants.datastore.newKeyFactory().setKind(EventsDatabaseManagement.EVENTS).newKey(eventid))))
			    .build();
		Query<Entity> query=en;
		QueryResults<Entity> results =  txn.run(query);
		
		Entity e;
		while(results.hasNext()) {
			e=results.next();
			txn.delete(e.getKey());
		}
		return isIn;
	}
}
