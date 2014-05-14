package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="adLoginForm"
 * @author Jeff
 *
 */
public class AdLoginForm extends ActionForm {

	private String	advertiserId;
	private String	password;
	public String getAdvertiserId() {
		return advertiserId;
	}
	public void setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
