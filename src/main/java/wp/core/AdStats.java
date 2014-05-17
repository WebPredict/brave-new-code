package wp.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import wp.model.Ad;
import wp.model.AdStat;

public class AdStats {

	private String	id;
	private Date	startTime;
	private Date	endTime;
	private HashMap<Long, AdStat>	statMap = new HashMap<Long, AdStat>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Collection<AdStat> getAdStatList() {
		return statMap.values();
	}
	
	public void	setStatMap (HashMap<Long, AdStat> map) {
		statMap = map;
	}
	
	public void	incrementClicks (Ad ad) {
		AdStat	stat = getOrAddStat(ad);
		stat.setNumClicks(stat.getNumClicks() + 1);		
	}
	
	private AdStat	getOrAddStat (Ad ad) {
		AdStat	stat = statMap.get(ad.getId());
		if (stat == null) {
			stat = new AdStat();
			//stat.setAdId(ad.getId());
			//stat.setAdTitle(ad.getTitle());
			stat.setStartDate(new Date());
			stat.setEndDate(new Date());
			statMap.put(ad.getId(), stat);
		}
		return (stat);
	}
	
	public void	incrementImpressions (Ad ad) {
		AdStat	stat = getOrAddStat(ad);
		stat.setNumImpressions(stat.getNumImpressions() + 1);		
	}
	
}
