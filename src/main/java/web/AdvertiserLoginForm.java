package wp.web;

import org.apache.struts.validator.ValidatorForm;

/**
 * @struts.form name="advertiserLoginForm"
 * @author Jeff
 *
 */
public class AdvertiserLoginForm extends ValidatorForm {

	private String	username;
	private String  password;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
