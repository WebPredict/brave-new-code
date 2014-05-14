package wp.core;

public class EmailWithOtherUserException extends Exception {

	public EmailWithOtherUserException (String email) {
		super("Email address " + email + " is already associated with one or more WebPredict users.");
	}
}