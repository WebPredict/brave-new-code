package wp.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import wp.model.RatedPage;

/**
 * All page statistics for a single user.
 * @author Jeff
 *
 */
public class AllPageStats {

	public static final double	MIN_CAT_PROB = WordStats.MIN_PROB;
	
	private	HashMap<String, ArrayList<RatedPage>>	ratingsMap = new HashMap<String, ArrayList<RatedPage>>();
	
	public AllPageStats () { }
	
	public AllPageStats (HashMap<URL, RatedPage> ratedPages) {
		Collection<RatedPage>	pages = ratedPages.values();
		for (RatedPage page : pages) {
			String	rating = page.getRating();
			ArrayList<RatedPage>	rated = ratingsMap.get(rating);
			if (rated == null) {
				rated = new ArrayList<RatedPage>();
				ratingsMap.put(rating, rated);
			}
			rated.add(page);
		}
	}
	
	public int		numRatings (String category) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(category);
		if (ratings == null)
			return (0);
		return (ratings.size());
	}
	
	public boolean	commonLikelihood (String word, double prob, int numCategories) {
		
//		for (int i = 1; i <= numCategories; i++) {
//			double	likelihood = getProbability(word, i);
//			
//			if (likelihood < prob)
//				return (false);
//		}
		return (true);
	}

	private double	numDocsInCategory (String cat) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(cat);
		return (ratings == null ? 0 : ratings.size());
	}
	
	private double	catProb (String cat, String word) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(cat);
		double	total = 0;
		if (ratings != null && ratings.size() > 0) {			
			for (int i = 0; i < ratings.size(); i++) {
				if (ratings.get(i).getStats().hasWord(word)) {
					total += (1 - WordStats.MIN_PROB);
				}
				else
					total += WordStats.MIN_PROB;
			}
		}
		else
			return (0);
		
		return (total);
	}

	public double	getAveNumWords (String ratingCategory) {
		
		ArrayList<RatedPage>	ratings = ratingsMap.get(ratingCategory);
		double	total = 0;
		if (ratings != null && ratings.size() > 0) {			
			for (int i = 0; i < ratings.size(); i++) {
				total += ratings.get(i).getStats().getNumWords();						
			}
			return (total / (double)ratings.size());
		}
		return (0);
	}
	
	public double	getProbability (String word, String ratingCategory, boolean smoothProbs) {
		
		if (smoothProbs) {			
			double	catProb = catProb(ratingCategory, word);
			double		numDocs = numDocsInCategory(ratingCategory);
			
			// TODO this needs to be fast
			String	categoryOneLess = String.valueOf(Integer.parseInt(ratingCategory) - 1);
			catProb += catProb(categoryOneLess, word) / 2.0;
			numDocs += numDocsInCategory(categoryOneLess) / 2.0;
			
			String	categoryOneMore = String.valueOf(Integer.parseInt(ratingCategory) + 1);
			catProb += catProb(categoryOneMore, word) / 2.0;
			numDocs += numDocsInCategory(categoryOneMore) / 2.0;
			
			String	categoryTwoLess = String.valueOf(Integer.parseInt(ratingCategory) - 2);
			catProb += catProb(categoryTwoLess, word) / 4.0;
			numDocs += numDocsInCategory(categoryTwoLess) / 4.0;
			
			String	categoryTwoMore = String.valueOf(Integer.parseInt(ratingCategory) + 2);
			catProb += catProb(categoryTwoMore, word) / 4.0;
			numDocs += numDocsInCategory(categoryTwoMore) / 4.0;
			
			if (numDocs == 0)
				return (WordStats.MIN_PROB);
			return (catProb / numDocs);
		}
		
		
		// if we have no information for a category, we return 0.
		// New:
		// 1 document with 1 occurrence = 20% chance. 2 occurrences = 40% chance... 5+ occurrences = almost 100%
		// 1 document with 0 occurrence = almost 0% chance
		ArrayList<RatedPage>	ratings = ratingsMap.get(ratingCategory);
		double	total = 0;
		if (ratings != null && ratings.size() > 0) {			
			for (int i = 0; i < ratings.size(); i++) {
				int	wordCount = ratings.get(i).getStats().wordCount(word);
				if (wordCount > 0) {
					if (wordCount > 5)
						wordCount = 5;
					total += (.2 * (double)wordCount) - WordStats.MIN_PROB;
				}
				else
					total += WordStats.MIN_PROB;
				//if (ratings.get(i).getStats().hasWord(word))
					//total += 1 - WordStats.MIN_PROB;							
			}
						
			return (WordStats.adjust(total / (double)ratings.size()));
		}
		
		return (WordStats.MIN_PROB);
	}
	
	public double	getProbabilityOfCategory (String ratingCategory, boolean smooth) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(ratingCategory);
		double	size = ratings == null ? 0 : ratings.size();
		double	total = 0;
		for (Collection<RatedPage> pages : ratingsMap.values()) {
			if (pages != null)
				total += pages.size();
		}
		
		if (total == 0 || size == 0)
			return (MIN_CAT_PROB);
		return (size / total) - MIN_CAT_PROB;
	}
	
	public double	getProbabilityHasAds (String ratingCategory) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(ratingCategory);
		// 1 document with no occurrences = 25% chance
		// 1 document with 1 or more occurrences = 75% chance
		double	total = .5;
		if (ratings != null && ratings.size() > 0) {
			
			for (int i = 0; i < ratings.size(); i++) {
				if (ratings.get(i).getParsedPage().isHasAds())
					total += 1;							
			}
						
			return (WordStats.adjust(total / (double)(ratings.size() + 1)));		
		}
		
		return (total);
	}
	
	public double	getProbabilityHasPopups (String ratingCategory) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(ratingCategory);
		// 1 document with no occurrences = 25% chance
		// 1 document with 1 or more occurrences = 75% chance
		double	total = .5;
		if (ratings != null && ratings.size() > 0) {
			
			for (int i = 0; i < ratings.size(); i++) {
				if (ratings.get(i).getParsedPage().isHasPopups())
					total += 1;							
			}
						
			return (WordStats.adjust(total / (double)(ratings.size() + 1)));		
		}
		
		return (total);
	}	

	public double	getProbabilityOfLinkCount (String ratingCategory, int linkCount) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(ratingCategory);
		// 1 document with no occurrences = 25% chance
		// 1 document with 1 or more occurrences = 75% chance
		double	total = .5;
		if (ratings != null && ratings.size() > 0) {
			
			for (int i = 0; i < ratings.size(); i++) {
				int	catLinkCount = ratings.get(i).getParsedPage().getNumLinks();
				if (Math.abs(linkCount - catLinkCount) < (double)catLinkCount * .2d) // within 20%, say it matches
					total += 1;							
			}
						
			return (WordStats.adjust(total / (double)(ratings.size() + 1)));		
		}
		
		return (total);
	}	

	public double	getProbabilityOfImageCount (String ratingCategory, int imageCount) {
		ArrayList<RatedPage>	ratings = ratingsMap.get(ratingCategory);
		// 1 document with no occurrences = 25% chance
		// 1 document with 1 or more occurrences = 75% chance
		double	total = .5;
		if (ratings != null && ratings.size() > 0) {
			
			for (int i = 0; i < ratings.size(); i++) {
				int	catImageCount = ratings.get(i).getParsedPage().getNumImages();
				if (Math.abs(imageCount - catImageCount) < (double)catImageCount * .2d) // within 20%, say it matches
					total += 1;							
			}
						
			return (WordStats.adjust(total / (double)(ratings.size() + 1)));		
		}
		
		return (total);
	}	

	public double	getProbabilitySimilarSize (int size, double howClosePercent, int ratingCategory) {
		return (.5); // TODO
	}
	
	public double 	getProbabilitySimilarLinkContentRatio (double ratio, double howClosePercent, int ratingCategory) {
		return (.5);
	}
}
