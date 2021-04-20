package apdc.tpc.utils;

public class LoggedObject {

	private String status, email, name, token, additionalAttributes;
	private boolean gbo;
	
	public boolean isGbo() {
		return gbo;
	}
	public void setGbo(boolean gbo) {
		this.gbo = gbo;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAdditionalAttributes() {
		return additionalAttributes;
	}
	public void setAdditionalAttributes(String additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}
	public LoggedObject() {
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


}
