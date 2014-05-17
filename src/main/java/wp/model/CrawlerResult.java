package wp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="CrawlerResult")
public class CrawlerResult implements Serializable {

	private Long id;
	private String	urlStr;
	private String	contains;
	private String	prediction;
	private String	secondGuess;
	private User	user;
	private Date	timestamp;
	private String	title;
	private String	snippet;	
	private CrawlerImpl	crawler;
	private int		indexPosition;
	private boolean	posted;
	
	
	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public int getIndexPosition() {
		return indexPosition == -1 ? id.intValue() : indexPosition;
	}
	
	public void setIndexPosition(int indexPosition) {
		this.indexPosition = indexPosition;
	}

	public String getSecondGuess() {
		return secondGuess;
	}

	public void setSecondGuess(String secondGuess) {
		this.secondGuess = secondGuess;
	}

	@Column
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column
	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	@Column
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@ManyToOne
	public CrawlerImpl	getCrawler () {
		return (crawler);
	}

	public void	setCrawler (CrawlerImpl crawler) {
		this.crawler = crawler;
	}
	
	@ManyToOne
	public User	getUser () {
		return (user);
	}

	public void	setUser (User usr) {
		user = usr;
	}

	@Column
	public String getUrlStr() {
		if (urlStr != null && !urlStr.startsWith("http://"))
			urlStr = "http://" + urlStr;
		return urlStr;
	}

	@Transient
	public String	getUrlStrAbbr () {
		String	ret = getUrlStr();
		if (ret == null || ret.length() < 60)
			return (ret);
		return (ret.substring(0, 60));
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
}
