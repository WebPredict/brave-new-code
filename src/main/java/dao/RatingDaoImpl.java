package wp.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.core.Rater;
import wp.core.RatingCloudInfo;
import wp.core.RatingInfo;
import wp.core.RatingOutOfRangeException;
import wp.core.RatingProjection;
import wp.core.RecentStats;
import wp.core.UserCloudInfo;
import wp.core.UserDisabledException;
import wp.core.UserDoesntExistException;
import wp.core.UserQuotaException;
import wp.core.Utils;
import wp.core.WebUtils;
import wp.core.WordCloudInfo;
import wp.model.PageDiff;
import wp.model.Prediction;
import wp.model.Privacy;
import wp.model.RatedPage;
import wp.model.Recommendation;
import wp.model.User;
import wp.model.UserStats;
import wp.model.WordFreqPair;

/**
 * @spring.bean id="ratingDao"
 * @spring.property name="pageDao" ref="pageDao"
 * @spring.property name="userDao" ref="userDao"
 * @spring.property name="userStatsDao" ref="userStatsDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class RatingDaoImpl extends HibernateDaoSupport implements RatingDao {

	private PageDao	pageDao;
	private UserStatsDao	userStatsDao;
	private UserDao	userDao;
	
	// cached stuff:
	private static Date	lastRetrievedClouds = null; 
	private static Date lastRetrievedRatingCloud = null;
	private static Date lastRetrievedMemberCloud = null;
	private static Collection<WordCloudInfo>	wordCloudInfo = null;
	private static Collection<RatingCloudInfo>	ratingCloudInfo = null;
	private static Collection<UserCloudInfo>	userCloudInfo = null;
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserStatsDao getUserStatsDao() {
		return userStatsDao;
	}

	public void setUserStatsDao(UserStatsDao userStatsDao) {
		this.userStatsDao = userStatsDao;
	}

	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	@Transactional(readOnly=true)
	public List<PageDiff>		pageDiffs (String userId, URL reference, List<URL> diffs) throws SQLException {
		ArrayList<PageDiff>	ret = new ArrayList<PageDiff>();
		UserStats	stats = userStatsDao.findStatsFor(userId);

		for (URL curPage : diffs) {
			PageDiff	diff = Rater.getTheRater().difference(stats, reference, curPage);
			ret.add(diff);
		}
		return (ret);
	}
	
	@Transactional(readOnly=false)
	public Prediction predict(String userId, URL url, String cookieData, Collection<String> keywords, boolean summarize, int snippetSize) throws UserQuotaException, UserDoesntExistException, SQLException
	{
		UserStats	stats = userStatsDao.findStatsFor(userId);
		Prediction	pred = Rater.getTheRater().predictRating(stats, url, cookieData, keywords, summarize, snippetSize);
		userStatsDao.save(stats);
		return (pred);
	}  

	@Transactional(readOnly=false)
	public RatingInfo rate(String userId, URL url, String rating, String comment, String cookiesToSend) 
	throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, SQLException
	{
		UserStats	stats = userStatsDao.findStatsFor(userId);
		RatingInfo	info = Rater.getTheRater().ratePage(stats, url, rating, comment, cookiesToSend);
		userStatsDao.save(stats);
		return (info);
	}

	@Transactional(readOnly=false)
	public void	save (UserStats stats) throws SQLException {
		getHibernateTemplate().save(stats);
	}
	 
	public UserStats	findUserStats (String userId) throws SQLException {
		List<UserStats>	stats = getHibernateTemplate().find("from UserStats where id=?", userId);
		return (stats == null || stats.size() == 0) ? null : stats.get(0);
	}
	
	public Prediction predictRawContent(String userid, String rawContent, boolean summarize, int snippetSize)
			throws UserQuotaException, UserDoesntExistException, SQLException {
		File blah = null;
		PrintWriter	writer = null;
		try {
			blah = File.createTempFile("pred", null);
			BufferedWriter	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blah), "UTF-8"));
			writer = new PrintWriter(bw);
			writer.write(rawContent);
			writer.flush();
			writer.close();
			
			URL		url = new URL ("file://localhost/" + blah.getAbsolutePath());
			UserStats	stats = userStatsDao.findStatsFor(userid);
			return (Rater.getTheRater().predictRating(stats, url, null, null, summarize, snippetSize));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (writer != null)
				writer.close();
			if (blah != null)
				blah.delete();
		}
		return (null);
	}

	@Transactional(readOnly=false)
	public RatingInfo rateRawContent(String userid, String rawContent, String rawContentId, String rating, String comment)
			throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, SQLException {
		
		PrintWriter	writer = null;
		try {
			BufferedWriter	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Rater.getTheRater().getDataDir() + rawContentId + ".txt"), "UTF-8"));
			writer = new PrintWriter(bw);
			writer.write(rawContent);
			writer.flush();
			writer.close();
			URL	fileUrl = new URL("file://localhost/" + Rater.getTheRater().getDataDir() + rawContentId + ".txt");
			UserStats	stats = userStatsDao.findStatsFor(userid);
			RatingInfo	info = Rater.getTheRater().ratePage(stats, fileUrl, rating, comment, null);
			userStatsDao.save(stats);
			return (info);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (writer != null)
				writer.close();
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	public Collection<WordCloudInfo>	getWordCloud (int size, String ratedAs, boolean ignoreCommon, boolean dontUseCache) throws SQLException {	

		if (!dontUseCache) {
			synchronized (this) {
				long	currentTime = System.currentTimeMillis();
				if (lastRetrievedClouds != null && wordCloudInfo != null && (currentTime - lastRetrievedClouds.getTime() < 120000l))
					return (wordCloudInfo);
			}
		}
		
		HashMap<String, WordCloudInfo>	map = new HashMap<String, WordCloudInfo>();
		
		ListInfo	qInfo = new ListInfo();
		qInfo.setMaxResults(500);
		qInfo.setMaxPerPage(500);
		List<RatedPage>	rps = pageDao.findPublicRatedPages(qInfo);
		
		HashSet<String>	lookFor = new HashSet<String>();
		if (ratedAs != null) {
			StringTokenizer	tok = new StringTokenizer(ratedAs, ", ");
			while (tok.hasMoreTokens())
				lookFor.add(tok.nextToken().trim());
		}
		
		Collection<WordCloudInfo>	ret = null;
		
		if (rps != null) {
			HashSet<String>	vulgarities = Rater.getTheRater().getVulgarities();
			HashSet<String>	commonWords = Rater.getTheRater().getCommonWords();
			
			for (RatedPage rp : rps) {
				if (lookFor.size() > 0 && !lookFor.contains(rp.getRating().toLowerCase()))
					continue;
				
				HashMap<String, Integer>	freqMap = rp.getStats().getWordToFreqMap();
				
				for (String s : freqMap.keySet()) {
					if (s.length() > 18 || s.length() < 2)
						continue; // no crappy looking words
					
					if (vulgarities.contains(s))
						continue;
			
					if (ignoreCommon && commonWords.contains(s))
						continue;
					
					if (!s.matches("[a-z]+|[A-Z]+")) // cut the crap
						continue;
					
					WordCloudInfo	info;
					
					if (map.containsKey(s)) {
						info = map.get(s);
						info.setOccurrences(info.getOccurrences() + freqMap.get(s));
					}
					else {
						String	transS = s.trim();
						if (transS.equalsIgnoreCase(WebUtils.MONEY_TOKEN.trim()))
							transS = "MONEY";
						else if (transS.equalsIgnoreCase(WebUtils.NUM_TOKEN.trim()))
							transS = "NUMBER";
						else if (transS.equalsIgnoreCase(WebUtils.TIME_TOKEN.trim()))
							transS = "TIME";
						
						info = new WordCloudInfo(transS, freqMap.get(s));
						map.put(s, info);
					}
				}
			}
		}
	
		if (dontUseCache) {
			Collection<WordCloudInfo>	values = map.values();
			if (values.size() < size)
				return (values);
			else {
				ArrayList<WordCloudInfo>	toRet = new ArrayList<WordCloudInfo>();
				int	counter = 0;

				for (WordCloudInfo info : values) {
					toRet.add(info);
					if (++counter > size)
						break;
				}
				return (toRet);
			}
		}
		else {
			synchronized (this) {
				lastRetrievedClouds = new Date();
				wordCloudInfo = ret == null ? map.values() : ret;
				
				Collection<WordCloudInfo>	values = map.values();
				if (values.size() < size)
					wordCloudInfo = values;
				else {
					wordCloudInfo = new ArrayList<WordCloudInfo>();
					int	counter = 0;
	
					for (WordCloudInfo info : values) {
						wordCloudInfo.add(info);
						if (++counter > size)
							break;
					}
				}
			}
		}
		
		return (wordCloudInfo);
	}

	public Collection<RatingCloudInfo>	getRatingsCloud (int size) throws SQLException {	

		synchronized (this) {
			long	currentTime = System.currentTimeMillis();
			if (lastRetrievedRatingCloud != null && ratingCloudInfo != null && (currentTime - lastRetrievedRatingCloud.getTime() < 120000l))
				return (ratingCloudInfo);
		}

		int	counter = 0;
		HashMap<String, RatingCloudInfo>	map = new HashMap<String, RatingCloudInfo>();
		
		ListInfo	qInfo = new ListInfo();
		qInfo.setMaxResults(size);
		qInfo.setMaxPerPage(size);
		String []	notLikes = new String [] {"file:%"};
		String []	notLikesCols = new String [] {"urlStr"};
		qInfo.setNotLikes(notLikes);
		qInfo.setNotLikesCols(notLikesCols);
		List<RatedPage>	rps = pageDao.findPublicRatedPages(qInfo);
		
		Collection<RatingCloudInfo>	ret = null;
		
		if (rps != null) {
			//HashSet<String>	vulgarities = Rater.getTheRater().getVulgarities();
			
			for (RatedPage rp : rps) {
		
				if (ret != null)
					break;
				
				//String	title = rp.getParsedPage().getTitle();
				//String	firstLine = rp.getParsedPage().getFirstLine();
				
				//if (Utils.anyUncommonWordsMatch(vulgarities, title) || Utils.anyUncommonWordsMatch(vulgarities, firstLine))
				//	continue;
		
				RatingCloudInfo	info;
				
				String	rating = rp.getRating();
				
				if (map.containsKey(rating)) {
					info = map.get(rating);
					info.setOccurrences(info.getOccurrences() + 1);
				}
				else {
					info = new RatingCloudInfo(rating, 1, rp.getUrlStr());
					map.put(rating, info);
					counter++;
				}
				
			}
		}
	
		synchronized (this) {
			lastRetrievedRatingCloud = new Date();
			ratingCloudInfo = ret == null ? map.values() : ret;
		}
		return (ratingCloudInfo);
	}

	public Collection<UserCloudInfo>	getMostActiveMembers (int size) throws SQLException {	
		synchronized (this) {
			long	currentTime = System.currentTimeMillis();
			if (lastRetrievedMemberCloud != null && userCloudInfo != null && (currentTime - lastRetrievedMemberCloud.getTime() < 120000l))
				return (userCloudInfo);
		}
		
		List<UserCloudInfo>	list = new ArrayList<UserCloudInfo>();
		String	query = "select user.userId, count(rp.id) from RatedPage rp, User user where user.privacy = 0 and rp.user.id = " + 
			"user.id group by rp.user.id";
		 		
		List	result = getHibernateTemplate().find(query);
		
		if (result != null) {
			for (Object entry : result) {
				String	userId = (String)((Object [])entry) [0];
				int		numPages = ((Long)((Object [])entry) [1]).intValue();
				UserCloudInfo	info = new UserCloudInfo(userId, numPages);
				list.add(info);
			}
		}

		synchronized (this) {
			lastRetrievedMemberCloud = new Date();
			userCloudInfo = list;
		}
		return (list);
	}

	@Transactional(readOnly=false)
	public List<Recommendation>	getRecommendations (String userId, Collection<String> override, Collection<String> dontShow,
			int searchSize, int resultSize) throws SQLException, UserQuotaException, UserDisabledException {
		ListInfo	qInfo = new ListInfo();
		qInfo.setMaxResults(1000);
		qInfo.setMaxPerPage(1000);
		
		List<String>	rps = pageDao.findPublicRatedPageURLs(userId);
		UserStats ustats = userStatsDao.findStatsFor(userId);
		Collection<String> desirable = override == null ? ustats.getUser().getDesirableRatingsSet() : override;
		return (Rater.getTheRater().getRecommendations(rps, ustats, searchSize, resultSize, desirable, dontShow, pageDao));
	}

	public int	countRecentStats(ListInfo info) throws SQLException {
		return (pageDao.countRatedPages(info));
	}
	
	public RecentStats getRecentStats(ListInfo info) throws SQLException {
		
		if (info.getSortCols() == null) {
			info.setSortCols(new String [] {"id"});
			info.setAscendings(new boolean [] {false});
		}
		
		// TODO: don't show privately rated pages here
		List<RatedPage>	rps = pageDao.findRatedPages(info);
		List<RatingProjection>	proj = new ArrayList<RatingProjection>();
		
		if (rps != null) {
			for (RatedPage rp : rps) {
				
				//String	title = rp.getParsedPage().getTitle();
				//if (skipUglies && StringUtils.isEmpty(title))
					//continue; // TODO move to query: skip pages that won't show well
				RatingProjection	next = new RatingProjection();
				next.setRating(rp.getRating());
				next.setDate(rp.getTimestamp());
				URL	url = rp.getUrl();
				String	urlStrToUse = url.toString();
				if (url.getProtocol().indexOf("file") != -1)
					urlStrToUse = urlStrToUse.substring(urlStrToUse.lastIndexOf("/") + 1);
				if (Utils.hasVulgaritiesSubstring(urlStrToUse) || WebUtils.isUnsupportedFormat(urlStrToUse))
					continue;
			
				String	title = rp.getParsedPage().getTitle();
				if (Utils.hasVulgarities(title))
					continue;
				if (title != null) title = title.trim();
				
				String	firstLine = rp.getParsedPage().getFirstLine();
				if (Utils.hasVulgarities(firstLine))
					continue;
				if (firstLine != null) firstLine = firstLine.trim();
				
				if (StringUtils.isEmpty(title) || StringUtils.isEmpty(firstLine))
					continue; // skip crap pages
				
				next.setUrl(urlStrToUse);
				next.setTitle(title);
				next.setSnippet(firstLine);
				
				User	usr = rp.getUser();
				Privacy	p = usr.getPrivacy();
				//if (p == Privacy.PRIVATE) -- this can't be done here - moved to query
				//	continue; 
				if (p == Privacy.PUBLIC) {
					next.setUserId(usr.getUserId());
					next.setMainPhotoId(usr.getMainPhotoId());
				}
				if (usr.getCategorySet().isOrdered())
					next.setMaxRating(usr.getMaxRating());
				proj.add(next);
			}
		}
		
		RecentStats	stats = new RecentStats();
		
		stats.setRatings(proj);
		String	query = "select count(rp.id) from RatedPage rp";
	 		
		List	result = getHibernateTemplate().find(query);
	
		if (result != null) {
			for (Object entry : result) {
				int		numPages = ((Long)entry).intValue();
				stats.setTotalPagesRated(numPages);
			}
		}
		
		stats.setTotalPagesPredicted(Rater.getTheRater().getNumTotalPagesPredicted());
		return (stats);
	}

	public List<WordFreqPair> getGlobalWordStats(int max, String search, final boolean mostFrequent) {
		HashMap<String, Integer>	freqMap = Rater.getTheRater().getGlobalStats().getWordToFreqMap();

		List<WordFreqPair>	list = new ArrayList<WordFreqPair>();

		if (!StringUtils.isEmpty(search)) {
			Integer	freq = freqMap.get(search);
			if (freq != null)
				list.add(new WordFreqPair(search, freq));
		}
		else {
			for (String s : freqMap.keySet()) {
				list.add(new WordFreqPair(s, freqMap.get(s)));
			}
			Collections.sort(list, new Comparator () {

				public int compare(Object arg0, Object arg1) {
					WordFreqPair	p0 = (WordFreqPair)arg0;
					WordFreqPair	p1 = (WordFreqPair)arg1;
					return (mostFrequent ? p1.freq - p0.freq : p0.freq - p1.freq);
				}
				
			});
			
			if (list.size() > max)
				list = list.subList(0, max);
		}
		return (list);
	}

	public Prediction [] predict(URL url, int maxUsers, int minHistSize, int snippetSize) throws UserQuotaException,
			UserDoesntExistException, SQLException {
		
		List<User>	users = userDao.listUsers(null);
		ArrayList<Prediction>	predsArr = new ArrayList<Prediction>();
		
		for (int i = 0; i < users.size(); i++) {
			UserStats	stats = userStatsDao.findStatsFor(users.get(i).getUserId());
			Collection<RatedPage>	rps = stats.getRatedPages();
			if (rps == null || rps.size() < minHistSize)
				continue;
			predsArr.add(Rater.getTheRater().predictRating(stats, url, false, null, null, false, snippetSize));
			if (predsArr.size() >= maxUsers)
				break;
		}
		
		Prediction []	preds = new Prediction [predsArr.size()];
		predsArr.toArray(preds);
		return preds;
	}

	public Prediction [] predictRawContent(String rawContent, int maxUsers, int minHistSize, int snippetSize)
			throws UserQuotaException, UserDoesntExistException, SQLException {
		List<User>	users = userDao.listUsers(null);
		
		ArrayList<Prediction>	predsArr = new ArrayList<Prediction>();
		
		File blah;
		PrintWriter	writer = null;
		URL	url = null;
		try {
			blah = File.createTempFile("pred", null);
			BufferedWriter	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blah), "UTF-8"));
			writer = new PrintWriter(bw);
			writer.write(rawContent);
			writer.flush();
			writer.close();
			
			url = new URL ("file://localhost/" + blah.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (writer != null)
				writer.close();
		}
		for (int i = 0; i < users.size(); i++) {
			UserStats stats = userStatsDao.findStatsFor(users.get(i).getUserId());
			Collection<RatedPage>	rps = stats.getRatedPages();
			if (rps == null || rps.size() < minHistSize)
				continue;
			predsArr.add(Rater.getTheRater().predictRating(stats, url, false, null, null, false, snippetSize));
			if (predsArr.size() >= maxUsers)
				break;
		}
		
		Prediction []	preds = new Prediction [predsArr.size()];
		predsArr.toArray(preds);
		return preds;
	}
}
