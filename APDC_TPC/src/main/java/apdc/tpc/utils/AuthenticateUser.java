package apdc.tpc.utils;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Datastore;

public class AuthenticateUser {

	private static final String EMAIL = "email";
	private static final String EXPIRATION_DATE = "expiration_date";
	private static final String TOKENS = "TOKENS";
	public AuthenticateUser() {
		// TODO Auto-generated constructor stub
	}

	public static int insertToken(Datastore d, AuthToken tk) {
		com.google.cloud.datastore.Key ctrsKey=d.newKeyFactory().setKind(TOKENS).newKey(tk.tokenID);
		int result=-1;
		Transaction txn = d.newTransaction();
		  try {
			Entity stats;
			stats=Entity.newBuilder(ctrsKey)
					.set(EMAIL,tk.username)
					.set(EXPIRATION_DATE,tk.expirationData)
					.build();
			txn.put(stats);
		    txn.commit();
		    result=1;
		  }catch(Exception e) {
		  } finally {
			  if(txn.isActive()) {
				  txn.rollback();
			  }
		  }
		  return result;
	}
	public static String validateToken(Datastore d,String token) {
		com.google.cloud.datastore.Key ctrsKey=d.newKeyFactory().setKind(TOKENS).newKey(token);
		long now;
		long experiationTime;
		String email=null;
		Entity tok = d.get(ctrsKey);
		if(tok!=null) {
			now = System.currentTimeMillis();
			experiationTime = tok.getLong(EXPIRATION_DATE);
			if(now>experiationTime) {
				removeToken(d,token);
			}else {
				email=tok.getString(EMAIL);
			}
		}
		return email;
	}
	
	public static int removeToken(Datastore d, String token) {
		int result=-1;
		com.google.cloud.datastore.Key ctrsKey=d.newKeyFactory().setKind(TOKENS).newKey(token);
		Transaction txn = d.newTransaction();
		  try {
			txn.delete(ctrsKey);
			txn.commit();
		    result=1;
		  }catch(Exception e) {
		  } finally {
			  try {
				  
			  }catch(Exception e) {
				  if(txn.isActive()) {
					  txn.rollback();
				  }
			  }
		  }
		  
		  return result;
	}
	public static int removeTokens(Datastore d, String email) {
		int result = -1;
		Transaction txn = d.newTransaction();
		  try {
			  Query<Entity> query = Query.newEntityQueryBuilder()
					    .setKind(TOKENS)
					    .setFilter(PropertyFilter.eq(EMAIL,email))
					    .build();
			  QueryResults<Entity> tasks = d.run(query);
			  while(tasks.hasNext()) {
				txn.delete(tasks.next().getKey());
			  }
			txn.commit();
		    result=1;
		  }catch(Exception e) {
		  } finally {
			  try {
				  
			  }catch(Exception e) {
				  if(txn.isActive()) {
					  txn.rollback();
				  }
			  }
		  }
		return result;
	}
}
