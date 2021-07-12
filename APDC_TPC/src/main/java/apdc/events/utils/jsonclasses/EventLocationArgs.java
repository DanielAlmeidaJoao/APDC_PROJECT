package apdc.events.utils.jsonclasses;

public class EventLocationArgs {

	public String getName() {
		return name;
	}
	public String getPostal_code() {
		return postal_code;
	}
	public String getLocality() {
		return locality;
	}
	public String getCountry_name() {
		return country_name;
	}
	public Coords getLoc() {
		return loc;
	}
	public void setName(String name) {
		this.name = name;
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
	public void setLoc(Coords loc) {
		this.loc = loc;
	}
	String name, postal_code, locality, country_name;
	Coords loc;
	/**
	 * loc:{ lat: place.geometry.location.lat(), lng: place.geometry.location.lng() },
    name:getPlaceName(place),
    postal_code:postalCode.textContent,
    locality:locality.textContent,
    country_name:countryName.textContent
	 */
	public EventLocationArgs() {
		// TODO Auto-generated constructor stub
	}

}
