package apdc.tpc.utils.tokens;

import java.io.UnsupportedEncodingException;

import java.util.Date;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class HandleTokens {
	private static final long THIRTY_MINUTES=30*60000;

	public HandleTokens() {
		// TODO Auto-generated constructor stub
	}

	public static String generateToken(String email) {
		Date date = new Date(System.currentTimeMillis()+(THIRTY_MINUTES));
		String jwt=null;
		try {
			jwt = Jwts.builder()
					  .setSubject("users/TzMUocMF4p")
					  .setExpiration(date)
					  .claim("userid",email)
					  .signWith(
					    SignatureAlgorithm.HS256,
					    "secret".getBytes("UTF-8")
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
				  .setSigningKey("secret".getBytes("UTF-8")) //a strong key must come here
				  .parseClaimsJws(token).getBody();
		/*
		try {
			
		} catch (ExpiredJwtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedJwtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedJwtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//assertEquals(scope, "self groups/admins");
		return claims;
	}
	public static String validateToken(String token) throws Exception {
		String userid = (String) getClaims(token).get("userid");
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
	
	public static NewCookie makeCookie(String name,String value,String domain) {
		//Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly
		Cookie ck = new Cookie(name, value, "/", domain);
		NewCookie nk = new NewCookie(ck,null,-1,null,true,true);
		return nk;
	}
	public static NewCookie destroyCookie(String name) {
		//Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly
		Cookie ck = new Cookie(name,"", "/", null);
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
