package wp.web;

import org.apache.struts.action.ActionForm;

public class CrawlerForm extends ActionForm {

	private String	url;
	private String	contains;
	private String	predicts;
	private boolean	remove;
	private String	intensity;
	private boolean	canLeaveDomain;
	private String		snippetSize;
	private String	maxLinksToFollow = "";
	private String	status;
	private boolean	disable;

	
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
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
	public boolean isRemove() {
		return remove;
	}
	public void setRemove(boolean remove) {
		this.remove = remove;
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
