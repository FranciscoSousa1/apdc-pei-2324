package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.ArrayList;
import java.util.List;

public class ChangeAttributesData {
	public String usernameToChange;
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
	public String role;
	public String state;
	public ChangeAttributesData() {
		
	}
	public ChangeAttributesData(String password, String confirmation, String email, 
			String name, String telephone, String visibility, String ocupation, String workplace, String address,
			String postalCode, String taxIdentification, String role, String state, String usernameToChange) {
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
		this.role = role;
		this.state = state;
		this.usernameToChange = usernameToChange;
	}

	public static String dataValidation(ChangeAttributesData data) {
		List<String> results = new ArrayList<String>();
		results.add(dataNull(data.usernameToChange));
		results.add(dataEmpty(data.usernameToChange));
		if (!data.email.isEmpty() && data.email != null)
		{
			results.add(DataValidation.emailValidation(data.email));
		}
		if (!data.name.isEmpty() && data.name != null)
		{
			results.add(DataValidation.nameValidation(data.name));
		}
		if (!data.password.isEmpty() && data.password != null)
		{
			results.add(DataValidation.passwordValidation(data.password));
		}
		if (!data.telephone.isEmpty() && data.telephone != null)
		{
			results.add(DataValidation.telephoneValidation(data.telephone));
		}
		if (!data.postalCode.isEmpty() && data.postalCode != null)
		{
			results.add(DataValidation.postalCodeValidation(data.postalCode));
		}
		if (!data.taxIdentification.isEmpty() && data.taxIdentification != null)
		{
			results.add(DataValidation.taxIdentificationValidation(data.taxIdentification));
		}
		if (!data.address.isEmpty() && data.address != null)
		{
			results.add(DataValidation.addressValidation(data.address));
		}
		for (String result : results) {
			if (!result.contains(DataValidation.VALID)) {
				return result;
			}

		}
		return DataValidation.ALL_DATA_VALID;
	}
	static String dataNull(String username) {
		return username == null ? "Username is null" : "Data is valid";
	}

	static String dataEmpty(String username) {
		return username.equals("") ? "Username is empty" : "Data is valid";
	}
}
