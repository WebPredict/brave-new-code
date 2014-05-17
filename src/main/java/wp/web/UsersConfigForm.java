package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="userConfigsForm"
 * @author jeff
 *
 */
public class UsersConfigForm extends ActionForm {

	private String	userConfigId;

	public String getUserConfigId() {
		return userConfigId;
	}

	public void setUserConfigId(String userConfigId) {
		this.userConfigId = userConfigId;
	}


}
