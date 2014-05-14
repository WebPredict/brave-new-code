package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="adsForm"
 * @author Jeff
 *
 */
public class AdsForm extends ActionForm {

	private String	adId;

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}
	
}
