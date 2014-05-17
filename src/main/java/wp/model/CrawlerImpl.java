package wp.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import wp.core.CachedURL;
import wp.core.Rater;
import wp.core.UserDisabledException;
import wp.core.UserQuotaException;
import wp.core.Utils;
import wp.core.WebUtils;
import wp.dao.UserStatsDao;
import wp.service.CrawlerService;

@Entity
@Table(name="Crawler")
public class CrawlerImpl implements Crawler, Serializable {

	private Long	id;
	private boolean	canLeaveDomainList;
	private CrawlingIntensity	intensityLevel;
	private boolean onlyLinksPrediction;
	private boolean	onlyLinksKeywords;
	private boolean preferLinksOnGoodSites;
	private User	user;
	private int		maxLinksFollow;
	private int		maxLinksFollowDomain;
	private int		maxFetchTime;
	private List<ParsedPage>	crawledPages = new ArrayList<ParsedPage>();
	private List<CrawlerResult>	crawledResults = new ArrayList<CrawlerResult>();
	private String	seedUrl;
	private String	desirablePredictions;
	private String	desirableWords;
	private boolean	enabled;
	private CrawlerStatus	status;
	
	public CrawlerStatus getStatus() {
		return status;
	}

	public void setStatus(CrawlerStatus status) {
		this.status = status;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDesirableWords() {
		return desirableWords;
	}

	public void setDesirableWords(String desirableWords) {
		this.desirableWords = desirableWords;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CrawlResults crawl(List<String> urlList) {
		// TODO Auto-generated method stub
		return null;
	}

	public static final long	MAX_TIME_TO_WAIT = 60000l; // 1 minute between page fetches
	
	private void	sort (LinkInfo [] linksArr, final LinkPreferenceEvaluator lpe) {
		
		Arrays.sort(linksArr, new Comparator () {
			public int	compare (Object o1, Object o2) {
				LinkInfo	l1 = (LinkInfo)o1;
				LinkInfo	l2 = (LinkInfo)o2;
				
				double		l1appeal = lpe.howAppealingIsThis(l1);
				double		l2appeal = lpe.howAppealingIsThis(l2);
				
				return (int)(l2appeal * 1000d - l1appeal * 1000d);
			}
		});
		
	}

	public static final int	MAX_SNIPPET_SIZE = 4096;

	private Queue<LinkInfo>	linkQueue = new LinkedList<LinkInfo>();
	
	private static final int	MAX_QUEUE_SIZE = 1000;

	public void asyncCrawl() throws SQLException {
		new Thread (new Runnable() {
			public void	run () {
				crawl();
			}
		}).start();
	}
	
	public void crawl() {	
		WebUtils	wu = new WebUtils();
		String	urlStr = Utils.normalizeUrlStr(seedUrl);
		
		ApplicationContext	appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		HibernateTransactionManager	manager = (HibernateTransactionManager)appContext.getBean("transactionManager");
		Session session = manager.getSessionFactory().getCurrentSession();
		
		try {			
			session.beginTransaction();
			
			CrawlerImpl	thisCrawler = (CrawlerImpl)session.load(CrawlerImpl.class, id);
			thisCrawler.setStatus(CrawlerStatus.RUNNING);
			session.persist(thisCrawler);
			session.flush();
			
			String		predictsStr = StringUtils.isEmpty(desirablePredictions) ? user.getDesirableRatings() : desirablePredictions;
			UserStatsDao	userStatsDao = (UserStatsDao)appContext.getBean("userStatsDao");
			//((UserStatsDaoImpl)userStatsDao).setSessionFactory(manager.getSessionFactory());
			
			UserStats	stats = userStatsDao.findStatsFor(user.getUserId());
			
			// TODO: how to avoid this hack? when I find UserStats from dao, it is no longer attached to this session immediately:
			Long	userStatsId = stats.getId();
			stats = (UserStats)session.load(UserStats.class, userStatsId);
			
			Collection<String>	predicts = null;
			if (StringUtils.isNotEmpty(predictsStr))
				predicts = Utils.extractCollection(predictsStr);
			
			Collection<String>	keywords = null;
			if (StringUtils.isNotEmpty(desirableWords))
				keywords = Utils.extractCollection(desirableWords.toLowerCase());

			CachedURL	cu = new CachedURL(urlStr);
			if (cu.crawlersForbidden()) 
				return;
			
			linkQueue.add(new LinkInfo(urlStr, null, null));
			int				counter = 0;
			HashSet<String>	recentlyCrawledLinks = new HashSet<String>();		
			
			if (crawledResults != null) {
				for (CrawlerResult cr : crawledResults) {
					Date	ts = cr.getTimestamp();
					if (ts != null && (System.currentTimeMillis() - ts.getTime() < 86400000 * 14))  // two weeks
						recentlyCrawledLinks.add(cr.getUrlStr());
				}
			}
			HashSet<String>	seenLinks = new HashSet<String>();
			HashMap<String, Integer>	linkToCounterMap = new HashMap<String, Integer>();
			while (!linkQueue.isEmpty() && counter <= maxLinksFollow) {
				
				LinkInfo	linkage = linkQueue.poll();
				String	link = linkage.getLink();
				
				if (seenLinks.contains(link))
					continue;
				else
					seenLinks.add(link);

				//System.out.println("CURRENT LINK: " + link);

				String	root = WebUtils.getRootDomain(link);
				Integer	domainCount = linkToCounterMap.get(root);
				if (domainCount == null)
					linkToCounterMap.put(root, 1);
				else
					linkToCounterMap.put(root, domainCount + 1);
				
				try {
					long	start = System.currentTimeMillis();
					
					CachedURL	url = new CachedURL(link);
					if (url.crawlersForbidden())
						continue;

					List<LinkInfo>	links = 
						canLeaveDomainList ? wu.extractLinks(url, true, false) : wu.extractLinks (url, false, true);
					LinkInfo []		linksArrTmp = new LinkInfo [links.size()];
					links.toArray(linksArrTmp);
					if (user != null)   
						sort(linksArrTmp, new LinkPreferenceEvaluatorImpl(user, stats));
					for (LinkInfo newLink : linksArrTmp) {
						if (linkQueue.size() >= MAX_QUEUE_SIZE)
							break;
						
						if (!seenLinks.contains(newLink.getLink()))
							linkQueue.add(newLink);
					}
					
					if (!recentlyCrawledLinks.contains(link)) {
						counter++;
						
						Prediction pred;
						try {
							pred = Rater.getTheRater().predictRating(stats, url, false, keywords, false, MAX_SNIPPET_SIZE);
							
							System.out.println (pred);
							
							CrawlerResult	cr = addCrawlerResult(pred, crawledResults, this, predicts, keywords, url.getURL().toString());
							if (cr != null)
								session.persist(cr); // todo - flush/commit at this point so results are incrementally available?
							
						} catch (UserQuotaException e) {
							e.printStackTrace();
							return; //
						} catch (UserDisabledException e) {
							e.printStackTrace();
							return;
						}
						catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						
						long	end = System.currentTimeMillis();
						long	multiplier = intensityLevel == null ? 10 : intensityLevel.getSpeed();
						long	timeToWait = end - start > 1000 ? (multiplier * (end - start)) : 10000l;
						
						if (timeToWait > MAX_TIME_TO_WAIT)
							timeToWait = MAX_TIME_TO_WAIT;
						
						recentlyCrawledLinks.add(link);
						Thread.sleep(timeToWait);
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			sort (predicts);
			
			thisCrawler.setStatus(CrawlerStatus.CRAWL_SUCCESS);
			session.persist(thisCrawler);
			session.getTransaction().commit();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			CrawlerImpl	thisCrawler = (CrawlerImpl)session.load(CrawlerImpl.class, id);
			thisCrawler.setStatus(CrawlerStatus.CRAWL_FAILED);
			session.persist(thisCrawler);
			session.getTransaction().commit();

		}
		finally {
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
	}
	
	private CrawlerResult	addCrawlerResult (Prediction pred, List<CrawlerResult> results, CrawlerImpl crawler,
			Collection<String> predicts, Collection<String> contains, String urlStr) {
		String	first = pred.getRating();
		String	second = pred.getSecondGuess();
		if (StringUtils.isNotEmpty(pred.getTitle()) && StringUtils.isNotEmpty(pred.getSnippet())) {
			CrawlerResult	ar = new CrawlerResult();
			ar.setContains(pred.getKeywordsFlattened());
			ar.setPrediction(first);
			ar.setSecondGuess(second);
			
			ar.setUrlStr(urlStr);
			ar.setTitle(pred.getTitle());
			ar.setSnippet(pred.getSnippet());
			ar.setUser(user);
			ar.setTimestamp(new Date());
			ar.setCrawler(crawler);
			results.add(ar);
			return (ar);
		}
		return (null);
	}

	public String getSeedUrl() {
		return seedUrl;
	}

	public void setSeedUrl(String seedUrl) {
		this.seedUrl = seedUrl;
	}

	public String getDesirablePredictions() {
		return desirablePredictions;
	}

	public void setDesirablePredictions(String desirablePredictions) {
		this.desirablePredictions = desirablePredictions;
	}

	@OneToMany(mappedBy="crawler")
	@OrderBy("indexPosition")
	public List<CrawlerResult> getCrawledResults() {
		return crawledResults;
	}

	public void setCrawledResults(List<CrawlerResult> crawledResults) {
		this.crawledResults = crawledResults;
	}

	private void	sort (final Collection<String> desirable) {
		// sort crawledPages on user's desirable predictions...
		
		if (crawledResults == null || user == null || desirable == null)
			return;
		
		CrawlerResult []	crawledResultsArr = new CrawlerResult [crawledResults.size()];
		crawledResults.toArray(crawledResultsArr);
		
		final Integer	maxRating = user.getMaxRating();
		final boolean	smooth = user.isSmoothProbs() && user.getCategorySet().isNumeric();
		Arrays.sort(crawledResultsArr, new Comparator () {
			public int	compare (Object o1, Object o2) {
				CrawlerResult	c1 = (CrawlerResult)o1;
				CrawlerResult	c2 = (CrawlerResult)o2;
				
				String			pred1 = c1.getPrediction();
				String			pred2 = c2.getPrediction();
				int				predVal1 = 0;
				int				predVal2 = 0;
				
				if (smooth) {
					try {
						double	predVal = Double.parseDouble(pred1);
						predVal1 = (int)Math.abs(maxRating - predVal);
					}
					catch (Exception e) {
					}
					try {
						double	predVal = Double.parseDouble(pred2);
						predVal2 = (int)Math.abs(maxRating - predVal);
					}
					catch (Exception e) {
					}

				}
				else {
					predVal1 = desirable.contains(pred1) ? 0 : 2;
					predVal2 = desirable.contains(pred2) ? 0 : 2;
				}
				
				// TODO: review this stuff for secondary predictions and smooth prediction schemes
				if (predVal1 == predVal2) {
					String			secondPred1 = c1.getSecondGuess();
					String			secondPred2 = c2.getSecondGuess();
					int				secondPredVal1 = 0;
					int				secondPredVal2 = 0;
					
					if (smooth) {
						try {
							double	predVal = Double.parseDouble(secondPred1);
							secondPredVal1 = (int)Math.abs(maxRating - predVal);
						}
						catch (Exception e) {
						}
						try {
							double	predVal = Double.parseDouble(secondPred2);
							secondPredVal2 = (int)Math.abs(maxRating - predVal);
						}
						catch (Exception e) {
						}

					}
					else {
						predVal1 = desirable.contains(secondPred1) ? 0 : 2;
						predVal2 = desirable.contains(secondPred2) ? 0 : 2;
					}
				}
				
				if (predVal1 == predVal2) {
					String	contains1 = c1.getContains();
					String	contains2 = c2.getContains();
					predVal1 = StringUtils.isNotEmpty(contains1) ? 0 : 2;
					predVal2 = StringUtils.isNotEmpty(contains2) ? 0 : 2;
				}
				
				return (predVal1 - predVal2);
			}
		});
		crawledResults = new ArrayList<CrawlerResult>();
		int	idx = 0;
		for (CrawlerResult result : crawledResultsArr) {
			result.setIndexPosition(idx++);
			crawledResults.add(result);
		}
	}
	
	@Transient
	public List<ParsedPage>	getCrawledPages () {
		return (crawledPages);
	}
	
	public CrawlResults crawl(LinkPreferenceEvaluator lpe) {
		// TODO Auto-generated method stub
		return null;
	}

	public CrawlResults crawlUrlStrings(List<String> urlList) {
		// TODO Auto-generated method stub
		return null;
	}

	public CrawlResults crawlUrls(List<URL> urlList) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getCanLeaveDomainList() {
		return (canLeaveDomainList);
	}

	public CrawlingIntensity getIntensityLevel() {
		return (intensityLevel);
	}

	@Column(name="maxLinksFollow")
	public int getMaxLinksToFollow() {
		return (maxLinksFollow);
	}

	@Column(name="maxLinksFollowDomain")
	public int getMaxLinksToFollowPerDomain() {
		return (maxLinksFollowDomain);
	}

	@Column(name="onlyLinksKeywords")
	public boolean getOnlyFollowLinksContainingKeywords() {
		return (onlyLinksKeywords);
	}

	@Column(name="onlyLinksPrediction")
	public boolean getOnlyFollowLinksWithGoodPredictionText() {
		return (this.onlyLinksPrediction);
	}

	@Column(name="preferLinksOnGoodSites")
	public boolean getPreferOtherLinksOnGoodSites() {
		return (preferLinksOnGoodSites);
	}

	@ManyToOne
	public User getUser() {
		return (user);
	}

	public void setCanLeaveDomainList(boolean can) {
		canLeaveDomainList = can;
	}

	public void setIntensityLevel(CrawlingIntensity intensity) {
		this.intensityLevel = intensity;
	}

	public void setMaxLinksToFollow(int max) {
		maxLinksFollow = max;
	}

	public void setMaxLinksToFollowPerDomain(int max) {
		maxLinksFollowDomain = max;
	}

	public void setOnlyFollowLinksContainingKeywords(boolean only) {
		onlyLinksKeywords = only;
	}

	public void setOnlyFollowLinksWithGoodPredictionText(boolean only) {
		onlyLinksPrediction = only;
	}

	public void setPreferOtherLinksOnGoodSites(boolean prefer) {
		preferLinksOnGoodSites = prefer;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getMaxFetchTimeCutoff() {
		return maxFetchTime;
	}

	public void setMaxFetchTimeCutoff(int maxFetchTime) {
		this.maxFetchTime = maxFetchTime;
	}

}
