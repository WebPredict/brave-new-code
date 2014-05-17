package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="recentRatingsForm"
 * @author Jeff
 *
 */
public class RecentRatingsForm extends ActionForm {

	private String	size = "";
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}

	
}
