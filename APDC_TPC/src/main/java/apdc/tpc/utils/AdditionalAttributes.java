package apdc.tpc.utils;

public class AdditionalAttributes {

    
	
	String bio, quote, instagram,facebook, twitter, website;
	long events, interestedEvents;
	public AdditionalAttributes(){
		bio="";
		quote="";
		instagram="";
		facebook="";
		twitter="";
		website="";
    }
    public long getEvents() {
		return events;
	}

	public long getInterestedEvents() {
		return interestedEvents;
	}

	public void setInterestedEvents(long interestedEvents) {
		this.interestedEvents = interestedEvents;
	}

	public void setEvents(long events) {
		this.events = events;
	}



	public String getBio() {
		return bio;
	}

	public String getQuote() {
		return quote;
	}

	public String getInstagram() {
		return instagram;
	}

	public String getFacebook() {
		return facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public String getWebsite() {
		return website;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public void setInstagram(String instagram) {
		this.instagram = instagram;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
}
