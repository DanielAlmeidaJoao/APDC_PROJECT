package apdc.events.utils;

//Imports the Google Cloud client library
import com.google.cloud.storage.Bucket;
import com.beoui.geocell.GeocellUtils;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.openlocationcode.OpenLocationCode;

import apdc.tpc.utils.SendEmail;
import apdc.tpc.utils.StorageMethods;
import ch.hsr.geohash.GeoHash;

public class Tester {

	public Tester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		/*
		Filter propertyFilter =
			    new FilterPredicate("height", FilterOperator.GREATER_THAN_OR_EQUAL, "");
			Query q = new Query("Person").setFilter(propertyFilter);
			
		Filter keyFilter =
			    new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, 0L);
			Query q2 = new Query("Person").setFilter(keyFilter);
		 // Instantiates a client
	    Storage storage = StorageOptions.getDefaultInstance().getService();

	    // The name for the new bucket
	    String bucketName = "danielBucket";  // "my-new-bucket";

	    // Creates the new bucket
	    Bucket bucket = storage.create(BucketInfo.of(bucketName));

	    System.out.printf("Bucket %s created.%n", bucket.getName());*/
		//SendEmail.send("joao@gmail.com","100101");
		
		//GeoHash geohash = GeoHash. withCharacterPrecision(53.244664, -6.140530, 12);
		//String geohashString = geohash.toBase32().substring(0, 3); //3 characters for around 100km of precision
		/*
		double latitude = 38.7222524;
		double longitude = -9.1393366;
		String [] arr = "38.5260437,-8.8909328".split(",");
		latitude=Double.parseDouble(arr[0]);
		longitude=Double.parseDouble(arr[1]);

		OpenLocationCode olc = new OpenLocationCode(latitude, longitude, 10); // the last parameter specifies the number of digits
		String code = olc.getCode(); // this will be the full code
		System.out.println("PLUS CODE : "+code.substring(0,4)); */
	}
}
