package wp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Feed implements Serializable {

	private Long	id;
	private String	name;
	private Date	earliest;
	private Date	latest;
	private String	ratings;
	private int		snippetSize;
	private User	user;
	private int		frequency;
	private Date	lastEmittedTime;
	
	public Date getLastEmittedTime() {
		return lastEmittedTime;
	}
	public void setLastEmittedTime(Date lastEmittedTime) {
		this.lastEmittedTime = lastEmittedTime;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	@ManyToOne
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getSnippetSize() {
		return snippetSize;
	}
	public void setSnippetSize(int snippetSize) {
		this.snippetSize = snippetSize;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getEarliest() {
		return earliest;
	}
	public void setEarliest(Date earliest) {
		this.earliest = earliest;
	}
	public Date getLatest() {
		return latest;
	}
	public void setLatest(Date latest) {
		this.latest = latest;
	}
	public String getRatings() {
		return ratings;
	}
	public void setRatings(String ratings) {
		this.ratings = ratings;
	}
	
	
	
}
