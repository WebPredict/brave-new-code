package wp.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

import wp.core.CachedURL;

@Entity
@Table(name="WebAlert")
public class Alert implements IAlert, Serializable {

	private Long id;
	private String	urlStr;
	private String	prediction;
	private String	contains;
	private Date	timestamp;
	private boolean	email;
	private boolean	addon;
	private boolean	crawl;
	private User	user;
	private Date	lastCrawled;
	private int		repeatTimePeriod; // seconds
	private int		snippetSize; // chars
	private Collection<String>	crawledUrls = new HashSet<String>();
	
	@ManyToOne
	public User	getUser () {
		return (user);
	}

	public void	setUser (User usr) {
		user = usr;
	}

	public Date getLastCrawled() {
		return lastCrawled;
	}

	public void setLastCrawled(Date lastCrawled) {
		this.lastCrawled = lastCrawled;
	}

	@CollectionOfElements
	public Collection<String> getCrawledUrls() {
		return crawledUrls;
	}

	public void setCrawledUrls(Collection<String> crawledUrls) {
		this.crawledUrls = crawledUrls;
	}

	public boolean	needToCrawl () {
		return (lastCrawled == null || System.currentTimeMillis() - lastCrawled.getTime() > 86400000l);
	}
	
	@Transient
	public Set<CachedURL>	getCrawledURLs () {
	
		HashSet<CachedURL>	ret = new HashSet<CachedURL>();
		if (this.crawledUrls != null)
			for (String url : crawledUrls) {
				try {
					ret.add(new CachedURL(url));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		return (ret);
	}
	
	public boolean isCrawl() {
		return crawl;
	}

	public void setCrawl(boolean crawl) {
		this.crawl = crawl;
	}

	@Column
	public boolean isEmail() {
		return email;
	}

	public void setEmail(boolean email) {
		this.email = email;
	}

	@Column
	public boolean isAddon() {
		return addon;
	}

	public void setAddon(boolean addon) {
		this.addon = addon;
	}

	@Column
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Column
	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	@Column
	public String getPrediction() {
		return prediction;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

	@Column
	public String getContains() {
		return contains;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	} 

	@Column(name="repeatPeriod")
	public int getRepeatTimePeriod() {
		return (repeatTimePeriod);
	}

	public int getSnippetSize() {
		return (snippetSize);
	}

	public void setRepeatTimePeriod(int repeatSeconds) {
		this.repeatTimePeriod = repeatSeconds;
	}

	public void setSnippetSize(int maxChars) {
		this.snippetSize = maxChars;
	}
}
