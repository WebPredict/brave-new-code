package wp.web;

import org.apache.struts.validator.ValidatorForm;

/**
 * @struts.form name="advertiserSignupForm"
 * @author Jeff
 *
 */
public class AdvertiserSignupForm extends ValidatorForm {

	private String	newusername;
	private String  password;
	private String	password2;
	private String  emailAddr;
	private String	jcaptcha_response;

	public String getPassword2() {
		return password2;
	}
	public void setPassword2(String password2) {
		this.password2 = password2;
	}
	public String getNewusername() {
		return newusername;
	}
	public void setNewusername(String newusername) {
		this.newusername = newusername;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailAddr() {
		return emailAddr;
	}

	public String getJcaptcha_response() {
		return jcaptcha_response;
	}
	public void setJcaptcha_response(String jcaptcha_response) {
		this.jcaptcha_response = jcaptcha_response;
	}
	/**
	 * @struts.validator type="required,email"
	 * @struts.validator-args arg0resource="email"
	 */
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

}
