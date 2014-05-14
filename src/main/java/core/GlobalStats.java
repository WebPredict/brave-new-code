package wp.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class GlobalStats {

	private HashMap<String, Integer>	wordToOccurMap = new HashMap<String, Integer>();
	private static final String	DEF_FILENAME = "globalstats.txt";
	private double	totalNumWords = 1;
	private double	lastTotalUsedGlobalWeighting;
	
	private HashMap<String, Double>		globalWeightingMap;
	
	public void	addStats (PageStats ps) {
		HashMap<String, Integer>	map = ps.getWordToFreqMap();
		
		for (String word : map.keySet()) {
			Integer	freq = wordToOccurMap.get(word);
			Integer	add = map.get(word);
			if (freq == null)
				freq = 0;
			totalNumWords += add;
			wordToOccurMap.put(word, freq + add);
		}
		
		if (ps.getNumWords() > (lastTotalUsedGlobalWeighting * .1d))
			recomputeGlobalWeighting();
	}
	
	private void	recomputeGlobalWeighting () {
		globalWeightingMap = new HashMap<String, Double>();
		
		for (String word : wordToOccurMap.keySet()) {
			Integer	occurrences = wordToOccurMap.get(word);
			if (occurrences == null)
				occurrences = 0;
			double	freq = (double)occurrences / (double)totalNumWords;
			
			double	weighting = 1d / Math.exp(2000d * freq);
			globalWeightingMap.put(word, weighting);
		}
		lastTotalUsedGlobalWeighting = totalNumWords;
	}
	
	public HashMap<String, Double>	getGlobalWeighting () {
		if (globalWeightingMap == null)
			recomputeGlobalWeighting();
		
		return (globalWeightingMap);
	}
	
	public HashMap<String, Integer> getWordToFreqMap() {
		return wordToOccurMap;
	}

	public double	getProbabilityAnyWordIsThisWord (String word) {
		Integer	freq = wordToOccurMap.get(word);
		if (freq == null || freq == 0)
			return (WordStats.MIN_PROB);
		
		return ((double)freq / (double)totalNumWords);
		//return (WordStats.MIN_PROB); // TODO finish this
	}
	
	public static GlobalStats	load (String dir) {

		int				total = 0;
		BufferedReader	br = null;
		HashMap<String, Integer>	map = new HashMap<String, Integer>();
		try {
			br = new BufferedReader(new FileReader(dir + DEF_FILENAME));
			String	line;
			while ((line = br.readLine()) != null) {
				int		sep = line.indexOf("===>");
				
				if (sep == -1) {
					System.out.println("BAD LINE IN GLOBAL STATS: " + line);
					continue;
				}
				String	word = line.substring(0, sep);
				int		freq = 1;
				try {
					freq = Integer.parseInt(line.substring(sep + 4));
					total += freq;
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
				}
				map.put(word, freq);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		GlobalStats	stats = new GlobalStats();
		stats.wordToOccurMap = map;
		stats.totalNumWords = total;
		return (stats);
	}
	
	public void	save () {
		PrintWriter	pw = null;
		
		try {
			pw = new PrintWriter(Rater.getTheRater().getDataDir() + DEF_FILENAME);
			for (String word : wordToOccurMap.keySet()) {
				pw.println(word + "===>" + wordToOccurMap.get(word));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (pw != null)
				pw.close();
		}
	}
}
