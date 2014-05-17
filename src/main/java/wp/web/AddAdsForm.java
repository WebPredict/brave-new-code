package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="addAdsForm"
 * @author Jeff
 *
 */
public class AddAdsForm extends ActionForm {

	private String	adId;

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}
	
	
}
