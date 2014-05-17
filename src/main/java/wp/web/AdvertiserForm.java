package wp.web;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.validator.ValidatorForm;

import wp.core.Utils;
import wp.model.Advertiser;
import wp.service.AdvertiserService;

/**
 * @struts.form name="advertiserForm"
 * @author Jeff
 *
 */
public class AdvertiserForm extends ValidatorForm {

	private String	id;
	private String	name;
	private String	description;
	private String	company;
	private String	phone;
	private String	email;
	private String	password;
	private String	password2;
	private String	budget;
	private Date	timestamp;
	private String	startDateStr;
	private String	endDateStr;
	private String	username;
	private String	dailyBudget;
	private boolean	distribute;
	
	public String getDailyBudget() {
		return dailyBudget;
	}

	public void setDailyBudget(String dailyBudget) {
		this.dailyBudget = dailyBudget;
	}

	public boolean isDistribute() {
		return distribute;
	}

	public void setDistribute(boolean distribute) {
		this.distribute = distribute;
	}

	public void	bind (Advertiser adver) {
		if (adver != null) {
			id = adver.getId().toString();
			description = adver.getDescription();
			name = adver.getName();
			company = adver.getCompany();
			phone = adver.getPhone();
			email = adver.getEmail();
			username = adver.getUsername();
			budget = String.valueOf(adver.getBudget());
			timestamp = adver.getTimestamp();
			password = password2 = adver.getPassword();
			startDateStr = Utils.stringStorage(adver.getCampaignStartDate());
			endDateStr = Utils.stringStorage(adver.getCampaignEndDate());
			dailyBudget = String.valueOf(adver.getDailyBudget());
		}
	}
	
	public String getUsername() {
		return username;
	}

	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="username"
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public String getStartDate() {
		return startDateStr;
	}

	public void setStartDate(String startDate) {
		this.startDateStr = startDate;
	}

	public String getEndDate() {
		return endDateStr;
	}

	public void setEndDate(String endDate) {
		this.endDateStr = endDate;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="password"
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public Advertiser	extract (AdvertiserService advertiserService) throws SQLException {
		Advertiser	adver;
		
		if (StringUtils.isEmpty(id))
			adver = new Advertiser();
		else
			adver = advertiserService.findById(new Long(id));
		
		adver.setDescription(description);
		adver.setName(name);
		adver.setCompany(company);
		adver.setPhone(phone);
		adver.setEmail(email);
		adver.setPassword(password);
		adver.setBudget(Double.parseDouble(budget));
		adver.setDailyBudget(Double.parseDouble(dailyBudget));
		adver.setUsername(username);
		
		adver.setCampaignStartDate(Utils.getTimestamp(startDateStr));
		adver.setCampaignEndDate(Utils.getTimestamp(endDateStr));
		return (adver);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	/**
	 * @struts.validator type="required,email"
	 * @struts.validator-args arg0resource="email"
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public String getBudget() {
		return budget;
	}

	public void setBudget(String budget) {
		this.budget = budget;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
