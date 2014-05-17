package wp.ws;

import java.net.URL;
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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Rating + prediction RESTful resource
 * @author Jeff
 *
 */
public interface RatingResource
{

	@DELETE
	@Path("/wp/rating/{userId}/{pageId}")
	void		delete (@PathParam("userId") Long userId, @PathParam("pageId") long pageId) throws ResourceException;

	@POST	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/rate/{userId}")
	RatingInfo  rate (@PathParam("userId") Long userId, @QueryParam("url") String url, @QueryParam("rating") String rating, 
						@QueryParam("comment") String comment, @QueryParam("cookieData") String cookieData) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, ResourceException;

	@POST	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/rateraw/{userId}")
	RatingInfo	rateRawContent (@PathParam("userId") Long userId, String rawContent, String rawContentId, String rating, String comment) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, ResourceException;

	@GET	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/predict/{userId}")
	Prediction  predict (@PathParam("userId") Long userId, @PathParam("url") String url, @PathParam("cookieData") String cookieData, 
		Collection<String> keywords, boolean summarize, int snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;

	@PUT	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/predictraw/{userId}")
	Prediction  predictRawContent (@PathParam("userId") Long userId, @FormParam("rawContent") String rawContent, @QueryParam("summarize") Boolean summarize, @QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;
	  
	@GET	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/predict/{userId}")
	Prediction  [] predict (@QueryParam("url") String url, @QueryParam("maxUsers") Integer maxUsers, @QueryParam("minHistSize") Integer minHistSize, @QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;

	@POST
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/predictraw/{userId}")
	Prediction  [] predictRawContent (@FormParam("rawContent") String rawContent, @QueryParam("maxUsers") Integer maxUsers, @QueryParam("minHistSize") Integer minHistSize, 
		@QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException;
  
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/{userId}")
	UserStats	findUserStats (@PathParam("userId") Long userId) throws ResourceException;

	@PUT	
	@Path("/wp/rating/save/{userId}")
	void	save (@PathParam("userId") Long userId, @FormParam("stats") String stats) throws ResourceException;

	@GET	
	@Path("/wp/rating/recentstats/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
	RecentStats	getRecentStats (@QueryParam("info") String info) throws ResourceException;
	
	@GET	
	@Path("/wp/rating/countstats/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	int		countRecentStats (@QueryParam("info") String info) throws ResourceException;
	
	@GET	
	@Path("/wp/rating/wordcloud/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
	Collection<WordCloudInfo>	getWordCloud(@QueryParam("size") Integer size, @QueryParam("ratedAs") String ratedAs, 
		@QueryParam("ignoreCommon") Boolean ignoreCommon, @QueryParam("dontUseCache") Boolean dontUseCache) throws ResourceException;

	@GET	
	@Path("/wp/rating/ratingscloud/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
	Collection<RatingCloudInfo>	getRatingsCloud(@QueryParam("size") Integer size) throws ResourceException;

	@GET	
	@Path("/wp/rating/mostactive")
    @Produces({ MediaType.APPLICATION_JSON })
	Collection<UserCloudInfo>	getMostActiveMembers(@QueryParam("size") Integer size) throws ResourceException;

	@GET	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/wp/rating/recommendations/{userId}")
	List<Recommendation>	getRecommendations (@PathParam("userId") String userId, @QueryParam("overrideList") String overrideList, 
								@QueryParam("dontShowList") String dontShowList, @QueryParam("searchSize") Integer searchSize, @QueryParam("resultSize") Integer resultSize) 
		throws ResourceException, UserQuotaException, UserDisabledException;
	
	@GET	
	@Path("/wp/rating/wordstats")
    @Produces({ MediaType.APPLICATION_JSON })
	List<WordFreqPair> getGlobalWordStats(@QueryParam("max") Integer max, @QueryParam("search") String search, @QueryParam("mostFrequent") Boolean mostFrequent);
	
	@GET	
	@Path("/wp/rating/pagediff/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
	List<PageDiff>		pageDiffs (@PathParam("userId") Long userId, @QueryParam("reference") String reference, @QueryParam("diffsList") String diffsList) throws ResourceException;
	
}
