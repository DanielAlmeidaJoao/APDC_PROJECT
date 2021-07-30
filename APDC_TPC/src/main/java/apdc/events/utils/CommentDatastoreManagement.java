package apdc.events.utils;

import java.util.LinkedList;

import java.util.List;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import apdc.events.utils.jsonclasses.CommentObject2;
import apdc.events.utils.jsonclasses.LoadCommentsResponse;
import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

public class CommentDatastoreManagement {
	
	public static final int EVENT_PAGE_SIZE = 6;

	public static final String COMMENTS_KIND="COMMENTS_OF_EVENTS";
	public static final String COMMENT_TEXT_PROP="COMMENT";
	public static final String COMMENT_OWNER_PROP="OWNER_ID";
	public static final String COMMENT_EVENT_PROP="EVENT_ID";
	public static final String COMMENT_DATE_PROP="COMMENT_DATE";

	
	public static final String COUNT_COMMENTS_PER_EVENT_KIND="NUMBER_OF_COMMENTS_OF_EVENTS";
	public static final String COUNT_COMMENTS_PER_EVENT_EVENTID_PROP="EVENT_ID";
	public static final String NUMBER_OF_COMMENTS_PROP="NUMBER_OF_COMMENTS";

	public CommentDatastoreManagement() {}
	
