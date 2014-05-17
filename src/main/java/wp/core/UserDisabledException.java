package wp.core;

public class UserDisabledException extends Exception {

	public UserDisabledException (String userId) {
		super("User " + userId + " is disabled.");
	}
}
