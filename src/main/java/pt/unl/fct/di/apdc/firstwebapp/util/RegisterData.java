package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {
	public String username;
	public String password;
	public String confirmation;
	public String email;
	public String name;
	public String telephone;
	public String visibility;
	public String ocupation;
	public String workplace;
	public String address;
	public String postalCode;
	public String taxIdentification;
	public RegisterData() {
		
	}
	public RegisterData(String username, String password, String confirmation, String email, 
			String name, String telephone, String visibility, String ocupation, String workplace, String address,
			String postalCode, String taxIdentification) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.telephone = telephone;
		this.visibility = visibility;
		this.ocupation = ocupation;
		this.workplace = workplace;
		this.address = address;
		this.postalCode = postalCode;
		this.taxIdentification = taxIdentification;
		this.password = password;
		this.confirmation = confirmation;
	}
	public boolean dataNull() {
		return username == null || password == null;
	}

	public boolean dataEmpty() {
		return username.equals("") || password.equals("");

	}
}
