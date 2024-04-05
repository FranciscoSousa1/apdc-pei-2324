package pt.unl.fct.di.apdc.firstwebapp.util;

public class UserPasswordChangerData {
	
	public String password;
	public String newPassword;
	public String confirmation;
	
	public UserPasswordChangerData() {
		
	}
	
	public UserPasswordChangerData(String password, String newPassword, String confirmation) {
		this.password = password;
		this.newPassword = newPassword;
		this.confirmation = confirmation;
	}
	public boolean dataNull() {
		return newPassword == null || password == null;
	}

	public boolean dataEmpty() {
		return newPassword.equals("") || password.equals("");

	}
}
