package apdc.tpc.headerFilters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


public class CharsetFilter implements ContainerRequestFilter {
	@Context
	private HttpServletRequest httpRequest;
	public CharsetFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			MediaType contentType = requestContext.getMediaType();
	        requestContext.getHeaders().putSingle("Content-Type", contentType.toString() + ";charset=UTF-8");
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		// TODO Auto-generated method stub
		try {
			requestContext.getHeaders().add("Character-Encoding","utf-8");
			httpRequest.setCharacterEncoding("charset=utf-8");
			
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
		} 
		System.out.println("----------------------------------------------------------------------------- FILTERING "+httpRequest.getCharacterEncoding());
	}

	
}
