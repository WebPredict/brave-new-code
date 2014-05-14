package wp.core;

public class WordCloudInfo {

	private String	word;
	private int		occurrences;
	
	public WordCloudInfo (String word, int occurrences) {
		this.word = word;
		this.occurrences = occurrences;
	}
	
	public int	getFontSize () {
		if (occurrences < 20)
			return (8 + occurrences);
		return (30);
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
