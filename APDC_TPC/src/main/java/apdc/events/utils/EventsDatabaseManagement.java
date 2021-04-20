package apdc.events.utils;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.Datastore;
import com.google.gson.Gson;

public class EventsDatabaseManagement {

	private static final String EVENTS="EVENTS";
	private static final String USERS = "USERS";
	private static final String NAME="NAME";
	private static final String DESCRIPTION="DESCRIPTION";
	private static final String GOAL="GOAL";
	private static final String LOCATION="LOCATION";
	private static final String MEETING_PLACE="MEETING_PLACE";
	private static final String START_DATE="START_DATE";
	private static final String END_DATE="END_DATE";
	private static final String DURATION="DURATION";
	
	public EventsDatabaseManagement() {
	}
	public void createEvent(Datastore datastore, EventData event,String email) {
		int result=-1;		
		//Generate automatically a key
		com.google.cloud.datastore.Key eventKey=datastore.allocateId(datastore.newKeyFactory()
				.addAncestors(PathElement.of(USERS,email))
				.setKind(EVENTS).newKey());
		Transaction txn = datastore.newTransaction();
		  try {
			  /**
			   * TODO
			   * Entity event;
			event=Entity.newBuilder(eventKey)
					
			txn.put(event);
		    txn.commit();
		    result=1;
			   * 
			   */
			
		  }catch(Exception e) {
		  }
		  return;
	}
}
