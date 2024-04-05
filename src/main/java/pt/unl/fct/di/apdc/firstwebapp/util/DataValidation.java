package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Cookie;

import pt.unl.fct.di.apdc.firstwebapp.Authentication.SignatureUtils;

public class DataValidation {
	public static final String USER = "USER";
	public static final String GBO = "GBO";
	public static final String GA = "GA";
	public static final String SU = "SU";
	public static final String key = "dhsjfhndkjvnjdsdjhfkjdsjfjhdskjhfkjsdhfhdkjhkfajkdkajfhdkmc";
	public static final String INACTIVE = "INACTIVE";
	public static final String ACTIVE = "ACTIVE";
	public static final String STATE = "state";
	public static final String PASSWORD = "password";
	
	public static final String USERNAME_EXPRESSION = "[a-z]+[0-9]+";
	public static final String USERNAME_NOT_VALID = "Username must not contain any special character and have a set of lower case characters and then"
			+ " a set of numbers.";
	public static final String USERNAME_VALID = "Username is valid.";
	
	public static final String EMAIL_EXPRESSION = "[A-Za-z]+[@][A-Za-z.]+[A-Za-z][.][a-z]+";
	public static final String EMAIL_NOT_VALID ="Email must be a set of capital and lower case letters, then the special character @, then letters that"
			+ "can be separated by an . (optional) and finally .<domain>";
	public static final String EMAIL_VALID = "Email is valid.";
	
	public static final String NAME_EXPRESSION = "[[A-Z][a-z]+[ ][A-Z][a-z]]+";
	public static final String NAME_NOT_VALID = "Name must be a Upper-case letter following by a set of lower-case letters and the names and the surnames must be separated"
			+ "by the name or the other surnames by having one space between them.";
	public static final String NAME_VALID = "Name is valid.";

	public static final String POSTAL_CODE_EXPRESSION = "[0-9]{4}[-][0-9]{3}";
	public static final String POSTAL_CODE_NOT_VALID = "Postal Code must be 4 numbers followed by an - and 3 other numbers.";
	public static final String POSTAL_CODE_VALID = "Postal Code is valid.";
	
	public static final String TAX_IDENTIFICATION_EXPRESSION = "[0-9]{9}";
	public static final String TAX_IDENTIFICATION_NOT_VALID = "Tax Identification must be 9 numbers.";
	public static final String TAX_IDENTIFICATION_VALID = "Tax Identification is valid.";
	
	public static final String PASSWORD_EXPRESSION = "[[a-zA-Z][0-9]+]+";
	public static final String PASSWORD_NOT_VALID = "Password must have at least one lower letter or one capital letter, and one number and have at least length 5.";
	public static final String PASSWORD_VALID = "Password is valid.";
	
	public static final String TELEPHONE_EXPRESSION = "[+][0-9]{1,3}[0-9]+";
	public static final String TELEPHONE_NOT_VALID =  "Telephone must be the international access code following by 9 numbers";
	public static final String TELEPHONE_VALID = "Telephone is valid.";
	
	public static final String ADDRESS_EXPRESSION = "[[[A-Z]?[a-z]+]?[ ][A-Z]?[a-z]+[ ]?]+";
	public static final String ADDRESS_NOT_VALID =  "Address must be [TODO]";
	public static final String ADDRESS_VALID = "Address is valid.";
	
	public static final String PASSWORD_MATCH = "Both passwords are valid and match.";
	
	public static final String PASSWORD_NOT_MATCH = "Passwords do not match.";
	
	public static final String VALID = "valid";

	public static final String ALL_DATA_VALID = "All data is valid.";
	
	public static final String ONE_OF_FIELDS_NULL = "One of the required fields are null.";
	
	public static final String ONE_OR_MORE_FIELDS_EMPTY = "One of the required fields are empty.";
	
	public static final String FIELDS_VALID = "Fields are valid.";
	
	public static final String ONE_OR_MORE_FIELDS_NULL = "One of the required fields are null.";
	
	public static String usernameValidation(String username) {
		String regExpressionUsername = USERNAME_EXPRESSION;
		Pattern pattern = Pattern.compile(regExpressionUsername);
		Matcher matcher = pattern.matcher(username);
		if (!matcher.matches()) {
			return USERNAME_NOT_VALID;
		} else
			return USERNAME_VALID;
	}

