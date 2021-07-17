package apdc.events.utils;

//Imports the Google Cloud client library
import com.google.cloud.storage.Bucket;


import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import apdc.tpc.utils.SendEmail;

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
		SendEmail.send("joao@gmail.com","100101");

	}
}
