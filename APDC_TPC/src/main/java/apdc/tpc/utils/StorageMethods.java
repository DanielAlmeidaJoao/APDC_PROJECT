package apdc.tpc.utils;

import java.util.logging.Logger;





import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import apdc.tpc.utils.tokens.HandleTokens;
import apdc.utils.conts.Constants;

public class StorageMethods {

	private static final String PASSWORD = "password";

	private static final String STATE = "state";
	//ROLES
	private static final String ROLE = "role";

	private static final String GBO = "GBO";
	private static final String USER = "USER";
	private static final String GA = "GA";
	private static final String SU = "SU";
	
	private static final String ENABLED = "ENABLED";
	private static final String DISABLED = "DISABLED";


	//private static final String GBO = "GBO";
	//private static final String GBO = "GBO";

	
	private static final String ADDITIONALS = "ADDITIONALS_INFO";
	private static final String PERFIL = "PERFIL";
	private static final String LOCALIDADE = "LOCALIDADE";
	private static final String MORADA_COMPLEMENTAR = "MORADA_COMPLEMENTAR";
	private static final String MORADA = "MORADA";
	private static final String TELEFONE = "TELEFONE";
	private static final String TELEMOVEL = "TELEMOVEL";
	private static final Logger LOG = Logger.getLogger(StorageMethods.class.getName());

