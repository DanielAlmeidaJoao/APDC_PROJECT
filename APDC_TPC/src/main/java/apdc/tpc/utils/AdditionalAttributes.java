package apdc.tpc.utils;

public class AdditionalAttributes {

    private String perfil, telefone, telemovel, morada, morada_complementar, localidade,email;
    
    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public AdditionalAttributes() {
		setPerfil("");
    	setTelefone("");
    	setTelemovel("");
    	setMorada("");
    	setMorada_complementar("");
    	setLocalidade("");
	}
    public AdditionalAttributes(String perfil, String telefone, String telemovel, String morada, String morada_complementar, String localidade) {
    	setPerfil(perfil);
    	setTelefone(telefone);
    	setTelemovel(telemovel);
    	setMorada(morada);
    	setMorada_complementar(morada_complementar);
    	setLocalidade(localidade);
    }
	public String getPerfil() {
		return perfil;
	}
	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getTelemovel() {
		return telemovel;
	}
	public void setTelemovel(String telemovel) {
		this.telemovel = telemovel;
	}
	public String getMorada() {
		return morada;
	}
	public void setMorada(String morada) {
		this.morada = morada;
	}
	public String getMorada_complementar() {
		return morada_complementar;
	}
	public void setMorada_complementar(String morada_complementar) {
		this.morada_complementar = morada_complementar;
	}
	public String getLocalidade() {
		return localidade;
	}
	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}
}
