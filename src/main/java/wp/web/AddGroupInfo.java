package wp.web;

public class AddGroupInfo {

	private String	groupId;
	private String	description;
	private boolean	alreadyAdded;
	private String	name;
	private String	id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isAlreadyAdded() {
		return alreadyAdded;
	}
	public void setAlreadyAdded(boolean alreadyAdded) {
		this.alreadyAdded = alreadyAdded;
	}
	
}
