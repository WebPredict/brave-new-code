package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="lookupPasswordForm"
 * @author Jeff
 *
 */
public class LookupPasswordForm extends ActionForm {

	private String	emailAddr;
	private String	username;
	public String getEmailAddr() {
		return emailAddr;
	}
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
