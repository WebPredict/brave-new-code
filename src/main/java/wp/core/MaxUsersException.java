package wp.core;

public class MaxUsersException extends Exception {

	public MaxUsersException (int max) {
		super ("The system cannot support any more new users at the moment.");
	}
}