	/**
	 * adds a comment to an event
	 * @param eventid
	 * @param userid
	 * @param comment
	 */
	public static CommentObject2 addComment(long eventid, long userid, String comment) {
		Transaction txn=null;
		try {
			Datastore datastore = Constants.datastore;
			
			  KeyFactory kf = datastore.newKeyFactory()
						.setKind(COMMENTS_KIND);
			  com.google.cloud.datastore.Key key=datastore.allocateId(kf.newKey());
		  txn = datastore.newTransaction();
			Entity entity;
			entity =Entity.newBuilder(key)
					.set(COMMENT_TEXT_PROP,comment)
					.set(COMMENT_OWNER_PROP,userid)
					.set(COMMENT_EVENT_PROP,eventid)
					.set(COMMENT_DATE_PROP,Timestamp.now())
					.build();
			txn.put(entity);
			increaceOrDecreaseNumberOfCommentsForEvent(eventid,true,txn);
			txn.commit();
			CommentObject2 result =  new CommentObject2(eventid,comment,Timestamp.now().toDate().toString(),key.getId(),userid);
			//to fecth the name of the owner
			Entity user = StorageMethods.getUser(Constants.datastore,userid);
			result.setOwnerName(user.getString(StorageMethods.NAME_PROPERTY));
			//fetch the owner profile picture object name
			result.setUrlProfilePicture(user.getString(StorageMethods.PROFILE_PICTURE_URL_PROP));
			result.setOwner(true);
			return result;
		}catch(Exception e) {
			txn.rollback();
		}
		return null;
	}
	/**
	 * increases or decreases the number of comments of an event
	 * @param eventid
	 * @param inc if true it increases, else  decreases
	 * @param txn
	 */
	public static void increaceOrDecreaseNumberOfCommentsForEvent(long eventid,boolean inc,Transaction txn) {
		Datastore datastore = Constants.datastore;
		com.google.cloud.datastore.Key key =datastore.newKeyFactory().setKind(COUNT_COMMENTS_PER_EVENT_KIND).newKey(eventid);
		long newValue;
	
	    Entity count = txn.get(key);
	    if(count==null) {
	    	newValue=1L;
	    }else if(inc){
	    	newValue=count.getLong(NUMBER_OF_COMMENTS_PROP)+1L;
	    }else {
	    	newValue=count.getLong(NUMBER_OF_COMMENTS_PROP)-1L;
	    }
		count = Entity.newBuilder(key)
		.set(NUMBER_OF_COMMENTS_PROP,newValue)
		.build();
		txn.put(count);
	}
	/**
	 * returns the number of comments of the event in the argument
	 * @param eventid
	 * @return
	 */
	public static long getNumberOfCommentsOfEvent(long eventid) {
		Datastore datastore = Constants.datastore;
		com.google.cloud.datastore.Key key =datastore.newKeyFactory().setKind(COUNT_COMMENTS_PER_EVENT_KIND).newKey(eventid);	
	    Entity count = datastore.get(key);
	    if(count==null) {
	    	return 0L;
	    }
	    return count.getLong(NUMBER_OF_COMMENTS_PROP);
	}
	/**
	 * loads comments of the event eventid
	 * @param eventid
	 * @param startCursor
	 * @param userid user performing the operation
	 * @return
	 */
	public static String loadComments(long eventid, String startCursor, long userid){
		try {
			Cursor startcursor=null;
			Query<Entity> query=null;
			Builder b=Query.newEntityQueryBuilder()
				    .setKind(COMMENTS_KIND)
				    .setFilter(PropertyFilter.eq(COMMENT_EVENT_PROP,eventid)).setLimit(EVENT_PAGE_SIZE);
			if (!"".equals(startCursor)) {
				System.out.println(" I AM CURSOR "+startCursor);
		      startcursor = Cursor.fromUrlSafe(startCursor); 
			  b=b.setStartCursor(startcursor);
		    }
			query=b.build();
			QueryResults<Entity> tasks = Constants.datastore.run(query);
		    Entity e;
			List<CommentObject2> comments = new LinkedList<>();
			CommentObject2 comment;
			Timestamp date;
			long ownerId;
			while(tasks.hasNext()){
				e = tasks.next();
				ownerId = e.getLong(COMMENT_OWNER_PROP);
				date = e.getTimestamp(COMMENT_DATE_PROP);
				comment =  new CommentObject2(e.getLong(COMMENT_EVENT_PROP),e.getString(COMMENT_TEXT_PROP),date.toDate().toString(),e.getKey().getId(),ownerId);
				//to fecth the name of the owner
				Entity user = StorageMethods.getUser(Constants.datastore,ownerId);
				comment.setOwnerName(user.getString(StorageMethods.NAME_PROPERTY));
				//fetch the owner profile picture object name
				comment.setUrlProfilePicture(user.getString(StorageMethods.PROFILE_PICTURE_URL_PROP));
				comment.setOwner(user.getKey().getId()==userid);
				comments.add(comment);
			}
			LoadCommentsResponse result = new LoadCommentsResponse(comments,tasks.getCursorAfter().toUrlSafe());
			return Constants.g.toJson(result);
		}catch(Exception e) {
			Constants.LOG.severe("GETTING THE COMMENTS OF THE EVENT "+eventid+" and returned "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * remove an entity from the kind that counts comments of events
	 * @param eventid
	 * @param datastore
	 * @param txn
	 */
	public static void removeEntryFromCommentsCounter(long eventid, Datastore datastore, Transaction txn) {
		com.google.cloud.datastore.Key key =datastore.newKeyFactory().setKind(COUNT_COMMENTS_PER_EVENT_KIND).newKey(eventid);
		txn.delete(key);
	}
	/**
	 * removes all comments of this event
	 * @param eventid
	 */
	public static void removeAllComments(long eventid) {
		Datastore datastore = Constants.datastore;
		EntityQuery  en =  Query.newEntityQueryBuilder()
			    .setKind(COMMENTS_KIND)
			    .setFilter(PropertyFilter.gt(COMMENT_EVENT_PROP,eventid))
			    .build();
		Query<Entity> query=en;
		QueryResults<Entity> results =  datastore.run(query);
		Transaction txn=datastore.newTransaction();
		Entity e;
		while(results.hasNext()) {
			e=results.next();
			txn.delete(e.getKey());
		}
		removeEntryFromCommentsCounter(eventid,datastore,txn);
	}
	/**
	 * remove a single comment from an event
	 * @param commentid
	 * @param userid
	 */
	public static boolean removeComment(long commentid,long userid) {
		Transaction txn=null;
		try {
			Datastore datastore = Constants.datastore;
			com.google.cloud.datastore.Key key=datastore.newKeyFactory().setKind(COMMENTS_KIND).newKey(commentid);
			Entity entity = datastore.get(key);
			if(entity!=null&&entity.getLong(COMMENT_OWNER_PROP)==userid) {
				txn = datastore.newTransaction();
				txn.delete(key);
				increaceOrDecreaseNumberOfCommentsForEvent(entity.getLong(COMMENT_EVENT_PROP),false,txn);
				txn.commit();
				return true;
			}
		}catch(Exception e) {
			txn.rollback();
		}
		return false;
	}
}
