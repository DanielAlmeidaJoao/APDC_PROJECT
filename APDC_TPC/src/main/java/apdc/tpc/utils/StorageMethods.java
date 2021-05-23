package apdc.tpc.utils;

import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import apdc.events.utils.moreAttributes.AdditionalAttributesOperations;
import apdc.utils.conts.Constants;

public class StorageMethods {

	public static final String PASSWORD = "password";

	public static final String STATE_PROP = "state";
	//ROLES
	public static final String ROLE_PROP = "role";
	
	public static final String NAME_PROPERTY = "name";

	public static final String EMAIL_PROP = "email";


	public static final String GBO = "GBO";
	public static final String USER = "USER";
	public static final String GA = "GA";
	public static final String SU = "SU";
	
	public static final String ENABLED = "ENABLED";
	public static final String DISABLED = "DISABLED";

	private static final String PRIVATE_VALUE = "PRIVATE";


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
	public static boolean getUser(Datastore datastore,String email) {
		Query<Entity> query = Query.newEntityQueryBuilder()
			    .setKind(USERS_KIND)
			    .setFilter(CompositeFilter.and(
			        PropertyFilter.eq(EMAIL_PROP, email))).build();
		//person = datastore.get(userKey);
		QueryResults<Entity> tasks = datastore.run(query);
		return tasks.hasNext();
	}
	public static long addUser(Datastore datastore, RegisterData data) {
		Transaction txn =null;
		long userid=0L;
		  try {
			KeyFactory kf = datastore.newKeyFactory().setKind(USERS_KIND);
			com.google.cloud.datastore.Key userKey =datastore.allocateId(kf.newKey());
			Entity person = datastore.get(userKey);
			if(person==null) {
				txn = datastore.newTransaction();
			    person = Entity.newBuilder(userKey)
			    		.set(EMAIL_PROP,data.getEmail())
						.set(PASSWORD,data.getPassword())
						.set(NAME_PROPERTY,data.getName())
						.set(ROLE_PROP,USER)
						.set(STATE_PROP,ENABLED)
						.build();
				txn.put(person);
			    txn.commit();
				userid = userKey.getId();
			    AdditionalAttributes ad = new AdditionalAttributes();
			    ad.perfil=PRIVATE_VALUE;
			    AdditionalAttributesOperations.addUserAdditionalInformation(datastore,ad,userid);
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
			}
		  }catch(Exception e) {
			LOG.severe(e.getLocalizedMessage());
		  }
		  return result;
	}
	public static int removeUser(com.google.cloud.datastore.Key ctrsKey) {
		Transaction txn = Constants.datastore.newTransaction();
		int result=-1;
		  try {
			  com.google.cloud.datastore.Key additionalInfo=Constants.datastore.newKeyFactory().setKind(AdditionalAttributesOperations.ADITIONALS).newKey(ctrsKey.getId());
				txn.delete(ctrsKey,additionalInfo);
			    txn.commit();
			    result=1;
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
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(email);
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
