package wp.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

import wp.core.AllPageStats;
import wp.core.Rater;

/**
 * @author jsanchez
 *
 */
@Entity
public class UserStats implements Serializable {

	private HashMap<URL, RatedPage>	ratedPagesMap = new HashMap<URL, RatedPage>();
	private User	user;
	private Map<String, Integer>	dailyUsage = new HashMap<String, Integer>();
	private Date	ratingsLastChanged = new Date();
	private Long	id;
	private Collection<RatedPage>	ratedPages = new HashSet<RatedPage>();
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@CollectionOfElements
	public Map<String, Integer> getDailyUsage() {
		return dailyUsage;
	}

	public void setDailyUsage(Map<String, Integer> dailyUsage) {
		this.dailyUsage = dailyUsage;
	}

	@OneToOne(cascade=CascadeType.ALL)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserStats () {
		
	}
	
	public UserStats(User user) {
		this.user = user;
	}

	@Transient
	public HashMap<URL, RatedPage>	getRatedPagesMap() {
		return (ratedPagesMap);
	}
	
	public void	removeRating (URL url) {
		RatedPage	rp = ratedPagesMap.get(url);
		if (rp != null) {
			ratedPagesMap.remove(url);
			ratedPages.remove(rp);
		}
	}
	
	@Column
	public Date	getRatingsLastChanged () {
		return (ratingsLastChanged);
	}
	
	public void setRatingsLastChanged(Date ratingsLastChanged) {
		this.ratingsLastChanged = ratingsLastChanged;
	}

	public boolean	pageRated (String rating, URL url, ParsedPage content, String comment) {
		
		RatedPage	rp = ratedPagesMap.get(url);
		boolean		updated;
		if (rp != null) {
			updated = true;
			rp.setRating(rating);
			rp.setParsedPage(content);
		}
		else {
			updated = false;
			rp = new RatedPage(rating, url, content);
		}
		rp.setComment(comment);
		
		ratedPagesMap.put(url, rp);
		
		if (!updated)
			ratedPages.add(rp);
		
		String	key = Rater.SHORT_DATE_FORMAT.format(new Date()) +"-rate";
		
		Integer	numRate = dailyUsage.get(key);
		if (numRate == null)
			dailyUsage.put(key, 1);
		else
			dailyUsage.put(key, ++numRate);
		ratingsLastChanged = new Date();
		return (updated);
	}
	
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
    public Collection<RatedPage> getRatedPages() {
		return ratedPages;
	}

	public void setRatedPages(Collection<RatedPage> ratedPages) {
		this.ratedPages = ratedPages;
		ratedPagesMap.clear();
		if (ratedPages != null) {
			for (RatedPage rp : ratedPages) {
				ratedPagesMap.put(rp.getUrl(), rp);
			}
		}
	}

	public void	pagePredicted () {
		if (true)
			return; // there's some problem with duplicate entries in this stupid dailyUsage collection in the DB - debug 12/05/2009
		String	key = Rater.SHORT_DATE_FORMAT.format(new Date()) +"-pred";
		
		Integer	numPred = dailyUsage.get(key);
		if (numPred == null)
			dailyUsage.put(key, 1);
		else
			dailyUsage.put(key, ++numPred);
	}
	
	@Transient
	public String getUserId() {
		return user == null ? null : user.getUserId();
	}

	@Transient
	public Integer getMaxRating() {
		return (getUser().getMaxRating());
	}

	@Transient
	public int	getNumPredictionsToday () {
		String	key = Rater.SHORT_DATE_FORMAT.format(new Date()) +"-pred";
		
		Integer	numPred = dailyUsage.get(key);
		if (numPred == null)
			return (0);
		return (numPred);
	}
	
	@Transient
	public int	getNumRatingsToday () {
		String	key = Rater.SHORT_DATE_FORMAT.format(new Date()) +"-rate";
		
		Integer	numRate = dailyUsage.get(key);
		if (numRate == null)
			return (0);
		return (numRate);
	}
	
	@Transient
	public AllPageStats	getAllPageStats () {
		return (new AllPageStats(ratedPagesMap));
	}

}
