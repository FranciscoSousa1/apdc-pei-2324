package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusData {
	public String username;
	public String state;
	
	public StatusData() {
		
	}
	
	public StatusData(String username, String state) {
		this.username = username;
		this.state = state;
	}
	public boolean dataNull() {
		return username == null || state == null;
	}

	public boolean dataEmpty() {
		return username.isEmpty() || state.isEmpty();

	}
}
