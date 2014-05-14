package wp.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import wp.core.PageStats;
import wp.core.Utils;

@Entity
public class RatedPage implements Serializable {

	private Long	id;
	private String	rating;
	private URL	url;
	private String	urlStr;
	private PageStats	stats;
	private ParsedPage	parsedPage;
	private String	comment;
	private Date	timestamp = new Date();
	private User	user;
	
	public static final boolean DEBUG = false;
	
	@Column(length=1024)
	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = Utils.normalizeUrlStr(urlStr);
		try {
			this.url = new URL(urlStr);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public User	getUser () {
		return (user);
	}

	public void	setUser (User usr) {
		user = usr;
	}
	
	@Column
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public RatedPage () {
		
	}
	
	public RatedPage (String rating, URL url, ParsedPage pp) {
		this.rating = rating;
		setUrl(url);
		this.parsedPage = pp;
	}

	@Transient
	public PageStats getStats() {
		if (stats == null && parsedPage != null) {
			boolean	ignoreCaps = user == null ? false : user.isIgnoreCapitalization();
			boolean	genericizeNumbers = user == null ? false : user.isGenericizeNumbers();
			this.stats = new PageStats(parsedPage.getRawContent(), ignoreCaps, genericizeNumbers);
		}
		return stats;
	}

	void	setStats (PageStats stats) {
		this.stats = stats;
	}
	
	@Transient
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		try {
			this.url = Utils.normalizeUrl(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.urlStr = url.toString();
	}

	@OneToOne(cascade=CascadeType.ALL)
	public ParsedPage	getParsedPage () {
		return (parsedPage);
	}
	
	public void	setParsedPage (ParsedPage parsedPage) {
		this.parsedPage = parsedPage;	
	}
	
	@Transient
	public boolean	isRatingNumeric () {
		try {
			Integer.parseInt(rating);
			return (true);
		}
		catch (Exception e) {
			return (false);
		}
	}
	
	@Column
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	
}
