package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="globalStatsForm"
 * @author Jeff
 *
 */
public class GlobalStatsForm extends ActionForm {

	private String	max;
	private String	search;
	private boolean	mostFrequent;
	
	public boolean isMostFrequent() {
		return mostFrequent;
	}
	public void setMostFrequent(boolean mostFrequent) {
		this.mostFrequent = mostFrequent;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	
	
}
