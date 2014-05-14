package wp.web;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.validator.ValidatorForm;

import wp.model.Ad;
import wp.model.Advertiser;
import wp.service.AdService;

/**
 * @struts.form name="adForm"
 * @author Jeff
 *
 */
public class AdForm extends ValidatorForm {

	private String	id;
	private String	title;
	private String	content;
	private String	link;
	private String	lowRange = "1";
	private String	highRange = "10";
	private String	numClicks = "0";
	private String	desirableRatings;
	private String	undesirableRatings;
	private String	numImpressions = "0";
	private boolean	matchHeadlines;
	private String	maxImpressionsPerUser = "10";
	private String	numDistinctUsers;
	
	public String getUndesirableRatings() {
		return undesirableRatings;
	}

	public void setUndesirableRatings(String undesirableRatings) {
		this.undesirableRatings = undesirableRatings;
	}

	public String getMaxImpressionsPerUser() {
		return maxImpressionsPerUser;
	}

	/**
	 * @struts.validator type="integer"
	 * @struts.validator-args arg0resource="maxImpressionsPerUser"
	 */
	public void setMaxImpressionsPerUser(String maxImpressionsPerUser) {
		this.maxImpressionsPerUser = maxImpressionsPerUser;
	}

	public String getNumDistinctUsers() {
		return numDistinctUsers;
	}

	public void setNumDistinctUsers(String numDistinctUsers) {
		this.numDistinctUsers = numDistinctUsers;
	}

	public String getDesirableRatings() {
		return desirableRatings;
	}

	public void setDesirableRatings(String desirableRatings) {
		this.desirableRatings = desirableRatings;
	}

	public boolean isMatchHeadlines() {
		return matchHeadlines;
	}

	public void setMatchHeadlines(boolean matchHeadlines) {
		this.matchHeadlines = matchHeadlines;
	}

	public String getNumClicks() {
		return numClicks;
	}

	public void setNumClicks(String numClicks) {
		this.numClicks = numClicks;
	}

	public String getNumImpressions() {
		return numImpressions;
	}

	public void setNumImpressions(String numImpressions) {
		this.numImpressions = numImpressions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="title"
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="content"
	 */
	public void setContent(String content) {
		this.content = content;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLowRange() {
		return lowRange;
	}

	/**
	 * @struts.validator type="integer"
	 * @struts.validator-args arg0resource="lowRange"
	 */
	public void setLowRange(String lowRange) {
		this.lowRange = lowRange;
	}

	public String getHighRange() {
		return highRange;
	}

	/**
	 * @struts.validator type="integer"
	 * @struts.validator-args arg0resource="highRange"
	 */
	public void setHighRange(String highRange) {
		this.highRange = highRange;
	}

	public void	bind (Ad ad) {
		if (ad != null) {
			id = ad.getId().toString();
			title = ad.getTitle();
			content = ad.getContent();
			link = ad.getLink();
			lowRange = String.valueOf(ad.getLowRange());
			highRange = String.valueOf(ad.getHighRange());
			numClicks = String.valueOf(ad.getNumClicks());
			numImpressions = String.valueOf(ad.getNumImpressions());
			desirableRatings = ad.getDesirableRatings();
			maxImpressionsPerUser = String.valueOf(ad.getMaxImpressionsPerUser());
			numDistinctUsers = String.valueOf(ad.getNumDistinctIPs());
		}
	}
	
	public Ad	extract (AdService adService, Advertiser advertiser) throws SQLException {

		Ad	ad; 
		if (StringUtils.isEmpty(id)) { 
			ad = new Ad(advertiser.getId());
			ad.setAdvertiser(advertiser);
		}
		else
			ad = adService.findById(advertiser.getId(), new Long(id));
		
		ad.setTitle(title);
		ad.setContent(content);
		ad.setLink(link);
		ad.setDesirableRatings(desirableRatings);
		if (!StringUtils.isEmpty(lowRange))
			ad.setLowRange(Integer.parseInt(lowRange));
		if (!StringUtils.isEmpty(highRange))
			ad.setHighRange(Integer.parseInt(highRange));
		ad.setMaxImpressionsPerUser(Integer.parseInt(maxImpressionsPerUser));
		return (ad);
	}
}
