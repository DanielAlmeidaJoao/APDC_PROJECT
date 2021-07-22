package apdc.tpc.resources;

import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

import apdc.events.utils.EventsDatabaseManagement;
import apdc.events.utils.GoogleCloudUtils;
import apdc.events.utils.jsonclasses.ChangeEmailArgs;
import apdc.events.utils.jsonclasses.ChangePasswordArgs;
import apdc.events.utils.jsonclasses.PasswordSizeRestrictions;
import apdc.events.utils.moreAttributes.AdditionalAttributesOperations;
import apdc.tpc.utils.AdditionalAttributes;
import apdc.tpc.utils.LoggedObject;
import apdc.tpc.utils.LoginData;
import apdc.tpc.utils.ProfileResponse;
import apdc.tpc.utils.RegisterData;
import apdc.tpc.utils.SendEmail;
import apdc.tpc.utils.StorageMethods;
//import apdc.tpc.utils.UserInfo;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;
import apdc.utils.conts.DatastoreConstants;

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
	public static boolean equalPasswords(String hashedPass,String password) {
		return hashedPass.equals(hashPassword(password));
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
				String url=GoogleCloudUtils.publicURL(profilePictureBucketName,objectName); //url
				StorageMethods.updateProfilePicture(userid,url);
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
		return password.length()<DatastoreConstants.getMinPasswordLength()||password.length()>DatastoreConstants.getMaxPasswordLength();
	}
	@POST
	@Path("/op1")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doRegister(RegisterData data, @CookieParam(Constants.VERIFICATION_CODE_COOKIE) String serverVcode) {
		LOG.severe("GOING TO REGISTER USER! pp "+data.getEmail());
		Response res=null;
		if(!validVerificationCode(serverVcode,data.getVcode())) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if(invalidPassword(data.getPassword())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean registered = StorageMethods.getUser(Constants.datastore,data.getEmail())!=null;
		if(registered) {
			return Response.status(Status.CONFLICT).build();
		}
		data.setPassword(hashPassword(data.getPassword()));
		data.setProfilePictureUrl("/imgs/Profile_avatar_placeholder_large.png");
		long userid = StorageMethods.addUser(Constants.datastore,data);
		if(userid>Constants.ZERO){
			String token=HandleTokens.generateToken(userid);
			NewCookie nk = HandleTokens.makeCookie(Constants.COOKIE_TOKEN,token,null);
			NewCookie k = HandleTokens.makeCookie(Constants.VERIFICATION_CODE_COOKIE,null,null,0);//removes
			res = Response.ok().cookie(nk,k).build();
			LOG.severe("USER REGISTERED! pp "+data.getEmail());
		}else {
			res= Response.status(Status.BAD_REQUEST).build();
		}
		return res;
	}
	private NewCookie sendVerificationCode(String email) {
		Random rand = new Random();
		int number = rand.nextInt(999999);			
		String vCode = String.format("%07d",number);
		SendEmail.send(email,vCode);
		//System.out.println(vCode);
		NewCookie k = HandleTokens.makeCookie(Constants.VERIFICATION_CODE_COOKIE,DigestUtils.sha512Hex(vCode),null,DatastoreConstants.getVerificationCodeCookieTime());
		return k;
	}
	@GET
	@Path("/vcd/{email}")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response getVerificationCode(@PathParam("email") String email, @QueryParam("n") String newUser) {
		LOG.severe("GOING TO SEND VERIFICATION CODE "+email);
		Response res=null;
		boolean userExists = StorageMethods.getUser(Constants.datastore,email)!=null;
		boolean creatingNewUser = email.equals(newUser);
		if(userExists && !creatingNewUser) {
			res = Response.ok().cookie(sendVerificationCode(email)).build();
		}else if(creatingNewUser && !userExists ) {
			res = Response.ok().cookie(sendVerificationCode(email)).build();
		}else if(creatingNewUser&&userExists) {
			res = Response.status(Status.CONFLICT).build();
		}else {
			res = Response.status(Status.NOT_FOUND).build();
		}		
		return res;
	}
	@POST
	@Path("/chgmailvcd")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response sendVerificationCodeOnChangingEmail(@CookieParam(Constants.COOKIE_TOKEN) String value, ChangeEmailArgs args) {
		Response res=null;
		LOG.info("GOING TO SEND VERIFICATION CODE TO THE NEW EMAIL");
		try {
			long userid = HandleTokens.validateToken(value);
			Entity user = StorageMethods.getUser(Constants.datastore,userid);
			boolean emailTaken = StorageMethods.getUser(Constants.datastore,args.getNewEmail())!=null;
			if(emailTaken) {
				return Response.status(Status.CONFLICT).build();
			}else if(user==null) {
				return Response.status(Status.NOT_FOUND).build();
			}else if(!equalPasswords(user.getString(StorageMethods.PASSWORD),args.getPassword())){
				return Response.status(Status.FORBIDDEN).build();
			}else {
				NewCookie nc = sendVerificationCode(args.getNewEmail());
				return Response.ok().cookie(nc).build();
			}
		}catch(Exception e) {
			res=Response.status(Status.UNAUTHORIZED).build();
		}
		return res;
	}
	@PUT
	@Path("/chgmail")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response ChangeEmail(@CookieParam(Constants.COOKIE_TOKEN) String loginToken, 
			@CookieParam(Constants.VERIFICATION_CODE_COOKIE) String serverVcode, ChangeEmailArgs args) {
		Response res=null;
		try {
			long userid = HandleTokens.validateToken(loginToken);
			Entity user = StorageMethods.getUser(Constants.datastore,userid);
			//THE PASSWORD ATTRIBUTE HAS THE VERIFICATION CODE
			if(user==null) {
				res=Response.status(Status.NOT_FOUND).build();
			}else if(validVerificationCode(serverVcode,args.getPassword())) {
				StorageMethods.updateEmail(user,args.getNewEmail());
				res=Response.ok().build();
			}else {
				res = Response.status(Status.NOT_ACCEPTABLE).build();
			}
		}catch(Exception e) {
			res=Response.status(Status.UNAUTHORIZED).build();
		}
		return res;
	}
	private boolean validVerificationCode(String serverVcode, String clientVcode) {
		return DigestUtils.sha512Hex(clientVcode).equals(serverVcode);
	}
	@POST
	@Path("/chgpwd")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response changePassword(@CookieParam(Constants.VERIFICATION_CODE_COOKIE) String serverVcode, ChangePasswordArgs args) {
		Response res=null;
		try {
			if(validVerificationCode(serverVcode,args.getVcode())) {
				StorageMethods.updatePassword(args.getEmail(),hashPassword(args.getPassword()));
				NewCookie k = HandleTokens.makeCookie(Constants.VERIFICATION_CODE_COOKIE,null,null,0);
				res = Response.ok().cookie(k).build();
			}else {
				res = Response.status(Status.NOT_ACCEPTABLE).build();
			}
		}catch(Exception e) {
			NewCookie k = HandleTokens.makeCookie(Constants.VERIFICATION_CODE_COOKIE,null,null,0);
			res = Response.status(Status.BAD_REQUEST).cookie(k).build();
			e.printStackTrace();
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
			    //String domain = httpHeaders.getHeaderString("host");
			    //domain=null;
			    long userid = user.getKey().getId();
				k = HandleTokens.makeCookie(Constants.COOKIE_TOKEN,HandleTokens.generateToken(userid),null);
				LoggedObject lo = new LoggedObject();
				lo.setEmail(data.getEmail());
				lo.setName(user.getString(StorageMethods.NAME_PROPERTY));
				lo.setProfilePictureURL(user.getString(StorageMethods.PROFILE_PICTURE_URL_PROP));
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
	public static final int CREATED_EVENTS_MULTPLIER = 10;
	public static final double INTERESTED_EVENTS_MULTPLIER = 2.5;

	@GET
	@Path("/infos/{userid}")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	public Response getAdditionalInfos(@CookieParam(Constants.COOKIE_TOKEN) String value, @PathParam("userid") String otheruser) {
		long userid;
		Response res=null;
		ProfileResponse obj;
		try {
			boolean loggedUser=false;
			userid = HandleTokens.validateToken(value);
			try {
				long ot = Long.parseLong(otheruser);
				loggedUser = ot==userid;
				userid = Long.parseLong(otheruser);
			}catch(Exception e) {
				loggedUser=true;
				System.out.println(e.getLocalizedMessage());
			}
			Entity user = StorageMethods.getUser(Constants.datastore,userid);
			if(user==null) {
				res = Response.status(Status.NOT_FOUND).build();
			}else {
				obj=AdditionalAttributesOperations.getAdditionalInfos(Constants.datastore,userid,user);
				obj.setViewingOwnProfile(loggedUser);
				obj.setParticipationScore(obj.getEvents()*CREATED_EVENTS_MULTPLIER+obj.getInterestedEvents()*INTERESTED_EVENTS_MULTPLIER);
				if(loggedUser) {
					obj.setEmail(user.getString(StorageMethods.EMAIL_PROP));
				}
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
		LOG.severe("GOING TO LOGOFF");
		ResponseBuilder rb = Response.ok();
		java.util.Map.Entry<String,Cookie> en;
		Iterator<java.util.Map.Entry<String,Cookie>> entries = headers.getCookies().entrySet().iterator();
		while(entries.hasNext()) {
			en=entries.next();
			NewCookie destroyed = HandleTokens.destroyCookie(en.getKey());
			System.out.println("NNEW COOKIE "+destroyed.getValue()+ " "+en.getKey());
			rb=rb.cookie(destroyed);
		}
		return rb.build();
	}
	@GET
	@Path("/recks")
	//@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	//@Context HttpHeaders headers
	public Response resetCookies(@Context HttpHeaders headers) {
		//HandleTokens.destroyToken(token);
		LOG.severe("GOING TO RESET COOKIE");
		ResponseBuilder rb = Response.ok();
		java.util.Map.Entry<String,Cookie> en;
		Iterator<java.util.Map.Entry<String,Cookie>> entries = headers.getCookies().entrySet().iterator();
		while(entries.hasNext()) {
			en=entries.next();
			if(!Constants.COOKIE_TOKEN.equals(en.getKey())) {
				NewCookie destroyed = HandleTokens.destroyCookie(en.getKey());
				rb=rb.cookie(destroyed);	
			}
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
	@PUT
	@Path("/updatename/{name}")
	public Response updateName(@CookieParam(Constants.COOKIE_TOKEN) String token, @PathParam("name") String newName) {
		Response resp=null;
		long userid;
		try {
			userid = HandleTokens.validateToken(token);
			Entity user = StorageMethods.getUser(Constants.datastore,userid);
			if(user!=null) {
				StorageMethods.updateName(user,newName);
				resp = Response.noContent().build();
			}else {
				resp = Response.status(Status.NOT_FOUND).build();
			}
		}catch(Exception e)
		{
			resp=Response.status(Status.UNAUTHORIZED).build();
		}
		return resp;
	}
	@GET
	@Path("/p/rtcs")
	public Response loadPasswordSizeRestrictions() {
		Response resp=null;
		try {
			PasswordSizeRestrictions p = DatastoreConstants.getRestrictions();
			if(p!=null) {
				return Response.ok().entity(p).build();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		return resp;
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

}
