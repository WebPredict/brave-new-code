package wp.model;

public class WordFreqPair {

	public String getWord() {
		if (word == null)
			return ("");
		if (word.length() > 30)
			return (word.substring(0, 30) + "...");
		return (word);
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int	getWordLength () {
		return (word.length());
	}
	
	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public String	word;
	public int		freq;
	
	public WordFreqPair (String w, Integer f) {
		word = w;
		freq = f;
	}
}
