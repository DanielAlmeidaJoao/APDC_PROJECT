package apdc.tpc.utils;
import java.util.LinkedList;

import java.util.List;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import apdc.events.utils.EventParticipationMethods;
import apdc.events.utils.GoogleCloudUtils;
import apdc.events.utils.moreAttributes.AdditionalAttributesOperations;
import apdc.tpc.resources.LoginManager;
import apdc.utils.conts.Constants;

public class StorageMethods {

	public static final String PASSWORD = "password";

	public static final String STATE_PROP = "state";
	//ROLES
	public static final String ROLE_PROP = "role";
	
	public static final String NAME_PROPERTY = "name";

	public static final String EMAIL_PROP = "email";
	
	public static final String PROFILE_PICTURE_URL_PROP = "PROFILE_PICTURE_URL";



	public static final String GBO = "GBO";
	public static final String USER = "USER";
	public static final String GA = "GA";
	public static final String SU = "SU";
	
	public static final String ENABLED = "ENABLED";
	public static final String DISABLED = "DISABLED";

	private static final String PRIVATE_VALUE = "PRIVATE";

    public static final String DEFAULT_AVATAR_OBJECT_NAME="Profile_avatar_placeholder_large.png";


	//private static final String GBO = "GBO";
	//private static final String GBO = "GBO";

	

	public static final String USERS_KIND="Users";
	private static final Logger LOG = Logger.getLogger(StorageMethods.class.getName());

	
	public static Entity getUser(Datastore datastore,LoginData data) {
		Entity person=null;
		try {
			//com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(data.getEmail());
			Query<Entity> query = Query.newEntityQueryBuilder()
				    .setKind(USERS_KIND)
				    .setFilter(CompositeFilter.and(
				        PropertyFilter.eq(EMAIL_PROP, data.getEmail()),PropertyFilter.eq(PASSWORD, data.getPassword()))).build();
			//person = datastore.get(userKey);
			QueryResults<Entity> tasks = datastore.run(query);
			if(tasks.hasNext()) {
				person=tasks.next();
				if(ENABLED.equals(person.getString(STATE_PROP))){
					return person;
				}else {
					person=null;
				}
			}
		}catch(Exception e) {
			
		}
		return person;
	}
	public static Entity getUser(Datastore datastore,long userid) {
		Entity person=null;
		try {
			com.google.cloud.datastore.Key userkey=datastore.newKeyFactory().setKind(USERS_KIND).newKey(userid);
			person = datastore.get(userkey);
		}catch(Exception e) {
			
		}
		return person;
	}

