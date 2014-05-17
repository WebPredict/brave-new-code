package wp.core;

import java.util.HashMap;

/**
 * @author jsanchez
 *
 */
public class WordStats {

	public static final double	MIN_PROB = .0001;
	public static final double	MAX_PROB = 1d - MIN_PROB;
	
	public HashMap<String, Double>	wordProb = new HashMap<String, Double>();
	
	public double	getWordProbability (String word) {
		Double	freq = wordProb.get(word);
		
		if (freq == null)
			return (MIN_PROB);
		return (freq);
	}
	
	public static double	adjust (double prob) {
		if (prob < WordStats.MIN_PROB)
			return (MIN_PROB);
		
		if (prob > MAX_PROB)
			return (MAX_PROB);
		return (prob);
	}
}
