package wp.core;

public class AlreadyExistsException extends Exception {

	public AlreadyExistsException (String itemType, String groupId) {
		super(itemType + " " + groupId + " already exists.");
	}
}