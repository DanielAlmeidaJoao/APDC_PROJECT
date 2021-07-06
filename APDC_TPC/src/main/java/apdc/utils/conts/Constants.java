package apdc.utils.conts;

import java.util.logging.Logger;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.Gson;

public interface Constants {
	
	public static final int ZERO = 0;
	
	public static final String COOKIE_TOKEN ="token";
	
	public static final String VERIFICATION_CODE_COOKIE ="vrfcck";

	public static final String GET_EVENT_CURSOR_CK ="crsck";
	public static final String ALL_USERS_CURSOR ="kjdsalj";

	public static final String GET_REPORTED_EVENTS_CURSOR_CK ="rptdcrsck";
	public static final String FINISHED_EVENTS_CURSOR_CK ="fnesck";
	public static final String USER_EVENTS_CURSOR_CK ="userevntscrs";
	public static final String USER_INTERESTED_EVENTS_CURSOR_CK ="useritrstdscrs";



	public static final String EVENT_ID = "eventId";
	
	public static final String EVENT_FORMDATA_KEY="evd";
	public static final String EVENT_PICS_FORMDATA_KEY="imgs";
	
	public static final int MAX_IMAGES_PER_EVENTS=5;

	///
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm"; 
	
	public static final String ID="ID";

	public static final Gson g = new Gson();
	public static final Datastore datastore =	DatastoreOptions.getDefaultInstance().getService();
	
	public static final Logger LOG = Logger.getLogger("GENERAL_LOG");

	public static final int PASSWORD_MINLENGTH=6;
	public static final int PASSWORD_MAXLENGTH=12;



}
