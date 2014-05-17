package wp.core;

public class RatingCloudInfo {

	private String	word;
	private int		occurrences;
	private String	link;
	
	public RatingCloudInfo (String word, int occurrences, String link) {
		this.word = word;
		this.occurrences = occurrences;
		this.link = link;
	}
	
	public int	getFontSize () {
		if (occurrences < 20)
			return (10 + occurrences);
		return (30);
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}

	public int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}
	
}
