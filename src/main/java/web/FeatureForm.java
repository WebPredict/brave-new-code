package wp.web;

import org.apache.struts.action.ActionForm;

public class FeatureForm extends ActionForm {

	private boolean keep;
	private String	name;
	
	public boolean isKeep() {
		return keep;
	}
	public void setKeep(boolean keep) {
		this.keep = keep;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
