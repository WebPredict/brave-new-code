package wp.core;

public class UserAlreadyExistsException extends Exception {

	public UserAlreadyExistsException (String userId) {
		super("User " + userId + " already exists.");
	}
}
