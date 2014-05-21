package wp.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import wp.model.Ad;
import wp.model.ParsedPage;
import wp.model.RatedPage;
import wp.model.UserStats;
import wp.utils.Utils;

public class AdEngine {

	public static AdEngine	AD_ENGINE;
	
	public List<Ad>	getAdsFor (UserStats stats, List<Ad> allAds, Collection<String> goodRatingSet, int num) throws SQLException {
		Collection<RatedPage>	rps = stats.getRatedPages();
		ArrayList<AdMatch>	matches = new ArrayList<AdMatch>();
		for (RatedPage rp : rps) {
			String	lowerRating = rp.getRating().toLowerCase();
			//if (!goodRatingSet.contains(rp.getRating()))
				//continue;
			
			// for now, just looking at keywords, title, description:
			ParsedPage	pp = rp.getParsedPage();
			HashSet<String>	keywords = pp.getKeywordSet();
			String	title = pp.getTitle();
			String	desc = pp.getDescription();
			
			for (Ad ad : allAds) {
				if (!ad.isEnabled())
					continue;
				
				double	matchScore = 0;
				// TODO: use these
				int	lowRange = ad.getLowRange();
				int	highRange = ad.getHighRange();
				Collection<String>	desirableRatings = ad.getDesirableRatingsSet();
				
				if (desirableRatings != null && desirableRatings.size() > 0) {
					if (desirableRatings.contains(lowerRating)) 
						matchScore += 2;
				}
				String	adTitle = ad.getTitle();
				String	adContent = ad.getContent();
				matchScore += Utils.anyUncommonWordsMatch(title, adTitle) ? 2 : 0;
				matchScore += Utils.anyUncommonWordsMatch(desc, adTitle) ? 2 : 0;
				matchScore += Utils.anyUncommonWordsMatch(keywords, adTitle) ? 2 : 0;
				
				matchScore += Utils.anyUncommonWordsMatch(title, adContent) ? 1 : 0;
				matchScore += Utils.anyUncommonWordsMatch(desc, adContent) ? 1 : 0;
				matchScore += Utils.anyUncommonWordsMatch(keywords, adContent) ? 1 : 0;
		
				matches.add(new AdMatch(ad, matchScore));
			}
		}
		
		return (sortAndTrim(matches, num));
	}

	public List<Ad>	sortAndTrim (List<AdMatch> matches, int num) {
		Collections.sort(matches, new Comparator () {

			public int compare(Object arg0, Object arg1) {
				AdMatch	m1 = (AdMatch)arg0;
				AdMatch	m2 = (AdMatch)arg1;
				
				return ((int)(m2.matchScore - m1.matchScore));
			}
			
		});
		
		List<Ad>	ads = new ArrayList<Ad>();
		
		for (int i = 0; i < matches.size(); i++) {
			if (ads.size() >= num)
				break;
			Ad	ad = matches.get(i).ad;
			if (!ads.contains(ad))
				ads.add(ad);			
		}
		return (ads);
	}
	
	public static AdEngine	getAdEngine () {
		
		if (AD_ENGINE == null) {
			try {
				AD_ENGINE = new AdEngine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return (AD_ENGINE);
	}

	
	public List<Ad>	getAdsFor (RecentStats rstats, List<Ad> allAds, Collection<String> goodRatingSet, int num) throws SQLException {
		List<RatingProjection>	rps = rstats.getRatings();
		ArrayList<AdMatch>	matches = new ArrayList<AdMatch>();
		for (RatingProjection rp : rps) {
			String	rating = rp.getRating();
			
			//if (!goodRatingSet.contains(rating))
				//continue;
			
			String	snippet = rp.getSnippet();
			String	title = rp.getTitle();
			String	url = rp.getUrl();
			
			for (Ad ad : allAds) {
				if (!ad.isEnabled())
					continue;
				double	matchScore = 0;
				String	adTitle = ad.getTitle();
				String	adContent = ad.getContent();
				matchScore += Utils.anyUncommonWordsMatch(title, adTitle) ? 2 : 0;
				matchScore += Utils.anyUncommonWordsMatch(snippet, adTitle) ? 2 : 0;
				matchScore += Utils.anyUncommonWordsMatch(url, adTitle) ? 2 : 0;
				
				matchScore += Utils.anyUncommonWordsMatch(title, adContent) ? 1 : 0;
				matchScore += Utils.anyUncommonWordsMatch(snippet, adContent) ? 1 : 0;
				matchScore += Utils.anyUncommonWordsMatch(url, adContent) ? 1 : 0;			
				
				matches.add(new AdMatch(ad, matchScore));
			}
		}
		return (sortAndTrim(matches, num));
	}
	
	static class AdMatch {
		
		private Ad	ad;
		private double	matchScore;
		
		public AdMatch (Ad ad, double matchScore) {
		
		this.ad = ad;
		this.matchScore = matchScore;
		}
	}
	
	public List<Ad>	filter (List<Ad> ads, List<RatedPage> context) throws SQLException {
		ArrayList<Ad>	filtered = new ArrayList<Ad>();
		HashSet<Ad>		seen = new HashSet<Ad>();
		for (RatedPage result : context) {
			String	title = result.getParsedPage().getTitle();
			String	snippet = result.getParsedPage().getFirstLine();
			for (Ad ad : ads) {
				if (seen.contains(ad))
					continue;
				boolean	match = Utils.anyUncommonWordsMatch(ad.getTitle(), title) ||
					Utils.anyUncommonWordsMatch(ad.getTitle(), snippet);
				if (match) {
					filtered.add(ad);
					seen.add(ad);
				}
			}
			
		}
		return (filtered);
	}
	
	public List<Ad>	filterSearch (List<Ad> ads, List<SearchResult> context) throws SQLException {
		ArrayList<Ad>	filtered = new ArrayList<Ad>();
		HashSet<Ad>		added = new HashSet<Ad>();
		for (SearchResult result : context) {
			for (Ad ad : ads) {
				if (added.contains(ad))
					continue;
				
				boolean	match = Utils.anyUncommonWordsMatch(ad.getTitle(), result.getDetails()) ||
					Utils.anyUncommonWordsMatch(ad.getTitle(), result.getSnippet());
				if (match) {
					filtered.add(ad);
					added.add(ad);
					break;
				}
			}
			
		}
		return (filtered);
	}
}
