package wp.service;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wp.core.RatingCloudInfo;
import wp.core.RatingInfo;
import wp.core.RatingOutOfRangeException;
import wp.core.RecentStats;
import wp.model.Recommendation;
import wp.core.UserCloudInfo;
import wp.core.UserDisabledException;
import wp.core.UserDoesntExistException;
import wp.core.UserQuotaException;
import wp.core.WordCloudInfo;
import wp.dao.ListInfo;
import wp.dao.RatingDao;
import wp.dao.UserStatsDao;
import wp.model.PageDiff;
import wp.model.Prediction;
import wp.model.RatedPage;
import wp.model.UserStats;
import wp.model.WordFreqPair;

/**
 * @spring.bean id="ratingService"
 * @spring.property name="ratingDao" ref="ratingDao"
 * @spring.property name="userStatsDao" ref="userStatsDao"
 * @author jsanchez 
 * 
 */
public class RatingServiceImpl implements RatingService
{
	private RatingDao	ratingDao;
	private UserStatsDao	userStatsDao;
	
	public UserStatsDao getUserStatsDao() {
		return userStatsDao;
	}

	public void setUserStatsDao(UserStatsDao userStatsDao) {
		this.userStatsDao = userStatsDao;
	}

	public RatingDao getRatingDao() {
		return ratingDao;
	}

	public void setRatingDao(RatingDao ratingDao) {
		this.ratingDao = ratingDao;
	}

	public UserStats findUserStats(String userid) throws SQLException {
		return (userStatsDao.findStatsFor(userid));
	}

	public RecentStats getRecentStats(ListInfo info)
			throws SQLException {
		return (ratingDao.getRecentStats(info));
	}

	public List<Recommendation> getRecommendations(String userId, Collection<String> override, Collection<String> dontShow, int searchSize, int resultSize)
			throws SQLException, UserQuotaException, UserDisabledException {
		return (ratingDao.getRecommendations(userId, override, dontShow, searchSize, resultSize));
	}
	
	public Collection<WordCloudInfo> getWordCloud(int size, String ratedAs, boolean ignoreCommon, boolean dontUseCache)
			throws SQLException {
		return (ratingDao.getWordCloud(size, ratedAs, ignoreCommon, dontUseCache));
	}

	public Collection<RatingCloudInfo> getRatingsCloud(int size)
		throws SQLException {
		return (ratingDao.getRatingsCloud(size));
	}
	
	public List<PageDiff>		pageDiffs (String userId, URL reference, List<URL> diffs) throws SQLException {
		return (ratingDao.pageDiffs(userId, reference, diffs));
	}

	public Prediction predict(String userid, URL url, String cookieData, Collection<String> keywords, boolean summarize, int snippetSize)
			throws UserQuotaException, UserDoesntExistException, SQLException { 
		return (ratingDao.predict(userid, url, cookieData, keywords, summarize, snippetSize));
	}
	
	public Prediction predictRawContent(String userid, String rawContent, boolean summarize, int snippetSize)
			throws UserQuotaException, UserDoesntExistException, SQLException {
		return (ratingDao.predictRawContent(userid, rawContent, summarize, snippetSize));
	}
	
	public RatingInfo rate(String userid, URL url, String rating, String comment, String cookieData)
			throws UserQuotaException, UserDoesntExistException,
			RatingOutOfRangeException, UserDisabledException, SQLException {
		return (ratingDao.rate(userid, url, rating, comment, cookieData));
	}
	
	public RatingInfo rateRawContent(String userid, String rawContent,
			String rawContentId, String rating, String comment)
			throws UserQuotaException, UserDoesntExistException,
			RatingOutOfRangeException, UserDisabledException, SQLException {
		return (ratingDao.rateRawContent(userid, rawContent, rawContentId, rating, comment));
	}
	
	public void save(UserStats stats) throws SQLException {
		ratingDao.save(stats);
	}

	public void delete(UserStats stats, RatedPage rp) throws SQLException {
		userStatsDao.delete(stats, rp);
	}

	public List<WordFreqPair> getGlobalWordStats(int max, String search, boolean mostFrequent) {
		return (ratingDao.getGlobalWordStats(max, search, mostFrequent));
	}

	public int countRecentStats(ListInfo info) throws SQLException {
		return (ratingDao.countRecentStats(info));
	}

	public Prediction [] predict(URL url, int maxUsers, int minHistSize, int snippetSize) throws UserQuotaException,
			UserDoesntExistException, SQLException {
		return (ratingDao.predict(url, maxUsers, minHistSize, snippetSize));
	}

	public Prediction [] predictRawContent(String rawContent, int maxUsers, int minHistSize, int snippetSize)
			throws UserQuotaException, UserDoesntExistException, SQLException {
		return (ratingDao.predictRawContent(rawContent, maxUsers, minHistSize, snippetSize));
	}

	public Collection<UserCloudInfo> getMostActiveMembers(int size)
			throws SQLException {
		return (ratingDao.getMostActiveMembers(size));
	}
	
} 
