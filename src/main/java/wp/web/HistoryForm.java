package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="historyForm"
 * @author HP_Administrator
 *
 */
public class HistoryForm extends ActionForm {

	private String	url;
	private String	remove;
	private String	rating;
	private String	refetch;
	
	public String getRefetch() {
		return refetch;
	}

	public void setRefetch(String refetch) {
		this.refetch = refetch;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getRemove() {
		return remove;
	}

	public void setRemove(String remove) {
		this.remove = remove;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
