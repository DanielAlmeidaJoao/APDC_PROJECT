package apdc.events.utils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import apdc.utils.conts.Constants;

public class ImageKindsUtils {

	public static final String URL_PROP="PUBLIC_URL";
	public static final String USERS_PROFILE_PICTURES_KIND="USERS_PROFILE_PICTURES";
	public static final String EVENTS_IMAGES_KIND="EVENTS_IMAGES";

	public ImageKindsUtils() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * saves the google cloud url into the datastore
	 * @param entityid entity id, which can be either user or event
	 * @param kind the kind name
	 * @param objectName unique id of the data that was stored in the google cloud, it will be used to build a public url
	 */
	public static String addObjectName(long entityid, String kind,String objectName) {
		Datastore datastore = Constants.datastore;
		Transaction txn =null;
		String oldurl=null;
		  try {
			com.google.cloud.datastore.Key entityKey=datastore.newKeyFactory().setKind(kind).newKey(entityid);
			Entity entry = datastore.get(entityKey);
			txn = datastore.newTransaction();
			if(entry!=null) {
				oldurl=entry.getString(URL_PROP);
			}
			entry = Entity.newBuilder(entityKey)
		    		.set(URL_PROP,objectName)
					.build();
			txn.put(entry);
		    txn.commit();
		  }catch(Exception e) {
			  if(txn!=null) {
				  txn.rollback();
			  }
			  e.printStackTrace();
		  }
		  return oldurl;
	}
	/**
	 * removes the url from datastore
	 * @param entityid
	 * @param kind
	 * @param publicURL
	 */
	public  static void removeObjectName(long entityid, String kind,String objectName) {
		Datastore datastore = Constants.datastore;
		Transaction txn =null;
		  try {
			com.google.cloud.datastore.Key entityKey =datastore.newKeyFactory().setKind(kind).newKey(entityid);
			txn = datastore.newTransaction();
			txn.delete(entityKey);
		    txn.commit();
		  }catch(Exception e) {
			  if(txn!=null) {
				  txn.rollback();
			  }
			  e.printStackTrace();
		  }
	}
	/**
	 * gets the url from datastore
	 * @param entityid
	 * @param kind
	 * @return
	 */
	public static String getObjectName(long entityid, String kind) {
		Datastore datastore = Constants.datastore;
		  try {
			com.google.cloud.datastore.Key entityKey =datastore.newKeyFactory().setKind(kind).newKey(entityid);
			Entity entry = datastore.get(entityKey);
			return entry.getString(URL_PROP);
		  }catch(Exception e) {
			  System.out.println(e.getLocalizedMessage());
		  }
		  return null;
	}
}
