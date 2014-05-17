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

@Entity
@Table(name="Recommendation")
public class Recommendation implements Serializable {

	private Long id;
	private String	urlStr;
	private String	prediction;
	private Date	timestamp;
	private User	user;
	private boolean	dontShow;
	private String	title;
	private String	snippet;
	
	@Column
	public boolean isDontShow() {
		return dontShow;
	}

	public void setDontShow(boolean dontShow) {
		this.dontShow = dontShow;
	}

	@ManyToOne
	public User	getUser () {
		return (user);
	}

	public void	setUser (User usr) {
		user = usr;
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
	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
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
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
