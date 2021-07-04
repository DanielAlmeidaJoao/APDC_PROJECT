package apdc.events.utils.jsonclasses;

public class ChangePasswordArgs {

	String email, password, vcode;
	
	public String getVcode() {
		return vcode;
	}
	public ChangePasswordArgs() {
		// TODO Auto-generated constructor stub
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
}
