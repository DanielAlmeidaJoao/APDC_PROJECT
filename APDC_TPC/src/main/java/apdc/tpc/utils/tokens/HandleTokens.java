package apdc.tpc.utils.tokens;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class HandleTokens {
	private static final long THIRTY_MINUTES=30*60000;

	public HandleTokens() {
		// TODO Auto-generated constructor stub
	}

	public static String generateToken(String email) {
		Date date = new Date(System.currentTimeMillis()+(THIRTY_MINUTES/30));
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
	private static Claims getClaims(String token){
		Claims claims=null;
		try {
			claims = Jwts.parser()
			  .setSigningKey("secret".getBytes("UTF-8")) //a strong key must come here
			  .parseClaimsJws(token).getBody();
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
		}
		//assertEquals(scope, "self groups/admins");
		return claims;
	}
	public static String validateToken(String token) {
		String userid = (String) getClaims(token).get("userid");
		//assertEquals(scope, "self groups/admins");
		return userid;
	}
	
	public static void destroyToken(String token) {
		Claims claims = getClaims(token);
		claims.clear();
		claims.setExpiration(null);
	}
	
	public static void main(String [] args) {
		String token = generateToken("daniel");
		System.out.println(token);
		System.out.println("Going to sleep...");
		System.out.println("Alive!");
		System.out.println(validateToken(token));
		destroyToken(token);
		System.out.println("DESTROYED!");
		System.out.println(validateToken(token));
	}
}
