package wp.web;

import org.apache.struts.action.ActionForm;

public class AlertForm extends ActionForm {

	private boolean	email;
	private boolean addon;
	private boolean	crawl;
	private String	url;
	private String	contains;
	private String	predicts;
	private boolean	remove;
	private String	intensity;
	private boolean	canLeaveDomain;
	private String		snippetSize;
	private String	maxLinksToFollow = "1000";
	
	public String getMaxLinksToFollow() {
		return maxLinksToFollow;
	}
	public void setMaxLinksToFollow(String maxLinksToFollow) {
		this.maxLinksToFollow = maxLinksToFollow;
	}
	public String getSnippetSize() {
		return snippetSize;
	}
	public void setSnippetSize(String snippetSize) {
		this.snippetSize = snippetSize;
	}
	public String getIntensity() {
		return intensity;
	}
	public void setIntensity(String intensity) {
		this.intensity = intensity;
	}
	public boolean isCanLeaveDomain() {
		return canLeaveDomain;
	}
	public void setCanLeaveDomain(boolean canLeaveDomain) {
		this.canLeaveDomain = canLeaveDomain;
	}
	public boolean isCrawl() {
		return crawl;
	}
	public void setCrawl(boolean crawl) {
		this.crawl = crawl;
	}
	public boolean isRemove() {
		return remove;
	}
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	public boolean isEmail() {
		return email;
	}
	public void setEmail(boolean email) {
		this.email = email;
	}
	public boolean isAddon() {
		return addon;
	}
	public void setAddon(boolean addon) {
		this.addon = addon;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContains() {
		return contains;
	}
	public void setContains(String contains) {
		this.contains = contains;
	}
	public String getPredicts() {
		return predicts;
	}
	public void setPredicts(String predicts) {
		this.predicts = predicts;
	}
	
	
}
