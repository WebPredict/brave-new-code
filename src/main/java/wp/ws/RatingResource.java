package wp.ws;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import wp.core.RatingCloudInfo;
import wp.core.RatingInfo;
import wp.core.RatingOutOfRangeException;
import wp.core.RecentStats;
import wp.core.UserCloudInfo;
import wp.core.UserDisabledException;
import wp.core.UserDoesntExistException;
import wp.core.UserQuotaException;
import wp.core.WordCloudInfo;
import wp.model.PageDiff;
import wp.model.Prediction;
import wp.model.Recommendation;
import wp.model.UserStats;
import wp.model.WordFreqPair;


/**
 * Rating + prediction RESTful resource
 * @author Jeff
 *
 */
public interface RatingResource
{
	void		delete (@PathParam("userId") Long userId, @QueryParam("pageUrl") String pageUrl) throws ResourceException;

	RatingInfo  rate (@PathParam("userId") Long userId, @QueryParam("url") String url, @QueryParam("rating") String rating, 
						@QueryParam("comment") String comment, @QueryParam("cookieData") String cookieData) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, ResourceException;

	RatingInfo	rateRawContent (@PathParam("userId") Long userId, String rawContent, String rawContentId, String rating, String comment) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, ResourceException;

	Prediction  predict (@PathParam("userId") Long userId, @PathParam("url") String url, @PathParam("cookieData") String cookieData, 
		Collection<String> keywords, boolean summarize, int snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;

	Prediction  predictRawContent (@PathParam("userId") Long userId, @FormParam("rawContent") String rawContent, @QueryParam("summarize") Boolean summarize, @QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;
	  
	Prediction  [] predict (@QueryParam("url") String url, @QueryParam("maxUsers") Integer maxUsers, @QueryParam("minHistSize") Integer minHistSize, @QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;

	Prediction  [] predictRawContent (@FormParam("rawContent") String rawContent, @QueryParam("maxUsers") Integer maxUsers, @QueryParam("minHistSize") Integer minHistSize, 
		@QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;
  
	UserStats	findUserStats (@PathParam("userId") Long userId) throws ResourceException;

	void	save (@PathParam("userId") Long userId, @FormParam("stats") String stats) throws ResourceException;

	RecentStats	getRecentStats (@QueryParam("info") String info) throws ResourceException;
	
	int		countRecentStats (@QueryParam("info") String info) throws ResourceException;
	
	Collection<WordCloudInfo>	getWordCloud(@QueryParam("size") Integer size, @QueryParam("ratedAs") String ratedAs, 
		@QueryParam("ignoreCommon") Boolean ignoreCommon, @QueryParam("dontUseCache") Boolean dontUseCache) throws ResourceException;

	Collection<RatingCloudInfo>	getRatingsCloud(@QueryParam("size") Integer size) throws ResourceException;

	Collection<UserCloudInfo>	getMostActiveMembers(@QueryParam("size") Integer size) throws ResourceException;

	List<Recommendation>	getRecommendations (@PathParam("userId") String userId, @QueryParam("overrideList") String overrideList, 
								@QueryParam("dontShowList") String dontShowList, @QueryParam("searchSize") Integer searchSize, @QueryParam("resultSize") Integer resultSize) 
		throws ResourceException, UserQuotaException, UserDisabledException;
	
	List<WordFreqPair> getGlobalWordStats(@QueryParam("max") Integer max, @QueryParam("search") String search, @QueryParam("mostFrequent") Boolean mostFrequent);
	
	List<PageDiff>		pageDiffs (@PathParam("userId") Long userId, @QueryParam("reference") String reference, @QueryParam("diffsList") String diffsList) throws ResourceException;
	
}