	public static String emailValidation(String email) {
		String regExpressionUsername = EMAIL_EXPRESSION;
		Pattern pattern = Pattern.compile(regExpressionUsername);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			return EMAIL_NOT_VALID;
		} else
			return EMAIL_VALID;
	}

	public static String nameValidation(String name) {
		String regExpressionName = NAME_EXPRESSION;
		Pattern pattern = Pattern.compile(regExpressionName);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.matches()) {
			return NAME_NOT_VALID;
		} else
			return NAME_VALID;
	}
	public static String postalCodeValidation(String postalCode) {
		String regExpressionpostalCode = POSTAL_CODE_EXPRESSION;
		Pattern pattern = Pattern.compile(regExpressionpostalCode);
		Matcher matcher = pattern.matcher(postalCode);
		if (postalCode != "")
		{
			if (!matcher.matches()) {
				return POSTAL_CODE_EXPRESSION;
			} else
				return POSTAL_CODE_NOT_VALID;
		}
		return POSTAL_CODE_VALID;
	}
	public static String taxIdentificationValidation(String taxIdentification) {
		String regExpressionTaxIdentification = TAX_IDENTIFICATION_EXPRESSION;
		Pattern pattern = Pattern.compile(regExpressionTaxIdentification);
		Matcher matcher = pattern.matcher(taxIdentification);
		if (taxIdentification != "" )
		{
			if (!matcher.matches()) {
				return TAX_IDENTIFICATION_NOT_VALID;
			}return TAX_IDENTIFICATION_VALID;
		}
		return TAX_IDENTIFICATION_VALID;
	}

	public static String passwordValidation(String password) {
		String regExpressionPassword = PASSWORD_EXPRESSION;
		if (!match(regExpressionPassword, password) || password.length() < 5) {
			return PASSWORD_NOT_VALID;
		} else
			return PASSWORD_VALID;
	}

	public static String telephoneValidation(String telephone) {
		String regExpressionTelephone = TELEPHONE_EXPRESSION;
		if (!match(regExpressionTelephone, telephone) || telephone.length() < 11 || telephone.length() > 13) {
			return TELEPHONE_NOT_VALID;
		} else
			return TELEPHONE_VALID;
	}
	
	public static String addressValidation(String address) {
		//TODO address
		/*String regExpressionAddress = ADDRESS_EXPRESSION;
		if (!match(regExpressionAddress, address)) {
			return ADDRESS_NOT_VALID;
		} else*/
			return ADDRESS_VALID;
	}

	public static String passwordMatch(RegisterData data) {
		return data.confirmation.equals(data.password) ? PASSWORD_MATCH : PASSWORD_NOT_MATCH;
	}

	public static boolean match(String expression, String data) {
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(data);
		return matcher.matches();
	}

	public static String dataValidation(RegisterData data) {
		List<String> results = new ArrayList<String>();
		results.add(dataNull(data));
		results.add(dataEmpty(data));
		results.add(usernameValidation(data.username));
		results.add(emailValidation(data.email));
		results.add(nameValidation(data.name));
		results.add(passwordValidation(data.password));
		results.add(telephoneValidation(data.telephone));
		if (!data.postalCode.isEmpty())
		{
			results.add(postalCodeValidation(data.postalCode));
		}
		if (!data.taxIdentification.isEmpty())
		{
			results.add(taxIdentificationValidation(data.taxIdentification));
		}
		if (!data.address.isEmpty())
		{
			results.add(addressValidation(data.address));
		}
		for (String result : results) {
			if (!result.contains(VALID)) {
				return result;
			}

		}
		return ALL_DATA_VALID;
	}

	public static String dataNull(RegisterData data) {
		if (data.email == null || data.username == null || data.name == null || data.password == null
				|| data.confirmation == null || data.telephone == null) {
			return ONE_OR_MORE_FIELDS_NULL;
		}
		return FIELDS_VALID;
	}

	public static String dataEmpty(RegisterData data) {
		if (data.username.isEmpty() || data.email.isEmpty() || data.name.isEmpty() || data.password.isEmpty()
				|| data.confirmation.isEmpty() || data.telephone.isEmpty()) {
			return ONE_OR_MORE_FIELDS_EMPTY;
		} else
			return FIELDS_VALID;
	}

	public static String dataEmpty(LoginData data) {
		if (data.username.isEmpty() || data.password.isEmpty()) {
			return  ONE_OR_MORE_FIELDS_EMPTY;
		} else
			return FIELDS_VALID;
	}
	public static boolean checkPermissions(Cookie cookie, String databaseRole, String role) {
		if (cookie == null || cookie.getValue() == null) {
			return false;
		}

		String value = cookie.getValue();
		String[] values = value.split("\\.");

		String signatureNew = SignatureUtils.calculateHMac(key,
				values[0] + "." + values[1] + "." + values[2] + "." + values[3] + "." + values[4]);
		String signatureOld = values[5];

		if (!signatureNew.equals(signatureOld)) {
			return false;
		}

		int neededRole = convertRole(role);
		int userInSessionRole = convertRole(databaseRole);

		if (userInSessionRole < neededRole) {
			return false;
		}

		if (System.currentTimeMillis() > (Long.valueOf(values[3]) + Long.valueOf(values[4]) * 1000)) {

			return false;
		}

		return true;
	}

	public static int convertRole(String role) {
		int result = 0;

		switch (role) {
		case USER:
			result = 0;
			break;
		case GBO:
			result = 1;
			break;
		case GA:
			result = 2;
			break;
		case SU:
			result = 3;
			break;
		}
		return result;
	}
}
