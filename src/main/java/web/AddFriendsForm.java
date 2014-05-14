package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="addFriendsForm"
 * @author Jeff
 *
 */
public class AddFriendsForm extends ActionForm {

	private String	filter;
	private String	userId;
	private String	removeUserId;
	private String	addUserId;
	
	public String getRemoveUserId() {
		return removeUserId;
	}

	public void setRemoveUserId(String removeUserId) {
		this.removeUserId = removeUserId;
	}

	public String getUserId() {
		return userId;
	}

	public String getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(String addUserId) {
		this.addUserId = addUserId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
}
