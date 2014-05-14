package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="messageForm"
 * @author Jeff
 *
 */
public class MessageForm extends ActionForm {

	private String	subject;
	private String	message;
	private String	toUserId;
	
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
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
	
}

