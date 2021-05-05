package apdc.utils.conts;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.Gson;

public interface Constants {
	
	public static final int ZERO = 0;
	public static final String COOKIE_TOKEN ="token";
	public static final String GET_EVENT_CURSOR_CK ="crsck";

	public static final Gson g = new Gson();
	public static final Datastore datastore =	DatastoreOptions.getDefaultInstance().getService();



}
