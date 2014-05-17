package wp.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class WebStat implements Serializable {

	private String	urlStr;
	private int		numKeystrokes;
	private int		secondsSpent;
	private Long	id;
	private User	user;
	
	
	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUrlStr() {
		return urlStr;
	}
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	public int getNumKeystrokes() {
		return numKeystrokes;
	}
	public void setNumKeystrokes(int numKeystrokes) {
		this.numKeystrokes = numKeystrokes;
	}
	public int getSecondsSpent() {
		return secondsSpent;
	}
	public void setSecondsSpent(int secondsSpent) {
		this.secondsSpent = secondsSpent;
	}
		
	
}
