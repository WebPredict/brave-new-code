package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="addGroupsForm"
 * @author Jeff
 *
 */
public class AddGroupsForm extends ActionForm {

	private String	filter;
	private String	userId;
	private String	removeGroupId;
	private String	addGroupId;
	
	public String getRemoveGroupId() {
		return removeGroupId;
	}

	public void setRemoveGroupId(String removeGroupId) {
		this.removeGroupId = removeGroupId;
	}

	public String getGroupId() {
		return userId;
	}

	public String getAddGroupId() {
		return addGroupId;
	}

	public void setAddGroupId(String addGroupId) {
		this.addGroupId = addGroupId;
	}

	public void setGroupId(String userId) {
		this.userId = userId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
}
