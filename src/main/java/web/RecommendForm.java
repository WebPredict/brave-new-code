package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="recommendForm"
 * @author Jeff
 *
 */
public class RecommendForm extends ActionForm {

	private String	desirableRatings;
	private String	doit;
	private String	hideit;
	private String 	maxPagesToSearch;
	private String	maxResults;

	private boolean []	dontShow = new boolean [50];
	
	public void init () {
		for (int i = 0; i < dontShow.length; i++)
			dontShow [i] = false;
	}
	
	public boolean[] getDontShow() {
		return dontShow;
	}

	public void setDontShow(boolean[] dontShow) {
		this.dontShow = dontShow;
	}

	public String getHideit() {
		return hideit;
	}

	public void setHideit(String hideit) {
		this.hideit = hideit;
	}

	public String getMaxPagesToSearch() {
		return maxPagesToSearch;
	}

	public void setMaxPagesToSearch(String maxPagesToSearch) {
		this.maxPagesToSearch = maxPagesToSearch;
	}

	public String getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(String maxResults) {
		this.maxResults = maxResults;
	}

	public String getDoit() {
		return doit;
	}

	public void setDoit(String doit) {
		this.doit = doit;
	}

	public String getDesirableRatings() {
		return desirableRatings;
	}

	public void setDesirableRatings(String desirableRatings) {
		this.desirableRatings = desirableRatings;
	}
	
}
