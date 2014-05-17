package wp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import wp.core.Constants;


public class Prediction {

	private String	secondGuess;
	private String	rating;
	private String	userid;
	private String	url;
	private String	problem;
	private Integer []	probsPerCategory;
	private String []	aveWordProbPerCategory;
	private String []	aveNumWordsPerCategory;
	private String []	numMatchesPerCategory;
	private String []	numWeightedMatchesPerCategory;
	private List<String>	categories;
	private long	timeTook;
	private String	existingRating;
	private Integer		maxRating;
	private ArrayList<String>	debugInfo;
	private String	textAnalyzed;
	private HashSet<String>		weightedWordSet;
	private boolean		littleOrNoContent;
	private boolean		hasAds;
	private boolean		hasPopups;
	private int			numLinks;
	private int			numImages;
	private String		title;
	private String		snippet;
	private String		summary;
	private HashSet<String>	keywords = new HashSet<String>();
	
	public Prediction () {
		
	}
	
	public HashSet<String> getKeywords() {
		return keywords;
	}

	public String	getKeywordsFlattened () {
		StringBuffer	buf = new StringBuffer();
		if (keywords != null)
			for (String s : keywords) {
				if (buf.length() > 0)
					buf.append(", ");
				buf.append(s);
			}
		return (buf.toString());
	}
	
