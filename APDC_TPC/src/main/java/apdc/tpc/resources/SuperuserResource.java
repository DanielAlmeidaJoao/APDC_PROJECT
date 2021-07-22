package apdc.tpc.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Entity;

import apdc.events.utils.EventsDatabaseManagement;
import apdc.events.utils.Pair;
import apdc.events.utils.superuser.Operations;
import apdc.tpc.utils.StorageMethods;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/super")
public class SuperuserResource {

	public SuperuserResource() {
		// TODO Auto-generated constructor stub
	}

	private boolean isSuperuser(long userid) {
		Entity person = StorageMethods.getUser(Constants.datastore,userid);
		if(person==null||!StorageMethods.SU.equalsIgnoreCase(person.getString(StorageMethods.ROLE_PROP))) {
			return false;
		}
		return true;
	}
	/**
	 * loads upcoming events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/users")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response getAllUsers(@CookieParam(Constants.ALL_USERS_CURSOR) String value, @CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		Response resp;
		//list, cursor
		Pair<String,String> result=null;
		print("GOING TO LOAD USERS");
		try {
			long userid = HandleTokens.validateToken(token.getValue());
			if(isSuperuser(userid)) {
				result = Operations.getUsers(userid, value);
				NewCookie nk = HandleTokens.makeCookie(Constants.ALL_USERS_CURSOR,result.getV2(),token.getDomain());
				resp = Response.ok().cookie(nk).entity(result.getV1()).build();
			}else {
				resp = Response.status(Status.FORBIDDEN).build();
			}
			
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	/**
	 * loads upcoming events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/role")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	public Response getRole(@CookieParam(Constants.COOKIE_TOKEN) String value) {
		Response resp;
		//list, cursor
		try {
			long userid = HandleTokens.validateToken(value);
			Entity person = StorageMethods.getUser(Constants.datastore,userid);
			if(person.getString(StorageMethods.ROLE_PROP).equals(StorageMethods.SU)) {
				resp = Response.ok().entity(true+"").build();
			}else {
				resp = Response.ok().entity(false+"").build();
			}			
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	@DELETE
	@Path("/users/{userid}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response deleteUser(@CookieParam(Constants.COOKIE_TOKEN) Cookie token, @PathParam("userid") long usertoremove, @FormParam("p") String password) {
		//list, cursor
		Status result=Status.FORBIDDEN;
		print("GOING TO REMOVE "+usertoremove);
		try {
			long userid = HandleTokens.validateToken(token.getValue());
			Entity person = StorageMethods.getUser(Constants.datastore,userid);
			password=LoginManager.hashPassword(password);
			if(person.getString(StorageMethods.PASSWORD).equals(password)&&Operations.is_SU_user(person)) {
				result = Operations.removeUser(Constants.datastore,usertoremove);
				print("REMOVED ?? "+result);
			}else {
				result=Status.UNAUTHORIZED;
			}			
		}catch(Exception e) {
			Constants.LOG.severe("FAILED TO REMOVE USER "+usertoremove);
		}
		return Response.status(result).build();
	}
	
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED +";charset=utf-8")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	public Response changeState(@CookieParam(Constants.COOKIE_TOKEN) Cookie token, @FormParam("u") long usertoremove, @FormParam("p") String password) {
		//list, cursor
		Response resp;
		Pair<String,Status> pair;
		print("GOING TO REMOVE "+usertoremove);
		try {
			long userid = HandleTokens.validateToken(token.getValue());
			Entity person = StorageMethods.getUser(Constants.datastore,userid);
			password=LoginManager.hashPassword(password);
			if(person.getString(StorageMethods.PASSWORD).equals(password)&&Operations.is_SU_user(person)) {
				pair = Operations.changeUserState(Constants.datastore,usertoremove);
				if(pair.getV2()==Status.OK) {
					resp=Response.ok().entity(pair.getV1()).build();
				}else {
					resp=Response.status(pair.getV2()).build();
				}
			}else {
				resp=Response.status(Status.UNAUTHORIZED).build();
			}			
		}catch(Exception e) {
			resp=Response.status(Status.FORBIDDEN).build();
			Constants.LOG.severe("FAILED TO CHANGE STATUS USER "+usertoremove+ " "+e.getLocalizedMessage());
		}
		return resp;
	}
	/**
	 * loads upcoming events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/reported")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response getReportedEvents(@CookieParam(Constants.GET_REPORTED_EVENTS_CURSOR_CK) String value, @CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		Response resp;
		try {
			System.out.println("REPORTED EVENTS ");
			long userid = HandleTokens.validateToken(token.getValue());
			//data, cursor
			Pair<String,String> pair = EventsDatabaseManagement.getReportedEvents(value,userid);
			NewCookie nk = HandleTokens.makeCookie(Constants.GET_REPORTED_EVENTS_CURSOR_CK,pair.getV2(),token.getDomain());
			resp = Response.ok().cookie(nk).entity(pair.getV1()).build();
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	
	public static void print(String msg) {
		Constants.LOG.severe(msg);
	}
}
