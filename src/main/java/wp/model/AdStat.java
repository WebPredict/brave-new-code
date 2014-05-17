package wp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import wp.core.Constants;

@Entity
public class AdStat implements Serializable {

	private Ad		ad;
	private int		numClicks;
	private int		numImpressions;
	private int		numDistinctIPs;
	private double	cost;
	private Date	startDate;
	private Date	endDate;
	private Long	id;
	
	@Transient
	public int getNumDistinctIPs() {
		return numDistinctIPs;
	}

	public void setNumDistinctIPs(int numDistinctIPs) {
		this.numDistinctIPs = numDistinctIPs;
	}

	@ManyToOne
	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Transient
	public String getAdTitle() {
		return ad.getTitle();
	}
	
	@Transient
	public String	getCostStr () {
		return (Constants.COST_FORMAT.format(getCost()));
	}
	
	@Column
	public double getCost() {
		cost = Ad.DEFAULT_CLICK_COST * ad.getNumClicks() + Ad.DEFAULT_IMPRESSION_COST * ad.getNumImpressions();
		return cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	@Column
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Column
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Transient
	public Long getAdId() {
		return ad.getId();
	}
	
	@Column
	public int getNumClicks() {
		return numClicks;
	}
	public void setNumClicks(int numClicks) {
		this.numClicks = numClicks;
	}
	
	@Column
	public int getNumImpressions() {
		return numImpressions;
	}
	public void setNumImpressions(int numImpressions) {
		this.numImpressions = numImpressions;
	}
	
}
