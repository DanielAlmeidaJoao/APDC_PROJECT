package apdc.tpc.utils;

public class ProfileResponse extends AdditionalAttributes {

	String  profilePicture, name, email;
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	boolean viewingOwnProfile;

	public ProfileResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public String getName() {
		return name;
	}

	public boolean isViewingOwnProfile() {
		return viewingOwnProfile;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setViewingOwnProfile(boolean viewingOwnProfile) {
		this.viewingOwnProfile = viewingOwnProfile;
	}

}
