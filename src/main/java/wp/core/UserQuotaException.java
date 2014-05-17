package wp.core;

public class UserQuotaException extends Exception {

	public UserQuotaException (int quota, String userId, String what) {
		super ("User '" + userId + "' exceeded quota " + quota + " for " + what + ".");
	}
}
