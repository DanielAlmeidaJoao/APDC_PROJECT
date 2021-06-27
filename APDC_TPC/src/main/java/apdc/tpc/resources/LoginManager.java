package apdc.tpc.resources;

import java.util.Iterator;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Entity;
import com.google.gson.Gson;

import apdc.events.utils.CountEventsUtils;
import apdc.events.utils.GoogleCloudUtils;
import apdc.events.utils.ImageKindsUtils;
import apdc.events.utils.moreAttributes.AdditionalAttributesOperations;
import apdc.tpc.utils.AdditionalAttributes;
import apdc.tpc.utils.LoggedObject;
import apdc.tpc.utils.LoginData;
import apdc.tpc.utils.RegisterData;
import apdc.tpc.utils.StorageMethods;
//import apdc.tpc.utils.UserInfo;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
public class LoginManager {
	@Context private HttpServletRequest request;
	
	private static final Logger LOG = Logger.getLogger(LoginManager.class.getName());
	public static final String profilePictureBucketName="profile_pics46335560256500";


	//AUTHOR: DANIEL JOAO, COPYING IT WITHOUT MY CONSENT IS A CRIME, LEADING UP TO 7 YEARS IN JAIL	
	private final Gson g = new Gson();

	public static String hashPassword(String password) {
		return DigestUtils.sha512Hex(password);
	}
	@GET
	@Path("/{username}")
	public Response getUser(@PathParam("username") String username ) {
		boolean rs;
		if(username.trim().equals("daniel")) {
			rs = true;
		}else {
			rs= false;
		}
		return Response.ok().entity(g.toJson(rs)).build();
	}
	/**
	 * saves the profile photo of the user calling the operation
	 * @param value
	 * @return
	 */
	@POST
	@Path("/savep")
	@Consumes(MediaType.MULTIPART_FORM_DATA +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response saveProfilePicture(@CookieParam(Constants.COOKIE_TOKEN) String value){
		Response response;
		try {
			long userid = HandleTokens.validateToken(value);			
			try {
				String objectName=userid+""+System.currentTimeMillis();
				Part p = request.getPart("profilePicture");
				GoogleCloudUtils.uploadObject(profilePictureBucketName,objectName,p.getInputStream());
				String oldobjectName = ImageKindsUtils.addUserProfilePicture(userid,objectName);
				GoogleCloudUtils.deleteObject(profilePictureBucketName,oldobjectName);
				System.out.println("IMAGE SAVED WITH SUCCESS!");
				String url=GoogleCloudUtils.publicURL(profilePictureBucketName,objectName); //url
				response = Response.ok(Constants.g.toJson(url)).build();
			}catch(Exception e) {
				e.printStackTrace();
				response = Response.status(Status.BAD_REQUEST).build();
			}
		}catch(Exception e) {
			response = Response.status(Status.UNAUTHORIZED).build();
		}
		return response;
	}
	private boolean invalidPassword(String password) {
		return password.length()<Constants.PASSWORD_MINLENGTH||password.length()>Constants.PASSWORD_MAXLENGTH;
	}
	@POST
	@Path("/op1")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doRegister(RegisterData data) {
		LOG.severe("GOING TO REGISTER USER! pp "+data.getEmail());
		Response res=null;
		if(invalidPassword(data.getPassword())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean registered = StorageMethods.getUser(Constants.datastore,data.getEmail());
		if(registered) {
			return Response.status(Status.CONFLICT).build();
		}
		data.setPassword(hashPassword(data.getPassword()));
		long userid = StorageMethods.addUser(Constants.datastore,data);
		if(userid>Constants.ZERO){
			String token=HandleTokens.generateToken(userid);
			NewCookie nk = HandleTokens.makeCookie(Constants.COOKIE_TOKEN,token,null);
			res= Response.ok().cookie(nk).build();
			LOG.severe("USER REGISTERED! pp "+data.getEmail());
		}else {
			res= Response.status(Status.BAD_REQUEST).build();
		}
		return res;
	}
	
	@POST
	@Path("/op2")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doLogin(@Context HttpHeaders httpHeaders, @CookieParam(Constants.COOKIE_TOKEN) String value, LoginData data) {
		NewCookie k=null;
		Response response;
		try {
			data.setPassword(hashPassword(data.getPassword()));
			System.out.println(data.getPassword());
			Entity user = StorageMethods.getUser(Constants.datastore,data);
			if(user==null) {
				response=Response.status(Status.UNAUTHORIZED).build();
			}else{
				//AuthToken at = new AuthToken(user.getString("email"));
			    String domain = httpHeaders.getHeaderString("host");
			    domain=null;
			    long userid = user.getKey().getId();
				k = HandleTokens.makeCookie(Constants.COOKIE_TOKEN,HandleTokens.generateToken(userid),domain);
				LoggedObject lo = new LoggedObject();
				lo.setEmail(data.getEmail());
				lo.setName(user.getString(StorageMethods.NAME_PROPERTY));
				String objectName=ImageKindsUtils.getObjectName(userid,ImageKindsUtils.USERS_PROFILE_PICTURES_KIND);
				lo.setProfilePictureURL(GoogleCloudUtils.publicURL(profilePictureBucketName,objectName));
				response=Response.ok().cookie(k).entity(g.toJson(lo)).build();
			}
		}catch(Exception e) {
			response=Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}
	@POST
	@Path("/op3")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	public Response doUpdateAdditionalInfos(@CookieParam(Constants.COOKIE_TOKEN) String value, AdditionalAttributes ads) {
		long userid;
		Status result;
		System.out.println("GOING TOOOOOOOOOOO ");
		try {
			userid = HandleTokens.validateToken(value);
			result=AdditionalAttributesOperations.addUserAdditionalInformation(Constants.datastore,ads,userid);
		}catch(Exception e) {
			result=Status.UNAUTHORIZED;
		}		
		return Response.status(result).build();
	}
	@GET
	@Path("/infos")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	public Response getAdditionalInfos(@CookieParam(Constants.COOKIE_TOKEN) String value) {
		long userid;
		Response res=null;
		AdditionalAttributes obj;
		System.out.println("GOING TOOOOOOOOOOO LOAD ADDITIONAL INFORMATION");
		try {
			userid = HandleTokens.validateToken(value);
			obj=AdditionalAttributesOperations.getAdditionalInfos(Constants.datastore,userid);
			if(obj==null) {
				Response.status(Status.NOT_FOUND).build();
			}else {
				obj.setEvents(CountEventsUtils.getNumberOfEvents(userid,Constants.datastore));
				res=Response.ok().entity(Constants.g.toJson(obj)).build();
			}
		}catch(Exception e) {
			Constants.LOG.severe(e.getLocalizedMessage());
			res=Response.status(Status.UNAUTHORIZED).build();
		}		
		return res;
	}
	/**
	 * do a logout of the logged user. Removes all cookies
	 * @param headers
	 * @return
	 */
	@GET
	@Path("/op7")
	//@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	//@Context HttpHeaders headers
	public Response doLogout(@Context HttpHeaders headers) {
		//HandleTokens.destroyToken(token);
		ResponseBuilder rb = Response.ok();
		java.util.Map.Entry<String,Cookie> en;
		Iterator<java.util.Map.Entry<String,Cookie>> entries = headers.getCookies().entrySet().iterator();
		while(entries.hasNext()) {
			en=entries.next();
			NewCookie destroyed = HandleTokens.destroyCookie(en.getKey());
			rb=rb.cookie(destroyed);
		}
		return rb.build();
	}
	@DELETE
	@Path("/op8")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED +";charset=utf-8")
	public Response doRemove(@CookieParam(Constants.COOKIE_TOKEN) String token, @FormParam("p") String password) {
		ResponseBuilder rb;
		Constants.LOG.severe("GOING TO REMOVE USER!");
		try {
			long userid = HandleTokens.validateToken(token);
			password=hashPassword(password);
			if(StorageMethods.removeUser(Constants.datastore,userid,password)>Constants.ZERO) {
				rb=Response.status(Status.OK);
				Constants.LOG.severe("USER REMOVED "+userid);
			}else {
				Constants.LOG.severe("ERRROR "+userid);
				rb=Response.status(Status.UNAUTHORIZED);
			}
		}catch (Exception e) {
			rb=Response.status(Status.BAD_REQUEST);
		}
		return rb.build();
	}
	/*
	@POST
	@Path("/op9")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doSpy(LoginData data) {
		//email -> token
		//password -> other user email
		UserInfo u = StorageMethods.getOtherUser(Constants.datastore,data);
		return Response.ok().entity(g.toJson(u)).build();
	}*/
	/*
	@POST
	@Path("/op10")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response blockOtherUsers(ChangeOtherUser data) {
		//data.email -> token
		//data.password -> password
		//data.name -> the email of another user
		LOG.severe(" I AM GOING TO CHANGE USERS BULLSHIT");
		LOG.severe(data.toString());
		String u = StorageMethods.disableUser(Constants.datastore,data);
		if(u.equals("2")) {
			try {
				HandleTokens.destroyToken(data.getEmail());
			}catch(Exception e) {
				
			}
		}
		return Response.ok().entity(u).build();
	}*/
	@POST
	@Path("/op11")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doChangePassword(RegisterData data) {
		
		/**
		 * name: oldPass,
	       password: newPass,
	       email: token,
		 */
		/*
		String email=null;
		try {
			email = HandleTokens.validateToken(data.getEmail());
		}catch(Exception e) {
			
		}
		if(email!=null) {
			email = StorageMethods.updatePassword(Constants.datastore, email,data.getPassword(),data.getName()); //result
		}else {
			email="-1";
		}
		return Response.ok().entity(email).build();*/
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}
}
