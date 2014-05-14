package wp.core;

public class UserCloudInfo {

	private String	username;
	private int		occurrences;
	
	public UserCloudInfo (String username, int occurrences) {
		this.username = username;
		this.occurrences = occurrences;
	}
	
	public int	getFontSize () {
		if (occurrences < 20)
			return (10 + occurrences);
		return (30);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}
	
}
