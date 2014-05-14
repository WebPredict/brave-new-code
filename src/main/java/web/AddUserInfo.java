package wp.web;

public class AddUserInfo {

	private String	userId;
	private String	name;
	private boolean	alreadyAdded;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isAlreadyAdded() {
		return alreadyAdded;
	}
	public void setAlreadyAdded(boolean alreadyAdded) {
		this.alreadyAdded = alreadyAdded;
	}
	
}
