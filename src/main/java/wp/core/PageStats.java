package wp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import wp.utils.WebUtils;

public class PageStats {

	private int	numWords;	
	private HashMap<String, Integer>	wordToFreqMap = new HashMap<String, Integer>();
	
	public PageStats () { }
	
	public	PageStats (String rawContent, boolean ignoreCaps, boolean genericizeNumbers) {
		if (rawContent == null)
			return;
		
		StringTokenizer	tok = new StringTokenizer(rawContent);
		numWords = tok.countTokens();
		wordToFreqMap.clear();
		
		while (tok.hasMoreTokens()) {
			String	cur = WebUtils.cleanWord(tok.nextToken(), ignoreCaps);
			
			if (cur == null)
				continue; // skip junk
			if (genericizeNumbers) {
				cur = cur.replaceAll(WebUtils.TIME_EXPR, WebUtils.TIME_TOKEN);					
				cur = cur.replaceAll(WebUtils.MONEY_EXPR, WebUtils.MONEY_TOKEN);					
				cur = cur.replaceAll(WebUtils.NUM_EXPR, WebUtils.NUM_TOKEN);
			}
			
			Integer	f = wordToFreqMap.get(cur);
			if (f == null) {
				wordToFreqMap.put(cur, 1);
			}
			else {
				wordToFreqMap.put(cur, f + 1);
			}
		}
	}

	PageStats (int numWords, HashMap<String, Integer> wordToFreqMap) {
		this.numWords = numWords;
		this.wordToFreqMap = wordToFreqMap;
	}
	
	public boolean	hasWord (String word) {
		return (wordToFreqMap.containsKey(word));
	}
	
	public int		wordCount (String word) {
		Integer	count = wordToFreqMap.get(word);
		return (count == null ? 0 : count);
	}
	
	public double	getProbability (String word) {
		Integer	count = wordToFreqMap.get(word);
		if (count == null)
			return (WordStats.MIN_PROB);
		
		return WordStats.adjust((double)count / (double)numWords);		
	}
	
	public int getNumWords() {
		return numWords;
	}

	public int getNumDistinctWords() {
		return wordToFreqMap.size();
	}
	
	public HashMap<String, Integer> getWordToFreqMap() {
		return wordToFreqMap;
	}
	
	public static PageStats	load (BufferedReader br) throws IOException {
		int		numWords = Integer.parseInt(br.readLine().substring("Number of words: ".length()));
		int		numDistinctWords = Integer.parseInt(br.readLine().substring("Number of distinct words: ".length()));
		
		HashMap<String, Integer>	map = new HashMap<String, Integer>();
		
		String	line;		
		while ((line = br.readLine()) != null) {
			if (line.equals("END_PAGE_STATS_DATA"))
				break;
			
			int	idx = line.indexOf(" --OCCURRENCES--> ");
			String	word = line.substring(0, idx);
			Integer	freq = Integer.parseInt(line.substring(idx + " --OCCURRENCES--> ".length()));
			map.put(word, freq);
		}
		PageStats	stats = new PageStats(numWords, map);
		
		return (stats);
	}
	
	public void	save (PrintWriter pw) {
		
		pw.println("Number of words: " + numWords);
		pw.println("Number of distinct words: " + wordToFreqMap.size());
		
		Iterator<String>	iter = wordToFreqMap.keySet().iterator();
		while (iter.hasNext()) {
			String	next = iter.next();
			Integer	freq = wordToFreqMap.get(next);
			pw.println(next + " --OCCURRENCES--> " + freq);
		}
		pw.println("END_PAGE_STATS_DATA");
	}
}