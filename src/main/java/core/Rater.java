package wp.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.classifier4J.summariser.SimpleSummariser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import wp.dao.PageDao;
import wp.model.PageDiff;
import wp.model.ParsedPage;
import wp.model.Prediction;
import wp.model.RatedPage;
import wp.model.User;
import wp.model.UserStats;

public class Rater {

	private HashMap<String, HashMap<String,Prediction>>	userPredictionCache = new HashMap<String, HashMap<String, Prediction>>();
	private HashMap<String, String>	userCodes = new HashMap<String, String>();
	private GlobalStats	globalStats;
	
	public String	dataDir = "c:/WebPredictData/"; // System.getProperty("java.io.tmpdir") + "/";	
	public String	cacheDir = "c:/WebPredictCache/";
	public String	commonDir = "c:/WebPredictData/";
	private String	serverName = "http://localhost:8889";
	private String	addOnVersion = "1.3.1";
	
	public String getAddOnVersion() {
		return addOnVersion;
	}

	public void setAddOnVersion(String addOnVersion) {
		this.addOnVersion = addOnVersion;
	}

	private static boolean	DEBUG = false;
	private static boolean	USING_CACHE = false;
	
	private static Rater	THE_RATER = null;
	
	private int	numTotalPagesRated = 0;
	private int	numTotalPagesPredicted = 0;

	public static final SimpleDateFormat	SHORT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	
	public static final int	MAX_WORDS_TO_CONSIDER = 10000;
	
	public static HashSet<String>	VULGAR_WORDS;
	public static HashSet<String>	COMMON_WORDS;
	
