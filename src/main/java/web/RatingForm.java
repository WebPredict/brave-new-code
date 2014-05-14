package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="ratingForm"
 * @author HP_Administrator
 *
 */
public class RatingForm extends ActionForm {

	private String	username;
	private String	newusername;
	private String  password;
	private String  emailAddr;
	private String	link = "";
	private String	rating = "";
	private String	comment = "";
	private String	captcha = "";
	private String	rawContent = "";
	private String	contentId = String.valueOf(System.currentTimeMillis());
	private String	minHistorySize;
	private String	numUsers;
	private String	pageTried = "";
	private String	anonLoginId = "";

	public String getAnonLoginId() {
		return anonLoginId;
	}
	public void setAnonLoginId(String anonLoginId) {
		this.anonLoginId = anonLoginId;
	}
	public String getPageTried() {
		return pageTried;
	}
	public void setPageTried(String pageTried) {
		this.pageTried = pageTried;
	}
	public String getMinHistorySize() {
		return minHistorySize;
	}
	public void setMinHistorySize(String minHistorySize) {
		this.minHistorySize = minHistorySize;
	}
	public String getNumUsers() {
		return numUsers;
	}
	public void setNumUsers(String numUsers) {
		this.numUsers = numUsers;
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getRawContent() {
		return rawContent;
	}
	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}
	public String getNewusername() {
		return newusername;
	}
	public void setNewusername(String newusername) {
		this.newusername = newusername;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailAddr() {
		return emailAddr;
	}
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	public String getLink() {
		return link; 
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	
}
