package wp.core;

public class UserDoesntExistException extends Exception {

	public UserDoesntExistException (String id) {
		super("User " + id + " doesn't exist");
	}
}
