package apdc.tpc.resources;

import java.io.File;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.google.cloud.datastore.Datastore;
import apdc.events.utils.EventsDatabaseManagement;
import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

@Path("/events")
public class EventsResources {
	//private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());
	@Context
	private HttpServletRequest httpRequest;
	private static final Logger LOG = Logger.getLogger(EventsResources.class.getName());

	public EventsResources() {
	}
	private static String getPartString(HttpServletRequest httpRequest) {
		try {
			Part p = httpRequest.getPart("evd");
			byte [] b = new byte[(int) p.getSize()];
			InputStream is = p.getInputStream();
			is.read(b);
			String h = new String(b);
			return h;
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * creates a new event
	 * @param data JSON object with the information to create a new event
	 * @return a response object with the creation status inside
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doCreateEvent(@CookieParam(Constants.COOKIE_TOKEN) String value){
		Response response;
		Datastore ds = Constants.datastore;
		try {
			//String email = HandleTokens.validateToken(value);
			String email ="daniel";
			String result = EventsDatabaseManagement.createEvent(ds,httpRequest,email);
			response = Response.ok().entity(Constants.g.toJson(result)).build();
		}catch(Exception e) {
			response = Response.status(Status.FORBIDDEN).build();
		}
		return response;
	}
	@POST
	@Path("/images")
	@Consumes(MediaType.MULTIPART_FORM_DATA +";charset=utf-8")
	@Produces(MediaType.TEXT_PLAIN +";charset=utf-8")
	public Response doImages(){
		try {
			System.out.println("GOING TO SAVE IMAGS!");
			Part p = httpRequest.getPart("path");
			InputStream uploadedInputStream = httpRequest.getPart("file").getInputStream();
			System.out.println(p.toString());
			System.out.println(p.getContentType());
			byte [] b = new byte[(int) p.getSize()];
			p.getInputStream().read(b);
			String h = new String(b);
			System.out.println("I AM JEAN-CLAUD VAN DAMME!");
			System.out.println("I AM HELLO "+h);
			writeToFile( uploadedInputStream,h);
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		InputStream uploadedInputStream;
        FormDataContentDisposition fileDetail;
        String path;
		LOG.severe("PATH -------> "+path);
		System.out.println("I AM JEAN-CLAUD VAN DAMME!");
		System.out.println(fileDetail);
		writeToFile( uploadedInputStream,"daniel");*/
		return Response.status(200).entity("FENDER WILL NEVER HAVE THE INFORMATION!").build();
	}
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,String uploadedFileLocation) {

	    try {
	        OutputStream out;
	        int read = 0;
	        byte[] bytes = new byte[1024];
	        File f = new File("C:\\Users\\djoao\\Desktop\\"+uploadedFileLocation);
	        out =new FileOutputStream(f) ;
	        while ((read = uploadedInputStream.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        out.flush();
	        out.close();
	    } catch (IOException e) {

	        e.printStackTrace();
	    }


	   }
	/**
	 * loads all registered events 
	 * @param value offset value stored in the specified cookie
	 * @param token session token stored in the specified token
	 * @return 200 if the operation is success
	 */
	@GET
	@Path("/view")
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response doGetEvent(@CookieParam(Constants.GET_EVENT_CURSOR_CK) String value ,@CookieParam(Constants.COOKIE_TOKEN) Cookie token) {
		Response resp;
		String result [] =null;
		try {
			HandleTokens.validateToken(token.getValue());
			result = EventsDatabaseManagement.getEvents(value);
			NewCookie nk = HandleTokens.makeCookie(Constants.GET_EVENT_CURSOR_CK,result[1],token.getDomain());
			resp = Response.ok().cookie(nk).entity(result[0]).build();
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
	/**
	 * deletes a particular event 
	 * @param eventId the event to be deleted
	 * @param token the logged user token to verify if the user is has permission to do so
	 * @return
	 */
	@DELETE
	@Path("/delete/{eventId}")
	public Response doDeleteEvent(@PathParam(Constants.EVENT_ID) String eventId, @CookieParam(Constants.COOKIE_TOKEN) String token) {
		Response resp;
		LOG.severe("GOING TO REMOVE THIS EVENT "+eventId);
		resp = Response.ok().build();
		
		try {
			String email = HandleTokens.validateToken(token);
			EventsDatabaseManagement.deleteEvent(eventId,email);
			resp = Response.ok().build();
		}catch(Exception e) {
			resp = Response.status(Status.FORBIDDEN).build();
		}
		return resp;
	}
}
