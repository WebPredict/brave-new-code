package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="usersForm"
 * @author Jeff
 *
 */
public class UsersForm extends ActionForm {

	private String	userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
