package wp.core;

public class GroupAlreadyExistsException extends Exception {

	public GroupAlreadyExistsException (String groupId) {
		super("Group " + groupId + " already exists.");
	}
}