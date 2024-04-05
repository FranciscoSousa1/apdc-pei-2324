package pt.unl.fct.di.apdc.firstwebapp.util;

public class RemoveData {
	public String username;
	public RemoveData() {
		
	}
	public RemoveData(String username) {
		this.username = username;
	}
	public boolean dataNull() {
		return username == null;
	}

	public boolean dataEmpty() {
		return username.equals("");

	}
}