	public static UserInfo getOtherUser(Datastore datastore,LoginData data) {
		//data.email -> token
		//data.password -> other user email
		UserInfo u = new UserInfo();
		try {
			String loggedUser = HandleTokens.validateToken(data.getEmail());
			if(loggedUser==null) {
				u.setStatus("-1");//MUST LOGIN AGAIN
				return u;
			}
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(loggedUser);		

			Entity person=datastore.get(userKey);
			if(person!=null) {
				if(GBO.equals(person.getString(ROLE))) {
					com.google.cloud.datastore.Key userKey2 =datastore.newKeyFactory().setKind("Users").newKey(data.getPassword());
					person = datastore.get(userKey2);
					if(person!=null) {
						u.setEmail(data.getPassword());
						u.setName(person.getString(Constants.NAME_PROPERTY));
						u.setRole(person.getString(ROLE));
						u.setState(person.getString(STATE));
						u.setStatus("1");
					}else {
						u.setStatus("-4"); //USER DOES NOT EXIST
					}
				}else {
					u.setStatus("-3"); //NOT ALLOWED

				}
			}else {
				u.setStatus("-2"); //AN UNEXPECTED ERROR
			}
		}catch (Exception e) {
			u.setStatus("-5");
		}
		
		return u;
	}
	private static boolean canChangeRole(Entity e,Entity victim,ChangeOtherUser data) {
		if(! (GA.equals(e.getString(ROLE))||SU.equals(e.getString(ROLE)))){
			return false;
		}
		if(! ( USER.equals(victim.getString(ROLE))&&GBO.equalsIgnoreCase(data.getAttributeValue()))) {
			return false;
		}
		return true;
	}
	private static boolean canChangeState(Entity e,Entity victim,ChangeOtherUser data) {
		if(USER.equalsIgnoreCase(victim.getString(ROLE))&&(GA.equals(e.getString(ROLE))||SU.equals(e.getString(ROLE))||GBO.equals(e.getString(ROLE)))){
			return true;
		}else if(GBO.equalsIgnoreCase(victim.getString(ROLE))&&(GA.equals(e.getString(ROLE))||SU.equals(e.getString(ROLE)))) {
			return true;
		}else if(GA.equalsIgnoreCase(victim.getString(ROLE))&&SU.equals(e.getString(ROLE))) {
			return true;
		}
		return false;
	}
	private static boolean validAttribute(String value) {
		if(ENABLED.equalsIgnoreCase(value)) {
			return true;
		}else if(DISABLED.equalsIgnoreCase(value)) {
			return true;
		}else if(SU.equalsIgnoreCase(value)) {
			return true;
		}else if(GBO.equalsIgnoreCase(value)) {
			return true;
		}else if(GA.equalsIgnoreCase(value)) {
			return true;
		}else if(USER.equalsIgnoreCase(value)) {
			return true;
		}
		return false;
	}
	private static LoginData attributesToChange(Entity e,Entity victim,ChangeOtherUser data) {
		LoginData dt = new LoginData();
		dt.setEmail(victim.getString(ROLE));
		dt.setPassword(victim.getString(STATE));
		LOG.severe("OK -- 1"+dt.getEmail());
		LOG.severe("OK -- 2"+dt.getPassword());
		LOG.severe("OK == "+e.getString(STATE));
		LOG.severe("OK == "+victim.getString(STATE));
		LOG.severe("OK ==+ "+e.getString(ROLE));
		LOG.severe("OK ==+ "+victim.getString(ROLE));

		if(!validAttribute(data.getAttributeValue())) {
			LOG.severe("OK -- 3"+data.getAttributeValue());
			return null;
		}else if(ROLE.equalsIgnoreCase(data.getAttribute())||STATE.equalsIgnoreCase(data.getAttribute())) {
			LOG.severe("OK -- 4"+data.getAttributeValue());
			if(canChangeRole(e,victim,data)) {
				LOG.severe("OK -- 5"+data.getAttributeValue());
				dt.setEmail(data.getAttributeValue());
			}else if(canChangeState(e,victim,data)) {
				LOG.severe("OK -- 6"+data.getAttributeValue());
				dt.setPassword(data.getAttributeValue());
			}else {
				LOG.severe("OK -- 7"+data.getAttributeValue());
				dt=null;
			}
		}else {
			dt=null;
		}
		return dt;
	}
	public static String disableUser(Datastore datastore,ChangeOtherUser data) {
		//data.email -> token
		//data.password -> password
		//data.name -> the email of another user
		Transaction txn=null;
		String result="-1";
		try {
			String loggedUser = HandleTokens.validateToken(data.getToken());
			LOG.severe("OK-1");
			if(loggedUser==null) {
				return result;
			}
			LOG.severe("OK-2");
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(loggedUser);		
			txn = datastore.newTransaction();
			Entity person=txn.get(userKey);
			Entity victim;
			LOG.severe("OK-2");
			if(person!=null&&ENABLED.equalsIgnoreCase(person.getString(STATE))) {
				LOG.severe("OK-3");
				if(data.getPassword().equals(person.getString(PASSWORD))&&GA.equals(person.getString(ROLE))) {
					LOG.severe("OK-4");
					com.google.cloud.datastore.Key userKey2 =datastore.newKeyFactory().setKind("Users").newKey(data.getEmail());
					LOG.severe("OK-5");
					if(userKey2!=null) {
						LOG.severe("OK-6");
						victim = txn.get(userKey2);
						LOG.severe("OK-7");
						LoginData dt = attributesToChange(person,victim,data);
						LOG.severe("OK-8");
						if(dt!=null) {
							LOG.severe("OK-9");
							victim = Entity.newBuilder(userKey2)
									.set(PASSWORD,victim.getString(PASSWORD))
									.set(Constants.NAME_PROPERTY,victim.getString(Constants.NAME_PROPERTY))
									.set(ROLE,dt.getEmail())
									.set(STATE,dt.getPassword())
									.build();
							txn.update(victim);
						    txn.commit();
						    if(dt.getEmail().equalsIgnoreCase(victim.getString(ROLE))) {
							    result="1";
						    }else {
						    	result="2";
						    }
						}else {
						    result="-33";
						}
					}else {
						result="-4";
						 //USER DOES NOT EXIST
					}
				}else {
					//result="-3"; //NOT ALLOWED
				    result="-333";
				}
			}else {
				result="-2"; //AN UNEXPECTED ERROR
			}
		}catch (Exception e) {
			result="-5";
		} finally {
			rollBack(txn);
		}
		return result;
	}
	public static Entity getUser(Datastore datastore,LoginData data) {
		Entity person=null;
		try {
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(data.getEmail());
			person = datastore.get(userKey);
			if(data.getPassword().equals(person.getString(PASSWORD))&&("ENABLED".equals(person.getString(STATE)))){
				return person;
			}else {
				return null;
			}
		}catch(Exception e) {
			
		}
		return person;
	}
	public static LoggedObject addUser(Datastore datastore, RegisterData data, Gson g) {
		Transaction txn =null;
		LoggedObject lo = new LoggedObject();
		lo.setStatus("-1");
		  try {
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(data.getEmail());
			Entity person = datastore.get(userKey);
			if(person==null) {
				txn = datastore.newTransaction();
			    person = Entity.newBuilder(userKey)
						.set(PASSWORD,data.getPassword())
						.set(Constants.NAME_PROPERTY,data.getName())
						.set(ROLE,USER)
						.set(STATE,ENABLED)
						.build();
				txn.put(person);
			    txn.commit();
			    AdditionalAttributes ad = new AdditionalAttributes();
			    ad.setEmail(data.getEmail());
			    ad.setPerfil("PRIVADO");
			    addUserAdditionalInformation(datastore,ad);
				
				lo.setEmail(data.getEmail());
				lo.setName(data.getName());
				lo.setStatus("1");
				lo.setAdditionalAttributes(g.toJson(ad));
			}else {
				lo.setStatus("-2");
			}
		  }catch(Exception e) {
				LOG.severe(e.getLocalizedMessage());
		  }finally {
			  rollBack(txn);
		  }
		  
		  return lo;
	}
	public static AdditionalAttributes getAdditionalAttributes(Datastore datastore, String email) {
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADDITIONALS).newKey(email);
		  Entity stats=datastore.get(ctrsKey);
		  AdditionalAttributes ad=null;
		  if(stats!=null) {
			  ad = new AdditionalAttributes(stats.getString(PERFIL),stats.getString(TELEFONE),stats.getString(TELEMOVEL),stats.getString(MORADA),stats.getString(MORADA_COMPLEMENTAR),stats.getString(LOCALIDADE));
		  }
		return ad;
	}
	public static int addUserAdditionalInformation(Datastore datastore, AdditionalAttributes ad) {
		int result=-1;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADDITIONALS).newKey(ad.getEmail());
		
		Transaction txn = datastore.newTransaction();
		  try {
			Entity stats;
			stats=Entity.newBuilder(ctrsKey)
					.set(PERFIL,ad.getPerfil())
					.set(TELEFONE,ad.getTelefone())
					.set(TELEMOVEL,ad.getTelemovel())
					.set(MORADA,ad.getMorada())
					.set(MORADA_COMPLEMENTAR,ad.getMorada_complementar())
					.set(LOCALIDADE,ad.getLocalidade())
					.build();
			txn.put(stats);
		    txn.commit();
		    result=1;
		  }catch(Exception e) {
			LOG.severe(e.getLocalizedMessage());
		  } finally {
			  rollBack(txn);
		  }
		  return result;
	}
	public static void rollBack(Transaction txn) {
		try {
			if (txn!=null&&txn.isActive()) {
			      txn.rollback();
			   }		
		}catch (Exception e) {
			
		}
	}
	public static int removeUser(Datastore datastore,LoginData data) {
		int result=-1;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind("Users").newKey(data.getEmail());
		
		Transaction txn = datastore.newTransaction();

		  try {
			Entity person = txn.get(ctrsKey);
			if(!(person!=null&&isEnabled(person))) {
				return result;
			}
			if(data.getPassword().equals(person.getString(PASSWORD))) {
				com.google.cloud.datastore.Key additionalInfo=datastore.newKeyFactory().setKind(ADDITIONALS).newKey(data.getEmail());

				txn.delete(ctrsKey,additionalInfo);
			    txn.commit();
			    result=1;
			}else {
				result=-2;
			}
			
		  }catch(Exception e) {
			LOG.severe(e.getLocalizedMessage());
		  } finally {
			  if(txn!=null) {
				  rollBack(txn);
			  }
		  }
		  return result;
	}
	
	public static String updatePassword(Datastore datastore, String email, String newPassword, String oldPass) {
		Transaction txn =null;
		String result="-1";
		  try {
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(email);
			txn = datastore.newTransaction();
			Entity person = txn.get(userKey);
			if(person==null) {
				return result;
			}
			if(!oldPass.equals(person.getString(PASSWORD))) {
				return "-2";
			}
			if(isEnabled(person)&&oldPass.equals(person.getString(PASSWORD))) {
			    person = Entity.newBuilder(userKey)
						.set(PASSWORD,newPassword)
						.set(Constants.NAME_PROPERTY,person.getString(Constants.NAME_PROPERTY))
						.set(ROLE,person.getString(ROLE))
						.set(STATE,person.getString(STATE))
						.build();
				txn.update(person);
			    txn.commit();
			    result="1";
			}
		  }catch(Exception e) {
		  }finally {
			  rollBack(txn);
		  }
		  return result;
	}
	private static boolean isEnabled(Entity person) {
		try {
			if(ENABLED.equalsIgnoreCase(person.getString(STATE))) {
				return true;
			}
		}catch(Exception e) {
			
		}
		return false;
	}
}
