package wp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import wp.core.Stringable;
import wp.core.Utils;

@Entity
public class UserMessage implements Stringable, Serializable {

	private Long	id;
	private String	subject;
	private String	msg;
	private String	userIdFrom;
	private Date	date = new Date();
	private boolean	alreadyViewed;
	private User	user;
	
	@Column
	public boolean isAlreadyViewed() {
		return alreadyViewed;
	}

	public void setAlreadyViewed(boolean alreadyViewed) {
		this.alreadyViewed = alreadyViewed;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Column(length=2048)
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Column
	public String getUserIdFrom() {
		return userIdFrom;
	}
	public void setUserIdFrom(String userIdFrom) {
		this.userIdFrom = userIdFrom;
	}
	
	@Column
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public static final String	FIELD_SEP = "%^%^";
	
	@Transient
	public String getStringStorage() {
		return (subject + FIELD_SEP + msg + FIELD_SEP + userIdFrom + FIELD_SEP + Utils.stringStorage(date) + FIELD_SEP + alreadyViewed);
	}
	
	public static UserMessage	fromString (String s) {
		StringTokenizer	tok = new StringTokenizer(s, FIELD_SEP); 
		
		UserMessage	msg = new UserMessage();
		msg.setSubject(tok.nextToken());
		msg.setMsg(tok.nextToken());
		msg.setUserIdFrom(tok.nextToken());
		msg.setDate(Utils.getTimestamp(tok.nextToken()));
		msg.setAlreadyViewed(Boolean.parseBoolean(tok.nextToken()));
		
		return (msg);
	}
}

