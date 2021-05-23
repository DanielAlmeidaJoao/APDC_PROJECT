package apdc.tpc.utils;

public class draft {

	public draft() {
		// TODO Auto-generated constructor stub
	}

	/*
	public static UserInfo getOtherUser(Datastore datastore,LoginData data) {
		//data.email -> token
		//data.password -> other user email
		UserInfo u = new UserInfo();
		try {
			String loggedUser = null;// HandleTokens.validateToken(data.getEmail());
			if(loggedUser==null) {
				u.setStatus("-1");//MUST LOGIN AGAIN
				return u;
			}
			com.google.cloud.datastore.Key userKey =datastore.newKeyFactory().setKind("Users").newKey(loggedUser);		

			Entity person=datastore.get(userKey);
			if(person!=null) {
				if(GBO.equals(person.getString(ROLE_PROP))) {
					com.google.cloud.datastore.Key userKey2 =datastore.newKeyFactory().setKind("Users").newKey(data.getPassword());
					person = datastore.get(userKey2);
					if(person!=null) {
						u.setEmail(data.getPassword());
						u.setName(person.getString(NAME_PROPERTY));
						u.setRole(person.getString(ROLE_PROP));
						u.setState(person.getString(STATE_PROP));
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
	} */
	/*
	private static boolean canChangeRole(Entity e,Entity victim,ChangeOtherUser data) {
		if(! (GA.equals(e.getString(ROLE_PROP))||SU.equals(e.getString(ROLE_PROP)))){
			return false;
		}
		if(! ( USER.equals(victim.getString(ROLE_PROP))&&GBO.equalsIgnoreCase(data.getAttributeValue()))) {
			return false;
		}
		return true;
	}*/
	/*
	private static boolean canChangeState(Entity e,Entity victim,ChangeOtherUser data) {
		if(USER.equalsIgnoreCase(victim.getString(ROLE_PROP))&&(GA.equals(e.getString(ROLE_PROP))||SU.equals(e.getString(ROLE_PROP))||GBO.equals(e.getString(ROLE_PROP)))){
			return true;
		}else if(GBO.equalsIgnoreCase(victim.getString(ROLE_PROP))&&(GA.equals(e.getString(ROLE_PROP))||SU.equals(e.getString(ROLE_PROP)))) {
			return true;
		}else if(GA.equalsIgnoreCase(victim.getString(ROLE_PROP))&&SU.equals(e.getString(ROLE_PROP))) {
			return true;
		}
		return false;
	}*/
	/*
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
	}*/
	/*
	private static LoginData attributesToChange(Entity e,Entity victim,ChangeOtherUser data) {
		LoginData dt = new LoginData();
		dt.setEmail(victim.getString(ROLE_PROP));
		dt.setPassword(victim.getString(STATE_PROP));
		LOG.severe("OK -- 1"+dt.getEmail());
		LOG.severe("OK -- 2"+dt.getPassword());
		LOG.severe("OK == "+e.getString(STATE_PROP));
		LOG.severe("OK == "+victim.getString(STATE_PROP));
		LOG.severe("OK ==+ "+e.getString(ROLE_PROP));
		LOG.severe("OK ==+ "+victim.getString(ROLE_PROP));

		if(!validAttribute(data.getAttributeValue())) {
			LOG.severe("OK -- 3"+data.getAttributeValue());
			return null;
		}else if(ROLE_PROP.equalsIgnoreCase(data.getAttribute())||STATE_PROP.equalsIgnoreCase(data.getAttribute())) {
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
	}*/
	/*
	public static String disableUser(Datastore datastore,ChangeOtherUser data) {
		//data.email -> token
		//data.password -> password
		//data.name -> the email of another user
		Transaction txn=null;
		String result="-1";
		try {
			String loggedUser = null;// HandleTokens.validateToken(data.getToken());
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
			if(person!=null&&ENABLED.equalsIgnoreCase(person.getString(STATE_PROP))) {
				LOG.severe("OK-3");
				if(data.getPassword().equals(person.getString(PASSWORD))&&GA.equals(person.getString(ROLE_PROP))) {
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
									.set(NAME_PROPERTY,victim.getString(NAME_PROPERTY))
									.set(ROLE_PROP,dt.getEmail())
									.set(STATE_PROP,dt.getPassword())
									.build();
							txn.update(victim);
						    txn.commit();
						    if(dt.getEmail().equalsIgnoreCase(victim.getString(ROLE_PROP))) {
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
			rollBack(txn);
		}
		return result;
	} */
}
