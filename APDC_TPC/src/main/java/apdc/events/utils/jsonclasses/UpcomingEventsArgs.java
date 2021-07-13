package apdc.events.utils.jsonclasses;

public class UpcomingEventsArgs {

	String postal_code,locality, country_name;
	public String getPostal_code() {
		return postal_code;
	}
	public String getLocality() {
		return locality;
	}
	public String getCountry_name() {
		return country_name;
	}
	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}
	public UpcomingEventsArgs() {
		// TODO Auto-generated constructor stub
	}

}