	public void initWordSets () {
		if (COMMON_WORDS != null)
			return;
		
		COMMON_WORDS = Utils.readWordSet(getDataDir() + "commonwords.csv");
		VULGAR_WORDS = Utils.readWordSet(getDataDir() + "vulgarwords.csv");
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getCommonDir() {
		return commonDir;
	}

	public void setCommonDir(String commonDir) {
		this.commonDir = commonDir;
	}

	public HashSet<String>	getCommonWords () {
		return (COMMON_WORDS);
	}
	
	public HashSet<String>	getVulgarities () {
		return (VULGAR_WORDS);
	}
	
	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	public String getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	public int getNumTotalPagesRated() {
		return numTotalPagesRated;
	}

	public void setNumTotalPagesRated(int numTotalPagesRated) {
		this.numTotalPagesRated = numTotalPagesRated;
	}

	public int getNumTotalPagesPredicted() {
		return numTotalPagesPredicted;
	}

	public void setNumTotalPagesPredicted(int numTotalPagesPredicted) {
		this.numTotalPagesPredicted = numTotalPagesPredicted;
	}
	
	public ArrayList<wp.model.Recommendation>	getRecommendations (List<String> rps, UserStats ustat, int searchSize, int resultSize, 
			Collection<String> desirableRatings, Collection<String> dontShow, PageDao pageDao) 
	throws UserQuotaException, UserDisabledException {
		if (desirableRatings == null || desirableRatings.size() == 0)
			return (null);
		
		int							counter = 0;
		ArrayList<wp.model.Recommendation>	recs = new ArrayList<wp.model.Recommendation>();
		int	maxPagesToConsider = rps.size() < searchSize ? rps.size() : searchSize; // TODO reasonable cutoff here - this is a heavy operation... needs to use cache
		HashSet<String>	seenUrls = new HashSet<String>();
		Collection<String>	keywords = null; // TODO hook it up
		
		while (counter < maxPagesToConsider && seenUrls.size() < resultSize) {
			String	origUrl = rps.get(counter++); //RandomUtils.nextInt(rps.size()));
			
			URL url;
			try {
				if (origUrl == null || (dontShow != null && dontShow.contains(origUrl)))
					continue;
				
				url = Utils.normalizeUrl(new URL(origUrl));	
				String	urlStr = url.toString();
				
				Prediction pred = predictRating(ustat, new CachedURL(url, 8640000000l, null), false, keywords, false, DEFAULT_SNIPPET_SIZE);
				
				if (desirableRatings.contains(pred.getRating()) && !seenUrls.contains(urlStr)) {
					ParsedPage	pp = pageDao.findParsedPageBYURL(origUrl);
					
					if (StringUtils.isEmpty(pp.getTitle()) || StringUtils.isEmpty(pp.getFirstLine()))
						continue; // avoid crappy results
					
					wp.model.Recommendation	rec = new wp.model.Recommendation();
					rec.setPrediction(pred.getRating());
					rec.setSnippet(pp.getFirstLine());
					rec.setTitle(pp.getTitle());
					rec.setUrlStr(urlStr);
					recs.add(rec);
					seenUrls.add(urlStr);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Collections.sort(recs, new Comparator () {

			public int compare(Object arg0, Object arg1) {
				wp.model.Recommendation	r0 = (wp.model.Recommendation)arg0;
				wp.model.Recommendation	r1 = (wp.model.Recommendation)arg1;
				
				return (r1.getPrediction().compareTo(r0.getPrediction()));
			}
			
		});
		
		return (recs);
	}

	public String	generateNewUserConfirmCode (String userId) {
		String	code = String.valueOf(System.currentTimeMillis());
		userCodes.put(userId, code);
		return (code); 
	}
	
	public boolean	checkUserConfirmCode (String userId, String code) {
		return (userCodes.get(userId) != null && userCodes.get(userId).equals(code));
	}
	
	public RatingInfo	ratePage (UserStats stats, CachedURL url, String rating) throws UserQuotaException, UserDisabledException {
		return (ratePage(stats, url, rating, null));
	}
	
	public RatingInfo	ratePage (UserStats stats, CachedURL url, String rating, String comment) 
	throws UserQuotaException, UserDisabledException {
		User	user = stats.getUser();
		
		if (user.isDisabled())
			throw new UserDisabledException (stats.getUserId());
		
		int	quota = user.getDailyRatingQuota();
		if (stats.getNumRatingsToday() > quota)
			throw new UserQuotaException (quota, stats.getUserId(), "rating");
		
		try {
			URL			normalizedUrl = Utils.normalizeUrl(url.getURL());	
			RatedPage	rp = stats.getRatedPagesMap().get(normalizedUrl);
			ParsedPage	content = rp == null ? null : rp.getParsedPage();
			content = new WebUtils().newParse(url, user.isGenericizeNumbers(), 
					user.isConsiderAds(), user.isUseStrictParser(), DEFAULT_SNIPPET_SIZE, content);
			return (ratePage(stats, url.getURL(), content, rating, comment));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (new RatingInfo(RatingStatus.failed, e.toString(), url.getURL(), rating));
		}
		
	} 
	
	public RatingInfo	ratePage (UserStats stats, URL url, String rating, String comment,
			String cookiesToSend) throws UserQuotaException, UserDisabledException {
		return (ratePage(stats, new CachedURL(url, cookiesToSend), rating, comment));
	} 
	
	private RatingInfo	ratePage (UserStats stats, URL url, ParsedPage content, String rating, String comment) {			
		try {					
			boolean	updated = stats.pageRated(rating, url, content, comment);
			if (!updated) {
				synchronized (this) {
					HashMap<String, Prediction>	cache = userPredictionCache.get(stats.getUserId());
					if (cache != null)
						cache.clear();
					numTotalPagesRated++;
					
					PageStats	ps = new PageStats(content.getRawContent(), false, false);
					globalStats.addStats(ps);
					save(); 
				}
			}
			return (new RatingInfo(updated ? RatingStatus.updated : RatingStatus.added, null, url, rating));
		} 
		catch (Exception e) {
			e.printStackTrace();
			return (new RatingInfo(RatingStatus.failed, e.toString(), url, rating));
		}		
	}
	
	public GlobalStats	getGlobalStats () {
		return (globalStats);
	}

	public static final int	DEFAULT_SNIPPET_SIZE = 255;
	
	public PageDiff	difference (UserStats stats, URL reference, URL compare) {

		PageDiff	diff = new PageDiff();
		try {
			CachedURL	refCache = new CachedURL(reference);
			CachedURL	compareCache = new CachedURL(compare);

			User	user = stats.getUser();
			
			boolean			genericizeNumbers = user.isGenericizeNumbers();
			boolean			ignoreCaps = user.isIgnoreCapitalization();
			boolean			considerAds = user.isConsiderAds();
			boolean			useStrict = user.isUseStrictParser();
			
			ParsedPage	refContent = new WebUtils().newParse(refCache, genericizeNumbers, considerAds, 
					useStrict, DEFAULT_SNIPPET_SIZE, null);
			PageStats	refPs = new PageStats(refContent.getRawContent(), ignoreCaps, genericizeNumbers);		
			HashMap<String, Integer>	refMap = refPs.getWordToFreqMap();		
			
			ParsedPage	diffContent = new WebUtils().newParse(compareCache, genericizeNumbers, considerAds, 
					useStrict, DEFAULT_SNIPPET_SIZE, null);
			PageStats	diffPs = new PageStats(diffContent.getRawContent(), ignoreCaps, genericizeNumbers);		
			HashMap<String, Integer>	diffMap = diffPs.getWordToFreqMap();		
			
			
			double	diffScore = 0;
			Set<String>	refKeys = refMap.keySet();
			Set<String>	diffKeys = diffMap.keySet();
			Collection<String>	diffSet = CollectionUtils.intersection(refKeys, diffKeys);
			
			double	total = refKeys.size() + diffKeys.size();
			if (total > 0)
				diffScore = (double)diffSet.size() / total;
			
			diff.setDiffScore(diffScore);
			diff.setSnippet(diffContent.getFirstLine());
			diff.setTitle(diff.getTitle());
			diff.setUrlStr(compare.toString());
			
		}
		catch (Exception e) {
			
		}
		
		return (diff);
	}
	
	public Prediction predictRawContent(UserStats stats, String rawContent, int snippetSize)
			throws UserQuotaException, UserDoesntExistException, SQLException {
		File blah;
		PrintWriter	writer = null;
		try {
			blah = File.createTempFile("pred", null);
			BufferedWriter	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blah), "UTF-8"));
			writer = new PrintWriter(bw);
			writer.write(rawContent);
			writer.flush();
			writer.close();
			
			URL		url = new URL ("file://localhost/" + blah.getAbsolutePath());
			return (predictRating(stats, url, null, null, false, snippetSize));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (writer != null)
				writer.close();
		}
		return (null);
	}

	public Prediction	predictRating (UserStats stats, URL url, String cookieData, 
			Collection<String> keywords, boolean summarize, int snippetSize) {
		return (predictRating(stats, url, true, cookieData, keywords, summarize, snippetSize));
	}
	
	public Prediction	predictRating (UserStats stats, URL url, boolean countIt, String cookieData, 
			Collection<String> keywords, boolean summarize, int snippetSize) {
		try {
			return (predictRating(stats, new CachedURL(Utils.normalizeUrl(url), cookieData), countIt, keywords, summarize, snippetSize));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String	userId = stats == null ? "UNKNOWN USER" : stats.getUserId();
			String	urlStr = url == null ? "UNKNOWN URL" : url.toString();
			return (new Prediction("Could not predict because: " + e.toString(), userId, urlStr, 0));
		}
	}
	
	public Prediction	predictRating (UserStats stats, CachedURL url, boolean summarize, int snippetSize) throws UserQuotaException, UserDisabledException {	
		return (predictRating(stats, url, true, null, summarize, snippetSize));
	}
	
	public Prediction	predictRating (UserStats stats, CachedURL url, boolean countIt, Collection<String> keywords, 
			boolean summarize, int snippetSize) 
		throws UserQuotaException, UserDisabledException {	
		try { 
			return (predictRatingInt(stats, url, countIt, keywords, summarize, snippetSize));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (new Prediction("Could not predict because: " + e.toString(), stats.getUserId(), url.toString(), 0));
		}
	}
	
	private Prediction	predictRatingInt (UserStats stats, CachedURL cachedUrl, boolean countIt, 
			Collection<String> keywords, boolean summarize, int snippetSize) 
	throws UserQuotaException, UserDisabledException, IOException {
		User	user = stats.getUser();
		URL		url = cachedUrl.getURL();
		if (user.isDisabled())
			throw new UserDisabledException (stats.getUserId());
		
		if (countIt) {
			int	quota = user.getDailyPredictionQuota();
			if (stats.getNumPredictionsToday() > quota)
				throw new UserQuotaException (quota, stats.getUserId(), "predictions");
		}
		
		Date	startTime = new Date();
		String	userId = stats.getUserId();
		
		if (USING_CACHE) {
			if (userPredictionCache.containsKey(userId)) {
				HashMap<String, Prediction>	cache = userPredictionCache.get(userId);
				if (cache.containsKey(url.toString())) {
					Date	end = new Date();
					Prediction	pred = cache.get(url.toString());
					pred.setTimeTook(end.getTime() - startTime.getTime());
					return (pred);
				}
			}
			else 
				userPredictionCache.put(userId, new HashMap<String, Prediction>());
		}
		
		AllPageStats aps = stats.getAllPageStats();		
				
		// TODO: cache the parsed page, not the raw content
		if (user.isUseCache())
			cachedUrl.setCacheLifeMillis(86400000 * 1000); // long time
		else
			cachedUrl.setCacheLifeMillis(10); // so getContent does not refetch page inside tight method calls -- TODO do somewhere else 
		
		ParsedPage	content = null;
		RatedPage	existing = stats.getRatedPagesMap().get(url);
		if (existing != null)
			content = existing.getParsedPage();

		boolean			genericizeNumbers = user.isGenericizeNumbers();
		boolean			ignoreCaps = user.isIgnoreCapitalization();
		content = new WebUtils().newParse(cachedUrl, user.isGenericizeNumbers(), user.isConsiderAds(), 
				user.isUseStrictParser(), DEFAULT_SNIPPET_SIZE, content);
		PageStats	ps = new PageStats(content.getRawContent(), ignoreCaps, genericizeNumbers);		
		HashMap<String, Integer>	map = ps.getWordToFreqMap();		
		HashMap<String, Double>	catToLogOddsMap = new HashMap<String, Double>();
		StringBuffer	infoBuf = new StringBuffer();
		HashMap<String, Double> aveWordProbPerCategoryMap = new HashMap<String, Double>();
		HashMap<String, Integer> numMatchedWordsPerCategoryMap = new HashMap<String, Integer>();
		HashMap<String, Integer> numMatchedWeightedWordsPerCategoryMap = new HashMap<String, Integer>();
		HashMap<String, Double> aveNumWordsPerCategoryMap = new HashMap<String, Double>();
		HashSet<String>	weightedWordSet = new HashSet<String>();
		List<String>	cats = user.getCategorySet().getCategories();
		int				catCount = 0;
		HashMap<String, Double>	globalWeighting = globalStats.getGlobalWeighting();
		HashSet<String>	seenKeywords = new HashSet<String>();
		
		ArrayList<String>	debugInfo = new ArrayList<String>();
		for (String category : cats) {		
			int					wordRepeats = 0;
			double				totalProb = 0;
			Iterator<String>	wordIter = map.keySet().iterator();
			boolean				firstTime = catCount == 0;
			
			if (DEBUG) {
				System.out.println ("===========================================");
				System.out.println ("PROBABILITIES FOR CATEGORY: " + category);
			}
			
			boolean	smooth = user.isSmoothProbs() && Utils.isInteger(category);
			
			aveNumWordsPerCategoryMap.put(category, aps.getAveNumWords(category));
			HashMap<Integer, Double>	idxToGlobalProbMap = new HashMap<Integer, Double>();
			
			ArrayList<Double>	wordProbs = new ArrayList<Double>();
			ArrayList<String>	wordList = new ArrayList<String>();
			
			while (wordIter.hasNext()) {
				String	next = wordIter.next();
	
				if (keywords != null && keywords.contains(next.toLowerCase()))
					seenKeywords.add(next);
				
				// Hmm should we respect case here if user prefs say so? Hard to say without
				// context (e.g., it's a common word that starts a sentence).
				// TODO: update common words to include versions with first letter caps.
				if (user.isIgnoreCommonWords() && COMMON_WORDS.contains(next.toLowerCase()))
					continue;
				
				Integer	occurrences = map.get(next);
				
				if (occurrences > 1)
					wordRepeats += occurrences - 1;
				
				double	prob = aps.getProbability(next, category, smooth);
				totalProb += prob;

				//boolean	wordInCat = aps.wordInCategory(next, category);
				boolean	wordInCat = prob > WordStats.MIN_PROB;
				if (wordInCat) {
					Integer	val = numMatchedWordsPerCategoryMap.get(category);
					if (val == null)
						val = 1;
					else
						val++;
					numMatchedWordsPerCategoryMap.put(category, val);
					
					//if (DEBUG)
						//System.out.println("MATCHED: " + next + " IN CAT: " + category);
				}
				
				if (firstTime && user.isReturnAnalyzedText()) 
					infoBuf.append(next + " ");
				
				if (prob > 0) {// 0 here really means we've never seen the word before, and thus have no info on it	
					int	occursToUse = user.isCountRepeats() ? occurrences : 1;
					for (int repeat = 0; repeat < occursToUse; repeat++) {
						wordProbs.add(prob);
						wordList.add(next);
						
						if (user.isUsePreciseWordProb()) {
							Double	globalProb = globalWeighting.get(next);
							if (globalProb != null)
								idxToGlobalProbMap.put(wordProbs.size() - 1, globalProb);
						}
						
						if (wordProbs.size() > user.getMaxWordsToConsider())
							break; // guard against really long documents
					}
					
					if (wordProbs.size() > user.getMaxWordsToConsider())
						break; // guard against really long documents
				
				}
			}
			
			// TODO: for things like link/content ratio, decide how to divide up into bins of some small number
			
			HashMap<Integer, Double>	idxToWeightMap = new HashMap<Integer, Double>();
			
			if (user.isConsiderAds()) {
				boolean	hasAds = content.isHasAds();
				double	probHasAds = aps.getProbabilityHasAds(category);
				if (hasAds)
					wordProbs.add(probHasAds);
				else
					wordProbs.add(1d - probHasAds);
				
				wordList.add("HAS_ADS");
				
				idxToWeightMap.put(wordProbs.size() - 1, 10d); // who knows
			}
			
			if (user.isConsiderPopups()) {
				boolean	hasPopups = content.isHasPopups();
				double	probHasPopups = aps.getProbabilityHasPopups(category);
				if (hasPopups)
					wordProbs.add(probHasPopups);
				else
					wordProbs.add(1d - probHasPopups);
				
				wordList.add("HAS_POPUPS");
				
				idxToWeightMap.put(wordProbs.size() - 1, 10d); // who knows
			}
			
			if (user.isConsiderNumLinks()) {
				int	numLinks = content.getNumLinks();
				double	linkCountProb = aps.getProbabilityOfLinkCount(category, numLinks);
				wordProbs.add(linkCountProb);
				
				wordList.add("LINK_COUNT");
				
				idxToWeightMap.put(wordProbs.size() - 1, 10d); // who knows
			}
			if (user.isConsiderNumImages()) {
				int	numImages = content.getNumImages();
				double	imageCountProb = aps.getProbabilityOfImageCount(category, numImages);
				wordProbs.add(imageCountProb);
				
				wordList.add("IMAGE_COUNT");
				
				idxToWeightMap.put(wordProbs.size() - 1, 10d); // who knows
			}
				
			double	weight = user.getHeadlinesWeight();
			
			addProbs(wordProbs, wordList, content.getKeywords(), ",", weight, idxToWeightMap, numMatchedWeightedWordsPerCategoryMap, 
					weightedWordSet, aps, category, smooth, ignoreCaps);
			addProbs(wordProbs, wordList, content.getTitle(), " ", weight, idxToWeightMap, numMatchedWeightedWordsPerCategoryMap, 
					weightedWordSet, aps, category, smooth, ignoreCaps);
			addProbs(wordProbs, wordList, content.getDescription(), " ", weight, idxToWeightMap, numMatchedWeightedWordsPerCategoryMap, 
					weightedWordSet, aps, category, smooth, ignoreCaps);
			addProbs(wordProbs, wordList, content.getHeadlines(), " ", weight, idxToWeightMap, numMatchedWeightedWordsPerCategoryMap, 
					weightedWordSet, aps, category, smooth, ignoreCaps);
						
			if (user.isWeightLinks()) {
				for (String link : content.getLinksSet()) {
					addProbs(wordProbs, wordList, link, " ", weight, idxToWeightMap, numMatchedWeightedWordsPerCategoryMap, 
							weightedWordSet, aps, category, smooth, ignoreCaps);
				}
			}
			
			double	aveWordProbPerCat = wordProbs.size() == 0 ? 0 : (totalProb / (double)wordProbs.size());
			aveWordProbPerCategoryMap.put(category, aveWordProbPerCat);
			
			if (DEBUG) {
				int	contentSize = content.getContentSize();
				
				System.out.println();
				String	debug = null;
				if (catCount == 0) {
					debug = "Total number of word repeats for this document: " + wordRepeats;
					System.out.println (debug);
					debugInfo.add(debug);
					debug = "Content size: " + contentSize + " stripped content size: " + content.getStrippedContentSize();
					debugInfo.add(debug);
				}
				debug = "AVERAGE WORD PROBABILITY FOR CATEGORY: " + category + ": " + aveWordProbPerCat;
				//debugInfo.add(debug);
				System.out.println (debug);				
			}
			
			Double []	wordProbsArr = new Double [wordProbs.size()];
			wordProbs.toArray(wordProbsArr);
						
			if (DEBUG && catCount == 0)
				System.out.println ("Distinct words seen: " + wordProbs.size());
			
			double	newTotalLogOdds = 0; 
			double	newTotalNotLogOdds = 0; 
		
			for (int j = 0; j < wordProbsArr.length; j++) {
				Double	weighting = idxToWeightMap.get(j);
				if (weighting == null) 
					weighting = 1d;
				
				Double	globWeighting = idxToGlobalProbMap.get(j);

				if (DEBUG && catCount == 0)
					System.out.println("WORD: " + wordList.get(j) + " WEIGHTING: " + 
							weighting + " GLOBAL WEIGHTING: " + globWeighting + 
							" FREQUENCY: " + globalStats.getProbabilityAnyWordIsThisWord(wordList.get(j)));

				if (globWeighting != null)
					weighting *= globWeighting;
				
				newTotalNotLogOdds += weighting * Math.log(1d - wordProbsArr [j]);					
				newTotalLogOdds += weighting * Math.log(wordProbsArr [j]);									
			}
			
			double	probOfCategory = aps.getProbabilityOfCategory(category, smooth);
			double	notProbOfCategory = 1d - probOfCategory;
			
			newTotalLogOdds += Math.log(probOfCategory);
			newTotalNotLogOdds += Math.log(notProbOfCategory);
			
			catToLogOddsMap.put(category, newTotalLogOdds - newTotalNotLogOdds); 				
			catCount++;
		}
						
		double	total = 0;
		for (Double logOdds : catToLogOddsMap.values())
			total += logOdds;
		
		Set<String>	categories = catToLogOddsMap.keySet();
		Integer		maxRating = stats.getMaxRating(); // can be null if user has no ordered numeric set of ratings!
		if (maxRating == null)
			maxRating = categories.size();
		
		HashMap<String, Integer>	normalizedLogOddsPerCategory = new HashMap<String, Integer>();		
		double	aveLogOdds = maxRating == 0 ? 0 : total / (double)maxRating;
		
		for (String category : categories) {
			normalizedLogOddsPerCategory.put(category, (int)Math.round(catToLogOddsMap.get(category) - aveLogOdds));
			
			if (DEBUG)
				System.out.println ("LOG ODDS OF CATEGORY " + category + " IS: " + normalizedLogOddsPerCategory.get(category));			
		}
		
		int	maxLogOdds = Integer.MIN_VALUE;
		String		resultCategory = "";
		for (String category : categories) {
			if (maxLogOdds <= normalizedLogOddsPerCategory.get(category)) {
				resultCategory = category;
				maxLogOdds = normalizedLogOddsPerCategory.get(category);
			}
		}
		int	prevMax = maxLogOdds;
		maxLogOdds = Integer.MIN_VALUE;
		String		secondGuess = null;
		for (String category : categories) {
			Integer	normOdds = normalizedLogOddsPerCategory.get(category);
			if (maxLogOdds <= normOdds && normOdds < prevMax) {
				secondGuess = category;
				maxLogOdds = normOdds;
			}
		}
		
		if (DEBUG)
			System.out.println ("MAX LOG ODDS: " + maxLogOdds);
		
		if (DEBUG)
			System.out.println ("FINAL RATING: " + resultCategory);
		
		RatedPage	rp = stats.getRatedPagesMap().get(url);
		String		existingRating = rp == null ? null : rp.getRating();
		boolean		littleOrNoContent = content.getContentSize() < 30;
		Date	endTime = new Date();
		
		Prediction	pred = new Prediction(resultCategory, secondGuess, cats, normalizedLogOddsPerCategory, 
				aveWordProbPerCategoryMap, numMatchedWordsPerCategoryMap, numMatchedWeightedWordsPerCategoryMap, aveNumWordsPerCategoryMap, null, stats.getUserId(), url.toString(), 
				endTime.getTime() - startTime.getTime(), existingRating, stats.getMaxRating(), debugInfo, weightedWordSet, littleOrNoContent);
		
		pred.setKeywords(seenKeywords);
		pred.setTitle(content.getTitle());
		pred.setSnippet(content.getFirstLine());
		
		if (summarize) {
			SimpleSummariser	ss = new SimpleSummariser();
			String				raw = content.getRawContent();
			String				summary = ss.summarise(raw, 2);
			pred.setSummary(summary);
		}
		
		if (user.isReturnAnalyzedText())
			pred.setTextAnalyzed(infoBuf.toString());
		
		if (user.isConsiderAds())
			pred.setHasAds(content.isHasAds());

		if (user.isConsiderPopups())
			pred.setHasPopups(content.isHasPopups());
		
		if (user.isConsiderNumImages())
			pred.setNumImages(content.getNumImages());
		
		if (user.isConsiderNumLinks())
			pred.setNumLinks(content.getNumLinks());
		
		try {
			stats.pagePredicted();
			//stats.save();
			synchronized (this) {
				if (USING_CACHE) {
					HashMap<String, Prediction>	cache = userPredictionCache.get(userId);
					cache.put(url.toString(), pred);
				}
				if (countIt)
					numTotalPagesPredicted++;
				save();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println ("PREDICTION for " + url + " took: " + (endTime.getTime() - startTime.getTime()) + " milliseconds.");
				
		return (pred);
	}

	public Prediction	predictRatingSimpleText (UserStats stats, String text,  
			Collection<String> keywords, int snippetSize) 
	throws UserQuotaException, UserDisabledException, IOException {
		if (text == null)
			text = "";
		text = text.trim();
		
		User	user = stats.getUser();
		if (user.isDisabled())
			throw new UserDisabledException (stats.getUserId());
		
		Date	startTime = new Date();				
		AllPageStats aps = stats.getAllPageStats();		
				
		boolean			genericizeNumbers = user.isGenericizeNumbers();
		boolean			ignoreCaps = user.isIgnoreCapitalization();
		PageStats	ps = new PageStats(text, ignoreCaps, genericizeNumbers);		
		HashMap<String, Integer>	map = ps.getWordToFreqMap();		
		HashMap<String, Double>	catToLogOddsMap = new HashMap<String, Double>();
		StringBuffer	infoBuf = new StringBuffer();
		HashMap<String, Double> aveWordProbPerCategoryMap = new HashMap<String, Double>();
		HashMap<String, Integer> numMatchedWordsPerCategoryMap = new HashMap<String, Integer>();
		HashMap<String, Integer> numMatchedWeightedWordsPerCategoryMap = new HashMap<String, Integer>();
		HashMap<String, Double> aveNumWordsPerCategoryMap = new HashMap<String, Double>();
		HashSet<String>	weightedWordSet = new HashSet<String>();
		List<String>	cats = user.getCategorySet().getCategories();
		int				catCount = 0;
		HashMap<String, Double>	globalWeighting = globalStats.getGlobalWeighting();
		HashSet<String>	seenKeywords = new HashSet<String>();
		
		ArrayList<String>	debugInfo = new ArrayList<String>();
		for (String category : cats) {		
			int					wordRepeats = 0;
			double				totalProb = 0;
			Iterator<String>	wordIter = map.keySet().iterator();
			boolean				firstTime = catCount == 0;
			
			if (DEBUG) {
				System.out.println ("===========================================");
				System.out.println ("PROBABILITIES FOR CATEGORY: " + category);
			}
			
			boolean	smooth = user.isSmoothProbs() && Utils.isInteger(category);
			
			aveNumWordsPerCategoryMap.put(category, aps.getAveNumWords(category));
			HashMap<Integer, Double>	idxToGlobalProbMap = new HashMap<Integer, Double>();
			
			ArrayList<Double>	wordProbs = new ArrayList<Double>();
			ArrayList<String>	wordList = new ArrayList<String>();
			
			while (wordIter.hasNext()) {
				String	next = wordIter.next();
	
				if (keywords != null && keywords.contains(next.toLowerCase()))
					seenKeywords.add(next);
				
				// Hmm should we respect case here if user prefs say so? Hard to say without
				// context (e.g., it's a common word that starts a sentence).
				// TODO: update common words to include versions with first letter caps.
				if (user.isIgnoreCommonWords() && COMMON_WORDS.contains(next.toLowerCase()))
					continue;
				
				Integer	occurrences = map.get(next);
				
				if (occurrences > 1)
					wordRepeats += occurrences - 1;
				
				double	prob = aps.getProbability(next, category, smooth);
				totalProb += prob;

				//boolean	wordInCat = aps.wordInCategory(next, category);
				boolean	wordInCat = prob > WordStats.MIN_PROB;
				if (wordInCat) {
					Integer	val = numMatchedWordsPerCategoryMap.get(category);
					if (val == null)
						val = 1;
					else
						val++;
					numMatchedWordsPerCategoryMap.put(category, val);
					
					//if (DEBUG)
						//System.out.println("MATCHED: " + next + " IN CAT: " + category);
				}
				
				if (firstTime && user.isReturnAnalyzedText()) 
					infoBuf.append(next + " ");
				
				if (prob > 0) {// 0 here really means we've never seen the word before, and thus have no info on it	
					int	occursToUse = user.isCountRepeats() ? occurrences : 1;
					for (int repeat = 0; repeat < occursToUse; repeat++) {
						wordProbs.add(prob);
						wordList.add(next);
						
						if (user.isUsePreciseWordProb()) {
							Double	globalProb = globalWeighting.get(next);
							if (globalProb != null)
								idxToGlobalProbMap.put(wordProbs.size() - 1, globalProb);
						}
						
						if (wordProbs.size() > user.getMaxWordsToConsider())
							break; // guard against really long documents
					}
					
					if (wordProbs.size() > user.getMaxWordsToConsider())
						break; // guard against really long documents
				
				}
			}
			
			// TODO: for things like link/content ratio, decide how to divide up into bins of some small number
			
			HashMap<Integer, Double>	idxToWeightMap = new HashMap<Integer, Double>();
			
			double	aveWordProbPerCat = wordProbs.size() == 0 ? 0 : (totalProb / (double)wordProbs.size());
			aveWordProbPerCategoryMap.put(category, aveWordProbPerCat);
			
			if (DEBUG) {
				int	contentSize = text.length();
				
				System.out.println();
				String	debug = null;
				if (catCount == 0) {
					debug = "Total number of word repeats for this document: " + wordRepeats;
					System.out.println (debug);
					debugInfo.add(debug);
					debug = "Content size: " + contentSize + " stripped content size: " + text.length();
					debugInfo.add(debug);
				}
				debug = "AVERAGE WORD PROBABILITY FOR CATEGORY: " + category + ": " + aveWordProbPerCat;
				//debugInfo.add(debug);
				System.out.println (debug);				
			}
			
			Double []	wordProbsArr = new Double [wordProbs.size()];
			wordProbs.toArray(wordProbsArr);
						
			if (DEBUG && catCount == 0)
				System.out.println ("Distinct words seen: " + wordProbs.size());
			
			double	newTotalLogOdds = 0; 
			double	newTotalNotLogOdds = 0; 
		
			for (int j = 0; j < wordProbsArr.length; j++) {
				Double	weighting = idxToWeightMap.get(j);
				if (weighting == null) 
					weighting = 1d;
				
				Double	globWeighting = idxToGlobalProbMap.get(j);

				if (DEBUG && catCount == 0)
					System.out.println("WORD: " + wordList.get(j) + " WEIGHTING: " + 
							weighting + " GLOBAL WEIGHTING: " + globWeighting + 
							" FREQUENCY: " + globalStats.getProbabilityAnyWordIsThisWord(wordList.get(j)));

				if (globWeighting != null)
					weighting *= globWeighting;
				
				newTotalNotLogOdds += weighting * Math.log(1d - wordProbsArr [j]);					
				newTotalLogOdds += weighting * Math.log(wordProbsArr [j]);									
			}
			
			double	probOfCategory = aps.getProbabilityOfCategory(category, smooth);
			double	notProbOfCategory = 1d - probOfCategory;
			
			newTotalLogOdds += Math.log(probOfCategory);
			newTotalNotLogOdds += Math.log(notProbOfCategory);
			
			catToLogOddsMap.put(category, newTotalLogOdds - newTotalNotLogOdds); 				
			catCount++;
		}
						
		double	total = 0;
		for (Double logOdds : catToLogOddsMap.values())
			total += logOdds;
		
		Set<String>	categories = catToLogOddsMap.keySet();
		Integer		maxRating = stats.getMaxRating(); // can be null if user has no ordered numeric set of ratings!
		if (maxRating == null)
			maxRating = categories.size();
		
		HashMap<String, Integer>	normalizedLogOddsPerCategory = new HashMap<String, Integer>();		
		double	aveLogOdds = maxRating == 0 ? 0 : total / (double)maxRating;
		
		for (String category : categories) {
			normalizedLogOddsPerCategory.put(category, (int)Math.round(catToLogOddsMap.get(category) - aveLogOdds));
			
			if (DEBUG)
				System.out.println ("LOG ODDS OF CATEGORY " + category + " IS: " + normalizedLogOddsPerCategory.get(category));			
		}
		
		int	maxLogOdds = Integer.MIN_VALUE;
		String		resultCategory = "";
		for (String category : categories) {
			if (maxLogOdds <= normalizedLogOddsPerCategory.get(category)) {
				resultCategory = category;
				maxLogOdds = normalizedLogOddsPerCategory.get(category);
			}
		}
		int	prevMax = maxLogOdds;
		maxLogOdds = Integer.MIN_VALUE;
		String		secondGuess = null;
		for (String category : categories) {
			Integer	normOdds = normalizedLogOddsPerCategory.get(category);
			if (maxLogOdds <= normOdds && normOdds < prevMax) {
				secondGuess = category;
				maxLogOdds = normOdds;
			}
		}
		
		if (DEBUG)
			System.out.println ("MAX LOG ODDS: " + maxLogOdds);
		
		if (DEBUG)
			System.out.println ("FINAL RATING: " + resultCategory);
		
		boolean		littleOrNoContent = text.length() < 30;
		Date	endTime = new Date();
		
		Prediction	pred = new Prediction(resultCategory, secondGuess, cats, normalizedLogOddsPerCategory, 
				aveWordProbPerCategoryMap, numMatchedWordsPerCategoryMap, numMatchedWeightedWordsPerCategoryMap, aveNumWordsPerCategoryMap, null, 
				stats.getUserId(), "transient text string", 
				endTime.getTime() - startTime.getTime(), null, stats.getMaxRating(), debugInfo, weightedWordSet, littleOrNoContent);
		
		pred.setKeywords(seenKeywords);
		pred.setSnippet(text);
		
		System.out.println ("PREDICTION for '" + text + "' took: " + (endTime.getTime() - startTime.getTime()) + " milliseconds.");
				
		return (pred);
	}

	private static void	addProbs (ArrayList<Double> wordProbs, ArrayList<String> wordList, String lineToSplit, 
			String splitter, double weight, HashMap<Integer, Double> idxToWeightMap, 
			HashMap<String, Integer> numMatchedWeightedWordsPerCategoryMap, HashSet<String> weightedWordSet, 
			AllPageStats aps, String category, boolean smooth, boolean ignoreCaps) {
		
		if (lineToSplit != null) {			
			StringTokenizer	tok = new StringTokenizer(lineToSplit, splitter);
			
			while (tok.hasMoreTokens()) {
				String	nextToken = tok.nextToken().trim();
				if (ignoreCaps)
					nextToken = nextToken.toLowerCase();
				
				double	prob = aps.getProbability(nextToken, category, smooth);
				if (prob > 0) {// 0 here really means we've never seen the word before, and thus have no info on it					
					wordProbs.add(prob);
					wordList.add(nextToken);
					if (weight != 1) {
						idxToWeightMap.put(wordProbs.size() - 1, weight);
						
						boolean	wordInCat = prob > WordStats.MIN_PROB;
						if (wordInCat) {
							Integer	val = numMatchedWeightedWordsPerCategoryMap.get(category);
							if (val == null)
								val = 1;
							else
								val++;
							numMatchedWeightedWordsPerCategoryMap.put(category, val);
							
							//if (DEBUG)
								//System.out.println("MATCHED: " + next + " IN CAT: " + category);
							if (!weightedWordSet.contains(nextToken))
								weightedWordSet.add(nextToken);
						}
						
					}
				}
			}
		}
	}

	private static void	addProbs (ArrayList<Double> wordProbs, HashSet<String> words, 
			double weight, HashMap<Integer, Double> idxToWeightMap, AllPageStats aps, String category, boolean smooth) {		
		if (words != null) {
			for (String word : words) {
				double	prob = aps.getProbability(word, category, smooth);
				if (prob > 0) {// 0 here really means we've never seen the word before, and thus have no info on it					
					wordProbs.add(prob);
					if (weight != 1)
						idxToWeightMap.put(wordProbs.size() - 1, weight);
				}
			}
		}
	}

	public static Rater  loadOrCreate () throws IOException {
		// The point of this spring bean crap is to make it so that certain properties (directories for Rater)
		// are configurable via an xml file - they'll need to be different on Linux deployment:
		ApplicationContext	appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		Rater	rater = (Rater)appContext.getBean("rater");
		
		BufferedReader	br = null;
		String			fileName = rater.getDataDir() + "/rater.rat";
		rater.initWordSets(); 		
		rater.globalStats = GlobalStats.load(rater.getDataDir());
		
		try {
			if (new File(fileName).exists()) {
				
				br = new BufferedReader(new FileReader(fileName));
				br.readLine();
				int	numTotalPagesRated = Integer.parseInt(br.readLine().substring("Num Total Pages Rated: ".length()));
				rater.setNumTotalPagesRated(numTotalPagesRated);
				int	numTotalPagesPredicted = Integer.parseInt(br.readLine().substring("Num Total Pages Predicted: ".length()));
				rater.setNumTotalPagesPredicted(numTotalPagesPredicted);
			}
			
			return (rater);
		}
		finally {
			if (br != null)
				br.close();
		}
	}

	public void  save () throws IOException {
		PrintWriter	pw = null;	 

		try
		{
			String	filename = dataDir + "rater.rat";
			pw = new PrintWriter(filename);
			pw.println("Rating Information");
			pw.println("Num Total Pages Rated: " + numTotalPagesRated);
			pw.println("Num Total Pages Predicted: " + numTotalPagesPredicted);
			globalStats.save();
		} 
		finally {
			if (pw != null)
				pw.close();
		}

	}

	public static Rater	getTheRater () {
		
		if (THE_RATER == null) {
		
			try {
				THE_RATER = Rater.loadOrCreate();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return (THE_RATER);
	}
	
	public static String getParsedPage(URL url)throws Exception {
		ParsedPage page = new WebUtils().getContent(new CachedURL(url), null, true, DEFAULT_SNIPPET_SIZE);
		return page.toString();
	}
	
	public static void	showIntersection (URL url, URL url2, boolean ignoreCaps) throws IOException {
		String		content = new WebUtils().getRawContent(url, null).rawContent;
		String		content2 = new WebUtils().getRawContent(url2, null).rawContent;
		PageStats	ps = new PageStats(content, ignoreCaps, false);
		PageStats	ps2 = new PageStats(content2, ignoreCaps, false);
		HashMap<String, Integer>	map = ps.getWordToFreqMap();
		int			mapSize = map.size();
		HashMap<String, Integer>	map2 = ps2.getWordToFreqMap();
		int			map2Size = map2.size();
		
		map.keySet().retainAll(map2.keySet());
		
		int			interSize = map.keySet().size();
		
		System.out.println ("Common words between " + url + " and " + url2 + ":");
		
		for (String s : map.keySet()) {
			System.out.println(s);
		}
		
		System.out.println ("Num distinct words in " + url + ": " + mapSize + ", " + 
				url2 + ": " + map2Size + ", intersection: " + interSize);
	}

	public static String	nextArg (String param, String [] args, int idx) throws Exception {
		if (args.length <= idx)
			throw new Exception ("Required value for parameter " + param);
		return (args [idx]);
	}
	
	public static void main (String [] args) throws Exception {
		if (args.length == 0) {
			System.out.println ("\nUsage: Rater [-r URL RATING] [-p URL] [-cr] [-cl] [-ru] [-sh] -u|-cu USERID [-n MAXRATING]");
			System.out.println ("Typical usage: create a user profile, then rate one or more pages, then");
			System.out.println ("predict rating for a new page.\n");
			System.out.println ("Examples:");
			System.out.println ("Creating user with rating range 1-N: Rater -cu jeff -n 10");
			System.out.println ("Rating a page: Rater -r \"http://www.cnn.com\" 5 -u jeff");
			System.out.println ("Removing user: Rater -ru jeff");
			System.out.println ("Clearing rating for one site: Rater -u jeff -cr \"http://www.cnn.com\"");
			System.out.println ("Listing users: Rater -li");
			System.out.println ("Predicting: Rater -p \"http://www.cnn.com\" -u jeff");
			System.out.println ("Clearing existing user rating history: Rater -cl -u jeff");
			System.out.println ("Showing user rating history: Rater -sh -u jeff");
			System.out.println ("Show intersection between two pages: Rater -in \"http://www.cnn.com\" \"http://www.msnbc.com\"");
			System.out.println ("Computing word statistics for all users combined (and saving to file): Rater -ws wordstats.txt");
		}
		else {
			boolean	createUser = false;
			boolean	clearHistory = false;
			String	userid = null;
			String	url = null;
			String		rating = "";
			boolean	predicting = false;
			int		maxRating = 10;
			boolean	showHistory = false;
			boolean	useCache = true;
			boolean	listUsers = false;
			boolean	removeUser = false;
			String	clearRatingFor = null;
			String	url2 = null;
			boolean computeStats = false;
			String	statsFile = "wordstats.txt";
			String	dataDirToUse = null;
			boolean saveStuff = false;
			String stuffFile = null;
			
			for (int i = 0; i < args.length; i++) {
				String	arg = args [i];
				if (arg.equals("-cu")) {
					createUser = true;
					userid = nextArg("-cu", args, ++i);
				}
				else if (arg.equals("-u")) {
					userid = nextArg("-u", args, ++i);
				}
				else if (arg.equals("-cr")) {
					clearRatingFor = nextArg("-cr", args, ++i);
				}
				else if (arg.equals("-cl")) {
					clearHistory = true;
				}
				else if (arg.equals("-ws")) {
					computeStats = true;
					statsFile = nextArg("-ws", args, ++i);
				}
				else if (arg.equals("-sh")) {
					showHistory = true;
				}
				else if (arg.equals("-ru")) {
					removeUser = true;
					userid = nextArg("-u", args, ++i);
				}
				else if (arg.equals("-li")) {
					listUsers = true;
				}
				else if (arg.equals("-n")) {
					maxRating = Integer.parseInt(nextArg("-n", args, ++i));
				}
				else if (arg.equals("-d")) {
					DEBUG = true;
				}
				else if (arg.equals("-dd")) {
					dataDirToUse = nextArg("-dd", args, ++i);
					if (!dataDirToUse.endsWith("/"))
						dataDirToUse += "/";
				}
				else if (arg.equals("-nc")) {
					useCache = false;
				}
				else if (arg.equals("-in")) {					
					url = nextArg("-in", args, ++i);	
					url2 = nextArg("-in", args, ++i);
				}
				else if (arg.equals("-r")) {
					url = nextArg("-r", args, ++i);
					rating = nextArg("-r", args, ++i);
				}
				else if (arg.equals("-p")) {
					url = nextArg("-p", args, ++i);
					predicting = true;
				}
				else if (arg.equals("-ss")) {
					saveStuff = true;
					stuffFile = nextArg("-ss", args, ++i);
				}
				else
				{
					System.out.println ("Unknown command line argument: " + arg);
				}
			}
			
			if (saveStuff) {
				PrintWriter pw = null;
				try {

				String	filename = stuffFile;
				pw = new PrintWriter(filename);
				pw.print(Rater.getParsedPage(new URL(url)));
				System.out.println ("Saved statistics to file " + statsFile);
				} catch (Exception e){
					e.printStackTrace();
				} finally {
					if (pw != null)
					try {
					pw.close();
					} catch (Exception e){
						System.out.println("no luck!");
						System.exit(0);
					}
				}
			}
			
//			if (computeStats) {
//				Rater.computeStatsAndSave(statsFile);
//				System.out.println ("Saved statistics to file " + statsFile);
//			}
			
			if (url2 != null) {
				Rater.showIntersection(new URL(url), new URL(url2), true);
				//System.out.println ("Showed intersection bewteen " + url + " and " + url2);
			}
		
			
			UserStats	stats = null;
			String	filename = Rater.getTheRater().getDataDir() + userid + ".wpu";
			
			if (userid == null)
				return;
			
			
//			if (createUser) {
//				User	newUser = Rater.createUser(userid, maxRating);
//				System.out.println ("Created user profile with userid: " + userid + " at: " + filename);
//			}
//			else {
//				if (!UserStats.userExists(userid))
//					throw new Exception ("Existing user profile " + userid + " doesn't exist.");
//				
//				stats = UserStats.load(filename);
//				System.out.println ("Loaded existing user profile with userid: " + userid + " (data file: " + filename + ")");
//			}
//			
//			if (clearRatingFor != null) {
//				if (stats == null)
//					throw new Exception ("Userid is required for clear history command");
//				
//				URL	clearUrl = new URL(clearRatingFor);
//				RatedPage	rp = stats.getRatedPages().get(clearUrl);	
//				if (rp == null)
//					System.out.println ("No rating exists for page " + clearRatingFor + " for user " + userid);
//				else {
//					stats.getRatedPages().remove(clearUrl);
//					stats.save(filename);	
//					System.out.println ("Cleared rating for user profile with userid: " + userid + " (data file: " + filename + ")");
//				}
//			}
//			if (clearHistory) {
//				if (stats == null)
//					throw new Exception ("Userid is required for clear history command");
//				stats.getRatedPages().clear();	
//				stats.save(filename);	
//				System.out.println ("Cleared ratings history for user profile with userid: " + userid + " (data file: " + filename + ")");
//			}
//			
			if (showHistory) {
				if (stats == null)
					throw new Exception ("Userid is required for clear history command");
				
				HashMap<URL, RatedPage>	rated = stats.getRatedPagesMap();
				if (rated == null || rated.size() == 0)
					System.out.println ("No rated pages for user " + stats.getUserId());
				else {
					Iterator<URL>	iter = rated.keySet().iterator();
					System.out.println ("User " + userid + " has rated the following " + rated.keySet().size() + " page(s): ");
					while (iter.hasNext()) {
						URL	next = iter.next();
						RatedPage	value = rated.get(next);
						System.out.println ("RATED: " + next + " AS: " + value.getRating());
					}
				}
			}
			
//			Rater	rater = null;
//			
//			if (predicting) {
//				if (rater == null)
//					rater = Rater.loadOrCreate();
//				
//				Prediction	p = useCache ? rater.predictRating(stats, new CachedURL(url)) : rater.predictRating(stats, new URL(url));
//				System.out.println(p);
//			}
//						
		}
	}
}