	public static boolean isSuperUser(long userid) {
		try {
			Entity person = getUser(Constants.datastore,userid);
			if(person.getString(StorageMethods.ROLE_PROP).equals(StorageMethods.SU)) {
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static Entity getUser(Datastore datastore,String email) {
		Entity user=null;
		try {
			Query<Entity> query = Query.newEntityQueryBuilder()
				    .setKind(USERS_KIND)
				    .setFilter(PropertyFilter.eq(EMAIL_PROP,email)).build();
			//person = datastore.get(userKey);
			QueryResults<Entity> tasks = datastore.run(query);
			user = tasks.next();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return user;
	}

	public static void updateProfilePicture(long userid,String newProfileUrl) {
		Datastore datastore=Constants.datastore;
		com.google.cloud.datastore.Key userKey = datastore.newKeyFactory().setKind(USERS_KIND).newKey(userid);
		Entity e = datastore.get(userKey);
		RegisterData data = new RegisterData();
		data.setEmail(e.getString(EMAIL_PROP));
		data.setName(e.getString(NAME_PROPERTY));
		data.setPassword(e.getString(PASSWORD));
		data.setProfilePictureUrl(newProfileUrl);
		String [] res=e.getString(PROFILE_PICTURE_URL_PROP).split("/");
		String oldobjectName=res[res.length-1];
		updateUser(userKey,e,data);
		GoogleCloudUtils.deleteObject(LoginManager.profilePictureBucketName,oldobjectName);
	}
	public static void updatePassword(String email,String password) {
		Datastore datastore=Constants.datastore;
		Entity user = getUser(datastore, email);
		if(!user.getString(PASSWORD).equals(password)) {
			RegisterData data = new RegisterData();
			data.setEmail(user.getString(EMAIL_PROP));
			data.setName(user.getString(NAME_PROPERTY));
			data.setPassword(password);
			data.setProfilePictureUrl(user.getString(PROFILE_PICTURE_URL_PROP));
			updateUser(user.getKey(),user,data);
		}
	}
	/**
	 * updates name, email, password or profile picture url
	 * @param userid
	 * @param e
	 */
	private static void updateUser(com.google.cloud.datastore.Key userKey,Entity e, RegisterData data) {
		Datastore datastore=Constants.datastore;
		Transaction txn=null;
		try {
			txn = datastore.newTransaction();
			e = Entity.newBuilder(e.getKey())
		    		.set(EMAIL_PROP,data.getEmail())
					.set(PASSWORD,data.getPassword())
					.set(NAME_PROPERTY,data.getName())
					.set(ROLE_PROP,e.getString(ROLE_PROP))
					.set(STATE_PROP,e.getString(STATE_PROP))
					.set(PROFILE_PICTURE_URL_PROP,data.getProfilePictureUrl())
					.build();
			txn.put(e);
			txn.commit();
		}catch(Exception ex) {
			if(txn!=null) {
				txn.rollback();
			}
			ex.printStackTrace();
		}
	}
	public static long addUser(Datastore datastore, RegisterData data) {
		Transaction txn =null;
		long userid=0L;
		  try {
			KeyFactory kf = datastore.newKeyFactory().setKind(USERS_KIND);
			com.google.cloud.datastore.Key userKey = datastore.allocateId(kf.newKey());
			Entity person = datastore.get(userKey);
			if(person==null) {
				txn = datastore.newTransaction();
			    person = Entity.newBuilder(userKey)
			    		.set(EMAIL_PROP,data.getEmail())
						.set(PASSWORD,data.getPassword())
						.set(NAME_PROPERTY,data.getName())
						.set(ROLE_PROP,USER)
						.set(STATE_PROP,ENABLED)
						.set(PROFILE_PICTURE_URL_PROP,data.getProfilePictureUrl())
						.build();
				txn.put(person);
			    txn.commit();
			}
		  }catch(Exception e) {
				LOG.severe(e.getLocalizedMessage());
				rollBack(txn);
		  }
		  return userid;
	}
	
	public static void rollBack(Transaction txn) {
		try {
			if (txn!=null&&txn.isActive()) {
			      txn.rollback();
			   }		
		}catch (Exception e) {
			
		}
	}
	public static int removeUser(Datastore datastore,long userid, String password) {
		int result=-1;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(USERS_KIND).newKey(userid);
		  try {
			Entity person = datastore.get(ctrsKey);
			if(isEnabled(person)&&password.equals(person.getString(PASSWORD))) {
				/*
				com.google.cloud.datastore.Key additionalInfo=datastore.newKeyFactory().setKind(AdditionalAttributesOperations.ADITIONALS).newKey(userid);
				txn.delete(ctrsKey,additionalInfo);
			    txn.commit();*/
			    result=removeUser(ctrsKey);
			    GoogleCloudUtils.deleteObject(LoginManager.profilePictureBucketName,userid+"");
			}
		  }catch(Exception e) {
			LOG.severe(e.getLocalizedMessage());
		  }
		  return result;
	}
	public static int removeUser(com.google.cloud.datastore.Key ctrsKey) {
		Transaction txn = Constants.datastore.newTransaction();
		int result=-1;
		boolean ok=false;
		  try {
			  com.google.cloud.datastore.Key additionalInfo=Constants.datastore.newKeyFactory().setKind(AdditionalAttributesOperations.ADITIONALS).newKey(ctrsKey.getId());
				txn.delete(ctrsKey,additionalInfo);
				ok  = EventParticipationMethods.removeFromInterestedEvents(ctrsKey.getId(),txn);
				
				if(ok) {
				    txn.commit();
				    result=1;
				}else {
					txn.rollback();
				}
		  }catch(Exception e) {
			LOG.severe(e.getLocalizedMessage());
			if(txn!=null) {
				  rollBack(txn);
			  }
		  }
		  return result;
	}
	
	public static String updatePassword(Datastore datastore, String email, String newPassword, String oldPass) {
		Transaction txn =null;
		String result="-1";
		  try {
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind(StorageMethods.USERS_KIND).newKey(email);
			txn = datastore.newTransaction();
			Entity person = txn.get(userKey);
			if(person==null) {
				return result;
			}
			if(!oldPass.equals(person.getString(PASSWORD))) {
				return "-2";
			}
			if(isEnabled(person)&&oldPass.equals(person.getString(PASSWORD))) {
			    person = Entity.newBuilder(userKey)
						.set(PASSWORD,newPassword)
						.set(NAME_PROPERTY,person.getString(NAME_PROPERTY))
						.set(ROLE_PROP,person.getString(ROLE_PROP))
						.set(STATE_PROP,person.getString(STATE_PROP))
						.build();
				txn.update(person);
			    txn.commit();
			    result="1";
			}
		  }catch(Exception e) {
			  rollBack(txn);
		  }
		  return result;
	}
	private static boolean isEnabled(Entity person) {
		try {
			if(ENABLED.equalsIgnoreCase(person.getString(STATE_PROP))) {
				return true;
			}
		}catch(Exception e) {
			
		}
		return false;
	}
}
