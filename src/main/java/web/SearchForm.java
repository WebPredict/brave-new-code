package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="searchForm"
 * @author Jeff
 *
 */
public class SearchForm extends ActionForm {

	private String	search;
	private String	groupName;
	private String	lowest;
	private String	highest;
	private String	longerThan;
	private String	shorterThan;
	private String	ratedBy;
	private String	ratedAs;
	private boolean	hasAds;
	private boolean	hasPopups;
	private String	times;
	
	
	public String getShorterThan() {
		return shorterThan;
	}

	public void setShorterThan(String shorterThan) {
		this.shorterThan = shorterThan;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getLongerThan() {
		return longerThan;
	}

	public void setLongerThan(String longerThan) {
		this.longerThan = longerThan;
	}

	public String getRatedBy() {
		return ratedBy;
	}

	public void setRatedBy(String ratedBy) {
		this.ratedBy = ratedBy;
	}

	public String getRatedAs() {
		return ratedAs;
	}

	public void setRatedAs(String ratedAs) {
		this.ratedAs = ratedAs;
	}

	public boolean isHasAds() {
		return hasAds;
	}

	public void setHasAds(boolean hasAds) {
		this.hasAds = hasAds;
	}

	public boolean isHasPopups() {
		return hasPopups;
	}

	public void setHasPopups(boolean hasPopups) {
		this.hasPopups = hasPopups;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getLowest() {
		return lowest;
	}

	public void setLowest(String lowest) {
		this.lowest = lowest;
	}

	public String getHighest() {
		return highest;
	}

	public void setHighest(String highest) {
		this.highest = highest;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
}
