package wp.core;

public class UserMatch {

	private String	description;
	private String	userId;
	private String	name;
	private double		matchQuality;
	private String	mainPhotoId;
	
	public String getMainPhotoId() {
		return mainPhotoId;
	}
	public void setMainPhotoId(String mainPhotoId) {
		this.mainPhotoId = mainPhotoId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
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
	public double getMatchQuality() {
		return matchQuality;
	}
	public void setMatchQuality(double matchQuality) {
		this.matchQuality = matchQuality;
	}
}
