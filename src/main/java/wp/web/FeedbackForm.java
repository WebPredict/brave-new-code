package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="feedbackForm"
 * @author Jeff
 *
 */
public class FeedbackForm extends ActionForm {

	private String	subject;
	private String	message;
	private String	emailAddr;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getEmailAddr() {
		return emailAddr;
	}
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
}
