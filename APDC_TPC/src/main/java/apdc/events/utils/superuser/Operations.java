package apdc.events.utils.superuser;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.EntityQuery.Builder;

import apdc.events.utils.Pair;
import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

public class Operations {
	private static final int LIMIT=6;
	public Operations() {
		// TODO Auto-generated constructor stub
	}

	public static Pair<String,String> getUsers(long userid, String cursor) {
		
		Cursor startcursor=null;
		Query<Entity> query=null;
		Pair<String, String> result;
		
		Builder b=Query.newEntityQueryBuilder()
			    .setKind(StorageMethods.USERS_KIND)
			    .setLimit(LIMIT);
		if(cursor!=null) {
	      startcursor = Cursor.fromUrlSafe(cursor); 
		  b=b.setStartCursor(startcursor);
	    }
		query=b.build();
		QueryResults<Entity> tasks = Constants.datastore.run(query);
	    Entity e;
		List<FrontEndUserObject> events = new LinkedList<>();
		while(tasks.hasNext()){
			e = tasks.next();
			events.add(makeUser(e));
		}
		result=new Pair<String, String>(Constants.g.toJson(events),tasks.getCursorAfter().toUrlSafe());
		return result;
	}
	
	private static FrontEndUserObject makeUser(Entity en) {
		//(String name, String email, long userid, String role, String state)		
		FrontEndUserObject user = new FrontEndUserObject(en.getString(StorageMethods.NAME_PROPERTY),
				en.getString(StorageMethods.EMAIL_PROP), en.getKey().getId(),
				en.getString(StorageMethods.ROLE_PROP), 
				en.getString(StorageMethods.STATE_PROP));
		
		return user;
	}
	public static boolean is_SU_user(Entity en) {
		return en.getString(StorageMethods.ROLE_PROP).equals(StorageMethods.SU);
	}
	public static Status removeUser(Datastore datastore,long userid) {
		Status result;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(StorageMethods.USERS_KIND).newKey(userid);
		  try {
			Constants.LOG.severe("GOING TO REMOVE "+userid);
			Entity person = datastore.get(ctrsKey);
			if(!is_SU_user(person)) {
			    if(StorageMethods.removeUser(ctrsKey)>Constants.ZERO) {
			    	result=Status.OK;
			    }else {
			    	result=Status.BAD_REQUEST;
			    }
			}else {
				result=Status.UNAUTHORIZED;
			}
		  }catch(Exception e) {
			  result=Status.BAD_REQUEST;
			  Constants.LOG.severe("FAILED TO REMOVE "+userid);
		  }
		  return result;
	}
	
	public static String newState(String oldState) {
		if(StorageMethods.ENABLED.equals(oldState)) {
			return StorageMethods.DISABLED;
		}else {
			return StorageMethods.ENABLED;
		}
	}
	public static Pair<String,Status> changeUserState(Datastore datastore,long userid) {
		Pair<String,Status> pair = new Pair<>();
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(StorageMethods.USERS_KIND).newKey(userid);
		  try {
		    String newState=null;
			Constants.LOG.severe("GOING TO CHANGE STATE "+userid);
			Entity person = datastore.get(ctrsKey);
			if(!is_SU_user(person)) {
				Transaction txn = datastore.newTransaction();
		    	person=txn.get(ctrsKey);
		    	newState=newState(person.getString(StorageMethods.STATE_PROP));
			    person = Entity.newBuilder(ctrsKey)
			    		.set(StorageMethods.EMAIL_PROP,person.getString(StorageMethods.EMAIL_PROP))
						.set(StorageMethods.PASSWORD,person.getString(StorageMethods.PASSWORD))
						.set(StorageMethods.NAME_PROPERTY,person.getString(StorageMethods.NAME_PROPERTY))
						.set(StorageMethods.ROLE_PROP,person.getString(StorageMethods.ROLE_PROP))
						.set(StorageMethods.STATE_PROP,newState)
						.build();
				txn.put(person);
			    txn.commit();
		    	pair.setV1(newState);
		    	pair.setV2(Status.OK);
			}else {
		    	pair.setV2(Status.UNAUTHORIZED);
			}
		  }catch(Exception e) {
			  pair.setV2(Status.BAD_REQUEST);
			  Constants.LOG.severe("FAILED TO CHANGE STATE "+userid);
		  }
		  return pair;
	}
	
	
}
