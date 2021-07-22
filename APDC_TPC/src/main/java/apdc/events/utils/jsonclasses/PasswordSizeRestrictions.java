package apdc.events.utils.jsonclasses;

public class PasswordSizeRestrictions {

	int minPasswordSize, maxPasswordSize;
	public void setMinPasswordSize(int minPasswordSize) {
		this.minPasswordSize = minPasswordSize;
	}
	public void setMaxPasswordSize(int maxPasswordSize) {
		this.maxPasswordSize = maxPasswordSize;
	}
	public int getMinPasswordSize() {
		return minPasswordSize;
	}
	public int getMaxPasswordSize() {
		return maxPasswordSize;
	}
	public PasswordSizeRestrictions() {
		// TODO Auto-generated constructor stub
	}

}
