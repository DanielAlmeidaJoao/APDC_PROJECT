package apdc.events.utils;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.Identity;
import com.google.cloud.Policy;

import com.google.cloud.storage.StorageRoles;

public class GoogleCloudUtils {

	/**
	 * creates a bucket on google cloud
	 * @param bucketName
	 */
	public static void createBucket(String bucketName) {
		 // Instantiates a client
	    Storage storage = StorageOptions.getDefaultInstance().getService();

	    // Creates the new bucket
	    Bucket bucket = storage.create(BucketInfo.of(bucketName));

	    System.out.printf("Bucket %s created.%n", bucket.getName());
	}
	public static void makeBucketPublic(String bucketName) {
	    // The ID of your GCP project
	    // String projectId = "your-project-id";

	    // The ID of your GCS bucket
	    // String bucketName = "your-unique-bucket-name";

	    //Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		Storage storage = StorageOptions.getDefaultInstance().getService();
	    Policy originalPolicy = storage.getIamPolicy(bucketName);
	    storage.setIamPolicy(
	        bucketName,
	        originalPolicy
	            .toBuilder()
	            .addIdentity(StorageRoles.objectViewer(), Identity.allUsers()) // All users can view
	            .build());
	    
	    System.out.println("Bucket " + bucketName + " is now publicly readable");
	  }
	/**
	 * uploads an object to google cloud
	 * @param bucketName
	 * @param objectName
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static String uploadObject(String bucketName, String objectName, InputStream in) throws IOException {
		    //Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
			Storage storage = StorageOptions.getDefaultInstance().getService();
			/*
			 * Example of creating a blob from a byte array: 
				String bucketName = "my-unique-bucket";
				 String blobName = "my-blob-name";
				 BlobId blobId = BlobId.of(bucketName, blobName);
				 BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
				 Blob blob = storage.create(blobInfo, "Hello, World!".getBytes(UTF_8

			 */
		    BlobId blobId = BlobId.of(bucketName,objectName);
		    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
		    //Path p = new File("das");

		    //storage.create(blobInfo, Files.readAllBytes(Paths.get("files/"+objectName)));
		    Blob b = storage.create(blobInfo,IOUtils.toByteArray(in));
		    storage.createAcl(blobId,Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

		    //Blob com.google.cloud.storage.Storage.get(BlobId blob)
//		    System.out.println(
//		    "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
		    return b.getName();
	}
	public static String uploadObject(String bucketName, String objectName, byte [] content) throws IOException {
		Storage storage = StorageOptions.getDefaultInstance().getService();
	    BlobId blobId = BlobId.of(bucketName,objectName);
	    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
	    Blob b = storage.create(blobInfo,content);
	    storage.createAcl(blobId,Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
	    return b.getName();
}
	public static Blob hasThisObject(String bucketName, String objectName) {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		/*
		 * Example of creating a blob from a byte array: 
			String bucketName = "my-unique-bucket";
			 String blobName = "my-blob-name";
			 BlobId blobId = BlobId.of(bucketName, blobName);
			 BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
			 Blob blob = storage.create(blobInfo, "Hello, World!".getBytes(UTF_8
		 */
	    BlobId blobId = BlobId.of(bucketName, objectName);
	    Blob b =  storage.get(blobId);
	    return b;
	}
	/**
	 * download object from google cloud
	 * @param bucketName
	 * @param objectName
	 * @return
	 */
	public static byte [] downloadObject(String bucketName, String objectName) {
		System.out.println("Going to read from bucket!");
		Blob b=hasThisObject(bucketName,objectName); 
		if(b==null) {
			return null;
		}
	    System.out.println(b.getMediaLink());
	    String g = new String(b.getContent());
	    System.out.println("DOWNLOAD STUFF "+g);
	    return b.getContent();
	}
	public static void saveAvatarPicture(String bucketName, String objectName) {
		try {
			/*
	    	File profileAvatar = new File(".\\imgs\\Profile_avatar_placeholder_large.png");
	    	byte [] bytes = new byte[(int) profileAvatar.length()];
	    	FileInputStream fin = new FileInputStream(profileAvatar);
	    	fin.read(bytes);*/
	    	uploadObject(bucketName,objectName,Files.readAllBytes(Paths.get(".\\imgs\\Profile_avatar_placeholder_large.png")));
	    	//fin.close();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	/**
	 * remove an object from google cloud
	 * @param bucketName
	 * @param objectName
	 */
	public static void deleteObject(String bucketName, String objectName) {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		/*
		 * Example of creating a blob from a byte array: 
			String bucketName = "my-unique-bucket";
			 String blobName = "my-blob-name";
			 BlobId blobId = BlobId.of(bucketName, blobName);
			 BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
			 Blob blob = storage.create(blobInfo, "Hello, World!".getBytes(UTF_8
		 */
	    BlobId blobId = BlobId.of(bucketName, objectName);
	    storage.delete(blobId);
	}
	public static String publicURL(String bucketName, String objectName) {
		return String.format("https://storage.googleapis.com/%s/%s",bucketName,objectName);
	}

}
