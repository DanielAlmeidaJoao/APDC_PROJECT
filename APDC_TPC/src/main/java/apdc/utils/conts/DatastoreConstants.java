package apdc.utils.conts;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.EntityQuery.Builder;

import apdc.events.utils.jsonclasses.PasswordSizeRestrictions;
import apdc.events.utils.superuser.FrontEndUserObject;

public class DatastoreConstants {
	private static final String SESSION_TIME = "SESSION_TIME";
	private static final String MAX_N_IMAGES_UPLOADS = "MAX_N_IMAGES_UPLOADS";
	private static final String USERS_EVENTS_PAGESIZE = "USERS_EVENTS_PAGESIZE";
	private static final String INTERESTED_EVENTS_PAGESIZE = "INTERESTED_EVENTS_PAGESIZE";
	private static final String FINISHED_EVENTS_PAGESIZE = "FINISHED_EVENTS_PAGESIZE";
	private static final String UPCOMING_EVENTS_PAGESIZE = "UPCOMING_EVENTS_PAGESIZE";
	private static final String REPORTED_EVENTS_PAGESIZE = "REPORTED_EVENTS_PAGESIZE";
	private static final String FETCH_USERS_EVENTS_PAGESIZE = "FETCH_USERS_PAGESIZE";
	
	private static final String VERIFICATION_CODE_COOKIE_TIME = "VERIFICATION_COOKIE_TIME";


	private static final String MAX_PASSWORD_LENGTH = "MAX_PASSWORD_LENGTH";
	private static final String MIN_PASSWORD_LENGTH = "MIN_PASSWORD_LENGTH";
	private static final String CONSTANTS_KIND="CONSTANTS";

	public DatastoreConstants() {}

	/**
	private static void initConstants() {
		Transaction	txn=null;
		try {
			Datastore datastore = Constants.datastore;
			 com.google.cloud.datastore.Key eventKey;
			  KeyFactory kf = datastore.newKeyFactory()
						.setKind(CONSTANTS_KIND);
			  eventKey=datastore.allocateId(kf.newKey());  
			txn = datastore.newTransaction();

			  Entity.Builder  builder =Entity.newBuilder(eventKey)
						.set(MIN_PASSWORD_LENGTH,6)
						.set(MAX_PASSWORD_LENGTH,15)
						.set(UPCOMING_EVENTS_PAGESIZE,5)
						.set(FINISHED_EVENTS_PAGESIZE,5)
						.set(INTERESTED_EVENTS_PAGESIZE,5)
						.set(USERS_EVENTS_PAGESIZE,5)
						.set(MAX_N_IMAGES_UPLOADS,5)
						.set(REPORTED_EVENTS_PAGESIZE,5)
						.set(FETCH_USERS_EVENTS_PAGESIZE,5)
						.set(SESSION_TIME,-1) //ends when the browser closes
						.set(VERIFICATION_CODE_COOKIE_TIME,4*60);
			  	
			  Entity en = builder.build();
			  txn.put(en);
			  txn.commit();
		}catch(Exception e) {
			if(txn!=null) {
				txn.rollback();
			}
		}
	}*/
	public static int getVerificationCodeCookieTime() {
		return getProperty(VERIFICATION_CODE_COOKIE_TIME);
	}

	public static int getSessionTime() {
		return getProperty(SESSION_TIME);
	}

	public static int getMaxNImagesUploads() {
		return getProperty(MAX_N_IMAGES_UPLOADS);
	}

	public static int getUsersEventsPagesize() {
		return getProperty(USERS_EVENTS_PAGESIZE);
	}

	public static int getInterestedEventsPagesize() {
		return getProperty(INTERESTED_EVENTS_PAGESIZE);
	}

	public static int getFinishedEventsPagesize() {
		return getProperty(FINISHED_EVENTS_PAGESIZE);
	}

	public static int getUpcomingEventsPagesize() {
		return getProperty(UPCOMING_EVENTS_PAGESIZE);
	}

	public static int getReportedEventsPagesize() {
		return getProperty(REPORTED_EVENTS_PAGESIZE);
	}

	public static int getFetchUsersEventsPagesize() {
		return getProperty(FETCH_USERS_EVENTS_PAGESIZE);
	}

	public static int getMaxPasswordLength() {
		return getProperty(MAX_PASSWORD_LENGTH);
	}

	public static int getMinPasswordLength() {
		return getProperty(MIN_PASSWORD_LENGTH);
	}

	private static Entity getEntity() {
		try {
			Query<Entity> query=null;			
			Builder b=Query.newEntityQueryBuilder()
				    .setKind(CONSTANTS_KIND)
				    .setLimit(1);
			
			query=b.build();
			QueryResults<Entity> tasks = Constants.datastore.run(query);
			return tasks.next();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static int getProperty(String propertyName) {
		int propertyValue=0;
		try {
		    Entity e=getEntity();
			propertyValue = (int) e.getLong(propertyName);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return propertyValue;
	}
	public static PasswordSizeRestrictions getRestrictions() {
		PasswordSizeRestrictions p = null;
		try {
			Entity e = getEntity();
			p = new PasswordSizeRestrictions();
			p.setMaxPasswordSize((int)e.getLong(MAX_PASSWORD_LENGTH));
			p.setMinPasswordSize((int)e.getLong(MIN_PASSWORD_LENGTH));
			return p;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return p;
	}
}
