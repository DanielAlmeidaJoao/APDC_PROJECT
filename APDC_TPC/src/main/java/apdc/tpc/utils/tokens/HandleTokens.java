package apdc.tpc.utils.tokens;

import java.io.UnsupportedEncodingException;

import java.util.Date;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import apdc.utils.conts.DatastoreConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class HandleTokens {
	private static final String SECRET = "secret";

	private static final long THIRTY_MINUTES=30*60000;

	private static final String USERID_CLAIM_PROP="userid";
	public HandleTokens() {
		// TODO Auto-generated constructor stub
	}

	public static String generateToken(long userid) {
		Date date = new Date(System.currentTimeMillis()+(THIRTY_MINUTES));
		String jwt=null;
		try {
			jwt = Jwts.builder()
					  .setSubject("users/TzMUocMF4p")
					  .setExpiration(date)
					  .claim(USERID_CLAIM_PROP,userid)
					  .signWith(
					    SignatureAlgorithm.HS256,
					    SECRET.getBytes("UTF-8")
					  )
					  .compact();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return jwt;
	}
	private static Claims getClaims(String token) throws Exception{
		Claims claims=null;
		claims = Jwts.parser()
				  .setSigningKey(SECRET.getBytes("UTF-8")) //a strong key must come here
				  .parseClaimsJws(token).getBody();
		return claims;
	}
	public static long validateToken(String token) throws Exception {
		long userid = (long) getClaims(token).get(USERID_CLAIM_PROP);
		//assertEquals(scope, "self groups/admins");
		return userid;
	}
	
	public static void destroyToken(String token) {
		try {
			Claims claims = getClaims(token);
			claims.clear();
			claims.setExpiration(null);
		}catch(Exception e) {
			
		}

	}
	
	public static NewCookie makeCookie(String name,String value,String domain, int expirationTime) {
		//Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly
		Cookie ck = new Cookie(name, value, "/", domain);
		NewCookie nk = new NewCookie(ck,null,expirationTime,null,true,true);
		return nk;
	}
	public static NewCookie makeCookie(String name,String value,String domain) {
		//Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly
		Cookie ck = new Cookie(name, value, "/", domain);
		NewCookie nk = new NewCookie(ck,null,DatastoreConstants.getSessionTime(),null,true,true);
		return nk;
	}
	public static NewCookie destroyCookie(String name) {
		//Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly
		Cookie ck = new Cookie(name,null, "/", null);
		NewCookie nk = new NewCookie(ck,null,-1,null,false,false);
		return nk;
	}
	
	public static void main(String [] args) {
		/*
		String token = generateToken("daniel");
		System.out.println(token);
		System.out.println("Going to sleep...");
		System.out.println("Alive!");
		System.out.println(validateToken(token));
		destroyToken(token);
		System.out.println("DESTROYED!");
		System.out.println(validateToken(token)); */
	}
}
