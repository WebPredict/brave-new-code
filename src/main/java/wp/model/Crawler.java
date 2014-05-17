package wp.model;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

/**
 * Wish list:
 * -how to decide which links to follow?
	a) look at link text or surrounding neighborhood
	b) look at domain on which link is, and how it has rated
	-intensity of the crawler
	-restricted domains
	-robots.txt file respected
	-max # of total links to crawl
	-max # of links per site to crawl
 * @author Jeff
 *
 */
public interface Crawler {

	/**
	 * List of URL seeds to crawl.
	 * @param urlList
	 * @return
	 */
	CrawlResults	crawlUrls (List<URL> urlList);
	
	/**
	 * List of URL seed strings to crawl.
	 * @param urlList
	 * @return
	 */
	CrawlResults	crawlUrlStrings (List<String> urlList);
	
	void	asyncCrawl () throws SQLException;
	
	/**
	 * Crawls all desired crawlable links, and uses lpe to order links to follow next.
	 */
	CrawlResults	crawl (LinkPreferenceEvaluator lpe); 
	
	/**
	 * User associated with this crawl. Not required if you're going to pass in a LinkPreferenceEvaluator when crawling.
	 * @param user
	 */
	void	setUser (User user);
	
	User	getUser ();
	
	void	setSeedUrl (String url);
	
	String	getSeedUrl ();
	
	void	setDesirablePredictions (String list);
	
	String	getDesirablePredictions ();

	void	setDesirableWords (String list);
	
	String	getDesirableWords ();

	/**
	 * Use to tune (coarse-grained) how aggressive this crawler is.
	 * @param intensity
	 */
	void	setIntensityLevel (CrawlingIntensity intensity);
	
	CrawlingIntensity	getIntensityLevel ();

	void	setStatus (CrawlerStatus stats);
	CrawlerStatus	getStatus ();
	
	public boolean isEnabled ();
	void	setEnabled (boolean e);
	
	/**
	 * Total number of links to crawl for a particular crawl run.
	 * @param max
	 */
	void	setMaxLinksToFollow (int max);
	
	int		getMaxLinksToFollow ();

	/**
	 * Total number of links to crawl for a particular domain in a crawl run.
	 * @param max
	 */
	void	setMaxLinksToFollowPerDomain (int max);
	
	int		getMaxLinksToFollowPerDomain ();

	/** Can the crawler leave the set of set URLs (root domains) or not?
	 * 
	 * @param can
	 */
	void	setCanLeaveDomainList (boolean can);
	
	boolean	getCanLeaveDomainList ();
	
	/**
	 * If link text contains certain keywords (desirable to this crawler's user), it will be added to list of URLs to crawl.
	 * @param only
	 */
	void	setOnlyFollowLinksContainingKeywords (boolean only);
	
	boolean	getOnlyFollowLinksContainingKeywords ();
	
	/**
	 * If link text predicts a certain way (desirable to this crawler's user), it will be added to list of URLs to crawl.
	 * @param only
	 */
	void	setOnlyFollowLinksWithGoodPredictionText (boolean only);
	
	boolean	getOnlyFollowLinksWithGoodPredictionText ();
	
	/**
	 * If a page on a particular domain predicted desirably, prefer (put ahead in the crawl queue)
	 * links to pages on this domain in the future.
	 * @param prefer
	 */
	void	setPreferOtherLinksOnGoodSites (boolean prefer);
	
	boolean	getPreferOtherLinksOnGoodSites ();
	
	/**
	 * If a site is taking longer than this to fetch a page, stop crawling the site for the duration of this crawl.
	 * @param seconds
	 */
	void	setMaxFetchTimeCutoff (int seconds);
	
	int		getMaxFetchTimeCutoff ();
}
