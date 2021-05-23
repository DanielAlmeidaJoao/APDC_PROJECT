package apdc.events.utils.superuser;

public class FrontEndUserObject {

	/*
	 * NAME
		EMAIL
		USERID
		ROLE
		STATE
	 */
	public String name, email, role, state;
	long userid;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public FrontEndUserObject() {
		// TODO Auto-generated constructor stub
	}
	public FrontEndUserObject(String name, String email, long userid, String role, String state) {
		setName(name);
		setEmail(email);
		setUserid(userid);
		setRole(role);
		setState(state);
	}

}
