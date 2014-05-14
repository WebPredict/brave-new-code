package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="wordCloudForm"
 * @author Jeff
 *
 */
public class WordCloudForm extends ActionForm {

	private String	size = "200";
	private String	ratedAs = "8, 9, 10";
	private boolean	ignoreCommon;
	
	public String getRatedAs() {
		return ratedAs;
	}
	public void setRatedAs(String ratedAs) {
		this.ratedAs = ratedAs;
	}
	public boolean isIgnoreCommon() {
		return ignoreCommon;
	}
	public void setIgnoreCommon(boolean ignoreCommon) {
		this.ignoreCommon = ignoreCommon;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
}
