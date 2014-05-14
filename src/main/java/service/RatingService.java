package wp.service;

import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import wp.core.RatingCloudInfo;
import wp.core.RatingInfo;
import wp.core.RatingOutOfRangeException;
import wp.core.RecentStats;
import wp.core.UserCloudInfo;
import wp.core.UserDisabledException;
import wp.core.UserDoesntExistException;
import wp.core.UserQuotaException;
import wp.core.WordCloudInfo;
import wp.dao.ListInfo;
import wp.model.PageDiff;
import wp.model.Prediction;
import wp.model.RatedPage;
import wp.model.Recommendation;
import wp.model.UserStats;
import wp.model.WordFreqPair;

/**
 * Rating + prediction services
 * @author Jeff
 *
 */
public interface RatingService
{
	void		delete (UserStats stats, RatedPage rp) throws SQLException;
	
	RatingInfo  rate (String userid, URL url, String rating, String comment, String cookieData) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, SQLException;
	
	RatingInfo	rateRawContent (String userid, String rawContent, String rawContentId, String rating, String comment) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, SQLException;
	
	Prediction  predict (String userid, URL url, String cookieData, Collection<String> keywords, boolean summarize, int snippetSize) 
		throws UserQuotaException, UserDoesntExistException, SQLException;

	Prediction  predictRawContent (String userid, String rawContent, boolean summarize, int snippetSize) 
		throws UserQuotaException, UserDoesntExistException, SQLException;
	  
	Prediction  [] predict (URL url, int maxUsers, int minHistSize, int snippetSize) 
		throws UserQuotaException, UserDoesntExistException, SQLException;

	Prediction  [] predictRawContent (String rawContent, int maxUsers, int minHistSize, int snippetSize) 
		throws UserQuotaException, UserDoesntExistException, SQLException;
  
	UserStats	findUserStats (String userid) throws SQLException;
	
	void	save (UserStats stats) throws SQLException;
	
	RecentStats	getRecentStats (ListInfo info) throws SQLException;
	
	int		countRecentStats (ListInfo info) throws SQLException;
	
	Collection<WordCloudInfo>	getWordCloud(int size, String ratedAs, boolean ignoreCommon, boolean dontUseCache) throws SQLException;

	Collection<RatingCloudInfo>	getRatingsCloud(int size) throws SQLException;

	Collection<UserCloudInfo>	getMostActiveMembers(int size) throws SQLException;

	List<Recommendation>	getRecommendations (String userId, Collection<String> override, Collection<String> dontShow,
			int searchSize, int resultSize) throws SQLException, UserQuotaException, UserDisabledException;
	
	List<WordFreqPair> getGlobalWordStats(int max, String search, boolean mostFrequent);
	
	List<PageDiff>		pageDiffs (String userId, URL reference, List<URL> diffs) throws SQLException;
	
}
