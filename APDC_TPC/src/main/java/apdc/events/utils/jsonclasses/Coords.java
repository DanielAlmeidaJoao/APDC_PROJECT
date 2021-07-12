package apdc.events.utils.jsonclasses;

public class Coords {
	double lat, lng;
	public Coords() {}
	public Coords(double lat, double lng) {
		this.lat=lat;
		this.lng=lng;
	}

	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
}
