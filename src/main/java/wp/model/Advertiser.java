package wp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Advertiser implements Serializable {

	private Long	id;
	private String	name;
	private String	username;
	private String	password;
	private String	description;
	private String	company;
	private String	phone;
	private String	email;
	private List<Ad>	adsList = new ArrayList<Ad>();
	private double	budget;
	private double	dailyBudget;
	private Date	timestamp = new Date();	
	private Date	campaignStartDate;
	private Date	campaignEndDate;
	
	@Column
	public Date getCampaignStartDate() {
		return campaignStartDate;
	}
	public void setCampaignStartDate(Date campaignStartDate) {
		this.campaignStartDate = campaignStartDate;
	}
	
	@Column
	public Date getCampaignEndDate() {
		return campaignEndDate;
	}
	public void setCampaignEndDate(Date campaignEndDate) {
		this.campaignEndDate = campaignEndDate;
	}
	
	@Column
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Column
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(length=1024)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	
	@Column
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Column
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Ad	findById (Long adId) {
		if (adsList == null)
			return (null);
		for (Ad ad : adsList)
			if (ad.getId().equals(adId))
				return(ad);
		return (null);
	}
	
	@OneToMany(mappedBy="advertiser")
    @OrderBy("id")
	public List<Ad> getAdsList() {
		return adsList;
	}
	public void setAdsList(List<Ad> adsList) {
		this.adsList = adsList;
	}
	
	@Column
	public double getBudget() {
		return budget;
	}
	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	@Column
	public double getDailyBudget() {
		return dailyBudget;
	}
	public void setDailyBudget(double dailyBudget) {
		this.dailyBudget = dailyBudget;
	}
	
	@Column
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
