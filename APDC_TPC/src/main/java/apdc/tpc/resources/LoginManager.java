package apdc.tpc.resources;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.Entity;
import com.google.gson.Gson;

import apdc.tpc.utils.AdditionalAttributes;
import apdc.tpc.utils.ChangeOtherUser;
import apdc.tpc.utils.LoggedObject;
import apdc.tpc.utils.LoginData;
import apdc.tpc.utils.RegisterData;
import apdc.tpc.utils.StorageMethods;
import apdc.tpc.utils.UserInfo;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
public class LoginManager {
	@Context private HttpServletRequest request;
	
	private static final Logger LOG = Logger.getLogger(LoginManager.class.getName());

	//AUTHOR: DANIEL JOAO, COPYING IT WITHOUT MY CONSENT IS A CRIME, LEADING UP TO 7 YEARS IN JAIL	
	private final Gson g = new Gson();

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

	@POST
	@Path("/op1")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doRegister(RegisterData data) {
		LOG.severe("USER REGISTERED! pp "+data.getEmail());
		Response res=null;
		LoggedObject lo = StorageMethods.addUser(Constants.datastore,data,g);
		if(lo.getStatus().equals("1")) {
			String token=HandleTokens.generateToken(lo.getEmail());
			if(token!=null) {
				lo.setStatus("1");
				lo.setToken(token);
			}else {
				lo.setStatus("3");
			}
		}
		
		res= Response.ok().entity(g.toJson(lo)).build();
		return res;
	}
	
	@POST
	@Path("/op2")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doLogin(@Context HttpHeaders httpHeaders, @CookieParam(Constants.COOKIE_TOKEN) String value, LoginData data) {
		String res=null;
		LoggedObject lo = new LoggedObject();
		NewCookie k=null;
		try {
			Entity user = StorageMethods.getUser(Constants.datastore,data);
			if(user==null) {
				lo.setStatus("0");
			}else{
				//AuthToken at = new AuthToken(user.getString("email"));
			    String domain = httpHeaders.getHeaderString("host");
				k = HandleTokens.makeCookie(Constants.COOKIE_TOKEN,HandleTokens.generateToken(data.getEmail()),domain);
				lo.setEmail(data.getEmail());
				lo.setName(user.getString("name"));
				lo.setToken(HandleTokens.generateToken(lo.getEmail()));
				if(lo.getToken()==null) {
					lo.setStatus("2");
				}else {
					lo.setStatus("1");
					lo.setGbo("GBO".equals(user.getString("role")));
				}
				AdditionalAttributes ad= StorageMethods.getAdditionalAttributes(Constants.datastore,data.getEmail());
				if(ad==null) {
					lo.setAdditionalAttributes("0");
				}else {
					lo.setAdditionalAttributes(g.toJson(ad));
				}
			}
		}catch(Exception e) {
			lo.setStatus("-1");
		}
		lo.setToken(HandleTokens.generateToken(lo.getEmail()));
		res= g.toJson(lo);
		return  Response.ok().cookie(k).entity(res).build();
	}
	@POST
	@Path("/op3")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	public Response doUpdateAdditionalInfos(AdditionalAttributes ads) {
		if(request!=null) {
			LOG.severe("REQUEST IS NULL");
		}else {
			LOG.severe("PQ GOING AWEL");
		}
		String email = null;
		try {
			email = HandleTokens.validateToken(ads.getEmail());
		}catch(Exception e) {
			
		}
		String result = "-2";
		
		if(email!=null) {
			ads.setEmail(email);
			result=""+StorageMethods.addUserAdditionalInformation(Constants.datastore, ads);
		}else {
			result="TOKEN NOT FOUND";
		}
		return Response.ok().entity(result).build();
	}

	@GET
	@Path("/op7")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	//@Context HttpHeaders headers
	public Response doLogout() {
		//HandleTokens.destroyToken(token);
	    NewCookie tokenCookie = HandleTokens.destroyCookie(Constants.COOKIE_TOKEN,"");
	    NewCookie eventOffsetCookie = HandleTokens.destroyCookie(Constants.GET_EVENT_CURSOR_CK,"");

		int result =1;
		return Response.ok().cookie(tokenCookie,eventOffsetCookie).entity(result).build();
	}
	@POST
	@Path("/op8")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doRemove(LoginData data) {
		String token= data.getEmail();
		String tk =null;
		try {
			 tk= HandleTokens.validateToken(data.getEmail());
		}catch (Exception e) {
			
		}
		String res="";
		if(tk==null) {
			res="SESSION EXPIRED!";
		}else {
			data.setEmail(tk);
			if (StorageMethods.removeUser(Constants.datastore, data)>0) {
				HandleTokens.destroyToken(token);
				res="REMOVED WITH SUCCESS!";
			}else {
				res="THERE WAS AN ERROR!";
			}
		}
		return Response.ok().entity(res).build();
	}
	@POST
	@Path("/op9")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doSpy(LoginData data) {
		//email -> token
		//password -> other user email
		UserInfo u = StorageMethods.getOtherUser(Constants.datastore, data);
		return Response.ok().entity(g.toJson(u)).build();
	}
	
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
	}
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
		return Response.ok().entity(email).build();
	}
}
