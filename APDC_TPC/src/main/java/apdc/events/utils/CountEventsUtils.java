package apdc.events.utils;

import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;import com.google.cloud.datastore.Transaction;

public class CountEventsUtils {
	private static final Logger LOG = Logger.getLogger(CountEventsUtils.class.getName());
	
	public static void makeUserEventCounterKind(long userid, Datastore datastore,boolean inc) {
		LOG.info("GOING TO UPDATE THE NUMBER OF EVENTS USER "+userid+" HAS.");
		String COUNT_EVENTS_PER_USER_KIND="EVENTS_USER";
		String COUNT="COUNT";

		Transaction txn =null;
		com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind(COUNT_EVENTS_PER_USER_KIND).newKey(userid);
		try {
			long newValue;
			txn = datastore.newTransaction();
		
		    Entity count = txn.get(userKey);
		    
		    if(count==null) {
		    	newValue=0L;
		    }else if(inc){
		    	newValue=count.getLong(COUNT)+1L;
		    }else {
		    	newValue=count.getLong(COUNT)-1L;
		    }
    		count = Entity.newBuilder(userKey)
    		.set(COUNT,newValue)
			.build();
			txn.put(count);
		    txn.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void removeUserEventCounterKind(long userid, Datastore datastore,boolean inc) {
		LOG.info("GOING TO UPDATE THE NUMBER OF EVENTS USER "+userid+" HAS.");
		String COUNT_EVENTS_PER_USER_KIND="EVENTS_USER";
		String COUNT="COUNT";

		Transaction txn =null;
		com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind(COUNT_EVENTS_PER_USER_KIND).newKey(userid);
		try {
			txn = datastore.newTransaction();
			txn.delete(userKey);;
		    txn.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static long getNumberOfEvents(long userid, Datastore datastore) {
		LOG.info("GOING TO UPDATE THE NUMBER OF EVENTS USER "+userid+" HAS.");
		String COUNT_EVENTS_PER_USER_KIND="EVENTS_USER";
		String COUNT="COUNT";

		Transaction txn =null;
		com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind(COUNT_EVENTS_PER_USER_KIND).newKey(userid);
		try {
			Entity count = datastore.get(userKey);
			if(count!=null) {
				return count.getLong(COUNT);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}
}