	public void setKeywords(HashSet<String> keywords) {
		this.keywords = keywords;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public boolean isHasKeywords() {
		return keywords != null && keywords.size() > 0;
	}
	
	public int getNumLinks() {
		return numLinks;
	}

	public void setNumLinks(int numLinks) {
		this.numLinks = numLinks;
	}

	public int getNumImages() {
		return numImages;
	}

	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	public boolean isHasAds() {
		return hasAds;
	}

	public void setHasAds(boolean hasAds) {
		this.hasAds = hasAds;
	}

	public boolean isHasPopups() {
		return hasPopups;
	}

	public void setHasPopups(boolean hasPopups) {
		this.hasPopups = hasPopups;
	}

	public HashSet<String> getWeightedWordSet() {
		return weightedWordSet;
	}

	public String[] getNumWeightedMatchesPerCategory() {
		return numWeightedMatchesPerCategory;
	}

	public void setNumWeightedMatchesPerCategory(
			String[] numWeightedMatchesPerCategory) {
		this.numWeightedMatchesPerCategory = numWeightedMatchesPerCategory;
	}

	public void setWeightedWordSet(HashSet<String> weightedWordSet) {
		this.weightedWordSet = weightedWordSet;
	}

	public void setAveWordProbPerCategory(String[] aveWordProbPerCategory) {
		this.aveWordProbPerCategory = aveWordProbPerCategory;
	}

	public String[] getAveNumWordsPerCategory() {
		return aveNumWordsPerCategory;
	}

	public void setAveNumWordsPerCategory(String[] aveNumWordsPerCategory) {
		this.aveNumWordsPerCategory = aveNumWordsPerCategory;
	}

	public String[] getNumMatchesPerCategory() {
		return numMatchesPerCategory;
	}

	public void setNumMatchesPerCategory(String[] numMatchesPerCategory) {
		this.numMatchesPerCategory = numMatchesPerCategory;
	}

	public String getTextAnalyzed() {
		return textAnalyzed;
	}

	public void setTextAnalyzed(String textAnalyzed) {
		this.textAnalyzed = textAnalyzed;
	}

	public String getSecondGuess() {
		return secondGuess;
	}

	public void setSecondGuess(String secondGuess) {
		this.secondGuess = secondGuess;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String[] getAveWordProbPerCategory() {
		return aveWordProbPerCategory;
	}

	public Integer[] getProbsPerCategory() {
		return probsPerCategory;
	}

	public Prediction (String problem, String userid, String url, Integer maxRating) {
		this ("", null, null, null, null, null, null, null, 
				problem, userid, url, 0, null, maxRating, new ArrayList<String>(), null, true);
	}
	
	public long getTimeTook() {
		return timeTook;
	}

	public void setTimeTook(long timeTook) {
		this.timeTook = timeTook;
	}

	public Prediction (String rating, String secondGuess, List<String> categories, HashMap<String, Integer> probsPerCategoryMap, 
			HashMap<String, Double> aveWordProbPerCategoryMap, HashMap<String, Integer> numMatchedWordsPerCategoryMap, 
			HashMap<String, Integer> numMatchedWeightedWordsPerCategoryMap, 
			HashMap<String, Double> aveNumWordsPerCategoryMap, 
			String problem, String userid, String url, long timeTook, String existingRating, Integer maxRating, 
			ArrayList<String> debugInfo, HashSet<String> weightedWordSet, boolean littleOrNoContent) {
		this.rating = rating;
		this.secondGuess = secondGuess;
		this.problem = problem;
		this.userid = userid;
		this.url = url;
		
		this.categories = categories;
		int	size = categories == null ? 0 : this.categories.size();
		this.probsPerCategory = new Integer [size];
		this.aveWordProbPerCategory = new String [size];
		this.numMatchesPerCategory = new String [size];
		this.numWeightedMatchesPerCategory = new String [size];
		this.aveNumWordsPerCategory = new String [size];
		
		if (categories != null) {
			int	count = 0;
			for (String key : this.categories) {
				this.probsPerCategory [count] = probsPerCategoryMap.get(key);
				count++;
			}
			
			count = 0;			
			for (String key : this.categories) {
				this.aveWordProbPerCategory [count] = Constants.SMALL_NUM_FORMAT.format(aveWordProbPerCategoryMap.get(key));
				count++;
			}
			
			count = 0;			
			for (String key : this.categories) {
				Integer	numMatched = numMatchedWordsPerCategoryMap.get(key);
				if (numMatched != null)
					this.numMatchesPerCategory [count] = Constants.SMALL_NUM_FORMAT.format(numMatched);
				else
					this.numMatchesPerCategory [count] = "0";
				count++;
			}
			
			count = 0;			
			for (String key : this.categories) {
				Integer	numMatched = numMatchedWeightedWordsPerCategoryMap.get(key);
				if (numMatched != null)
					this.numWeightedMatchesPerCategory [count] = Constants.SMALL_NUM_FORMAT.format(numMatched);
				else
					this.numWeightedMatchesPerCategory [count] = "0";
				count++;
			}
			
			count = 0;			
			for (String key : this.categories) {
				this.aveNumWordsPerCategory [count] = Constants.SMALL_NUM_FORMAT.format(aveNumWordsPerCategoryMap.get(key));
				count++;
			}
		}
		this.timeTook = timeTook;
		this.existingRating = existingRating;
		this.maxRating = maxRating;
		this.debugInfo = debugInfo;
		this.weightedWordSet = weightedWordSet;
		this.littleOrNoContent = littleOrNoContent;
	}

	public boolean	hasLittleOrNoContent () {
		return (littleOrNoContent);
	}
	
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}
	
	public String	getExistingRating () {
		return (existingRating);
	}
	
	public Integer	getMaxRating () {
		return (maxRating);
	}
	
	public boolean	isSuccess () {
		return (problem == null);
	}
	
	public List<String>	getDebugInfo () {
		return (debugInfo);
	}
	
	public String	toString () {
		if (problem != null)
			return ("Prediction failed because: " + problem);
		
		return ("Prediction(s): " + rating + (secondGuess != null ? ", " + secondGuess : "") + " for " + prepUrl() + " (took: " + timeTook + " milliseconds).");
	}

	public String getUserid() {
		return userid;
	}

	private String	prepUrl () {
		return (url.startsWith("file:") ? url.substring(url.lastIndexOf("/")) : url);
	}
	
	public String getUrl() {
		return url;
	}
}