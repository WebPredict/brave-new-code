package wp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

@Entity
public class Ad implements Serializable {

	public static final double	DEFAULT_CLICK_COST = .1;
	public static final double	DEFAULT_IMPRESSION_COST = .001;
	
	private Long	id;
	private String	title;
	private String	content;
	private String	link;
	private boolean	enabled = true;
	private int		lowRange = 1;
	private int		highRange = 10;
	private int		numImpressions;
	private int		maxImpressionsPerUser = 10;
	private int		numClicks;
	private Long	advertiserId;
	private String	desirableRatings;
	private Advertiser	advertiser;
	private List<AdStat>	stats;
	private Collection<String>	desirableRatingsSet = new HashSet<String>();
	private int		numDistinctIPs;
	
	@OneToMany(mappedBy="ad")
    @OrderBy("id")
	public List<AdStat> getStats() {
		return stats;
	}

	public void setStats(List<AdStat> stats) {
		this.stats = stats;
		initStatsIfNeeded();
	}

	// TODO: make work
	@Transient
	public int getNumDistinctIPs() {
		return numDistinctIPs;
	}

	public void setNumDistinctIPs(int numDistinctIPs) {
		this.numDistinctIPs = numDistinctIPs;
	}
	
	@Column
	public int getMaxImpressionsPerUser() {
		return maxImpressionsPerUser;
	}

	public void setMaxImpressionsPerUser(int maxImpressionsPerUser) {
		this.maxImpressionsPerUser = maxImpressionsPerUser;
	}

	@Transient
	public Collection<String>	getDesirableRatingsSet () {
		return (desirableRatingsSet);
	}
	
	@Column(length=1024)
	public String getDesirableRatings() {
		return desirableRatings;
	}

	public void setDesirableRatings(String desirableRatings) {
		this.desirableRatings = desirableRatings;
		
		desirableRatingsSet.clear();
		if (desirableRatings != null) {
			StringTokenizer	tok = new StringTokenizer(desirableRatings, ",");
			while (tok.hasMoreTokens())
				desirableRatingsSet.add(tok.nextToken().trim());
		}
	}
	
	private void	initStatsIfNeeded () {
		if (stats == null || stats.size() == 0) {
			stats = new ArrayList<AdStat>();
			AdStat stat = new AdStat();
			stat.setAd(this);
			stat.setStartDate(new Date());
			stat.setEndDate(new Date());
			stats.add(stat);
		}
	}
	
	public void	incrementClicks () {
		initStatsIfNeeded();
		AdStat	stat = stats.get(stats.size() - 1);
		stat.setNumClicks(stat.getNumClicks() + 1);
		numClicks++;
	}
	
	public void	incrementImpressions () {
		initStatsIfNeeded();
		AdStat	stat = stats.get(stats.size() - 1);
		stat.setNumImpressions(stat.getNumImpressions() + 1);
		numImpressions++;
	}
	
	@ManyToOne
	public Advertiser getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	public Ad () {		
	}
	
	public Ad (String title, String url, String content) {
		this.title = title;
		this.link = url;
		this.content = content;
	}
	
	public Ad (Long advertiserId) {
		this.advertiserId = advertiserId;
	}
	
	@Column
	public int getNumImpressions() {
		return numImpressions;
	}

	@Column
	public Long getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(Long advertiserId) {
		this.advertiserId = advertiserId;
	}

	public void setNumImpressions(int numImpressions) {
		this.numImpressions = numImpressions;
	}

	@Column
	public int getNumClicks() {
		return numClicks;
	}

	public void setNumClicks(int numClicks) {
		this.numClicks = numClicks;
	}

	@Transient
	public int	getHashCode () {
		return (id.hashCode());
	}
	
	public boolean	equals (Object o) {
		return ((o instanceof Ad) && ((Ad)o).getId().equals(id));
	}
	
	@Column
	public int getLowRange() {
		return lowRange;
	}
	public void setLowRange(int lowRange) {
		this.lowRange = lowRange;
	}
	
	@Column
	public int getHighRange() {
		return highRange;
	}
	public void setHighRange(int highRange) {
		this.highRange = highRange;
	}
	
	@Column
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	@Column
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
