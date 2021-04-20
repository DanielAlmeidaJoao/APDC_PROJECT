package apdc.tpc.utils;

public class UserInfo {

	private String name, email, status, role, state;
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public UserInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public UserInfo(String name, String email, String role,  String status, String state) {
		setName(name);
		setEmail(email);
		setStatus(status);
		setRole(role);
		setState(state);
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
