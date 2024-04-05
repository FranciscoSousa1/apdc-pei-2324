package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoleData {
	public String username;
	public String role;
	
	public RoleData() {
		
	}
	
	public RoleData(String username, String role) {
		this.username = username;
		this.role = role;
	}
	public static String passwordValidation(String password) {
		String regExpressionPassword = "[[a-zA-Z][0-9]+]+";
		if (!match(regExpressionPassword, password) || password.length() < 5) {
			return "Password must have at least one lower letter or one capital letter, and one number and have at least length 5.";
		} else
			return "Password is valid.";
	}
	public static boolean match(String expression, String data) {
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(data);
		return matcher.matches();
	}
	public boolean dataNull() {
		return username == null || role == null;
	}

	public boolean dataEmpty() {
		return username.equals("") || role.equals("");

	}
}
