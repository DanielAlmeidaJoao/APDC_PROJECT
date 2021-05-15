package apdc.utils.conts;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.Gson;

public interface Constants {
	
	public static final int ZERO = 0;
	
	public static final String COOKIE_TOKEN ="token";
	public static final String GET_EVENT_CURSOR_CK ="crsck";
	public static final String EVENT_ID = "eventId";
	
	public static final String EVENT_FORMDATA_KEY="evd";
	public static final String EVENT_PICS_FORMDATA_KEY="imgs";
	
	public static final int MAX_IMAGES_PER_EVENTS=5;

	///
	public static final String NAME_PROPERTY = "name";
	public static final String DATE_FORMAT = "yyyy-mm-dd HH:mm"; 
	
	public static final String ID="ID";

	public static final Gson g = new Gson();
	public static final Datastore datastore =	DatastoreOptions.getDefaultInstance().getService();



}
