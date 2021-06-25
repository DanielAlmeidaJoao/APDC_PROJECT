package apdc.tpc.utils;

public class AdditionalAttributes {

    public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMore_address() {
		return more_address;
	}

	public void setMore_address(String more_address) {
		this.more_address = more_address;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	String perfil, telephone, cellphone, address, more_address, locality;
	long events;
    
    public long getEvents() {
		return events;
	}

	public void setEvents(long events) {
		this.events = events;
	}

	public AdditionalAttributes(){
    	perfil="";
    	telephone="";
    	cellphone="";
    	address="";
    	more_address="";
    	locality="";
    }
}
