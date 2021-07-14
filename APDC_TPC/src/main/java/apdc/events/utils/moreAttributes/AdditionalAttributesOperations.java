package apdc.events.utils.moreAttributes;

import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;

import apdc.events.utils.CountEventsUtils;
import apdc.events.utils.EventParticipationMethods;
import apdc.tpc.utils.AdditionalAttributes;
import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

public class AdditionalAttributesOperations {
	public static final String ADITIONALS = "ADDITIONALS_INFO";
	private static final String BIO = "BIO";
	private static final String WEBSITE = "WEBSITE";
	private static final String FACEBOOK = "FACEBOOK";
	private static final String TWITTER = "TWITTER";
	private static final String INSTAGRAM = "INSTAGRAM";
	private static final String QUOTE = "QUOTE";
	public AdditionalAttributesOperations() {
		// TODO Auto-generated constructor stub
	}

	public static boolean removeAdditionalAttributes(Datastore datastore, long userid) {
		boolean result=false;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		
		Transaction txn = datastore.newTransaction();
		  try {
			txn.delete(ctrsKey);
		    txn.commit();
		    result=true;
		  }catch(Exception e) {
			Constants.LOG.severe(e.getLocalizedMessage());
		  } finally {
			  StorageMethods.rollBack(txn);
		  }
		  return result;
	}
	/**
	 * gets the additional information of the user passed in the argument
	 * @param datastore - datastore instance
	 * @param userid - userid
	 * @return user additional information if success else null
	 */
	public static AdditionalAttributes getAdditionalInfos(Datastore datastore, long userid) {
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		 AdditionalAttributes ad=null;
		  try {
			Entity stats=datastore.get(ctrsKey);
			ad = new AdditionalAttributes();
			ad.setBio(stats.getString(BIO));
			ad.setQuote(stats.getString(QUOTE));
			ad.setInstagram(stats.getString(INSTAGRAM));
			ad.setTwitter(stats.getString(TWITTER));
			ad.setFacebook(stats.getString(FACEBOOK));
			ad.setWebsite(stats.getString(WEBSITE));
			ad.setEvents(CountEventsUtils.getNumberOfEvents(userid,Constants.datastore));
			ad.setInterestedEvents(EventParticipationMethods.getNumberOfInterestedEvents(userid));
		  }catch(Exception e) {
			  EventParticipationMethods.print("AdditionalAttributesOperations","AdditionalAttributes",e.getLocalizedMessage());
		  }
		  return ad;
	}
	public static Status addUserAdditionalInformation(Datastore datastore, AdditionalAttributes ad, long userid) {
		Status result;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		
		Transaction txn = datastore.newTransaction();
		  try {
			Entity stats;
			stats=Entity.newBuilder(ctrsKey)
					.set(BIO,ad.getBio())
					.set(QUOTE,ad.getQuote())
					.set(INSTAGRAM,ad.getInstagram())
					.set(TWITTER,ad.getTwitter())
					.set(FACEBOOK,ad.getFacebook())
					.set(WEBSITE,ad.getWebsite())
					.build();
			txn.put(stats);
		    txn.commit();
		    result=Status.OK;
		  }catch(Exception e) {
			result=Status.BAD_REQUEST;
			print(e.getLocalizedMessage());
			  StorageMethods.rollBack(txn);
		  }
		  return result;
	}
	public static AdditionalAttributes getAdditionalAttributes(Datastore datastore, long userid) {
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		  Entity stats=datastore.get(ctrsKey);
		  AdditionalAttributes ad = new AdditionalAttributes();
;
		  if(stats!=null) {
			  //,stats.getString(TELEFONE),stats.getString(TELEMOVEL),stats.getString(MORADA),stats.getString(MORADA_COMPLEMENTAR),stats.getString(LOCALIDADE)
			  ad.setBio(stats.getString(BIO));
			  ad.setQuote(stats.getString(QUOTE));
			  ad.setInstagram(stats.getString(INSTAGRAM));
			  ad.setTwitter(stats.getString(TWITTER));
			  ad.setFacebook(stats.getString(FACEBOOK));
			  ad.setWebsite(stats.getString(WEBSITE));
		  }
		return ad;
	}
	private static void print(String msg) {
		Constants.LOG.severe(msg);
	}
}
