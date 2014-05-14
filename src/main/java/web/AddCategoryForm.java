package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="addCategoryForm"
 * @author Jeff
 *
 */
public class AddCategoryForm extends ActionForm {

	private String	link;
	private String	rating;
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	
	
}
